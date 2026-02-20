package com.company.hrms.performance.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

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
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsTask;
import com.company.hrms.performance.application.service.task.SaveCycleTask;
import com.company.hrms.performance.application.service.task.StartCycleTask;
import com.company.hrms.performance.domain.event.PerformanceCycleStartedEvent;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;
import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

@SpringBootTest(classes = {
        StartCycleServiceImpl.class,
        PublishCycleEventsTask.class,
        StartCycleServiceEventTest.Config.class
})
@org.springframework.test.context.ActiveProfiles("test")
@DisplayName("HR08 考核週期啟動事件測試")
@SuppressWarnings("null")
class StartCycleServiceEventTest {

    @Autowired
    private StartCycleServiceImpl startCycleService;

    @MockBean
    private LoadCycleTask loadCycleTask;

    @MockBean
    private StartCycleTask startCycleTask;

    @MockBean
    private SaveCycleTask saveCycleTask;

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
    @DisplayName("啟動週期時應發布 PerformanceCycleStartedEvent")
    void execCommand_ShouldPublishEvent_WhenCycleStarts() throws Exception {
        // Arrange
        String cycleId = "CYCLE-001";
        StartCycleRequest request = StartCycleRequest.builder().cycleId(cycleId).build();
        JWTModel currentUser = new JWTModel();
        currentUser.setUserId("admin");

        // Verify Injection
        Object injectedTask = org.springframework.test.util.ReflectionTestUtils.getField(startCycleService,
                "loadCycleTask");
        if (injectedTask == null) {
            System.out.println("FATAL: loadCycleTask is null in service!");
        } else {
            System.out.println("INFO: loadCycleTask is injected. Class: " + injectedTask.getClass().getName());
            boolean isMock = org.mockito.Mockito.mockingDetails(injectedTask).isMock();
            System.out.println("INFO: Injected task is mock? " + isMock);
            if (injectedTask == loadCycleTask) {
                System.out.println("INFO: Injected task IS SAME AS test field.");
            } else {
                System.out.println("FATAL: Injected task IS DIFFERENT from test field.");
            }
        }

        // 1. Create Cycle
        PerformanceCycle cycle = PerformanceCycle.create(
                "2025 Q1",
                CycleType.QUARTERLY,
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusMonths(3).plusDays(10),
                LocalDate.now().plusMonths(3).plusDays(20));

        // 2. Create & Configure Template
        EvaluationTemplate template = EvaluationTemplate.create("General Template", ScoringSystem.FIVE_GRADE, false);
        EvaluationItem item = EvaluationItem.createDefinition("Work Quality", 100, "Description", "Criteria");
        template.addEvaluationItem(item);

        // 3. Assign & Publish Template (Required for cycle.start())
        cycle.saveTemplate(template);
        cycle.publishTemplate();

        // Stub shouldExecute to return true (Pipeline requirement)
        org.mockito.Mockito.when(loadCycleTask.shouldExecute(any(StartCycleContext.class))).thenReturn(true);
        org.mockito.Mockito.when(startCycleTask.shouldExecute(any(StartCycleContext.class))).thenReturn(true);
        org.mockito.Mockito.when(saveCycleTask.shouldExecute(any(StartCycleContext.class))).thenReturn(true);

        // Simulate StartCycleTask behavior
        // Simulate LoadCycleTask behavior
        doAnswer(invocation -> {
            StartCycleContext ctx = invocation.getArgument(0);
            ctx.setCycle(cycle);
            return null;
        }).when(loadCycleTask).execute(any(StartCycleContext.class));

        // Simulate StartCycleTask behavior
        doAnswer(invocation -> {
            StartCycleContext ctx = invocation.getArgument(0);
            // Simulate starting cycle (triggers event)
            if (ctx.getCycle() != null) {
                ctx.getCycle().start();
            }
            return null;
        }).when(startCycleTask).execute(any(StartCycleContext.class));

        // Capture events when published (before they are cleared)
        final java.util.concurrent.atomic.AtomicReference<List<DomainEvent>> capturedEventsWrapper = new java.util.concurrent.atomic.AtomicReference<>();
        doAnswer(invocation -> {
            List<DomainEvent> events = invocation.getArgument(0);
            // Create a copy because correct implementation of publishAll should consume
            // them instantly
            // and the source list will be cleared immediately after
            capturedEventsWrapper.set(new java.util.ArrayList<>(events));
            return null;
        }).when(eventPublisher).publishAll(any());

        // Act
        startCycleService.execCommand(request, currentUser);

        // Assert
        verify(loadCycleTask).execute(any(StartCycleContext.class));
        verify(eventPublisher, times(1)).publishAll(any());

        List<DomainEvent> publishedEvents = capturedEventsWrapper.get();
        org.junit.jupiter.api.Assertions.assertNotNull(publishedEvents, "Events should have been published");
        assertEquals(1, publishedEvents.size());
        assertEquals(PerformanceCycleStartedEvent.class, publishedEvents.get(0).getClass());

        PerformanceCycleStartedEvent event = (PerformanceCycleStartedEvent) publishedEvents.get(0);
        assertEquals(cycle.getCycleId(), event.getCycleId());
    }
}
