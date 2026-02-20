package com.company.hrms.insurance.application.service.withdrawal.task;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.exception.ValidationException;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * ValidateWithdrawalTask 單元測試
 */
@DisplayName("ValidateWithdrawalTask 測試")
class ValidateWithdrawalTaskTest {

    private ValidateWithdrawalTask task;

    @BeforeEach
    void setUp() {
        task = new ValidateWithdrawalTask();
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("ACTIVE 狀態且退保日期有效時，應通過驗證")
        void execute_ActiveStatusValidDate_ShouldPass() throws Exception {
            // Given
            WithdrawalContext context = createContext("2025-12-31");
            context.setEnrollment(createActiveEnrollment());

            // When & Then - 不應拋出異常
            assertDoesNotThrow(() -> task.execute(context));
        }

        @Test
        @DisplayName("已退保狀態時，應拋出 DomainException")
        void execute_NonActiveStatus_ShouldThrowException() {
            // Given
            WithdrawalContext context = createContext("2025-12-31");
            InsuranceEnrollment enrollment = createActiveEnrollment();
            enrollment.withdraw(LocalDate.of(2025, 6, 30)); // 已退保，狀態為 WITHDRAWN
            context.setEnrollment(enrollment);

            // When & Then - Task 先檢查 WITHDRAWN 狀態，拋出「已經辦理退保」
            DomainException ex = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertTrue(ex.getMessage().contains("已經辦理退保"));
        }

        @Test
        @DisplayName("退保日期早於加保日期時，應拋出 ValidationException")
        void execute_WithdrawDateBeforeEnrollDate_ShouldThrowException() {
            // Given
            WithdrawalContext context = createContext("2024-12-01"); // 早於加保日期 2025-01-01
            context.setEnrollment(createActiveEnrollment());

            // When & Then
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> task.execute(context));
            assertTrue(ex.getMessage().contains("退保日期不可早於加保日期"));
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
