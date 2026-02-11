/**
 * Project ViewModel (專案管理視圖模型)
 * 前端顯示用的資料模型
 */

import type { BudgetType, ProjectStatus, ProjectType } from '../api/ProjectTypes';

/**
 * 專案ViewModel - 用於列表顯示
 */
export interface ProjectViewModel {
  id: string;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManagerName: string;
  projectTypeLabel: string;
  projectTypeColor: string;
  budgetTypeLabel: string;
  budgetAmount: number;
  budgetAmountDisplay: string;
  actualCost: number;
  actualCostDisplay: string;
  costUtilization: number; // 成本使用率 %
  costUtilizationDisplay: string;
  progress: number;
  progressDisplay: string;
  statusLabel: string;
  statusColor: string;
  plannedSchedule: string; // "YYYY/MM/DD - YYYY/MM/DD"
  isOverBudget: boolean; // 成本超支警示
  isDelayed: boolean; // 進度延遲警示
}

/**
 * 專案詳情ViewModel - 用於詳情頁面
 */
export interface ProjectDetailViewModel extends ProjectViewModel {
  customerId: string;
  projectManagerId: string;
  projectType: ProjectType;
  budgetType: BudgetType;
  status: ProjectStatus;
  budgetHours: number;
  actualHours: number;
  plannedStartDate: string;
  plannedEndDate: string;
  actualStartDate?: string;
  actualEndDate?: string;
  createdAt: string;
  updatedAt: string;
}


/**
 * 工項ViewModel (WBS)
 */
export interface TaskViewModel {
  key: string; // 用於 Table rowKey (通常同 id)
  id: string;
  projectId: string;
  parentTaskId?: string | null;
  taskCode: string;
  taskName: string;
  level: number;
  estimatedHours: number;
  actualHours: number;
  progress: number;
  progressDisplay: string;
  status: any; // TaskStatus
  statusLabel: string;
  statusColor: string;
  assigneeId?: string;
  assigneeName: string;
  children?: TaskViewModel[];
}
/**
 * 專案成員ViewModel
 */
export interface ProjectMemberViewModel {
  memberId: string;
  employeeId: string;
  employeeName: string;
  role: string;
  allocatedHours: number;
  actualHours: number;
  utilization: number; // (actual / allocated) * 100
  joinDate: string;
}

/**
 * 專案成本視圖模型
 */
export interface ProjectCostViewModel {
  projectId: string;
  budgetAmount: number;
  actualCost: number;
  costUtilization: number;
  budgetHours: number;
  actualHours: number;
  hourUtilization: number;
  profitMargin: number; // 毛利
  memberCosts: MemberCostViewModel[];
}

/**
 * 成員成本視圖模型
 */
export interface MemberCostViewModel {
  employeeId: string;
  employeeName: string;
  role: string;
  hours: number;
  hourlyRate: number;
  cost: number;
  costPercentage: number; // 佔總開發成本百分比
}
