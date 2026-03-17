package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.dto.request.LegalDeductionActionRequest;
import com.company.hrms.payroll.application.service.context.LegalDeductionActionContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

/**
 * FetchLegalDeductionTask 單元測試
 *
 * 驗證載入法扣款任務的正常與異常路徑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FetchLegalDeductionTask 單元測試")
class FetchLegalDeductionTaskTest {

	@Mock
	private ILegalDeductionRepository repository;

	@InjectMocks
	private FetchLegalDeductionTask task;

	private LegalDeduction createTestDeduction(String id) {
		return LegalDeduction.reconstitute(
				new DeductionId(id), "emp-001", "112-司執-12345",
				GarnishmentType.COURT_ORDER,
				new BigDecimal("500000"), new BigDecimal("100000"), new BigDecimal("400000"),
				1, LocalDate.of(2026, 1, 15), null,
				com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus.ACTIVE,
				"台北地方法院", null, null);
	}

	@Test
	@DisplayName("找到法扣款時應正確設定到 context")
	void shouldSetDeductionToContextWhenFound() throws Exception {
		// Given
		String deductionId = "deduction-001";
		LegalDeduction deduction = createTestDeduction(deductionId);

		when(repository.findById(any(DeductionId.class)))
				.thenReturn(Optional.of(deduction));

		LegalDeductionActionRequest request = new LegalDeductionActionRequest(deductionId, null);
		LegalDeductionActionContext context = new LegalDeductionActionContext(request, null, "SUSPEND");

		// When
		task.execute(context);

		// Then
		assertThat(context.getLegalDeduction())
				.as("context 中應設定法扣款")
				.isNotNull();
		assertThat(context.getLegalDeduction().getEmployeeId())
				.isEqualTo("emp-001");
	}

	@Test
	@DisplayName("找不到法扣款時應拋出 DomainException")
	void shouldThrowWhenDeductionNotFound() {
		// Given
		String deductionId = "non-existent-id";
		when(repository.findById(any(DeductionId.class)))
				.thenReturn(Optional.empty());

		LegalDeductionActionRequest request = new LegalDeductionActionRequest(deductionId, null);
		LegalDeductionActionContext context = new LegalDeductionActionContext(request, null, "SUSPEND");

		// When & Then
		assertThatThrownBy(() -> task.execute(context))
				.isInstanceOf(DomainException.class)
				.hasMessageContaining("找不到法扣款");
	}

	@Test
	@DisplayName("deductionId 為空時應拋出 IllegalArgumentException")
	void shouldThrowWhenDeductionIdIsBlank() {
		// Given
		LegalDeductionActionRequest request = new LegalDeductionActionRequest("", null);
		LegalDeductionActionContext context = new LegalDeductionActionContext(request, null, "SUSPEND");

		// When & Then
		assertThatThrownBy(() -> task.execute(context))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("法扣款 ID 為必填");
	}

	@Test
	@DisplayName("deductionId 為 null 時應拋出 IllegalArgumentException")
	void shouldThrowWhenDeductionIdIsNull() {
		// Given
		LegalDeductionActionRequest request = new LegalDeductionActionRequest(null, null);
		LegalDeductionActionContext context = new LegalDeductionActionContext(request, null, "SUSPEND");

		// When & Then
		assertThatThrownBy(() -> task.execute(context))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("法扣款 ID 為必填");
	}

	@Test
	@DisplayName("getName 應返回正確名稱")
	void shouldReturnCorrectName() {
		assertThat(task.getName()).isEqualTo("載入法扣款");
	}
}
