import { describe, expect, it } from 'vitest';
import { PayrollRunDto } from '../api/PayrollTypes';
import { PayrollViewModelFactory } from './PayrollViewModelFactory';

describe('PayrollViewModelFactory', () => {
  const mockPayrollRunDto: PayrollRunDto = {
    runId: 'run-1',
    organizationId: 'org-1',
    name: '2025年11月薪資',
    status: 'COMPLETED',
    payrollSystem: 'MONTHLY',
    start: '2025-11-01',
    end: '2025-11-30',
    payDate: '2025-12-05',
    totalDays: 30,
    totalEmployees: 50,
    processedEmployees: 50,
    successCount: 48,
    failureCount: 2,
    totalGrossPay: 2500000,
    totalNetPay: 2200000,
    totalDeductions: 300000,
    executedAt: '2025-12-01T10:00:00Z',
    completedAt: '2025-12-01T11:00:00Z',
    project_stats: [
      {
        project_id: 'proj-1',
        project_name: 'ERP專案',
        total_hours: 1000,
        total_amount: 500000
      }
    ]
  };

  describe('createFromDTO', () => {
    it('應該正確轉換薪資批次 DTO 為 ViewModel', () => {
      const vm = PayrollViewModelFactory.createFromDTO(mockPayrollRunDto);

      expect(vm.runId).toBe(mockPayrollRunDto.runId);
      expect(vm.name).toBe(mockPayrollRunDto.name);
      expect(vm.status).toBe(mockPayrollRunDto.status);
      expect(vm.totalEmployees).toBe(50);
      expect(vm.processedEmployees).toBe(50);
    });

    it('應該正確計算進度百分比', () => {
      const vm = PayrollViewModelFactory.createFromDTO(mockPayrollRunDto);
      expect(vm.progress).toBe(100);

      const partialDto = { ...mockPayrollRunDto, processedEmployees: 25 };
      const partialVm = PayrollViewModelFactory.createFromDTO(partialDto);
      expect(partialVm.progress).toBe(50);
    });

    it('應該正確對應狀態標籤', () => {
      const testCases: Array<{ status: PayrollRunDto['status']; expected: string }> = [
        { status: 'DRAFT', expected: '草稿' },
        { status: 'CALCULATING', expected: '計算中' },
        { status: 'COMPLETED', expected: '計算完成' },
        { status: 'SUBMITTED', expected: '待審核' },
        { status: 'APPROVED', expected: '已核准' },
        { status: 'REJECTED', expected: '已退回' },
        { status: 'PAID', expected: '已發薪' },
        { status: 'CANCELLED', expected: '已取消' },
      ];

      testCases.forEach(({ status, expected }) => {
        const dto: PayrollRunDto = { ...mockPayrollRunDto, status };
        const vm = PayrollViewModelFactory.createFromDTO(dto);
        expect(vm.statusLabel).toBe(expected);
      });
    });

    it('應該正確對應狀態顏色', () => {
      const testCases: Array<{ status: PayrollRunDto['status']; expected: string }> = [
        { status: 'DRAFT', expected: 'default' },
        { status: 'CALCULATING', expected: 'processing' },
        { status: 'COMPLETED', expected: 'warning' },
        { status: 'SUBMITTED', expected: 'warning' },
        { status: 'APPROVED', expected: 'success' },
        { status: 'REJECTED', expected: 'error' },
        { status: 'PAID', expected: 'cyan' },
        { status: 'CANCELLED', expected: 'default' },
      ];

      testCases.forEach(({ status, expected }) => {
        const dto: PayrollRunDto = { ...mockPayrollRunDto, status };
        const vm = PayrollViewModelFactory.createFromDTO(dto);
        expect(vm.statusColor).toBe(expected);
      });
    });

    it('應該正確格式化金額顯示', () => {
      const vm = PayrollViewModelFactory.createFromDTO(mockPayrollRunDto);

      expect(vm.totalGrossPayDisplay).toBe('$2,500,000');
      expect(vm.totalNetPayDisplay).toBe('$2,200,000');
      expect(vm.totalDeductionsDisplay).toBe('$300,000');
    });

    it('應該正確格式化期間顯示', () => {
      const vm = PayrollViewModelFactory.createFromDTO(mockPayrollRunDto);
      expect(vm.periodDisplay).toBe('2025/11/01 - 2025/11/30');
    });

    it('應該正確處理專案統計資料', () => {
      const vm = PayrollViewModelFactory.createFromDTO(mockPayrollRunDto);

      expect(vm.projectStats).toHaveLength(1);
      expect(vm.projectStats).toBeDefined();
      if (vm.projectStats && vm.projectStats.length > 0 && vm.projectStats[0]) {
        expect(vm.projectStats[0].projectName).toBe('ERP專案');
        expect(vm.projectStats[0].totalHours).toBe(1000);
        expect(vm.projectStats[0].totalAmountDisplay).toBe('$500,000');
      }
    });

    it('應該處理沒有專案統計的情況', () => {
      const dtoWithoutStats = { ...mockPayrollRunDto, project_stats: undefined };
      const vm = PayrollViewModelFactory.createFromDTO(dtoWithoutStats);

      expect(vm.projectStats).toBeUndefined();
    });

    it('應該處理零員工的情況', () => {
      const dtoWithZeroEmployees = { 
        ...mockPayrollRunDto, 
        totalEmployees: 0,
        processedEmployees: 0 
      };
      const vm = PayrollViewModelFactory.createFromDTO(dtoWithZeroEmployees);

      expect(vm.progress).toBe(0);
    });

    it('應該處理缺少金額的情況', () => {
      const dtoWithoutAmounts: PayrollRunDto = { 
        ...mockPayrollRunDto, 
        totalGrossPay: 0,
        totalNetPay: 0,
        totalDeductions: 0
      };
      const vm = PayrollViewModelFactory.createFromDTO(dtoWithoutAmounts);

      expect(vm.totalGrossPay).toBe(0);
      expect(vm.totalNetPay).toBe(0);
      expect(vm.totalDeductions).toBe(0);
      expect(vm.totalGrossPayDisplay).toBe('$0');
    });
  });

  describe('createListFromDTOs', () => {
    it('應該批量轉換 DTO 列表', () => {
      const dtos: PayrollRunDto[] = [
        mockPayrollRunDto,
        { ...mockPayrollRunDto, runId: 'run-2', name: '2025年12月薪資' }
      ];

      const vms = PayrollViewModelFactory.createListFromDTOs(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0]).toBeDefined();
      expect(vms[1]).toBeDefined();
      if (vms[0] && vms[1]) {
        expect(vms[0].runId).toBe('run-1');
        expect(vms[1].runId).toBe('run-2');
        expect(vms[1].name).toBe('2025年12月薪資');
      }
    });

    it('應該正確處理空列表', () => {
      const vms = PayrollViewModelFactory.createListFromDTOs([]);
      expect(vms).toHaveLength(0);
    });
  });
});
