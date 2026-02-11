import { describe, expect, it } from 'vitest';
import { PayslipDto, PayslipSummaryDto } from '../api/PayrollTypes';
import { PayslipViewModelFactory } from './PayslipViewModelFactory';

describe('PayslipViewModelFactory', () => {
  const mockPayslipDto: PayslipDto = {
    id: 'ps-1',
    payslip_code: 'PS-20251101',
    employee_id: 'emp-1',
    employee_name: 'John Doe',
    employee_code: 'E001',
    department_name: 'IT',
    pay_period_start: '2025-11-01',
    pay_period_end: '2025-11-30',
    payment_date: '2025-12-05',
    status: 'PAID',
    items: [
      { item_code: 'BASE_SALARY', item_name: '底薪', item_type: 'INCOME', amount: 50000 },
      { item_code: 'LABOR_INSURANCE', item_name: '勞保費', item_type: 'DEDUCTION', amount: 1000 },
    ],
    gross_pay: 50000,
    total_deductions: 1000,
    net_pay: 49000,
    created_at: '2025-12-01',
    updated_at: '2025-12-01',
  };

  it('應該正確轉換薪資單詳情 DTO 為 ViewModel', () => {
    const vm = PayslipViewModelFactory.createDetailFromDTO(mockPayslipDto);

    expect(vm.id).toBe(mockPayslipDto.id);
    expect(vm.payslipCode).toBe(mockPayslipDto.payslip_code);
    expect(vm.employeeName).toBe(mockPayslipDto.employee_name);
    expect(vm.grossPayDisplay).toBe('$50,000');
    expect(vm.netPayDisplay).toBe('$49,000');
    expect(vm.statusLabel).toBe('已發放');
    expect(vm.incomeItems).toHaveLength(1);
    expect(vm.deductionItems).toHaveLength(1);
  });

  it('應該正確轉換薪資單摘要 DTO 為 ViewModel', () => {
    const summaryDto: PayslipSummaryDto = {
      id: 'ps-1',
      payslip_code: 'PS-20251101',
      pay_period: '2025年11月',
      payment_date: '2025-12-05',
      gross_pay: 50000,
      net_pay: 49000,
      status: 'PAID',
    };

    const vm = PayslipViewModelFactory.createSummaryFromDTO(summaryDto);

    expect(vm.id).toBe(summaryDto.id);
    expect(vm.netPayDisplay).toBe('$49,000');
    expect(vm.statusLabel).toBe('已發放');
    expect(vm.canDownload).toBe(true);
  });
});
