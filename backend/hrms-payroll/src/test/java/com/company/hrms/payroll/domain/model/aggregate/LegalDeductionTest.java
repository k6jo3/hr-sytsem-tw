package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;

/**
 * LegalDeduction 領域模型單元測試
 *
 * 驗證法扣款的建立驗證、扣款邏輯、狀態轉換與法定可扣上限計算
 */
@DisplayName("LegalDeduction 領域模型測試")
class LegalDeductionTest {

	// 共用測試資料
	private static final String EMPLOYEE_ID = "emp-uuid-001";
	private static final String COURT_ORDER_NUMBER = "112-司執-12345";
	private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("500000");
	private static final int PRIORITY = 1;
	private static final LocalDate EFFECTIVE_DATE = LocalDate.of(2026, 1, 15);
	private static final String ISSUING_AUTHORITY = "台北地方法院";

	/**
	 * 建立標準測試用法扣款
	 */
	private LegalDeduction createDefaultDeduction() {
		return new LegalDeduction(
				DeductionId.generate(),
				EMPLOYEE_ID,
				COURT_ORDER_NUMBER,
				GarnishmentType.COURT_ORDER,
				TOTAL_AMOUNT,
				PRIORITY,
				EFFECTIVE_DATE,
				ISSUING_AUTHORITY);
	}

	// ========================================================================
	// 1. 建立法扣款
	// ========================================================================
	@Nested
	@DisplayName("1. 建立法扣款")
	class CreationTests {

		@Test
		@DisplayName("應正確建立法扣款，狀態為 ACTIVE、已扣金額為 0、剩餘金額等於總額")
		void shouldCreateLegalDeductionWithCorrectDefaults() {
			// When
			LegalDeduction deduction = createDefaultDeduction();

			// Then
			assertThat(deduction.getEmployeeId())
					.as("員工 ID 應正確設定")
					.isEqualTo(EMPLOYEE_ID);
			assertThat(deduction.getCourtOrderNumber())
					.as("扣押令編號應正確設定")
					.isEqualTo(COURT_ORDER_NUMBER);
			assertThat(deduction.getGarnishmentType())
					.as("扣款類型應為 COURT_ORDER")
					.isEqualTo(GarnishmentType.COURT_ORDER);
			assertThat(deduction.getTotalAmount())
					.as("扣押總額應正確設定")
					.isEqualByComparingTo(TOTAL_AMOUNT);
			assertThat(deduction.getDeductedAmount())
					.as("已扣金額應為 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額應等於總額")
					.isEqualByComparingTo(TOTAL_AMOUNT);
			assertThat(deduction.getPriority())
					.as("優先順序應正確設定")
					.isEqualTo(PRIORITY);
			assertThat(deduction.getEffectiveDate())
					.as("生效日應正確設定")
					.isEqualTo(EFFECTIVE_DATE);
			assertThat(deduction.getStatus())
					.as("初始狀態應為 ACTIVE")
					.isEqualTo(GarnishmentStatus.ACTIVE);
			assertThat(deduction.getIssuingAuthority())
					.as("執行機關應正確設定")
					.isEqualTo(ISSUING_AUTHORITY);
		}

