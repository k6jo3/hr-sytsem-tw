package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;

/**
 * SalaryStructure 領域模型單元測試
 *
 * 驗證薪資結構的建立、版本控制與計算規則
 */
@DisplayName("SalaryStructure 領域模型測試")
class SalaryStructureTest {

	// ========================================================================
	// 1. 薪資結構建立
	// ========================================================================
	@Nested
	@DisplayName("1. 薪資結構建立與初始化")
	class CreationTests {

		@Test
		@DisplayName("應正確建立月薪制薪資結構")
		void shouldCreateMonthlyStructure() {
			// Given & When
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("48000"),
					PayrollCycle.MONTHLY,
					LocalDate.of(2025, 1, 1));

			// Then
			assertThat(structure.getPayrollSystem())
					.as("薪資制度應為 MONTHLY")
					.isEqualTo(PayrollSystem.MONTHLY);
			assertThat(structure.getMonthlySalary())
					.as("月薪應正確儲存")
					.isEqualByComparingTo("48000");
			assertThat(structure.getCalculatedHourlyRate())
					.as("計算時薪 = 48000 ÷ 240 = 200")
					.isEqualByComparingTo("200");
			assertThat(structure.isActive())
					.as("新建立應為生效狀態")
					.isTrue();
		}

		@Test
		@DisplayName("應正確建立時薪制薪資結構")
		void shouldCreateHourlyStructure() {
			// Given & When
			SalaryStructure structure = SalaryStructure.createHourly(
					"EMP-001",
					new BigDecimal("200"),
					PayrollCycle.BI_WEEKLY,
					LocalDate.of(2025, 1, 1));

			// Then
			assertThat(structure.getPayrollSystem())
					.as("薪資制度應為 HOURLY")
					.isEqualTo(PayrollSystem.HOURLY);
			assertThat(structure.getHourlyRate())
					.as("時薪應正確儲存")
					.isEqualByComparingTo("200");
			assertThat(structure.getCalculatedHourlyRate())
					.as("計算時薪應等於設定的時薪")
					.isEqualByComparingTo("200");
		}

