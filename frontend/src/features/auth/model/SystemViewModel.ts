/**
 * 系統管理 ViewModel 定義
 * Domain Code: HR01
 */

/** 系統參數 ViewModel */
export interface SystemParameterViewModel {
  paramCode: string;
  paramName: string;
  paramValue: string;
  paramType: string;
  module: string;
  moduleLabel: string;
  category: string;
  categoryLabel: string;
  categoryColor: string;
  description: string;
  defaultValue: string;
  isEncrypted: boolean;
  isModified: boolean;
  updatedAtDisplay: string;
  updatedBy: string;
}

/** 功能開關 ViewModel */
export interface FeatureToggleViewModel {
  featureCode: string;
  featureName: string;
  module: string;
  moduleLabel: string;
  enabled: boolean;
  description: string;
  updatedAtDisplay: string;
  updatedBy: string;
}

/** 排程任務 ViewModel */
export interface ScheduledJobViewModel {
  jobCode: string;
  jobName: string;
  module: string;
  moduleLabel: string;
  cronExpression: string;
  cronDescription: string;
  enabled: boolean;
  description: string;
  lastExecutedAtDisplay: string;
  lastExecutionStatus: string | null;
  statusLabel: string;
  statusColor: string;
  lastErrorMessage: string | null;
  consecutiveFailures: number;
  needsAlert: boolean;
  updatedAtDisplay: string;
  updatedBy: string;
}
