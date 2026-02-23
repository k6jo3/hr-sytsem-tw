package com.company.hrms.training.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.TrainingStatisticsResponse;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

@ExtendWith(MockitoExtension.class)

class GetTrainingStatisticsServiceTest {

    @Mock
    private TrainingEnrollmentQueryRepository enrollmentRepository;

    private GetTrainingStatisticsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GetTrainingStatisticsServiceImpl(enrollmentRepository);
    }

    @Test
    @DisplayName("Should calculate statistics correctly")
    void shouldCalculateStatistics() {
        // Given
        TrainingEnrollmentEntity e1 = new TrainingEnrollmentEntity();
        e1.setStatus(EnrollmentStatus.COMPLETED);
        e1.setCompletedHours(new BigDecimal("10"));

        TrainingEnrollmentEntity e2 = new TrainingEnrollmentEntity();
        e2.setStatus(EnrollmentStatus.COMPLETED);
        e2.setCompletedHours(new BigDecimal("5"));

        TrainingEnrollmentEntity e3 = new TrainingEnrollmentEntity();
        e3.setStatus(EnrollmentStatus.REGISTERED); // Not completed

        when(enrollmentRepository.findPage(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(e1, e2, e3)));

        TrainingStatisticsQuery query = new TrainingStatisticsQuery();
        query.setStartDate(LocalDate.now());
        query.setEndDate(LocalDate.now());

        // When
        TrainingStatisticsResponse response = service.getResponse(query, null);

        // Then
        assertThat(response.getTotalEnrollments()).isEqualTo(3);
        assertThat(response.getCompletionRate()).isEqualTo(2.0 / 3.0); // 0.666...
        assertThat(response.getTotalTrainingHours()).isEqualByComparingTo(new BigDecimal("15"));
    }
}
