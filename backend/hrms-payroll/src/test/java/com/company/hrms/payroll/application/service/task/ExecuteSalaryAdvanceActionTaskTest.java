package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;

/**
 * ExecuteSalaryAdvanceActionTask 單元測試
 *
 * 驗證依 actionType 執行對應 Domain 操作的行為
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExecuteSalaryAdvanceActionTask 測試")
class ExecuteSalaryAdvanceActionTaskTest {

    @InjectMocks
    private ExecuteSalaryAdvanceActionTask task;

    private SalaryAdvanceContext context;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        context = new SalaryAdvanceContext();
        currentUser = JWTModel.builder()
                .userId("approver-001")
                .employeeId("approver-emp-001")
                .build();
        context.setCurrentUser(currentUser);
    }

    /**
     * 建立一筆 PENDING 狀態的預借申請
     */
    private SalaryAdvance createPendingAdvance() {
        return new SalaryAdvance(
                AdvanceId.generate(),
                "emp-uuid-001",
                new BigDecimal("30000"),
                3,
                "家庭急需");
    }

    @Nested
    @DisplayName("APPROVE 操作")
    class ApproveActionTests {

        @Test
        @DisplayName("應正確執行核准操作")
        void shouldApproveAdvance() throws Exception {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            context.setSalaryAdvance(advance);
            context.setActionType("APPROVE");
            context.setApprovedAmount(new BigDecimal("25000"));

            // When
            task.execute(context);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.APPROVED);
            assertThat(advance.getApprovedAmount()).isEqualByComparingTo("25000");
            assertThat(advance.getApproverId()).isEqualTo("approver-001");
        }
    }

    @Nested
    @DisplayName("REJECT 操作")
    class RejectActionTests {

        @Test
        @DisplayName("應正確執行駁回操作")
        void shouldRejectAdvance() throws Exception {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            context.setSalaryAdvance(advance);
            context.setActionType("REJECT");
            context.setRejectionReason("金額過高");

            // When
            task.execute(context);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.REJECTED);
            assertThat(advance.getRejectionReason()).isEqualTo("金額過高");
        }
    }

    @Nested
    @DisplayName("DISBURSE 操作")
    class DisburseActionTests {

        @Test
        @DisplayName("應正確執行撥款操作")
        void shouldDisburseAdvance() throws Exception {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            advance.approve("approver-001", new BigDecimal("25000"));
            context.setSalaryAdvance(advance);
            context.setActionType("DISBURSE");

            // When
            task.execute(context);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.DISBURSED);
            assertThat(advance.getDisbursementDate()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("CANCEL 操作")
    class CancelActionTests {

        @Test
        @DisplayName("應正確執行取消操作")
        void shouldCancelAdvance() throws Exception {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            context.setSalaryAdvance(advance);
            context.setActionType("CANCEL");

            // When
            task.execute(context);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("不支援的操作")
    class UnsupportedActionTests {

        @Test
        @DisplayName("不支援的操作類型應拋出 IllegalArgumentException")
        void shouldThrowForUnsupportedAction() {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            context.setSalaryAdvance(advance);
            context.setActionType("UNKNOWN");

            // When & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertThat(ex.getMessage()).contains("不支援的操作類型");
        }
    }

    @Test
    @DisplayName("getName 應返回 '執行預借薪資操作'")
    void shouldReturnCorrectName() {
        assertEquals("執行預借薪資操作", task.getName());
    }
}