		@Test
		@DisplayName("員工 ID 為空時應拋出 IllegalArgumentException")
		void shouldThrowWhenEmployeeIdIsBlank() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), "", COURT_ORDER_NUMBER,
					GarnishmentType.COURT_ORDER, TOTAL_AMOUNT,
					PRIORITY, EFFECTIVE_DATE, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("員工 ID 不可為空");
		}

		@Test
		@DisplayName("員工 ID 為 null 時應拋出 IllegalArgumentException")
		void shouldThrowWhenEmployeeIdIsNull() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), null, COURT_ORDER_NUMBER,
					GarnishmentType.COURT_ORDER, TOTAL_AMOUNT,
					PRIORITY, EFFECTIVE_DATE, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("員工 ID 不可為空");
		}

		@Test
		@DisplayName("扣押令編號為空時應拋出 IllegalArgumentException")
		void shouldThrowWhenCourtOrderNumberIsBlank() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), EMPLOYEE_ID, "",
					GarnishmentType.COURT_ORDER, TOTAL_AMOUNT,
					PRIORITY, EFFECTIVE_DATE, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("扣押令編號不可為空");
		}

		@Test
		@DisplayName("扣押總額為 0 時應拋出 IllegalArgumentException")
		void shouldThrowWhenTotalAmountIsZero() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), EMPLOYEE_ID, COURT_ORDER_NUMBER,
					GarnishmentType.COURT_ORDER, BigDecimal.ZERO,
					PRIORITY, EFFECTIVE_DATE, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("扣押總額必須 > 0");
		}

		@Test
		@DisplayName("扣押總額為負數時應拋出 IllegalArgumentException")
		void shouldThrowWhenTotalAmountIsNegative() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), EMPLOYEE_ID, COURT_ORDER_NUMBER,
					GarnishmentType.COURT_ORDER, new BigDecimal("-100"),
					PRIORITY, EFFECTIVE_DATE, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("扣押總額必須 > 0");
		}

		@Test
		@DisplayName("生效日為 null 時應拋出 IllegalArgumentException")
		void shouldThrowWhenEffectiveDateIsNull() {
			assertThatThrownBy(() -> new LegalDeduction(
					DeductionId.generate(), EMPLOYEE_ID, COURT_ORDER_NUMBER,
					GarnishmentType.COURT_ORDER, TOTAL_AMOUNT,
					PRIORITY, null, ISSUING_AUTHORITY))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("生效日不可為空");
		}
	}

	// ========================================================================
	// 2. 扣款邏輯 (deduct)
	// ========================================================================
	@Nested
	@DisplayName("2. 扣款邏輯")
	class DeductTests {

		@Test
		@DisplayName("正常扣款：剩餘金額應減少，已扣金額應增加")
		void shouldDeductCorrectly() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			BigDecimal deductAmount = new BigDecimal("50000");

			// When
			BigDecimal actual = deduction.deduct(deductAmount);

			// Then
			assertThat(actual)
					.as("實際扣款金額應為 50000")
					.isEqualByComparingTo("50000");
			assertThat(deduction.getDeductedAmount())
					.as("已扣金額應為 50000")
					.isEqualByComparingTo("50000");
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額應為 450000")
					.isEqualByComparingTo("450000");
			assertThat(deduction.getStatus())
					.as("尚未扣完，狀態應維持 ACTIVE")
					.isEqualTo(GarnishmentStatus.ACTIVE);
		}

		@Test
		@DisplayName("扣款金額超過剩餘金額時，僅扣剩餘金額並轉為 COMPLETED")
		void shouldCompleteWhenDeductExceedsRemaining() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();

			// When - 扣款金額大於總額
			BigDecimal actual = deduction.deduct(new BigDecimal("600000"));

			// Then
			assertThat(actual)
					.as("實際扣款金額應為剩餘金額 500000")
					.isEqualByComparingTo("500000");
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額應為 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
			assertThat(deduction.getStatus())
					.as("全額扣完後狀態應為 COMPLETED")
					.isEqualTo(GarnishmentStatus.COMPLETED);
		}

		@Test
		@DisplayName("剛好扣完時，狀態應轉為 COMPLETED")
		void shouldCompleteWhenDeductExactly() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();

			// When
			BigDecimal actual = deduction.deduct(TOTAL_AMOUNT);

			// Then
			assertThat(actual)
					.as("實際扣款金額應等於總額")
					.isEqualByComparingTo(TOTAL_AMOUNT);
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額應為 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
			assertThat(deduction.getStatus())
					.as("狀態應為 COMPLETED")
					.isEqualTo(GarnishmentStatus.COMPLETED);
		}

		@Test
		@DisplayName("非 ACTIVE 狀態扣款時應返回 0")
		void shouldReturnZeroWhenNotActive() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.suspend(); // 轉為 SUSPENDED

			// When
			BigDecimal actual = deduction.deduct(new BigDecimal("50000"));

			// Then
			assertThat(actual)
					.as("非 ACTIVE 狀態應返回 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額不應改變")
					.isEqualByComparingTo(TOTAL_AMOUNT);
		}

		@Test
		@DisplayName("TERMINATED 狀態扣款時應返回 0")
		void shouldReturnZeroWhenTerminated() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.terminate();

			// When
			BigDecimal actual = deduction.deduct(new BigDecimal("50000"));

			// Then
			assertThat(actual)
					.as("TERMINATED 狀態應返回 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
		}

		@Test
		@DisplayName("多次扣款應累加已扣金額")
		void shouldAccumulateDeductions() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();

			// When
			deduction.deduct(new BigDecimal("100000"));
			deduction.deduct(new BigDecimal("150000"));

			// Then
			assertThat(deduction.getDeductedAmount())
					.as("已扣金額應累加為 250000")
					.isEqualByComparingTo("250000");
			assertThat(deduction.getRemainingAmount())
					.as("剩餘金額應為 250000")
					.isEqualByComparingTo("250000");
		}
	}

	// ========================================================================
	// 3. 狀態轉換
	// ========================================================================
	@Nested
	@DisplayName("3. 狀態轉換")
	class StatusTransitionTests {

		@Test
		@DisplayName("suspend：ACTIVE -> SUSPENDED")
		void shouldSuspendFromActive() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			assertThat(deduction.getStatus()).isEqualTo(GarnishmentStatus.ACTIVE);

			// When
			deduction.suspend();

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 SUSPENDED")
					.isEqualTo(GarnishmentStatus.SUSPENDED);
		}

		@Test
		@DisplayName("suspend：非 ACTIVE 狀態應拋出 IllegalStateException")
		void shouldThrowWhenSuspendFromNonActive() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.suspend(); // 已轉為 SUSPENDED

			// When & Then
			assertThatThrownBy(() -> deduction.suspend())
					.isInstanceOf(IllegalStateException.class)
					.hasMessageContaining("僅執行中的法扣可暫停");
		}

		@Test
		@DisplayName("suspend：COMPLETED 狀態應拋出 IllegalStateException")
		void shouldThrowWhenSuspendFromCompleted() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.deduct(TOTAL_AMOUNT); // 扣完 -> COMPLETED

			// When & Then
			assertThatThrownBy(() -> deduction.suspend())
					.isInstanceOf(IllegalStateException.class)
					.hasMessageContaining("僅執行中的法扣可暫停");
		}

		@Test
		@DisplayName("resume：SUSPENDED -> ACTIVE")
		void shouldResumeFromSuspended() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.suspend();
			assertThat(deduction.getStatus()).isEqualTo(GarnishmentStatus.SUSPENDED);

			// When
			deduction.resume();

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應恢復為 ACTIVE")
					.isEqualTo(GarnishmentStatus.ACTIVE);
		}

		@Test
		@DisplayName("resume：非 SUSPENDED 狀態應拋出 IllegalStateException")
		void shouldThrowWhenResumeFromNonSuspended() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			// 狀態為 ACTIVE

			// When & Then
			assertThatThrownBy(() -> deduction.resume())
					.isInstanceOf(IllegalStateException.class)
					.hasMessageContaining("僅暫停中的法扣可恢復");
		}

		@Test
		@DisplayName("terminate：ACTIVE 狀態可終止")
		void shouldTerminateFromActive() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();

			// When
			deduction.terminate();

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 TERMINATED")
					.isEqualTo(GarnishmentStatus.TERMINATED);
		}

		@Test
		@DisplayName("terminate：SUSPENDED 狀態可終止")
		void shouldTerminateFromSuspended() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.suspend();

			// When
			deduction.terminate();

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 TERMINATED")
					.isEqualTo(GarnishmentStatus.TERMINATED);
		}

		@Test
		@DisplayName("terminate：COMPLETED 狀態也可終止")
		void shouldTerminateFromCompleted() {
			// Given
			LegalDeduction deduction = createDefaultDeduction();
			deduction.deduct(TOTAL_AMOUNT); // COMPLETED

			// When
			deduction.terminate();

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 TERMINATED")
					.isEqualTo(GarnishmentStatus.TERMINATED);
		}
	}

	// ========================================================================
	// 4. 法定可扣上限計算
	// ========================================================================
	@Nested
	@DisplayName("4. 法定可扣上限計算 (calculateMaxGarnishment)")
	class MaxGarnishmentTests {

		@Test
		@DisplayName("正常計算：取三分之一與保障後金額的較小值")
		void shouldCalculateMaxGarnishmentCorrectly() {
			// Given
			BigDecimal netSalary = new BigDecimal("60000");
			BigDecimal minimumLivingCost = new BigDecimal("14230"); // 台北市最低生活費
			BigDecimal dependentCost = new BigDecimal("5000");

			// When
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					netSalary, minimumLivingCost, dependentCost);

			// Then
			// 規則一：60000 / 3 = 20000
			// 規則二：60000 - 14230*1.2 - 5000 = 60000 - 17076 - 5000 = 37924
			// min(20000, 37924) = 20000
			assertThat(max)
					.as("應取三分之一與保障後金額的較小值")
					.isEqualByComparingTo("20000");
		}

		@Test
		@DisplayName("保障金額較高時，應取保障後金額")
		void shouldTakeProtectedAmountWhenSmaller() {
			// Given
			BigDecimal netSalary = new BigDecimal("30000");
			BigDecimal minimumLivingCost = new BigDecimal("14230");
			BigDecimal dependentCost = new BigDecimal("8000");

			// When
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					netSalary, minimumLivingCost, dependentCost);

			// Then
			// 規則一：30000 / 3 = 10000
			// 規則二：30000 - 14230*1.2 - 8000 = 30000 - 17076 - 8000 = 4924
			// min(10000, 4924) = 4924
			assertThat(max)
					.as("保障金額較高時應取保障後金額")
					.isEqualByComparingTo("4924");
		}

		@Test
		@DisplayName("淨額 <= 0 時應返回 0")
		void shouldReturnZeroWhenNetSalaryIsZero() {
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					BigDecimal.ZERO, new BigDecimal("14230"), BigDecimal.ZERO);

			assertThat(max)
					.as("淨額為 0 時應返回 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
		}

		@Test
		@DisplayName("淨額為負數時應返回 0")
		void shouldReturnZeroWhenNetSalaryIsNegative() {
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					new BigDecimal("-5000"), new BigDecimal("14230"), BigDecimal.ZERO);

			assertThat(max)
					.as("淨額為負數時應返回 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
		}

		@Test
		@DisplayName("保障金額超過淨額時應返回 0")
		void shouldReturnZeroWhenProtectedExceedsNet() {
			// Given - 最低生活費*1.2 + 扶養費 > 淨額
			BigDecimal netSalary = new BigDecimal("20000");
			BigDecimal minimumLivingCost = new BigDecimal("14230");
			BigDecimal dependentCost = new BigDecimal("10000");

			// 14230*1.2 + 10000 = 17076 + 10000 = 27076 > 20000

			// When
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					netSalary, minimumLivingCost, dependentCost);

			// Then
			assertThat(max)
					.as("保障金額超過淨額時應返回 0")
					.isEqualByComparingTo(BigDecimal.ZERO);
		}

		@Test
		@DisplayName("扶養費為 null 時應視為 0 計算")
		void shouldTreatNullDependentCostAsZero() {
			// Given
			BigDecimal netSalary = new BigDecimal("60000");
			BigDecimal minimumLivingCost = new BigDecimal("14230");

			// When
			BigDecimal max = LegalDeduction.calculateMaxGarnishment(
					netSalary, minimumLivingCost, null);

			// Then
			// 規則一：60000 / 3 = 20000
			// 規則二：60000 - 14230*1.2 - 0 = 60000 - 17076 = 42924
			// min(20000, 42924) = 20000
			assertThat(max)
					.as("扶養費為 null 時應視為 0")
					.isEqualByComparingTo("20000");
		}
	}

	// ========================================================================
	// 5. reconstitute 重建
	// ========================================================================
	@Nested
	@DisplayName("5. 從持久層重建")
	class ReconstituteTests {

		@Test
		@DisplayName("應正確從持久層重建法扣款（含部分扣款狀態）")
		void shouldReconstituteCorrectly() {
			// Given
			DeductionId id = DeductionId.generate();

			// When
			LegalDeduction deduction = LegalDeduction.reconstitute(
					id, EMPLOYEE_ID, COURT_ORDER_NUMBER,
					GarnishmentType.ADMINISTRATIVE_LEVY,
					new BigDecimal("300000"),
					new BigDecimal("100000"),
					new BigDecimal("200000"),
					2,
					EFFECTIVE_DATE,
					LocalDate.of(2027, 12, 31),
					GarnishmentStatus.ACTIVE,
					"國稅局",
					"稅執字第99999號",
					"欠稅強制執行");

			// Then
			assertThat(deduction.getId()).isEqualTo(id);
			assertThat(deduction.getGarnishmentType()).isEqualTo(GarnishmentType.ADMINISTRATIVE_LEVY);
			assertThat(deduction.getDeductedAmount()).isEqualByComparingTo("100000");
			assertThat(deduction.getRemainingAmount()).isEqualByComparingTo("200000");
			assertThat(deduction.getExpiryDate()).isEqualTo(LocalDate.of(2027, 12, 31));
			assertThat(deduction.getCaseNumber()).isEqualTo("稅執字第99999號");
			assertThat(deduction.getNote()).isEqualTo("欠稅強制執行");
		}
	}
}
