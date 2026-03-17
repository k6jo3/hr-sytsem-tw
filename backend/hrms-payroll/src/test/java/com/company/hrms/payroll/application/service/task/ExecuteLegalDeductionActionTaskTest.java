package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.dto.request.LegalDeductionActionRequest;
import com.company.hrms.payroll.application.service.context.LegalDeductionActionContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

/**
 * ExecuteLegalDeductionActionTask 單元測試
 *
 * 驗證各種法扣款狀態操作（暫停、恢復、終止）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExecuteLegalDeductionActionTask 單元測試")
class ExecuteLegalDeductionActionTaskTest {

	@Mock
	private ILegalDeductionRepository repository;

	@InjectMocks
	private ExecuteLegalDeductionActionTask task;

	private LegalDeduction createActiveDeduction() {
		return new LegalDeduction(
				DeductionId.generate(), "emp-001", "112-司執-12345",
				GarnishmentType.COURT_ORDER, new BigDecimal("500000"),
				1, LocalDate.of(2026, 1, 15), "台北地方法院");
	}

	private LegalDeductionActionContext createContext(LegalDeduction deduction, String actionType) {
		LegalDeductionActionRequest request = new LegalDeductionActionRequest("deduction-001", null);
		LegalDeductionActionContext context = new LegalDeductionActionContext(request, null, actionType);
		context.setLegalDeduction(deduction);
		return context;
	}

	// ========================================================================
	// SUSPEND 操作
	// ========================================================================
	@Nested
	@DisplayName("SUSPEND 操作")
	class SuspendTests {

		@Test
		@DisplayName("ACTIVE 狀態應成功暫停並儲存")
		void shouldSuspendActiveDeduction() throws Exception {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			LegalDeductionActionContext context = createContext(deduction, "SUSPEND");

			// When
			task.execute(context);

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 SUSPENDED")
					.isEqualTo(GarnishmentStatus.SUSPENDED);
			verify(repository, times(1)).save(deduction);
		}

		@Test
		@DisplayName("非 ACTIVE 狀態暫停應拋出 IllegalStateException")
		void shouldThrowWhenSuspendNonActive() {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			deduction.suspend(); // 轉為 SUSPENDED
			LegalDeductionActionContext context = createContext(deduction, "SUSPEND");

			// When & Then
			assertThatThrownBy(() -> task.execute(context))
					.isInstanceOf(IllegalStateException.class);
		}
	}

	// ========================================================================
	// RESUME 操作
	// ========================================================================
	@Nested
	@DisplayName("RESUME 操作")
	class ResumeTests {

		@Test
		@DisplayName("SUSPENDED 狀態應成功恢復並儲存")
		void shouldResumeFromSuspended() throws Exception {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			deduction.suspend(); // 先暫停
			LegalDeductionActionContext context = createContext(deduction, "RESUME");

			// When
			task.execute(context);

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應恢復為 ACTIVE")
					.isEqualTo(GarnishmentStatus.ACTIVE);
			verify(repository, times(1)).save(deduction);
		}

		@Test
		@DisplayName("非 SUSPENDED 狀態恢復應拋出 IllegalStateException")
		void shouldThrowWhenResumeNonSuspended() {
			// Given
			LegalDeduction deduction = createActiveDeduction(); // ACTIVE
			LegalDeductionActionContext context = createContext(deduction, "RESUME");

			// When & Then
			assertThatThrownBy(() -> task.execute(context))
					.isInstanceOf(IllegalStateException.class);
		}
	}

	// ========================================================================
	// TERMINATE 操作
	// ========================================================================
	@Nested
	@DisplayName("TERMINATE 操作")
	class TerminateTests {

		@Test
		@DisplayName("ACTIVE 狀態應成功終止並儲存")
		void shouldTerminateActiveDeduction() throws Exception {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			LegalDeductionActionContext context = createContext(deduction, "TERMINATE");

			// When
			task.execute(context);

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 TERMINATED")
					.isEqualTo(GarnishmentStatus.TERMINATED);
			verify(repository, times(1)).save(deduction);
		}

		@Test
		@DisplayName("SUSPENDED 狀態應成功終止並儲存")
		void shouldTerminateSuspendedDeduction() throws Exception {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			deduction.suspend();
			LegalDeductionActionContext context = createContext(deduction, "TERMINATE");

			// When
			task.execute(context);

			// Then
			assertThat(deduction.getStatus())
					.as("狀態應變為 TERMINATED")
					.isEqualTo(GarnishmentStatus.TERMINATED);
			verify(repository, times(1)).save(deduction);
		}
	}

	// ========================================================================
	// 不支援的操作
	// ========================================================================
	@Nested
	@DisplayName("不支援的操作類型")
	class UnsupportedActionTests {

		@Test
		@DisplayName("不支援的操作類型應拋出 IllegalArgumentException")
		void shouldThrowForUnsupportedAction() {
			// Given
			LegalDeduction deduction = createActiveDeduction();
			LegalDeductionActionContext context = createContext(deduction, "INVALID_ACTION");

			// When & Then
			assertThatThrownBy(() -> task.execute(context))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("不支援的法扣款操作類型");
		}
	}

	@Test
	@DisplayName("getName 應返回正確名稱")
	void shouldReturnCorrectName() {
		assertThat(task.getName()).isEqualTo("執行法扣款狀態操作");
	}
}
