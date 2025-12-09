import type {
  PayslipDto,
  PayslipSummaryDto,
  PayrollItemDto,
  PayslipStatus,
} from '../api/PayrollTypes';
import type {
  PayslipSummaryViewModel,
  PayslipDetailViewModel,
  PayrollItemViewModel,
} from '../model/PayrollViewModel';

/**
 * Payslip ViewModel Factory (薪資單視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class PayslipViewModelFactory {
  /**
   * 將 PayslipSummaryDto 轉換為 PayslipSummaryViewModel
   */
  static createSummaryFromDTO(dto: PayslipSummaryDto): PayslipSummaryViewModel {
    return {
      id: dto.id,
      payslipCode: dto.payslip_code,
      payPeriod: dto.pay_period,
      paymentDate: dto.payment_date,
      paymentDateDisplay: this.formatDate(dto.payment_date),
      grossPay: dto.gross_pay,
      grossPayDisplay: this.formatCurrency(dto.gross_pay),
      netPay: dto.net_pay,
      netPayDisplay: this.formatCurrency(dto.net_pay),
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      canView: this.canViewPayslip(dto.status),
      canDownload: this.canDownloadPayslip(dto.status),
    };
  }

  /**
   * 將 PayslipDto 轉換為 PayslipDetailViewModel
   */
  static createDetailFromDTO(dto: PayslipDto): PayslipDetailViewModel {
    const incomeItems = dto.items
      .filter((item) => item.item_type === 'INCOME')
      .map((item) => this.createItemViewModel(item));

    const deductionItems = dto.items
      .filter((item) => item.item_type === 'DEDUCTION')
      .map((item) => this.createItemViewModel(item));

    const payPeriod = `${dto.pay_period_start.substring(0, 7).replace('-', '年')}月`;
    const payPeriodDisplay = `${this.formatDate(dto.pay_period_start)} - ${this.formatDate(dto.pay_period_end)}`;

    return {
      id: dto.id,
      payslipCode: dto.payslip_code,
      employeeName: dto.employee_name,
      employeeCode: dto.employee_code,
      departmentName: dto.department_name,
      payPeriod,
      payPeriodDisplay,
      paymentDate: dto.payment_date,
      paymentDateDisplay: this.formatDate(dto.payment_date),
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      incomeItems,
      deductionItems,
      grossPay: dto.gross_pay,
      grossPayDisplay: this.formatCurrency(dto.gross_pay),
      totalDeductions: dto.total_deductions,
      totalDeductionsDisplay: this.formatCurrency(dto.total_deductions),
      netPay: dto.net_pay,
      netPayDisplay: this.formatCurrency(dto.net_pay),
      workDays: dto.work_days,
      overtimeHours: dto.overtime_hours,
      leaveDays: dto.leave_days,
      canDownload: this.canDownloadPayslip(dto.status),
    };
  }

  /**
   * 批量轉換薪資單摘要
   */
  static createSummaryListFromDTOs(dtos: PayslipSummaryDto[]): PayslipSummaryViewModel[] {
    return dtos.map((dto) => this.createSummaryFromDTO(dto));
  }

  // ========== Private Helper Methods ==========

  /**
   * 創建薪資項目ViewModel
   */
  private static createItemViewModel(item: PayrollItemDto): PayrollItemViewModel {
    return {
      itemCode: item.item_code,
      itemName: item.item_name,
      itemType: item.item_type,
      amount: item.amount,
      amountDisplay: this.formatCurrency(item.amount),
      description: item.description,
    };
  }

  /**
   * 對應薪資單狀態標籤
   */
  private static mapStatusLabel(status: PayslipStatus): string {
    const statusMap: Record<PayslipStatus, string> = {
      DRAFT: '草稿',
      CALCULATED: '已計算',
      APPROVED: '已核准',
      PAID: '已發放',
      VOID: '作廢',
    };
    return statusMap[status];
  }

  /**
   * 對應薪資單狀態顏色
   */
  private static mapStatusColor(status: PayslipStatus): string {
    const colorMap: Record<PayslipStatus, string> = {
      DRAFT: 'default',
      CALCULATED: 'processing',
      APPROVED: 'success',
      PAID: 'success',
      VOID: 'error',
    };
    return colorMap[status];
  }

  /**
   * 格式化貨幣
   */
  private static formatCurrency(amount: number): string {
    return `$${amount.toLocaleString('en-US')}`;
  }

  /**
   * 格式化日期
   */
  private static formatDate(isoDate: string): string {
    return isoDate.replace(/-/g, '/');
  }

  /**
   * 判斷是否可以查看薪資單
   */
  private static canViewPayslip(status: PayslipStatus): boolean {
    return status !== 'DRAFT' && status !== 'VOID';
  }

  /**
   * 判斷是否可以下載薪資單
   */
  private static canDownloadPayslip(status: PayslipStatus): boolean {
    return status === 'APPROVED' || status === 'PAID';
  }
}
