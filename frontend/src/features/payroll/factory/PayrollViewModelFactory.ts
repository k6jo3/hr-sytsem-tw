import type { PayrollRunDto } from '../api/PayrollTypes';
import type { PayrollRunViewModel } from '../model/PayrollViewModel';

/**
 * Payroll ViewModel Factory (薪資批次視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class PayrollViewModelFactory {
  /**
   * 將 PayrollRunDto 轉換為 PayrollRunViewModel
   */
  static createFromDTO(dto: PayrollRunDto): PayrollRunViewModel {
    const progress = dto.totalEmployees > 0 
      ? Math.round((dto.processedEmployees / dto.totalEmployees) * 100) 
      : 0;

    return {
      runId: dto.runId,
      organizationId: dto.organizationId,
      name: dto.name,
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      payrollSystem: dto.payrollSystem,
      start: dto.start,
      end: dto.end,
      periodDisplay: `${dto.start.replace(/-/g, '/')} - ${dto.end.replace(/-/g, '/')}`,
      payDate: dto.payDate,
      totalDays: dto.totalDays,
      totalEmployees: dto.totalEmployees,
      processedEmployees: dto.processedEmployees,
      progress,
      successCount: dto.successCount,
      failureCount: dto.failureCount,
      totalGrossPay: dto.totalGrossPay || 0,
      totalGrossPayDisplay: this.formatCurrency(dto.totalGrossPay || 0),
      totalNetPay: dto.totalNetPay || 0,
      totalNetPayDisplay: this.formatCurrency(dto.totalNetPay || 0),
      totalDeductions: dto.totalDeductions || 0,
      totalDeductionsDisplay: this.formatCurrency(dto.totalDeductions || 0),
      executedAt: dto.executedAt,
      completedAt: dto.completedAt,
      approvedAt: dto.approvedAt,
      paidAt: dto.paidAt,
      projectStats: dto.project_stats?.map(s => ({
        projectId: s.project_id,
        projectName: s.project_name,
        totalHours: s.total_hours,
        totalAmount: s.total_amount,
        totalAmountDisplay: this.formatCurrency(s.total_amount)
      }))
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: PayrollRunDto[]): PayrollRunViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * 對應狀態標籤
   */
  private static mapStatusLabel(status: string): string {
    const map: Record<string, string> = {
      'DRAFT': '草稿',
      'CALCULATING': '計算中',
      'COMPLETED': '計算完成',
      'SUBMITTED': '待審核',
      'APPROVED': '已核准',
      'REJECTED': '已退回',
      'PAID': '已發薪',
      'CANCELLED': '已取消',
    };
    return map[status] || status;
  }

  /**
   * 對應狀態顏色
   */
  private static mapStatusColor(status: string): string {
    const map: Record<string, string> = {
      'DRAFT': 'default',
      'CALCULATING': 'processing',
      'COMPLETED': 'warning',
      'SUBMITTED': 'warning',
      'APPROVED': 'success',
      'REJECTED': 'error',
      'PAID': 'cyan',
      'CANCELLED': 'default',
    };
    return map[status] || 'default';
  }

  /**
   * 格式化貨幣
   */
  private static formatCurrency(amount: number): string {
    return `$${amount.toLocaleString()}`;
  }
}
