import type { BudgetType, ProjectDto, ProjectStatus, ProjectType, TaskDto, TaskStatus } from '../api/ProjectTypes';
import type { ProjectDetailViewModel, ProjectViewModel, TaskViewModel } from '../model/ProjectViewModel';

/**
 * Project ViewModel Factory (專案管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class ProjectViewModelFactory {
  /**
   * 將 ProjectDto 轉換為 ProjectViewModel
   */
  static createFromDTO(dto: ProjectDto): ProjectViewModel {
    const costUtilization = this.calculateCostUtilization(dto.budget_amount, dto.actual_cost);
    const isOverBudget = costUtilization > 100;
    const isDelayed = this.isProjectDelayed(costUtilization, dto.progress);

    return {
      id: dto.id,
      projectCode: dto.project_code,
      projectName: dto.project_name,
      customerName: dto.customer_name,
      projectManagerName: dto.project_manager_name,
      projectTypeLabel: this.mapProjectTypeLabel(dto.project_type),
      projectTypeColor: this.mapProjectTypeColor(dto.project_type),
      budgetTypeLabel: this.mapBudgetTypeLabel(dto.budget_type),
      budgetAmount: dto.budget_amount,
      budgetAmountDisplay: this.formatCurrency(dto.budget_amount),
      actualCost: dto.actual_cost,
      actualCostDisplay: this.formatCurrency(dto.actual_cost),
      costUtilization,
      costUtilizationDisplay: this.formatPercentage(costUtilization),
      progress: dto.progress,
      progressDisplay: this.formatPercentage(dto.progress),
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      plannedSchedule: this.formatSchedule(dto.planned_start_date, dto.planned_end_date),
      isOverBudget,
      isDelayed,
    };
  }

  /**
   * 將 ProjectDto 轉換為 ProjectDetailViewModel
   */
  static createDetailFromDTO(dto: ProjectDto): ProjectDetailViewModel {
    const base = this.createFromDTO(dto);
    return {
      ...base,
      customerId: dto.customer_id,
      projectManagerId: dto.project_manager_id,
      projectType: dto.project_type,
      budgetType: dto.budget_type,
      status: dto.status,
      budgetHours: dto.budget_hours,
      actualHours: dto.actual_hours,
      plannedStartDate: dto.planned_start_date,
      plannedEndDate: dto.planned_end_date,
      actualStartDate: dto.actual_start_date,
      actualEndDate: dto.actual_end_date,
      createdAt: dto.created_at,
      updatedAt: dto.updated_at,
    };
  }

  /**
   * 將 TaskDto 轉換為 TaskViewModel
   */
  static createTaskViewModel(dto: TaskDto): TaskViewModel {
    return {
      key: dto.id,
      id: dto.id,
      projectId: dto.project_id,
      parentTaskId: dto.parent_task_id,
      taskCode: dto.task_code,
      taskName: dto.task_name,
      level: dto.level,
      estimatedHours: dto.estimated_hours,
      actualHours: dto.actual_hours,
      progress: dto.progress,
      progressDisplay: this.formatPercentage(dto.progress),
      status: dto.status,
      statusLabel: this.mapTaskStatusLabel(dto.status),
      statusColor: this.mapTaskStatusColor(dto.status),
      assigneeId: dto.assignee_id,
      assigneeName: dto.assignee_name || '-',
      children: dto.children && dto.children.length > 0 
        ? dto.children.map(child => this.createTaskViewModel(child))
        : undefined,
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: ProjectDto[]): ProjectViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  // ========== Private Helper Methods ==========

  /**
   * 對應專案類型標籤
   */
  private static mapProjectTypeLabel(type: ProjectType): string {
    const typeMap: Record<ProjectType, string> = {
      DEVELOPMENT: '新開發',
      MAINTENANCE: '維護',
      CONSULTING: '顧問',
    };
    return typeMap[type];
  }

  /**
   * 對應專案類型顏色
   */
  private static mapProjectTypeColor(type: ProjectType): string {
    const colorMap: Record<ProjectType, string> = {
      DEVELOPMENT: 'blue',
      MAINTENANCE: 'green',
      CONSULTING: 'purple',
    };
    return colorMap[type];
  }

  /**
   * 對應預算模式標籤
   */
  private static mapBudgetTypeLabel(type: BudgetType): string {
    const typeMap: Record<BudgetType, string> = {
      FIXED_PRICE: '固定價格',
      TIME_AND_MATERIAL: '實報實銷',
    };
    return typeMap[type];
  }

  /**
   * 對應專案狀態標籤
   */
  private static mapStatusLabel(status: ProjectStatus): string {
    const statusMap: Record<ProjectStatus, string> = {
      PLANNING: '規劃中',
      IN_PROGRESS: '進行中',
      COMPLETED: '已結案',
      ON_HOLD: '暫停',
      CANCELLED: '已取消',
    };
    return statusMap[status];
  }

  /**
   * 對應專案狀態顏色 (Ant Design Tag colors)
   */
  private static mapStatusColor(status: ProjectStatus): string {
    const colorMap: Record<ProjectStatus, string> = {
      PLANNING: 'default',
      IN_PROGRESS: 'processing',
      COMPLETED: 'success',
      ON_HOLD: 'warning',
      CANCELLED: 'error',
    };
    return colorMap[status];
  }

  /**
   * 對應工項狀態標籤
   */
  private static mapTaskStatusLabel(status: TaskStatus): string {
    const statusMap: Record<TaskStatus, string> = {
      NOT_STARTED: '未開始',
      IN_PROGRESS: '進行中',
      COMPLETED: '已完成',
      BLOCKED: '阻塞中',
    };
    return statusMap[status];
  }

  /**
   * 對應工項狀態顏色
   */
  private static mapTaskStatusColor(status: TaskStatus): string {
    const colorMap: Record<TaskStatus, string> = {
      NOT_STARTED: 'default',
      IN_PROGRESS: 'processing',
      COMPLETED: 'success',
      BLOCKED: 'error',
    };
    return colorMap[status];
  }

  /**
   * 格式化貨幣金額
   */
  private static formatCurrency(amount: number): string {
    return `$${amount.toLocaleString('en-US')}`;
  }

  /**
   * 格式化百分比
   */
  private static formatPercentage(value: number): string {
    return `${value}%`;
  }

  /**
   * 計算成本使用率
   */
  private static calculateCostUtilization(budget: number, actualCost: number): number {
    if (budget === 0) return 0;
    return Math.round((actualCost / budget) * 100);
  }

  /**
   * 格式化計畫時程
   */
  private static formatSchedule(startDate: string, endDate: string): string {
    const formatDate = (isoDate: string) => isoDate.replace(/-/g, '/');
    return `${formatDate(startDate)} - ${formatDate(endDate)}`;
  }

  /**
   * 判斷專案是否延遲
   * 規則：成本使用率超過進度+10%視為延遲
   */
  private static isProjectDelayed(costUtilization: number, progress: number): boolean {
    return costUtilization > progress + 10;
  }
}
