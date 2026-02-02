package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

/**
 * PayrollRun 領域模型單元測試
 *
 * 驗證薪資批次的生命週期管理與狀態轉換
 */
@DisplayName("PayrollRun 領域模型測試")
class PayrollRunTest {

    private PayrollRun createRun() {
        return PayrollRun.create(
                new RunId("PR-202512"),
                "Dec 2025 Payroll",
                "ORG-001",
                PayPeriod.ofMonth(2025, 12),
                PayrollSystem.MONTHLY,
                LocalDate.of(2026, 1, 5),
                "USER-001");
    }

    // ========================================================================
    // 1. 基本建立與初始化
    // ========================================================================
    @Nested
    @DisplayName("1. 薪資批次建立與初始化")
    class CreationAndInitializationTests {

        @Test
        @DisplayName("應以 DRAFT 狀態建立新的薪資批次")
        void shouldCreateDraftRun() {
            // Given
            PayrollRun run = createRun();

            // Then
            assertThat(run.getStatus())
                    .as("新建立的批次應為 DRAFT 狀態")
                    .isEqualTo(PayrollRunStatus.DRAFT);
            assertThat(run.getId().toString())
                    .as("批次 ID 應正確")
                    .isEqualTo("PR-202512");
            assertThat(run.getPayrollSystem())
                    .as("薪資制度應為 MONTHLY")
                    .isEqualTo(PayrollSystem.MONTHLY);
        }

        @Test
        @DisplayName("建立時應驗證組織 ID 不為空")
        void shouldValidateOrganizationId() {
            assertThatThrownBy(() -> PayrollRun.create(
                    new RunId("PR-001"),
                    "Test",
                    null, // invalid
                    PayPeriod.ofMonth(2025, 12),
                    PayrollSystem.MONTHLY,
                    LocalDate.of(2026, 1, 5),
                    "USER-001"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("建立時應驗證發薪日必須在計薪期間後")
        void shouldValidatePayDate() {
            assertThatThrownBy(() -> PayrollRun.create(
                    new RunId("PR-001"),
                    "Test",
                    "ORG-001",
                    PayPeriod.ofMonth(2025, 12),
                    PayrollSystem.MONTHLY,
                    LocalDate.of(2025, 12, 15), // before end date
                    "USER-001"))
                    .isInstanceOf(DomainException.class);
        }
    }

    // ========================================================================
    // 2. 狀態轉換與流程管理
    // ========================================================================
    @Nested
    @DisplayName("2. 薪資批次狀態轉換")
    class StateTransitionTests {

        @Test
        @DisplayName("DRAFT → CALCULATING: 開始執行計算")
        void shouldTransitionFromDraftToCalculating() {
            // Given
            PayrollRun run = createRun();

            // When
            run.startExecution("USER-001", 100);

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 CALCULATING")
                    .isEqualTo(PayrollRunStatus.CALCULATING);
            assertThat(run.getExecutedBy())
                    .as("應記錄執行者")
                    .isEqualTo("USER-001");
            assertThat(run.getStatistics().getTotalEmployees())
                    .as("應初始化員工統計")
                    .isEqualTo(100);
        }

        @Test
        @DisplayName("CALCULATING → COMPLETED: 完成計算")
        void shouldTransitionFromCalculatingToCompleted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);

            // When
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 COMPLETED")
                    .isEqualTo(PayrollRunStatus.COMPLETED);
            assertThat(run.getCompletedAt())
                    .as("應記錄完成時間")
                    .isNotNull();
        }

        @Test
        @DisplayName("COMPLETED → SUBMITTED: 送審")
        void shouldTransitionFromCompletedToSubmitted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());

            // When
            run.submit("USER-002");

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 SUBMITTED")
                    .isEqualTo(PayrollRunStatus.SUBMITTED);
            assertThat(run.getSubmittedBy())
                    .as("應記錄送審者")
                    .isEqualTo("USER-002");
        }

