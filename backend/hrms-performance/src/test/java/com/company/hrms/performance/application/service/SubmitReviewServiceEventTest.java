package com.company.hrms.performance.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.performance.api.request.SubmitReviewRequest;
import com.company.hrms.performance.application.service.context.SubmitReviewContext;
import com.company.hrms.performance.application.service.task.LoadReviewTask;
import com.company.hrms.performance.application.service.task.PublishReviewEventsTask;
import com.company.hrms.performance.application.service.task.SaveReviewTask;
import com.company.hrms.performance.application.service.task.SubmitEvaluationTask;
import com.company.hrms.performance.domain.event.PerformanceReviewSubmittedEvent;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;

@SpringBootTest(classes = {
        SubmitReviewServiceImpl.class,
        PublishReviewEventsTask.class,
        SubmitReviewServiceEventTest.Config.class
})
@org.springframework.test.context.ActiveProfiles("test")
@DisplayName("HR08 考核評估提交事件測試")
class SubmitReviewServiceEventTest {

    @Autowired
    private SubmitReviewServiceImpl submitReviewService;

    @MockBean
    private LoadReviewTask loadReviewTask;

    @MockBean
    private SubmitEvaluationTask submitEvaluationTask;

    @MockBean
    private SaveReviewTask saveReviewTask;

    @MockBean
    private EventPublisher eventPublisher;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager() {
            return mock(PlatformTransactionManager.class);
        }
    }

    @Test
    @DisplayName("提交考核時應發布 PerformanceReviewSubmittedEvent")
    void execCommand_ShouldPublishEvent_WhenReviewSubmitted() throws Exception {
        // Arrange
        String reviewIdStr = "REVIEW-001";
        List<EvaluationItem> items = new ArrayList<>();
        // Add valid items to pass domain validation (need at least 3)
        items.add(new EvaluationItem(UUID.randomUUID(), "Item 1", 20, "Desc", "Criteria", 4, "Good", null));
        items.add(new EvaluationItem(UUID.randomUUID(), "Item 2", 30, "Desc", "Criteria", 5, "Excellent", null));
        items.add(new EvaluationItem(UUID.randomUUID(), "Item 3", 50, "Desc", "Criteria", 3, "Fair", null));

        SubmitReviewRequest request = SubmitReviewRequest.builder()
                .reviewId(reviewIdStr)
                .evaluationItems(items)
                .comments("Great job")
                .build();

        JWTModel currentUser = new JWTModel();
        currentUser.setUserId("user1");

        // Create a Review aggregate
        PerformanceReview review = PerformanceReview.create(
                CycleId.create(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReviewType.SELF);

        // Stub shouldExecute to return true (Pipeline requirement)
        when(loadReviewTask.shouldExecute(any(SubmitReviewContext.class))).thenReturn(true);
        when(submitEvaluationTask.shouldExecute(any(SubmitReviewContext.class))).thenReturn(true);
        when(saveReviewTask.shouldExecute(any(SubmitReviewContext.class))).thenReturn(true);

        // Simulate LoadReviewTask: Set review to context
        doAnswer(invocation -> {
            SubmitReviewContext ctx = invocation.getArgument(0);
            ctx.setReview(review);
            return null;
        }).when(loadReviewTask).execute(any(SubmitReviewContext.class));

        // Simulate SubmitEvaluationTask: call domain logic (which now registers event)
        doAnswer(invocation -> {
            SubmitReviewContext ctx = invocation.getArgument(0);
            // Call domain logic
            ctx.getReview().submitEvaluation(ctx.getEvaluationItems(), ctx.getComments());
            return null;
        }).when(submitEvaluationTask).execute(any(SubmitReviewContext.class));

        // Stub SaveReviewTask: do nothing
        // (Default void mock does nothing)

        // Capture events from EventPublisher
        final java.util.concurrent.atomic.AtomicReference<List<DomainEvent>> capturedEventsWrapper = new java.util.concurrent.atomic.AtomicReference<>();
        doAnswer(invocation -> {
            List<DomainEvent> events = invocation.getArgument(0);
            capturedEventsWrapper.set(new ArrayList<>(events));
            return null;
        }).when(eventPublisher).publishAll(any());

        // Act
        submitReviewService.execCommand(request, currentUser);

        // Assert
        verify(loadReviewTask).execute(any(SubmitReviewContext.class));
        verify(submitEvaluationTask).execute(any(SubmitReviewContext.class));
        verify(eventPublisher, times(1)).publishAll(any());

        List<DomainEvent> publishedEvents = capturedEventsWrapper.get();
        assertNotNull(publishedEvents, "Events should have been published");
        assertEquals(1, publishedEvents.size());
        assertEquals(PerformanceReviewSubmittedEvent.class, publishedEvents.get(0).getClass());

        PerformanceReviewSubmittedEvent event = (PerformanceReviewSubmittedEvent) publishedEvents.get(0);
        assertEquals(review.getReviewId(), event.getReviewId());
        assertEquals("SELF", event.getReviewType());
    }
}
