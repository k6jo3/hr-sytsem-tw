package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.application.service.context.CreateLegalDeductionContext;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;

/**
 * InitLegalDeductionTask 單元測試
 *
 * 驗證根據請求正確初始化法扣款聚合根
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InitLegalDeductionTask 單元測試")
class InitLegalDeductionTaskTest {

	@InjectMocks
	private InitLegalDeductionTask task;

	@Test
	@DisplayName("應根據請求正確建立法扣款，context 中的 deduction 不為 null")
	void shouldInitLegalDeductionFromRequest() throws Exception {
		// Given
		CreateLegalDeductionRequest request = CreateLegalDeductionRequest.builder()
				.employeeId("emp-001")
				.courtOrderNumber("112-司執-99999")
				.garnishmentType("COURT_ORDER")
				.totalAmount(new BigDecimal("300000"))
				.priority(1)
				.effectiveDate(LocalDate.of(2026, 3, 1))
				.issuingAuthority("新北地方法院")
				.caseNumber("112年度司執字第99999號")
				.note("測試備註")
				.build();

		CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, null);

		// When
		task.execute(context);

		// Then
		assertThat(context.getLegalDeduction())
				.as("context 中的法扣款不應為 null")
				.isNotNull();
		assertThat(context.getLegalDeduction().getEmployeeId())
				.isEqualTo("emp-001");
		assertThat(context.getLegalDeduction().getCourtOrderNumber())
				.isEqualTo("112-司執-99999");
		assertThat(context.getLegalDeduction().getGarnishmentType())
				.isEqualTo(GarnishmentType.COURT_ORDER);
		assertThat(context.getLegalDeduction().getTotalAmount())
				.isEqualByComparingTo("300000");
		assertThat(context.getLegalDeduction().getStatus())
				.isEqualTo(GarnishmentStatus.ACTIVE);
		assertThat(context.getLegalDeduction().getDeductedAmount())
				.isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(context.getLegalDeduction().getCaseNumber())
				.isEqualTo("112年度司執字第99999號");
		assertThat(context.getLegalDeduction().getNote())
				.isEqualTo("測試備註");
	}

	@Test
	@DisplayName("priority 為 null 時應預設為 1")
	void shouldDefaultPriorityToOne() throws Exception {
		// Given
		CreateLegalDeductionRequest request = CreateLegalDeductionRequest.builder()
				.employeeId("emp-001")
				.courtOrderNumber("112-司執-11111")
				.garnishmentType("ADMINISTRATIVE_LEVY")
				.totalAmount(new BigDecimal("100000"))
				.priority(null)
				.effectiveDate(LocalDate.of(2026, 4, 1))
				.build();

		CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, null);

		// When
		task.execute(context);

		// Then
		assertThat(context.getLegalDeduction().getPriority())
				.as("priority 為 null 時應預設為 1")
				.isEqualTo(1);
	}

	@Test
	@DisplayName("無效的 garnishmentType 應拋出 IllegalArgumentException")
	void shouldThrowWhenInvalidGarnishmentType() {
		// Given
		CreateLegalDeductionRequest request = CreateLegalDeductionRequest.builder()
				.employeeId("emp-001")
				.courtOrderNumber("112-司執-11111")
				.garnishmentType("INVALID_TYPE")
				.totalAmount(new BigDecimal("100000"))
				.priority(1)
				.effectiveDate(LocalDate.of(2026, 4, 1))
				.build();

		CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, null);

		// When & Then
		assertThatThrownBy(() -> task.execute(context))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("getName 應返回正確名稱")
	void shouldReturnCorrectName() {
		assertThat(task.getName()).isEqualTo("初始化法扣款");
	}
}
