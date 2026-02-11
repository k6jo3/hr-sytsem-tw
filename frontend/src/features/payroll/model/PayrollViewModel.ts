/**
 * Payroll ViewModel (薪資管理視圖模型)
 * 前端顯示用的資料模型
 */

import type { PayrollItemType, PayslipStatus } from '../api/PayrollTypes';

/**
 * 薪資項目 ViewModel
 */
export interface PayrollItemViewModel {
  itemCode: string;
  itemName: string;
  itemType: PayrollItemType;
  amount: number;
  amountDisplay: string;
  description?: string;
}

/**
 * 薪資單摘要 ViewModel (列表顯示)
 */
export interface PayslipSummaryViewModel {
  id: string;
  payslipCode: string;
  payPeriod: string; // "2025年11月"
  paymentDate: string;
  paymentDateDisplay: string; // "2025/12/05"
  grossPay: number;
  grossPayDisplay: string;
  netPay: number;
  netPayDisplay: string;
  status: PayslipStatus;
  statusLabel: string;
  statusColor: string;
  canView: boolean;
  canDownload: boolean;
}

/**
 * 薪資單詳情 ViewModel
 */
export interface PayslipDetailViewModel {
  id: string;
  payslipCode: string;
  employeeName: string;
  employeeCode: string;
  departmentName?: string;
  payPeriod: string; // "2025年11月"
  payPeriodDisplay: string; // "2025/11/01 - 2025/11/30"
  paymentDate: string;
  paymentDateDisplay: string; // "2025/12/05"
  status: PayslipStatus;
  statusLabel: string;
  statusColor: string;

  // 薪資項目（分類）
  incomeItems: PayrollItemViewModel[];
  deductionItems: PayrollItemViewModel[];

  // 金額統計
  grossPay: number;
  grossPayDisplay: string;
  totalDeductions: number;
  totalDeductionsDisplay: string;
  netPay: number;
  netPayDisplay: string;

  // 工作統計
  workDays?: number;
  overtimeHours?: number;
  leaveDays?: number;

  // 權限
  canDownload: boolean;
}

/**
 * 薪資批次 ViewModel
 */
export interface PayrollRunViewModel {
  runId: string;
  organizationId: string;
  name: string;
  status: 'DRAFT' | 'CALCULATING' | 'COMPLETED' | 'APPROVED' | 'REJECTED' | 'PAID';
  statusLabel: string;
  statusColor: string;
  payrollSystem: string;
  start: string;
  end: string;
  periodDisplay: string;
  payDate: string;
  totalDays: number;
  totalEmployees: number;
  processedEmployees: number;
  progress: number;
  successCount: number;
  failureCount: number;
  totalGrossPay: number;
  totalGrossPayDisplay: string;
  totalNetPay: number;
  totalNetPayDisplay: string;
  totalDeductions: number;
  totalDeductionsDisplay: string;
  executedAt?: string;
  completedAt?: string;
  approvedAt?: string;
  paidAt?: string;

  // 專案統計 (HR07 Integration)
  projectStats?: {
    projectId: string;
    projectName: string;
    totalHours: number;
    totalAmount: number;
    totalAmountDisplay: string;
  }[];
}
