package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

/**
 * 薪資核准業務流程單元測試
 *
 * 驗證薪資核准的完整業務流程編排
 * 包括：送審 → 核准 → 發薪 以及異常處理（退回、取消）
 */
@DisplayName("薪資核准業務流程測試")
class PayrollApprovalPipelineTest {

	private PayrollRun createCompletedPayrollRun() {
		PayrollRun run = PayrollRun.create(
				new RunId("RUN-202512"),
				"2025年12月薪資核准",
				"ORG-001",
				PayPeriod.ofMonth(2025, 12),
				PayrollSystem.MONTHLY,
				LocalDate.of(2026, 1, 5),
				"USER-001");

		// 完成計算
		run.startExecution("USER-EXECUTOR", 100);
		run.complete(PayrollStatistics.builder()
				.processedEmployees(100)
				.totalGrossAmount(new BigDecimal("5000000"))
				.totalNetAmount(new BigDecimal("3750000"))
				.totalDeductions(new BigDecimal("1250000"))
				.build());

		return run;
	}

	// ========================================================================
	// 1. 送審流程
	// ========================================================================
	@Nested
	@DisplayName("1. 薪資批次送審")
	class SubmissionTests {

		@Test
		@DisplayName("應正確送審計算完成的批次（COMPLETED → SUBMITTED）")
		void shouldSubmitCompletedRun() {
			// Given
			PayrollRun run = createCompletedPayrollRun();

			// When
			run.submit("USER-SUBMITTER");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 SUBMITTED")
					.isEqualTo(PayrollRunStatus.SUBMITTED);
			assertThat(run.getSubmittedBy())
					.as("應記錄送審者 ID")
					.isEqualTo("USER-SUBMITTER");
			assertThat(run.getSubmittedAt())
					.as("應記錄送審時間")
					.isNotNull();
		}

		@Test
		@DisplayName("只有 COMPLETED 狀態才能送審")
		void shouldOnlySubmitFromCompleted() {
			// Given
			PayrollRun run = PayrollRun.create(
					new RunId("RUN-001"),
					"Test",
					"ORG-001",
					PayPeriod.ofMonth(2025, 12),
					PayrollSystem.MONTHLY,
					LocalDate.of(2026, 1, 5),
					"USER-001");

			// When & Then: DRAFT 狀態不能送審
			assertThatThrownBy(() -> run.submit("USER-SUBMITTER"))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("無法從");
		}

		@Test
		@DisplayName("送審時應驗證計算統計資訊已完成")
		void shouldValidateStatisticsBeforeSubmission() {
			// Given
			PayrollRun run = createCompletedPayrollRun();

			// When: 正常送審
			run.submit("USER-SUBMITTER");

			// Then: 統計資訊應完整
			assertThat(run.getStatistics().getProcessedEmployees())
					.as("已處理員工應為 100")
					.isEqualTo(100);
			assertThat(run.getStatistics().getTotalGrossAmount())
					.as("應發薪資應已計算")
					.isEqualByComparingTo("5000000");
		}
	}

	// ========================================================================
	// 2. 核准流程
	// ========================================================================
	@Nested
	@DisplayName("2. 薪資批次核准")
	class ApprovalTests {

		@Test
		@DisplayName("應正確核准送審的批次（SUBMITTED → APPROVED）")
		void shouldApproveSubmittedRun() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");

			// When
			run.approve("APPROVER-001");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 APPROVED")
					.isEqualTo(PayrollRunStatus.APPROVED);
			assertThat(run.getApprovedBy())
					.as("應記錄核准者 ID")
					.isEqualTo("APPROVER-001");
			assertThat(run.getApprovedAt())
					.as("應記錄核准時間")
					.isNotNull();
		}

		@Test
		@DisplayName("只有 SUBMITTED 狀態才能核准")
		void shouldOnlyApproveFromSubmitted() {
			// Given
			PayrollRun run = createCompletedPayrollRun();

			// When & Then: COMPLETED 狀態不能直接核准
			assertThatThrownBy(() -> run.approve("APPROVER-001"))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("無法從");
		}

		@Test
		@DisplayName("核准後應能發薪（APPROVED → PAID）")
		void shouldAllowPaymentAfterApproval() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");
			run.approve("APPROVER-001");

