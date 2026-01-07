package com.company.hrms.performance.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.performance.api.request.CreateCycleRequest;
import com.company.hrms.performance.api.response.CreateCycleResponse;
import com.company.hrms.performance.application.service.task.CreateCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsForCreateTask;
import com.company.hrms.performance.application.service.task.SaveCycleForCreateTask;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

/**
 * CreateCycleServiceImpl TDD 測試
 */
class CreateCycleServiceImplTest {

    @Mock
    private IPerformanceCycleRepository cycleRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CreateCycleServiceImpl service;

    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentUser = new JWTModel();

        // Manual instantiation with real tasks to test the pipeline flow
        CreateCycleTask createCycleTask = new CreateCycleTask();
        SaveCycleForCreateTask saveCycleTask = new SaveCycleForCreateTask(cycleRepository);
        PublishCycleEventsForCreateTask publishEventsTask = new PublishCycleEventsForCreateTask(eventPublisher);

        service = new CreateCycleServiceImpl(createCycleTask, saveCycleTask, publishEventsTask);
    }

    @Test
    void testCreateCycle_Success() throws Exception {
        // Given
        CreateCycleRequest request = CreateCycleRequest.builder()
                .cycleName("2025年度考核")
                .cycleType(CycleType.ANNUAL)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .selfEvalDeadline(LocalDate.of(2026, 1, 15))
                .managerEvalDeadline(LocalDate.of(2026, 1, 31))
                .build();

        // When
        CreateCycleResponse response = service.execCommand(request, currentUser);

        // Then
        assertNotNull(response);
        assertNotNull(response.getCycleId());

        // Verify repository save was called
        verify(cycleRepository, times(1)).save(any(PerformanceCycle.class));
    }

    @Test
    void testCreateCycle_WithInvalidDateRange_ShouldThrowException() {
        // Given
        CreateCycleRequest request = CreateCycleRequest.builder()
                .cycleName("2025年度考核")
                .cycleType(CycleType.ANNUAL)
                .startDate(LocalDate.of(2025, 12, 31))
                .endDate(LocalDate.of(2025, 1, 1)) // 結束日早於開始日
                .selfEvalDeadline(LocalDate.of(2026, 1, 15))
                .managerEvalDeadline(LocalDate.of(2026, 1, 31))
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            service.execCommand(request, currentUser);
        });
    }
}
