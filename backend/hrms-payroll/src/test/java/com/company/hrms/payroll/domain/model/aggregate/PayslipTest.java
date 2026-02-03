package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayslipStatus;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

/**
 * Payslip 領域模型單元測試
 *
 * 驗證薪資單的建立、計算與狀態管理
 */
@DisplayName("Payslip 領域模型測試")
class PayslipTest {

	private Payslip createPayslip() {
		return Payslip.create(
				new RunId("RUN-202512"),
				"EMP-001",
				"E001",
				"John Doe",
				PayPeriod.ofMonth(2025, 12),
				LocalDate.of(2026, 1, 5));
	}

	// ========================================================================
	// 1. 薪資單建立與初始化
	// ========================================================================
	@Nested
	@DisplayName("1. 薪資單建立與初始化")
	class CreationAndInitializationTests {

		@Test
		@DisplayName("應以 DRAFT 狀態建立新的薪資單")
		void shouldCreateDraftPayslip() {
			// Given
			Payslip payslip = createPayslip();

			// Then
			assertThat(payslip.getStatus())
					.as("新建立的薪資單應為 DRAFT 狀態")
					.isEqualTo(PayslipStatus.DRAFT);
			assertThat(payslip.getEmployeeId())
					.as("應正確儲存員工 ID")
					.isEqualTo("EMP-001");
			assertThat(payslip.getEmployeeName())
					.as("應正確儲存員工姓名")
					.isEqualTo("John Doe");
			assertThat(payslip.getBaseSalary())
					.as("初始底薪應為零")
					.isZero();
		}

		@Test
		@DisplayName("新建立的薪資單應初始化為空的統計資訊")
		void shouldInitializeEmptyStatistics() {
			// Given
			Payslip payslip = createPayslip();

			// Then
			assertThat(payslip.getTotalEarnings())
					.as("收入項目應為零")
					.isZero();
			assertThat(payslip.getTotalDeductions())
					.as("扣除項目應為零")
					.isZero();
			assertThat(payslip.isDraft())
					.as("應為草稿狀態")
					.isTrue();
		}
	}

	// ========================================================================
	// 2. 薪資計算
	// ========================================================================
	@Nested
	@DisplayName("2. 薪資計算與驗證")
	class SalaryCalculationTests {

		@Test
		@DisplayName("應正確計算應發薪資（底薪 + 收入項目 + 加班費 - 請假扣款）")
		void shouldCalculateGrossWageCorrectly() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000")); // 底薪 30,000
			payslip.addEarningItem(PayslipItem.createEarning("BONUS", "獎金", new BigDecimal("5000"), "績效獎金")); // 獎金
																												// 5,000
			payslip.setOvertimePay(
					OvertimePayDetail.builder()
							.weekdayHours(new BigDecimal("8"))
							.weekdayPay(new BigDecimal("1000"))
							.build());
			payslip.setLeaveDeduction(new BigDecimal("2000")); // 請假扣款 2,000

			// When
			payslip.calculate();

			// Then
			// 應發 = 30,000 + 5,000 + 1,000 - 2,000 = 34,000
			assertThat(payslip.getGrossWage())
					.as("應發薪資應為 34,000")
					.isEqualByComparingTo("34000");
		}

