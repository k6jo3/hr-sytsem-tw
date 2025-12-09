/**
 * Insurance ViewModel (保險管理視圖模型)
 * 前端顯示用的資料模型
 */

/**
 * 投保記錄 ViewModel
 */
export interface EnrollmentViewModel {
  enrollmentId: string;
  employeeName: string;
  insuranceTypeLabel: string;
  insuranceTypeColor: string;
  enrollDateDisplay: string;
  withdrawDateDisplay?: string;
  monthlySalaryDisplay: string;
  levelDisplay: string;
  statusLabel: string;
  statusColor: string;
  isActive: boolean;
  isPending: boolean;
  isWithdrawn: boolean;
  isReported: boolean;
}

/**
 * 保費明細 ViewModel
 */
export interface InsuranceFeesViewModel {
  laborEmployeeDisplay: string;
  laborEmployerDisplay: string;
  healthEmployeeDisplay: string;
  healthEmployerDisplay: string;
  pensionEmployerDisplay: string;
  totalEmployeeDisplay: string;
  totalEmployerDisplay: string;
  grandTotalDisplay: string;
}

/**
 * 投保歷程 ViewModel
 */
export interface EnrollmentHistoryViewModel {
  historyId: string;
  changeDateDisplay: string;
  changeTypeLabel: string;
  changeTypeColor: string;
  insuranceTypeLabel: string;
  monthlySalaryDisplay: string;
  levelDisplay: string;
  reason: string;
  operatorName?: string;
}

/**
 * 我的保險資訊 ViewModel
 */
export interface MyInsuranceInfoViewModel {
  employeeName: string;
  employeeCode: string;
  unitName: string;
  enrollments: EnrollmentViewModel[];
  fees: InsuranceFeesViewModel;
  history: EnrollmentHistoryViewModel[];
  hasActiveEnrollment: boolean;
  statusMessage: string;
  statusType: 'success' | 'warning' | 'error';
  // Convenience fields for display
  currentEnrollDate?: string;
  currentSalaryDisplay?: string;
  currentLevelDisplay?: string;
}

/**
 * 投保級距 ViewModel
 */
export interface InsuranceLevelViewModel {
  levelId: string;
  insuranceTypeLabel: string;
  levelNumber: number;
  monthlySalaryDisplay: string;
  effectiveDateDisplay: string;
  endDateDisplay?: string;
  isActive: boolean;
  // Rate displays
  laborEmployeeRateDisplay?: string;
  laborEmployerRateDisplay?: string;
  healthEmployeeRateDisplay?: string;
  healthEmployerRateDisplay?: string;
  pensionEmployerRateDisplay?: string;
}

/**
 * 投保單位 ViewModel
 */
export interface InsuranceUnitViewModel {
  unitId: string;
  unitCode: string;
  unitName: string;
  laborInsuranceNumber?: string;
  healthInsuranceNumber?: string;
  pensionNumber?: string;
  isActive: boolean;
  displayName: string;
}

/**
 * 保費試算結果 ViewModel
 */
export interface FeeCalculationViewModel {
  monthlySalaryDisplay: string;
  levelNumber: number;
  levelDisplay: string;
  fees: InsuranceFeesViewModel;
}
