/**
 * Insurance ViewModel (保險管理視圖模型)
 * 前端顯示用的資料模型
 */

import type { ChangeType, EnrollmentStatus, InsuranceType } from '../api/InsuranceTypes';

/**
 * 投保記錄 ViewModel
 */
export interface EnrollmentViewModel {
  id: string;
  employeeId: string;
  employeeName: string;
  insuranceUnitName: string;
  insuranceType: InsuranceType;
  insuranceTypeLabel: string;
  insuranceTypeColor: string;
  enrollDate: string;
  enrollDateDisplay: string;
  withdrawDate?: string;
  withdrawDateDisplay?: string;
  monthlySalary: number;
  monthlySalaryDisplay: string;
  levelNumber: number;
  levelDisplay: string;
  status: EnrollmentStatus;
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
  laborEmployee: number;
  laborEmployer: number;
  healthEmployee: number;
  healthEmployer: number;
  pensionEmployer: number;
  totalEmployee: number;
  totalEmployer: number;
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
  changeDate: string;
  changeDateDisplay: string;
  changeType: ChangeType;
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
