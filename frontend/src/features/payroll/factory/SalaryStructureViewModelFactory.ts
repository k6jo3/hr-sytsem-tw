import type { SalaryStructureDto } from '../api/PayrollTypes';

export interface SalaryItemViewModel {
  itemId: string;
  code: string;
  name: string;
  type: 'EARNING' | 'DEDUCTION';
  amount: number;
  amountDisplay: string;
  taxable: boolean;
  insurable: boolean;
}

export interface SalaryStructureViewModel {
  id: string;
  employeeId: string;
  employeeName: string;
  employeeNumber: string;
  /** 員工顯示文字：優先顯示「工號 姓名」，無資料時顯示截斷的 ID */
  employeeDisplay: string;
  payrollSystem: 'MONTHLY' | 'HOURLY';
  payrollSystemLabel: string;
  amountDisplay: string;
  effectiveDate: string;
  statusLabel: string;
  statusColor: string;
  active: boolean;
  items: SalaryItemViewModel[];
}

/**
 * SalaryStructure ViewModel Factory
 */
export class SalaryStructureViewModelFactory {
  static createFromDTO(dto: SalaryStructureDto): SalaryStructureViewModel {
    const amountDisplay = dto.payrollSystem === 'MONTHLY'
      ? `$${dto.monthlySalary?.toLocaleString()}`
      : `$${dto.hourlyRate?.toLocaleString()}/時`;

    // 組合員工顯示文字：優先「工號 姓名」，其次「姓名」，最後截斷 ID
    const employeeName = dto.employeeName ?? '';
    const employeeNumber = dto.employeeNumber ?? '';
    let employeeDisplay = '';
    if (employeeNumber && employeeName) {
      employeeDisplay = `${employeeNumber} ${employeeName}`;
    } else if (employeeName) {
      employeeDisplay = employeeName;
    } else if (employeeNumber) {
      employeeDisplay = employeeNumber;
    } else {
      employeeDisplay = dto.employeeId ? dto.employeeId.substring(0, 8) + '...' : '';
    }

    return {
      id: dto.id,
      employeeId: dto.employeeId,
      employeeName,
      employeeNumber,
      employeeDisplay,
      payrollSystem: dto.payrollSystem,
      payrollSystemLabel: dto.payrollSystem === 'MONTHLY' ? '月薪制' : '時薪制',
      amountDisplay,
      effectiveDate: dto.effectiveDate.replace(/-/g, '/'),
      statusLabel: dto.active ? '生效中' : '已失效',
      statusColor: dto.active ? 'success' : 'default',
      active: dto.active,
      items: (dto.items || []).map(item => ({
        ...item,
        amountDisplay: `$${item.amount.toLocaleString()}`
      }))
    };
  }

  static createListFromDTOs(dtos: SalaryStructureDto[]): SalaryStructureViewModel[] {
    return dtos.map(dto => this.createFromDTO(dto));
  }
}
