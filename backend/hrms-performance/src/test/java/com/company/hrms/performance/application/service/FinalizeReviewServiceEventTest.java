package com.company.hrms.performance.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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
import com.company.hrms.performance.api.request.FinalizeReviewRequest;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;
import com.company.hrms.performance.application.service.task.FinalizeReviewTask;
import com.company.hrms.performance.application.service.task.LoadReviewForFinalizeTask;
import com.company.hrms.performance.application.service.task.PublishReviewEventsForFinalizeTask;
import com.company.hrms.performance.application.service.task.SaveReviewForFinalizeTask;
import com.company.hrms.performance.domain.event.PerformanceReviewCompletedEvent;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;

@SpringBootTest(classes = {
        FinalizeReviewServiceImpl.class,
        PublishReviewEventsForFinalizeTask.class,
        FinalizeReviewServiceEventTest.Config.class
})
@org.springframework.test.context.ActiveProfiles("test")
@DisplayName("HR08 考核評等確認事件測試")
class FinalizeReviewServiceEventTest {

    @Autowired
    private FinalizeReviewServiceImpl finalizeReviewService;

    @MockBean
    private LoadReviewForFinalizeTask loadReviewForFinalizeTask;

    @MockBean
    private FinalizeReviewTask finalizeReviewTask;

    @MockBean
    private SaveReviewForFinalizeTask saveReviewForFinalizeTask;

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
    @DisplayName("確認考核時應發布 PerformanceReviewCompletedEvent")
    void execCommand_ShouldPublishEvent_WhenReviewFinalized() throws Exception {
        // Arrange
        String reviewIdStr = "REVIEW-001";
        BigDecimal finalScore = new BigDecimal("85.5");
        String finalRating = "A";
        String adjustmentReason = "Performance exceeded expectations";

        FinalizeReviewRequest request = FinalizeReviewRequest.builder()
                .reviewId(reviewIdStr)
                .finalScore(finalScore)
                .finalRating(finalRating)
                .adjustmentReason(adjustmentReason)
                .build();

        JWTModel currentUser = new JWTModel();
        currentUser.setUserId("hr-admin");

        // Create a Review aggregate in simulated PENDING_FINALIZE state
        PerformanceReview review = PerformanceReview.reconstitute(
                ReviewId.create(),
                CycleId.create(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReviewType.SELF,
                new ArrayList<>(),
                new BigDecimal("80"),
                "B",
                null,
                null,
                null,
                null,
                ReviewStatus.PENDING_FINALIZE, // State allows finalize
                java.time.LocalDateTime.now(),
                null,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now());

        // Stub shouldExecute to return true
        when(loadReviewForFinalizeTask.shouldExecute(any(FinalizeReviewContext.class))).thenReturn(true);
        when(finalizeReviewTask.shouldExecute(any(FinalizeReviewContext.class))).thenReturn(true);
        when(saveReviewForFinalizeTask.shouldExecute(any(FinalizeReviewContext.class))).thenReturn(true);

        // Simulate Load Task
        doAnswer(invocation -> {
            FinalizeReviewContext ctx = invocation.getArgument(0);
            ctx.setReview(review);
            return null;
        }).when(loadReviewForFinalizeTask).execute(any(FinalizeReviewContext.class));

        // Simulate Finalize Task (Domain Logic)
        doAnswer(invocation -> {
            FinalizeReviewContext ctx = invocation.getArgument(0);
            ctx.getReview().finalize(ctx.getFinalScore(), ctx.getFinalRating(), ctx.getAdjustmentReason());
            return null;
        }).when(finalizeReviewTask).execute(any(FinalizeReviewContext.class));

        // Capture events
        final List<DomainEvent>[] capturedEventsWrapper = new List[1];
        doAnswer(invocation -> {
            List<DomainEvent> events = invocation.getArgument(0);
            capturedEventsWrapper[0] = new ArrayList<>(events);
            return null;
        }).when(eventPublisher).publishAll(any());

        // Act
        finalizeReviewService.execCommand(request, currentUser);

        // Assert
        verify(loadReviewForFinalizeTask).execute(any(FinalizeReviewContext.class));
        verify(finalizeReviewTask).execute(any(FinalizeReviewContext.class));
        verify(eventPublisher, times(1)).publishAll(any());

        List<DomainEvent> publishedEvents = capturedEventsWrapper[0];
        assertNotNull(publishedEvents);
        assertEquals(1, publishedEvents.size());
        assertEquals(PerformanceReviewCompletedEvent.class, publishedEvents.get(0).getClass());

        PerformanceReviewCompletedEvent event = (PerformanceReviewCompletedEvent) publishedEvents.get(0);
        assertEquals(review.getReviewId(), event.getReviewId());
        assertEquals(finalScore, event.getFinalScore());
        assertEquals(finalRating, event.getFinalRating());
    }
}
