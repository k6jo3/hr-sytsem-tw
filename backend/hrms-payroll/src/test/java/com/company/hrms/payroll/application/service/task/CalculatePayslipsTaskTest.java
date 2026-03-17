package com.company.hrms.payroll.application.service.task;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.dto.request.CalculatePayrollRequest;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;
import com.company.hrms.payroll.domain.service.PayrollCalculationDomainService;

/**
 * 計算薪資單任務測試
 * 驗證 task 會載入法扣/預借資料，並在計算後持久化更新
 */
@ExtendWith(MockitoExtension.class)
class CalculatePayslipsTaskTest {

    @Mock
    private IPayslipRepository payslipRepository;
    @Mock
    private PayrollCalculationDomainService calculationService;
    @Mock
    private ILegalDeductionRepository legalDeductionRepository;
    @Mock
    private ISalaryAdvanceRepository salaryAdvanceRepository;

    @InjectMocks
    private CalculatePayslipsTask task;

    private CalculatePayrollContext context;

    @BeforeEach
    void setUp() {
        // 建立基本 context
        CalculatePayrollRequest request = new CalculatePayrollRequest("run-001");
        context = new CalculatePayrollContext(request, null);

        // 設定 PayrollRun
        PayrollRun run = PayrollRun.create(
                RunId.generate(),
                "2025年12月薪資",
                "ORG-001",
                PayPeriod.ofMonth(2025, 12),
                PayrollSystem.MONTHLY,
                LocalDate.of(2026, 1, 5),
                "SYSTEM");
        context.setPayrollRun(run);
    }

    @Test
    @DisplayName("應載入法扣款與預借薪資資料並傳入計算服務")
    void shouldLoadAndProcessLegalDeductionsAndAdvances() {
        // 準備薪資結構
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP-001", new BigDecimal("60000"), PayrollCycle.MONTHLY, LocalDate.now());
        context.setEligibleStructures(List.of(structure));

        // Mock 員工資訊（空 Map 表示使用預設值）
        context.setEmployeeInfoMap(Map.of());

        // 準備法扣款資料
        LegalDeduction ld = new LegalDeduction(
                DeductionId.generate(), "EMP-001", "COURT-001",
                GarnishmentType.COURT_ORDER, new BigDecimal("500000"),
                1, LocalDate.now(), "台北地方法院");

        // 準備預借薪資資料
        SalaryAdvance sa = SalaryAdvance.reconstitute(
                AdvanceId.generate(), "EMP-001", new BigDecimal("30000"),
                new BigDecimal("30000"), 6, new BigDecimal("5000"),
                BigDecimal.ZERO, new BigDecimal("30000"),
                LocalDate.now().minusMonths(1), LocalDate.now().minusDays(15),
                AdvanceStatus.DISBURSED, "急用", null, "MGR-001");

        // Mock repository 返回法扣與預借資料
        when(legalDeductionRepository.findActiveByEmployeeId("EMP-001"))
                .thenReturn(List.of(ld));
        when(salaryAdvanceRepository.findActiveByEmployeeId("EMP-001"))
                .thenReturn(List.of(sa));

        // 執行
        task.execute(context);

        // 驗證：calculationService.calculate 被呼叫時帶有法扣和預借參數
        verify(calculationService).calculate(
                any(Payslip.class),
                eq(structure),
                any(),
                eq(Collections.emptyList()),
                eq(List.of(ld)),
                eq(List.of(sa)));

        // 驗證：法扣與預借的 save 被呼叫（持久化更新）
        verify(legalDeductionRepository).save(ld);
        verify(salaryAdvanceRepository).save(sa);

        // 驗證：薪資單被儲存
        verify(payslipRepository).save(any(Payslip.class));
    }

    @Test
    @DisplayName("無法扣/預借資料時應正常計算")
    void shouldCalculateNormallyWithoutLegalDeductionOrAdvance() {
        // 準備薪資結構
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP-002", new BigDecimal("50000"), PayrollCycle.MONTHLY, LocalDate.now());
        context.setEligibleStructures(List.of(structure));
        context.setEmployeeInfoMap(Map.of());

        // 無法扣/預借資料
        when(legalDeductionRepository.findActiveByEmployeeId("EMP-002"))
                .thenReturn(Collections.emptyList());
        when(salaryAdvanceRepository.findActiveByEmployeeId("EMP-002"))
                .thenReturn(Collections.emptyList());

        // 執行
        task.execute(context);

        // 驗證：計算服務被呼叫，法扣和預借為空列表
        verify(calculationService).calculate(
                any(Payslip.class),
                eq(structure),
                any(),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()));

        // 不應呼叫法扣/預借的 save（空列表不需要持久化）
        verify(legalDeductionRepository, never()).save(any());
        verify(salaryAdvanceRepository, never()).save(any());
    }
}
