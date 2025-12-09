/**
 * Project ViewModel (專案管理視圖模型)
 * 前端顯示用的資料模型
 */

import type { ProjectType, ProjectStatus, BudgetType } from '../api/ProjectTypes';

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
 * 客戶ViewModel
 */
export interface CustomerViewModel {
  id: string;
  customerCode: string;
  customerName: string;
  contactPerson?: string;
  contactEmail?: string;
  contactPhone?: string;
  displayName: string; // "customerCode - customerName"
}
