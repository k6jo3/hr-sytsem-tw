/**
 * Payroll Management API Types
 * Domain Code: HR04 - 薪資管理服務
 */

// ========== Enums ==========

/**
 * 薪資單狀態
 */
export type PayslipStatus = 'DRAFT' | 'CALCULATED' | 'APPROVED' | 'PAID' | 'VOID';

/**
 * 薪資項目類型
 */
export type PayrollItemType = 'INCOME' | 'DEDUCTION';

/**
 * 薪資項目代碼
 */
export type PayrollItemCode =
  | 'BASE_SALARY' // 底薪
  | 'POSITION_ALLOWANCE' // 職務加給
  | 'MEAL_ALLOWANCE' // 伙食津貼
  | 'OVERTIME_PAY' // 加班費
  | 'BONUS' // 獎金
  | 'LABOR_INSURANCE' // 勞保費
  | 'HEALTH_INSURANCE' // 健保費
  | 'INCOME_TAX' // 所得稅
  | 'ABSENCE_DEDUCTION' // 缺勤扣款
  | 'OTHER_DEDUCTION'; // 其他扣款

// ========== DTOs ==========

/**
 * 薪資結構項目 DTO
 */
export interface SalaryItemDto {
  itemId: string;
  code: string;
  name: string;
  type: 'EARNING' | 'DEDUCTION';
  amount: number;
  fixedAmount: boolean;
  taxable: boolean;
  insurable: boolean;
}

/**
 * 薪資結構 DTO
 */
export interface SalaryStructureDto {
  id: string;
  employeeId: string;
  payrollSystem: 'MONTHLY' | 'HOURLY';
  payrollCycle: 'DAILY' | 'WEEKLY' | 'BI_WEEKLY' | 'MONTHLY';
  monthlySalary?: number;
  hourlyRate?: number;
  calculatedHourlyRate?: number;
  effectiveDate: string;
  endDate?: string;
  active: boolean;
  items: SalaryItemDto[];
}

/**
 * 薪資批次 DTO
 */
export interface PayrollRunDto {
  runId: string;
  organizationId: string;
  name: string;
  status: 'DRAFT' | 'CALCULATING' | 'COMPLETED' | 'APPROVED' | 'REJECTED' | 'PAID';
  payrollSystem: string;
  start: string;
  end: string;
  totalDays: number;
  totalEmployees: number;
  processedEmployees: number;
  successCount: number;
  failureCount: number;
  totalGrossPay: number;
  totalNetPay: number;
  totalDeductions: number;
  executedAt?: string;
  completedAt?: string;
  approvedAt?: string;
  paidAt?: string;
}

/**
 * 薪資項目 DTO
 */
export interface PayrollItemDto {
// ... existing code ...
  item_code: PayrollItemCode;
  item_name: string;
  item_type: PayrollItemType;
  amount: number;
  description?: string;
}

/**
 * 薪資項目定義 DTO
 */
export interface PayrollItemDefinitionDto {
  id: string;
  itemCode: string;
  itemName: string;
  itemType: 'EARNING' | 'DEDUCTION';
  taxable: boolean;
  insurable: boolean;
  calculationFormula?: string;
  description?: string;
  displayOrder: number;
  active: boolean;
}

/**
 * 薪資單 DTO
 */
export interface PayslipDto {
  id: string;
  payslip_code: string;
  employee_id: string;
  employee_name: string;
  employee_code: string;
  department_name?: string;
  pay_period_start: string; // 計薪起始日
  pay_period_end: string; // 計薪結束日
  payment_date: string; // 發薪日
  status: PayslipStatus;

  // 薪資項目
  items: PayrollItemDto[];

  // 金額統計
  gross_pay: number; // 應發薪資
  total_deductions: number; // 扣除總額
  net_pay: number; // 實發薪資

  // 工作統計
  work_days?: number;
  overtime_hours?: number;
  leave_days?: number;

  created_at: string;
  updated_at: string;
}

/**
 * 薪資單摘要 DTO (用於列表)
 */
export interface PayslipSummaryDto {
  id: string;
  payslip_code: string;
  pay_period: string; // "2025年11月"
  payment_date: string;
  gross_pay: number;
  net_pay: number;
  status: PayslipStatus;
}

// ========== Request/Response Types ==========

/**
 * 薪資結構請求
 */
export interface CreateSalaryStructureRequest {
  employeeId: string;
  payrollSystem: string;
  payrollCycle: string;
  monthlySalary?: number;
  hourlyRate?: number;
  effectiveDate: string;
  items: Omit<SalaryItemDto, 'itemId'>[];
}

export interface UpdateSalaryStructureRequest extends Partial<CreateSalaryStructureRequest> {
  active?: boolean;
}

/**
 * 薪資批次請求
 */
export interface StartPayrollRunRequest {
  name: string;
  payrollSystem: string;
  start: string;
  end: string;
  payDate: string;
}

export interface PayrollRunActionRequest {
  runId: string;
  reason?: string;
}

/**
 * 薪資單列表查詢請求
 */
export interface GetPayslipListRequest {
  runId?: string;
  employeeId?: string;
  status?: string;
  yearMonth?: string;
  page?: number;
  page_size?: number;
}

export interface GetPayslipListResponse {
  items: PayslipDto[];
  total: number;
}

/**
 * 取得我的薪資單列表請求
 */
export interface GetMyPayslipsRequest {
// ... existing code ...
// ... existing code ...
  year?: number; // 年度篩選
  page?: number;
  page_size?: number;
}

/**
 * 取得我的薪資單列表回應
 */
export interface GetMyPayslipsResponse {
  payslips: PayslipSummaryDto[];
  total: number;
  page: number;
  page_size: number;
}

/**
 * 取得薪資單詳情回應
 */
export interface GetPayslipDetailResponse {
  payslip: PayslipDto;
}

/**
 * 下載薪資單PDF請求
 */
export interface DownloadPayslipPdfRequest {
  payslip_id: string;
}