			// When
			run.markAsPaid("/bank/payroll-file.txt");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 PAID")
					.isEqualTo(PayrollRunStatus.PAID);
			assertThat(run.getBankFileUrl())
					.as("應記錄銀行轉帳檔案 URL")
					.isEqualTo("/bank/payroll-file.txt");
		}
	}

	// ========================================================================
	// 3. 退回與修正
	// ========================================================================
	@Nested
	@DisplayName("3. 薪資批次退回")
	class RejectionTests {

		@Test
		@DisplayName("應支援送審批次的退回")
		void shouldRejectSubmittedRun() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");

			// When: 退回原因
			String rejectReason = "Data inconsistency found - need recalculation";
			run.reject(rejectReason);

			// Then
			assertThat(run.getStatus())
					.as("退回後應回到 COMPLETED 狀態")
					.isEqualTo(PayrollRunStatus.COMPLETED);
			assertThat(run.getCancelReason())
					.as("應記錄退回原因")
					.isEqualTo(rejectReason);
			assertThat(run.getSubmittedBy())
					.as("送審者資訊應清除")
					.isNull();
			assertThat(run.getSubmittedAt())
					.as("送審時間應清除")
					.isNull();
		}

		@Test
		@DisplayName("只有 SUBMITTED 狀態才能退回")
		void shouldOnlyRejectFromSubmitted() {
			// Given
			PayrollRun run = createCompletedPayrollRun();

			// When & Then
			assertThatThrownBy(() -> run.reject("Reason"))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("未在送審狀態");
		}

		@Test
		@DisplayName("退回後應能重新送審（COMPLETED → SUBMITTED）")
		void shouldAllowResubmissionAfterRejection() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");
			run.reject("Need review");

			// When: 重新送審
			run.submit("USER-SUBMITTER-2");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 SUBMITTED")
					.isEqualTo(PayrollRunStatus.SUBMITTED);
			assertThat(run.getSubmittedBy())
					.as("應更新為新的送審者")
					.isEqualTo("USER-SUBMITTER-2");
		}
	}

	// ========================================================================
	// 4. 取消機制
	// ========================================================================
	@Nested
	@DisplayName("4. 薪資批次取消")
	class CancellationTests {

		@Test
		@DisplayName("SUBMITTED 狀態可以取消")
		void shouldCancelFromSubmitted() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");

			// When
			run.cancel("Administrative cancellation");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 CANCELLED")
					.isEqualTo(PayrollRunStatus.CANCELLED);
			assertThat(run.getCancelReason())
					.as("應記錄取消原因")
					.isEqualTo("Administrative cancellation");
			assertThat(run.isFinal())
					.as("CANCELLED 應為終態")
					.isTrue();
		}

		@Test
		@DisplayName("APPROVED 狀態可以取消（發薪前）")
		void shouldCancelFromApproved() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");
			run.approve("APPROVER-001");

			// When: 發薪前取消
			run.cancel("Unexpected issue found");

			// Then
			assertThat(run.getStatus())
					.as("狀態應為 CANCELLED")
					.isEqualTo(PayrollRunStatus.CANCELLED);
		}

		@Test
		@DisplayName("已發薪不能取消（終態）")
		void shouldNotCancelPaidRun() {
			// Given
			PayrollRun run = createCompletedPayrollRun();
			run.submit("USER-SUBMITTER");
			run.approve("APPROVER-001");
			run.markAsPaid("/bank/file.txt");

			// When & Then
			assertThatThrownBy(() -> run.cancel("Reason"))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("無法從");
		}
	}

	// ========================================================================
	// 5. 完整核准流程
	// ========================================================================
	@Nested
	@DisplayName("5. 完整薪資核准流程")
	class CompleteApprovalFlowTests {

		@Test
		@DisplayName("應支援完整的薪資核准流程（送審→核准→發薪）")
		void shouldCompleteFullApprovalFlow() {
			// Step 1: 建立並計算完成
			PayrollRun run = createCompletedPayrollRun();
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.COMPLETED);

			// Step 2: 送審
			run.submit("USER-SUBMITTER");
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.SUBMITTED);
			assertThat(run.getSubmittedBy()).isEqualTo("USER-SUBMITTER");

			// Step 3: 核准
			run.approve("APPROVER-001");
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.APPROVED);
			assertThat(run.getApprovedBy()).isEqualTo("APPROVER-001");

			// Step 4: 發薪
			run.markAsPaid("/bank/payroll-20251205.txt");
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.PAID);
			assertThat(run.getBankFileUrl()).isEqualTo("/bank/payroll-20251205.txt");
			assertThat(run.getPaidAt()).isNotNull();

			// Then: 驗證完整流程
			assertThat(run.isFinal())
					.as("PAID 應為終態")
					.isTrue();
		}

		@Test
		@DisplayName("應支援核准流程中的退回與重試")
		void shouldSupportRejectAndResubmit() {
			// Step 1: 建立並計算
			PayrollRun run = createCompletedPayrollRun();

			// Step 2: 第一次送審
			run.submit("USER-SUBMITTER-1");
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.SUBMITTED);

			// Step 3: 退回
			run.reject("Data validation failed");
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.COMPLETED);

			// Step 4: 重新送審
			run.submit("USER-SUBMITTER-2");
			assertThat(run.getSubmittedBy()).isEqualTo("USER-SUBMITTER-2");

			// Step 5: 核准與發薪
			run.approve("APPROVER-001");
			run.markAsPaid("/bank/file.txt");

			// Then
			assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.PAID);
		}
	}
}
