import { describe, expect, it } from 'vitest';
import { SalaryStructureDto } from '../api/PayrollTypes';
import { SalaryStructureViewModelFactory } from './SalaryStructureViewModelFactory';

describe('SalaryStructureViewModelFactory', () => {
  const mockMonthlySalaryDto: SalaryStructureDto = {
    id: 'ss-1',
    employeeId: 'emp-1',
    payrollSystem: 'MONTHLY',
    monthlySalary: 50000,
    effectiveDate: '2025-01-01',
    active: true,
    items: [
      {
        itemId: 'item-1',
        code: 'BASE',
        name: '底薪',
        type: 'EARNING',
        amount: 40000,
        taxable: true,
        insurable: true
      },
      {
        itemId: 'item-2',
        code: 'ALLOWANCE',
        name: '津貼',
        type: 'EARNING',
        amount: 10000,
        taxable: true,
        insurable: false
      }
    ]
  };

  const mockHourlySalaryDto: SalaryStructureDto = {
    id: 'ss-2',
    employeeId: 'emp-2',
    payrollSystem: 'HOURLY',
    hourlyRate: 250,
    effectiveDate: '2025-01-01',
    active: true,
    items: []
  };

  describe('createFromDTO', () => {
    it('應該正確轉換月薪制薪資結構 DTO 為 ViewModel', () => {
      const vm = SalaryStructureViewModelFactory.createFromDTO(mockMonthlySalaryDto);

      expect(vm.id).toBe('ss-1');
      expect(vm.employeeId).toBe('emp-1');
      expect(vm.payrollSystem).toBe('MONTHLY');
      expect(vm.payrollSystemLabel).toBe('月薪制');
      expect(vm.amountDisplay).toBe('$50,000');
      expect(vm.active).toBe(true);
    });

    it('應該正確轉換時薪制薪資結構 DTO 為 ViewModel', () => {
      const vm = SalaryStructureViewModelFactory.createFromDTO(mockHourlySalaryDto);

      expect(vm.id).toBe('ss-2');
      expect(vm.payrollSystem).toBe('HOURLY');
      expect(vm.payrollSystemLabel).toBe('時薪制');
      expect(vm.amountDisplay).toBe('$250/時');
    });

    it('應該正確格式化生效日期', () => {
      const vm = SalaryStructureViewModelFactory.createFromDTO(mockMonthlySalaryDto);
      expect(vm.effectiveDate).toBe('2025/01/01');
    });

    it('應該正確對應生效狀態標籤', () => {
      const activeVm = SalaryStructureViewModelFactory.createFromDTO(mockMonthlySalaryDto);
      expect(activeVm.statusLabel).toBe('生效中');
      expect(activeVm.statusColor).toBe('success');

      const inactiveDto = { ...mockMonthlySalaryDto, active: false };
      const inactiveVm = SalaryStructureViewModelFactory.createFromDTO(inactiveDto);
      expect(inactiveVm.statusLabel).toBe('已失效');
      expect(inactiveVm.statusColor).toBe('default');
    });

    it('應該正確轉換薪資項目', () => {
      const vm = SalaryStructureViewModelFactory.createFromDTO(mockMonthlySalaryDto);

      expect(vm.items).toHaveLength(2);
      expect(vm.items[0].code).toBe('BASE');
      expect(vm.items[0].name).toBe('底薪');
      expect(vm.items[0].type).toBe('EARNING');
      expect(vm.items[0].amount).toBe(40000);
      expect(vm.items[0].amountDisplay).toBe('$40,000');
      expect(vm.items[0].taxable).toBe(true);
      expect(vm.items[0].insurable).toBe(true);
    });

    it('應該處理沒有薪資項目的情況', () => {
      const vm = SalaryStructureViewModelFactory.createFromDTO(mockHourlySalaryDto);
      expect(vm.items).toHaveLength(0);
    });

    it('應該處理 items 為 undefined 的情況', () => {
      const dtoWithoutItems = { ...mockMonthlySalaryDto, items: undefined };
      const vm = SalaryStructureViewModelFactory.createFromDTO(dtoWithoutItems);
      expect(vm.items).toHaveLength(0);
    });
  });

  describe('createListFromDTOs', () => {
    it('應該批量轉換 DTO 列表', () => {
      const dtos = [mockMonthlySalaryDto, mockHourlySalaryDto];
      const vms = SalaryStructureViewModelFactory.createListFromDTOs(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0].payrollSystem).toBe('MONTHLY');
      expect(vms[1].payrollSystem).toBe('HOURLY');
    });

    it('應該正確處理空列表', () => {
      const vms = SalaryStructureViewModelFactory.createListFromDTOs([]);
      expect(vms).toHaveLength(0);
    });
  });
});
