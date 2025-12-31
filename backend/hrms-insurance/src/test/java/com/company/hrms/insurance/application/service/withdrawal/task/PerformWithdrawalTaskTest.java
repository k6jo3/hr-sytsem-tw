package com.company.hrms.insurance.application.service.withdrawal.task;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * PerformWithdrawalTask 單元測試
 */
@DisplayName("PerformWithdrawalTask 測試")
class PerformWithdrawalTaskTest {

    private PerformWithdrawalTask task;

    @BeforeEach
    void setUp() {
        task = new PerformWithdrawalTask();
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("執行退保後，狀態應變為 WITHDRAWN")
        void execute_ShouldChangeStatusToWithdrawn() throws Exception {
            // Given
            WithdrawalContext context = createContext("2025-12-31");
            InsuranceEnrollment enrollment = createActiveEnrollment();
            context.setEnrollment(enrollment);

            // When
            task.execute(context);

            // Then
            assertEquals(EnrollmentStatus.WITHDRAWN, enrollment.getStatus());
        }

        @Test
        @DisplayName("執行退保後，應設置退保日期")
        void execute_ShouldSetWithdrawDate() throws Exception {
            // Given
            WithdrawalContext context = createContext("2025-12-31");
            InsuranceEnrollment enrollment = createActiveEnrollment();
            context.setEnrollment(enrollment);

            // When
            task.execute(context);

            // Then
            assertEquals(LocalDate.of(2025, 12, 31), enrollment.getWithdrawDate());
        }
    }

    // ==================== Helper Methods ====================

    private WithdrawalContext createContext(String withdrawDate) {
        WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
                .withdrawDate(withdrawDate)
                .reason("離職")
                .build();
        return new WithdrawalContext("enrollment-001", request, "default");
    }

    private InsuranceEnrollment createActiveEnrollment() {
        InsuranceLevel level = new InsuranceLevel(
                LevelId.generate(),
                InsuranceType.LABOR,
                15,
                new BigDecimal("48200"),
                LocalDate.of(2025, 1, 1));

        return InsuranceEnrollment.enroll(
                "EMP001",
                new UnitId("unit-001"),
                InsuranceType.LABOR,
                level,
                LocalDate.of(2025, 1, 1));
    }
}