        @Test
        @DisplayName("SUBMITTED → APPROVED: 核准")
        void shouldTransitionFromSubmittedToApproved() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            run.submit("USER-002");

            // When
            run.approve("APPROVER-001");

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 APPROVED")
                    .isEqualTo(PayrollRunStatus.APPROVED);
            assertThat(run.getApprovedBy())
                    .as("應記錄核准者")
                    .isEqualTo("APPROVER-001");
        }

        @Test
        @DisplayName("APPROVED → PAID: 標記已發放")
        void shouldTransitionFromApprovedToPaid() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            run.submit("USER-002");
            run.approve("APPROVER-001");

            // When
            run.markAsPaid("/bank/file.txt");

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 PAID")
                    .isEqualTo(PayrollRunStatus.PAID);
            assertThat(run.getBankFileUrl())
                    .as("應記錄銀行檔案 URL")
                    .isEqualTo("/bank/file.txt");
            assertThat(run.isFinal())
                    .as("PAID 應為終態")
                    .isTrue();
        }

        @Test
        @DisplayName("無效的狀態轉換應拋出異常")
        void shouldThrowExceptionOnInvalidTransition() {
            // Given
            PayrollRun run = createRun();

            // When & Then: 不能直接從 DRAFT 轉到 APPROVED
            assertThatThrownBy(() -> run.approve("APPROVER-001"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("無法從");
        }
    }

    // ========================================================================
    // 3. 失敗處理與重試
    // ========================================================================
    @Nested
    @DisplayName("3. 計算失敗與重試機制")
    class FailureAndRetryTests {

        @Test
        @DisplayName("CALCULATING → FAILED: 計算失敗")
        void shouldTransitionFromCalculatingToFailed() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 100);

            // When
            run.fail("Database connection timeout");

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 FAILED")
                    .isEqualTo(PayrollRunStatus.FAILED);
            assertThat(run.getCancelReason())
                    .as("應記錄失敗原因")
                    .isEqualTo("Database connection timeout");
        }

        @Test
        @DisplayName("FAILED → DRAFT: 重置並重試")
        void shouldResetFailedRunToDraft() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 100);
            run.fail("Error message");

            // When
            run.reset();

            // Then
            assertThat(run.getStatus())
                    .as("狀態應重置為 DRAFT")
                    .isEqualTo(PayrollRunStatus.DRAFT);
            assertThat(run.getCancelReason())
                    .as("失敗原因應清除")
                    .isNull();
            assertThat(run.getExecutedBy())
                    .as("執行者應清除")
                    .isNull();
            assertThat(run.getStatistics().getTotalEmployees())
                    .as("統計應重置")
                    .isEqualTo(0);
        }

        @Test
        @DisplayName("非 FAILED 狀態不能重置")
        void shouldNotResetCompletedRun() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.empty());

            // When & Then
            assertThatThrownBy(run::reset)
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("只有失敗的批次可以重置");
        }
    }

    // ========================================================================
    // 4. 取消機制
    // ========================================================================
    @Nested
    @DisplayName("4. 薪資批次取消")
    class CancellationTests {

        @Test
        @DisplayName("DRAFT 狀態可以取消")
        void shouldCancelFromDraft() {
            // Given
            PayrollRun run = createRun();

            // When
            run.cancel("System maintenance");

            // Then
            assertThat(run.getStatus())
                    .as("狀態應為 CANCELLED")
                    .isEqualTo(PayrollRunStatus.CANCELLED);
            assertThat(run.getCancelReason())
                    .as("應記錄取消原因")
                    .isEqualTo("System maintenance");
            assertThat(run.isFinal())
                    .as("CANCELLED 應為終態")
                    .isTrue();
        }

        @Test
        @DisplayName("COMPLETED 狀態可以取消")
        void shouldCancelFromCompleted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());

            // When
            run.cancel("Data correction needed");

            // Then
            assertThat(run.getStatus())
                    .isEqualTo(PayrollRunStatus.CANCELLED);
        }

        @Test
        @DisplayName("SUBMITTED 狀態可以取消")
        void shouldCancelFromSubmitted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            run.submit("USER-002");

            // When
            run.cancel("Calculation error");

            // Then
            assertThat(run.getStatus())
                    .isEqualTo(PayrollRunStatus.CANCELLED);
        }
    }

    // ========================================================================
    // 5. 退回機制
    // ========================================================================
    @Nested
    @DisplayName("5. 薪資批次退回")
    class RejectTests {

        @Test
        @DisplayName("SUBMITTED 狀態可以退回")
        void shouldRejectFromSubmitted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            run.submit("USER-002");

            // When
            run.reject("Data inconsistency found");

            // Then
            assertThat(run.getStatus())
                    .as("退回後應回到 COMPLETED 狀態")
                    .isEqualTo(PayrollRunStatus.COMPLETED);
            assertThat(run.getCancelReason())
                    .as("應記錄退回原因")
                    .isEqualTo("Data inconsistency found");
            assertThat(run.getSubmittedBy())
                    .as("送審者資訊應清除")
                    .isNull();
        }

        @Test
        @DisplayName("非 SUBMITTED 狀態不能退回")
        void shouldNotRejectNonSubmitted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);

            // When & Then
            assertThatThrownBy(() -> run.reject("Reason"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("未在送審狀態");
        }
    }

    // ========================================================================
    // 6. 查詢方法驗證
    // ========================================================================
    @Nested
    @DisplayName("6. 狀態查詢方法")
    class QueryMethodTests {

        @Test
        @DisplayName("canExecute() 應只在 DRAFT 狀態返回 true")
        void shouldCanExecuteOnlyInDraft() {
            // Given
            PayrollRun run = createRun();

            // When & Then
            assertThat(run.canExecute())
                    .as("DRAFT 狀態應可執行")
                    .isTrue();

            run.startExecution("USER-001", 10);
            assertThat(run.canExecute())
                    .as("CALCULATING 狀態不應可執行")
                    .isFalse();
        }

        @Test
        @DisplayName("canSubmit() 應只在 COMPLETED 狀態返回 true")
        void shouldCanSubmitOnlyInCompleted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);

            // When & Then
            assertThat(run.canSubmit())
                    .as("CALCULATING 狀態不應可送審")
                    .isFalse();

            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            assertThat(run.canSubmit())
                    .as("COMPLETED 狀態應可送審")
                    .isTrue();
        }

        @Test
        @DisplayName("canApprove() 應只在 SUBMITTED 狀態返回 true")
        void shouldCanApproveOnlyInSubmitted() {
            // Given
            PayrollRun run = createRun();
            run.startExecution("USER-001", 10);
            run.complete(PayrollStatistics.builder().processedEmployees(10).build());
            run.submit("USER-002");

            // When & Then
            assertThat(run.canApprove())
                    .as("SUBMITTED 狀態應可核准")
                    .isTrue();

            run.approve("APPROVER-001");
            assertThat(run.canApprove())
                    .as("APPROVED 狀態不應可核准")
                    .isFalse();
        }

        @Test
        @DisplayName("終態狀態應返回 isFinal() = true")
        void shouldIdentifyFinalStates() {
            // Given
            PayrollRun paidRun = createRun();
            paidRun.startExecution("USER-001", 10);
            paidRun.complete(PayrollStatistics.builder().processedEmployees(10).build());
            paidRun.submit("USER-002");
            paidRun.approve("APPROVER-001");
            paidRun.markAsPaid("/bank/file.txt");

            // When & Then
            assertThat(paidRun.isFinal())
                    .as("PAID 應為終態")
                    .isTrue();

            PayrollRun cancelledRun = createRun();
            cancelledRun.cancel("Maintenance");
            assertThat(cancelledRun.isFinal())
                    .as("CANCELLED 應為終態")
                    .isTrue();
        }
    }
}