		@Test
		@DisplayName("應正確計算實發薪資（應發 - 扣除項目 - 保險 - 稅金）")
		void shouldCalculateNetWageCorrectly() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));
			payslip.addDeductionItem(PayslipItem.createDeduction("DEDUCT", "抵扣", new BigDecimal("1000"), "個人扣除")); // 扣除
																													// 1,000
			payslip.setInsuranceDeductions(
					InsuranceDeductions.builder()
							.laborInsurance(new BigDecimal("1500"))
							.healthInsurance(new BigDecimal("750"))
							.pensionSelfContribution(new BigDecimal("1500"))
							.build());
			payslip.setIncomeTax(new BigDecimal("2000")); // 所得稅 2,000

			// When
			payslip.calculate();

			// Then
			// 應發 = 30,000 (底薪)
			// 實發 = 30,000 - 1,000 - 3,750 - 2,000 = 23,250
			assertThat(payslip.getNetWage())
					.as("實發薪資應為 23,250")
					.isEqualByComparingTo("23250");
		}

		@Test
		@DisplayName("未計算時不能定案")
		void shouldNotFinalizeWithoutCalculation() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));

			// When & Then
			assertThatThrownBy(payslip::finalize)
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("請先執行薪資計算");
		}

		@Test
		@DisplayName("計算後可以定案")
		void shouldFinalizeAfterCalculation() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));

			// When
			payslip.calculate();
			payslip.finalize();

			// Then
			assertThat(payslip.getStatus())
					.as("定案後狀態應為 FINALIZED")
					.isEqualTo(PayslipStatus.FINALIZED);
		}
	}

	// ========================================================================
	// 3. 項目管理
	// ========================================================================
	@Nested
	@DisplayName("3. 薪資項目管理")
	class ItemManagementTests {

		@Test
		@DisplayName("應正確新增收入項目")
		void shouldAddEarningItem() {
			// Given
			Payslip payslip = createPayslip();
			PayslipItem earningItem = PayslipItem.createEarning("BONUS", "獎金", new BigDecimal("5000"), "Manual");

			// When
			payslip.addEarningItem(earningItem);

			// Then
			assertThat(payslip.getTotalEarnings())
					.as("收入項目應包含在總額中")
					.isEqualByComparingTo("5000");
		}

		@Test
		@DisplayName("應正確新增扣除項目")
		void shouldAddDeductionItem() {
			// Given
			Payslip payslip = createPayslip();
			PayslipItem deductionItem = PayslipItem.createDeduction("DEDUCT", "抵扣", new BigDecimal("1000"), "Manual");

			// When
			payslip.addDeductionItem(deductionItem);

			// Then
			assertThat(payslip.getTotalDeductions())
					.as("扣除項目應包含在總額中")
					.isEqualByComparingTo("1000");
		}

		@Test
		@DisplayName("不能在非 DRAFT 狀態新增項目")
		void shouldNotAddItemWhenNotDraft() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));
			payslip.calculate();
			payslip.finalize();

			// When & Then
			assertThatThrownBy(
					() -> payslip
							.addEarningItem(PayslipItem.createEarning("BONUS", "獎金", new BigDecimal("5000"), "Manual")))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("已定案的薪資單不可修改");
		}

		@Test
		@DisplayName("新增不符合類型的項目應拋出異常")
		void shouldThrowExceptionOnInvalidItemType() {
			// Given
			Payslip payslip = createPayslip();
			PayslipItem earningItem = PayslipItem.createEarning("BONUS", "獎金", new BigDecimal("5000"), "Manual");

			// When & Then: 試圖將收入項目新增為扣除
			assertThatThrownBy(() -> payslip.addDeductionItem(earningItem))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("非扣除項目");
		}
	}

	// ========================================================================
	// 4. 狀態管理與發送
	// ========================================================================
	@Nested
	@DisplayName("4. 薪資單狀態管理")
	class StatusManagementTests {

		@Test
		@DisplayName("DRAFT → FINALIZED: 定案薪資單")
		void shouldTransitionFromDraftToFinalized() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));
			payslip.calculate();

			// When
			payslip.finalize();

			// Then
			assertThat(payslip.getStatus())
					.as("狀態應為 FINALIZED")
					.isEqualTo(PayslipStatus.FINALIZED);
		}

		@Test
		@DisplayName("FINALIZED → SENT: 標記已發送")
		void shouldTransitionFromFinalizedToSent() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));
			payslip.calculate();
			payslip.finalize();

			// When
			payslip.markAsSent();

			// Then
			assertThat(payslip.getStatus())
					.as("狀態應為 SENT")
					.isEqualTo(PayslipStatus.SENT);
			assertThat(payslip.getEmailSentAt())
					.as("應記錄發送時間")
					.isNotNull();
			assertThat(payslip.isSent())
					.as("isSent() 應返回 true")
					.isTrue();
		}

		@Test
		@DisplayName("非 FINALIZED 狀態不能標記為已發送")
		void shouldNotMarkAsSentWhenNotFinalized() {
			// Given
			Payslip payslip = createPayslip();

			// When & Then
			assertThatThrownBy(payslip::markAsSent)
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("未定案");
		}
	}

	// ========================================================================
	// 5. PDF 與其他設定
	// ========================================================================
	@Nested
	@DisplayName("5. 薪資單文件與附加資訊")
	class DocumentAndAdditionalInfoTests {

		@Test
		@DisplayName("應正確設定 PDF URL")
		void shouldSetPdfUrl() {
			// Given
			Payslip payslip = createPayslip();
			String pdfUrl = "/documents/payslip-EMP-001-202512.pdf";

			// When
			payslip.setPdfUrl(pdfUrl);

			// Then
			assertThat(payslip.getPdfUrl())
					.as("PDF URL 應正確儲存")
					.isEqualTo(pdfUrl);
		}

		@Test
		@DisplayName("應在完整流程中正確設定所有資訊")
		void shouldCompleteFullPayslipWorkflow() {
			// Given
			Payslip payslip = createPayslip();
			payslip.setBaseSalary(new BigDecimal("30000"));
			payslip.addEarningItem(PayslipItem.createEarning("BONUS", "獎金", new BigDecimal("5000"), "Manual"));

			// When - 完整流程
			payslip.calculate();
			payslip.setPdfUrl("/documents/payslip.pdf");
			payslip.finalize();
			payslip.markAsSent();

			// Then - 驗證完整流程
			assertThat(payslip.getStatus())
					.as("最終狀態應為 SENT")
					.isEqualTo(PayslipStatus.SENT);
			assertThat(payslip.getGrossWage())
					.as("應發薪資應正確計算")
					.isEqualByComparingTo("35000");
			assertThat(payslip.getPdfUrl())
					.as("PDF URL 應已設定")
					.isNotNull();
			assertThat(payslip.getEmailSentAt())
					.as("發送時間應已記錄")
					.isNotNull();
		}
	}
}
