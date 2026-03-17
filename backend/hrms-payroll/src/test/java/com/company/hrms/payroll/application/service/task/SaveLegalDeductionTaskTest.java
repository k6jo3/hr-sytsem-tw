package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.application.service.context.CreateLegalDeductionContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

/**
 * SaveLegalDeductionTask 單元測試
 *
 * 驗證法扣款儲存任務正確呼叫 repository
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SaveLegalDeductionTask 單元測試")
class SaveLegalDeductionTaskTest {

	@Mock
	private ILegalDeductionRepository repository;

	@InjectMocks
	private SaveLegalDeductionTask task;

	@Test
	@DisplayName("context 中有法扣款時應呼叫 repository.save()")
	void shouldCallRepositorySaveWhenDeductionExists() throws Exception {
		// Given
		LegalDeduction deduction = new LegalDeduction(
				DeductionId.generate(), "emp-001", "112-司執-12345",
				GarnishmentType.COURT_ORDER, new BigDecimal("500000"),
				1, LocalDate.of(2026, 1, 15), "台北地方法院");

		CreateLegalDeductionRequest request = CreateLegalDeductionRequest.builder()
				.employeeId("emp-001").build();
		CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, null);
		context.setLegalDeduction(deduction);

		// When
		task.execute(context);

		// Then
		verify(repository, times(1)).save(deduction);
	}

	@Test
	@DisplayName("context 中法扣款為 null 時不應呼叫 repository.save()")
	void shouldNotCallSaveWhenDeductionIsNull() throws Exception {
		// Given
		CreateLegalDeductionRequest request = CreateLegalDeductionRequest.builder()
				.employeeId("emp-001").build();
		CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, null);
		// 不設定 legalDeduction（null）

		// When
		task.execute(context);

		// Then
		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("getName 應返回正確名稱")
	void shouldReturnCorrectName() {
		assertThat(task.getName()).isEqualTo("儲存法扣款");
	}
}
