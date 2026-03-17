package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;

/**
 * InitSalaryAdvanceTask 單元測試
 *
 * 驗證初始化預借薪資 Task 的行為
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InitSalaryAdvanceTask 測試")
class InitSalaryAdvanceTaskTest {

    @InjectMocks
    private InitSalaryAdvanceTask task;

    private SalaryAdvanceContext context;

    @BeforeEach
    void setUp() {
        context = new SalaryAdvanceContext();
        context.setEmployeeId("emp-uuid-001");
        context.setRequestedAmount(new BigDecimal("30000"));
        context.setInstallmentMonths(3);
        context.setReason("家庭急需");
    }

    @Nested
    @DisplayName("初始化成功")
    class SuccessTests {

        @Test
        @DisplayName("應成功建立 SalaryAdvance 並設置到 Context，狀態為 PENDING")
        void shouldInitAdvanceWithPendingStatus() throws Exception {
            // When
            task.execute(context);

            // Then
            assertThat(context.getSalaryAdvance()).as("advance 不應為 null").isNotNull();
            assertThat(context.getSalaryAdvance().getId()).as("ID 不應為 null").isNotNull();
            assertThat(context.getSalaryAdvance().getEmployeeId()).isEqualTo("emp-uuid-001");
            assertThat(context.getSalaryAdvance().getRequestedAmount()).isEqualByComparingTo("30000");
            assertThat(context.getSalaryAdvance().getInstallmentMonths()).isEqualTo(3);
            assertThat(context.getSalaryAdvance().getStatus()).isEqualTo(AdvanceStatus.PENDING);
            assertThat(context.getSalaryAdvance().getReason()).isEqualTo("家庭急需");
        }
    }

    @Nested
    @DisplayName("初始化失敗")
    class FailureTests {

        @Test
        @DisplayName("員工 ID 為空時應拋出例外")
        void shouldThrowWhenEmployeeIdIsBlank() {
            // Given
            context.setEmployeeId("");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("申請金額 <= 0 時應拋出例外")
        void shouldThrowWhenAmountIsZero() {
            // Given
            context.setRequestedAmount(BigDecimal.ZERO);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("分期月數 < 1 時應拋出例外")
        void shouldThrowWhenInstallmentMonthsIsZero() {
            // Given
            context.setInstallmentMonths(0);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '初始化預借薪資申請'")
    void shouldReturnCorrectName() {
        assertEquals("初始化預借薪資申請", task.getName());
    }
}