		@Test
		@DisplayName("建立時應驗證員工 ID 不為空")
		void shouldValidateEmployeeId() {
			// When & Then
			assertThatThrownBy(() -> SalaryStructure.createMonthly(
					null,
					new BigDecimal("48000"),
					PayrollCycle.MONTHLY,
					LocalDate.now()))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("建立時應驗證月薪不為負數")
		void shouldValidateMonthlySalary() {
			// When & Then
			assertThatThrownBy(() -> SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("-1000"),
					PayrollCycle.MONTHLY,
					LocalDate.now()))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("月薪必須大於 0");
		}
	}

	// ========================================================================
	// 2. 薪資項目管理
	// ========================================================================
	@Nested
	@DisplayName("2. 薪資項目管理")
	class SalaryItemManagementTests {

		@Test
		@DisplayName("應正確新增收入項目")
		void shouldAddEarningItem() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.now());

			SalaryItem earningItem = SalaryItem.createEarning(
					"ALLOWANCE", "津貼", new BigDecimal("5000"));

			// When
			structure.addSalaryItem(earningItem);

			// Then
			assertThat(structure.calculateMonthlyGross())
					.as("月薪應包含新增的收入項目")
					.isEqualByComparingTo("45000");
		}

		@Test
		@DisplayName("應正確新增扣除項目")
		void shouldAddDeductionItem() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.now());

			SalaryItem deductionItem = SalaryItem.createDeduction(
					"WELFARE", "福利金", new BigDecimal("1000"));

			// When
			structure.addSalaryItem(deductionItem);

			// Then
			assertThat(structure.calculateMonthlyGross())
					.as("應發薪資不應包含扣除項目")
					.isEqualByComparingTo("40000");
		}

		@Test
		@DisplayName("不允許重複新增相同項目代碼")
		void shouldPreventDuplicateItems() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.now());

			SalaryItem item1 = SalaryItem.createEarning("ALLOWANCE", "津貼", new BigDecimal("5000"));
			structure.addSalaryItem(item1);

			// When & Then
			SalaryItem item2 = SalaryItem.createEarning("ALLOWANCE", "津貼", new BigDecimal("6000"));
			assertThatThrownBy(() -> structure.addSalaryItem(item2))
					.isInstanceOf(DomainException.class)
					.hasMessageContaining("代碼已存在");
		}
	}

	// ========================================================================
	// 3. 薪資計算
	// ========================================================================
	@Nested
	@DisplayName("3. 薪資計算")
	class SalaryCalculationTests {

		@Test
		@DisplayName("應正確計算月薪應發額（基本薪資 + 應納保險的津貼）")
		void shouldCalculateMonthlyGrossSalary() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.now());

			structure.addSalaryItem(SalaryItem.createEarning("FOOD", "伙食", new BigDecimal("2400")));
			structure.addSalaryItem(SalaryItem.createNonTaxableEarning(
					"TRAVEL", "差旅", new BigDecimal("5000")));

			// When
			BigDecimal grossSalary = structure.calculateMonthlyGross();

			// Then
			assertThat(grossSalary)
					.as("應發薪資應為 47400")
					.isEqualByComparingTo("47400");
		}

		@Test
		@DisplayName("應正確計算保險薪資（基本薪資 + 應納保險津貼）")
		void shouldCalculateInsurableSalary() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.now());

			structure.addSalaryItem(SalaryItem.createEarning("FOOD", "伙食", new BigDecimal("2400")));
			structure.addSalaryItem(SalaryItem.createNonTaxableEarning(
					"TRAVEL", "差旅", new BigDecimal("5000")));

			// When
			BigDecimal insurableSalary = structure.calculateInsurableSalary();

			// Then
			assertThat(insurableSalary)
					.as("保險薪資應為 42400")
					.isEqualByComparingTo("42400");
		}

		@Test
		@DisplayName("時薪制應正確計算時薪（不作其他計算）")
		void shouldCalculateHourlySalary() {
			// Given
			SalaryStructure structure = SalaryStructure.createHourly(
					"EMP-001",
					new BigDecimal("250"),
					PayrollCycle.BI_WEEKLY,
					LocalDate.now());

			// When
			BigDecimal hourlyRate = structure.getCalculatedHourlyRate();

			// Then
			assertThat(hourlyRate)
					.as("時薪應正確計算")
					.isEqualByComparingTo("250");
		}
	}

	// ========================================================================
	// 4. 薪資結構版本管理
	// ========================================================================
	@Nested
	@DisplayName("4. 薪資結構版本管理")
	class VersionManagementTests {

		@Test
		@DisplayName("應支援薪資結構停效（版本更新）")
		void shouldDeactivateStructure() {
			// Given
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.of(2025, 1, 1));

			assertThat(structure.isActive())
					.as("初始應為生效狀態")
					.isTrue();

			// When
			structure.deactivate(LocalDate.of(2025, 12, 31));

			// Then
			assertThat(structure.isActive())
					.as("停效後應為非生效狀態")
					.isFalse();
			assertThat(structure.getEndDate())
					.as("應記錄結束日期")
					.isEqualTo(LocalDate.of(2025, 12, 31));
		}

		@Test
		@DisplayName("應追蹤薪資結構的生效與更新時間")
		void shouldTrackEffectiveDates() {
			// Given
			LocalDate effectiveDate = LocalDate.of(2025, 1, 1);
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					effectiveDate);

			// Then
			assertThat(structure.getEffectiveDate())
					.as("應記錄生效日期")
					.isEqualTo(effectiveDate);
			assertThat(structure.getUpdatedAt())
					.as("應記錄最後更新時間")
					.isNotNull();
		}
	}

	// ========================================================================
	// 5. 完整流程驗證
	// ========================================================================
	@Nested
	@DisplayName("5. 完整薪資結構流程")
	class FullWorkflowTests {

		@Test
		@DisplayName("應支援完整的薪資結構生命週期（建立→管理→停效）")
		void shouldSupportCompleteLifecycle() {
			// Step 1: 建立新結構
			SalaryStructure structure = SalaryStructure.createMonthly(
					"EMP-001",
					new BigDecimal("40000"),
					PayrollCycle.MONTHLY,
					LocalDate.of(2025, 1, 1));

			assertThat(structure.isActive()).isTrue();

			// Step 2: 新增薪資項目
			structure.addSalaryItem(SalaryItem.createEarning(
					"ALLOWANCE", "津貼", new BigDecimal("5000")));
			structure.addSalaryItem(SalaryItem.createDeduction(
					"WELFARE", "福利金", new BigDecimal("1000")));

			assertThat(structure.calculateMonthlyGross())
					.as("應發薪資應正確計算")
					.isEqualByComparingTo("45000");

			// Step 3: 停效結構
			structure.deactivate(LocalDate.of(2025, 12, 31));

			assertThat(structure.isActive()).isFalse();
			assertThat(structure.getEndDate()).isEqualTo(LocalDate.of(2025, 12, 31));
		}
	}
}
