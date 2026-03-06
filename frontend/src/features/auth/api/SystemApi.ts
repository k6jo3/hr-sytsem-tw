/**
 * 系統管理 API 模組
 * Domain Code: HR01
 */

import { MockConfig } from '../../../config/MockConfig';
import { apiClient } from '../../../shared/api/apiClient';

/** 系統參數 DTO */
export interface SystemParameterDto {
  paramCode: string;
  paramName: string;
  paramValue: string;
  paramType: string;
  module: string;
  category: string;
  description: string;
  defaultValue: string;
  encrypted: boolean;
  updatedAt: string | null;
  updatedBy: string | null;
}

/** 功能開關 DTO */
export interface FeatureToggleDto {
  featureCode: string;
  featureName: string;
  module: string;
  enabled: boolean;
  description: string;
  updatedAt: string | null;
  updatedBy: string | null;
}

/** 排程任務配置 DTO */
export interface ScheduledJobConfigDto {
  jobCode: string;
  jobName: string;
  module: string;
  cronExpression: string;
  enabled: boolean;
  description: string;
  lastExecutedAt: string | null;
  lastExecutionStatus: string | null;
  lastErrorMessage: string | null;
  consecutiveFailures: number;
  updatedAt: string | null;
  updatedBy: string | null;
}

/** 適配系統參數回應 */
function adaptParameterDto(raw: Record<string, unknown>): SystemParameterDto {
  return {
    paramCode: (raw.paramCode ?? raw.param_code ?? '') as string,
    paramName: (raw.paramName ?? raw.param_name ?? '') as string,
    paramValue: (raw.paramValue ?? raw.param_value ?? '') as string,
    paramType: (raw.paramType ?? raw.param_type ?? 'STRING') as string,
    module: (raw.module ?? '') as string,
    category: (raw.category ?? '') as string,
    description: (raw.description ?? '') as string,
    defaultValue: (raw.defaultValue ?? raw.default_value ?? '') as string,
    encrypted: (raw.encrypted ?? raw.isEncrypted ?? raw.is_encrypted ?? false) as boolean,
    updatedAt: (raw.updatedAt ?? raw.updated_at ?? null) as string | null,
    updatedBy: (raw.updatedBy ?? raw.updated_by ?? null) as string | null,
  };
}

/** 適配功能開關回應 */
function adaptToggleDto(raw: Record<string, unknown>): FeatureToggleDto {
  return {
    featureCode: (raw.featureCode ?? raw.feature_code ?? '') as string,
    featureName: (raw.featureName ?? raw.feature_name ?? '') as string,
    module: (raw.module ?? '') as string,
    enabled: (raw.enabled ?? false) as boolean,
    description: (raw.description ?? '') as string,
    updatedAt: (raw.updatedAt ?? raw.updated_at ?? null) as string | null,
    updatedBy: (raw.updatedBy ?? raw.updated_by ?? null) as string | null,
  };
}

/** 適配排程任務回應 */
function adaptJobDto(raw: Record<string, unknown>): ScheduledJobConfigDto {
  return {
    jobCode: (raw.jobCode ?? raw.job_code ?? '') as string,
    jobName: (raw.jobName ?? raw.job_name ?? '') as string,
    module: (raw.module ?? '') as string,
    cronExpression: (raw.cronExpression ?? raw.cron_expression ?? '') as string,
    enabled: (raw.enabled ?? false) as boolean,
    description: (raw.description ?? '') as string,
    lastExecutedAt: (raw.lastExecutedAt ?? raw.last_executed_at ?? null) as string | null,
    lastExecutionStatus: (raw.lastExecutionStatus ?? raw.last_execution_status ?? null) as string | null,
    lastErrorMessage: (raw.lastErrorMessage ?? raw.last_error_message ?? null) as string | null,
    consecutiveFailures: (raw.consecutiveFailures ?? raw.consecutive_failures ?? 0) as number,
    updatedAt: (raw.updatedAt ?? raw.updated_at ?? null) as string | null,
    updatedBy: (raw.updatedBy ?? raw.updated_by ?? null) as string | null,
  };
}

/** Mock 資料 */
const MockSystemApi = {
  getParameters: async (): Promise<SystemParameterDto[]> => [
    { paramCode: 'MAX_FAILED_LOGIN_ATTEMPTS', paramName: '登入失敗上限', paramValue: '5', paramType: 'INTEGER', module: 'HR01', category: 'SECURITY', description: '帳號鎖定前允許的最大登入失敗次數', defaultValue: '5', encrypted: false, updatedAt: null, updatedBy: null },
    { paramCode: 'ACCOUNT_LOCK_DURATION_MINUTES', paramName: '帳號鎖定時長', paramValue: '30', paramType: 'INTEGER', module: 'HR01', category: 'SECURITY', description: '帳號鎖定時長（分鐘）', defaultValue: '30', encrypted: false, updatedAt: null, updatedBy: null },
    { paramCode: 'PASSWORD_MIN_LENGTH', paramName: '密碼最短長度', paramValue: '8', paramType: 'INTEGER', module: 'HR01', category: 'SECURITY', description: '密碼最低字元數', defaultValue: '8', encrypted: false, updatedAt: null, updatedBy: null },
    { paramCode: 'EMPLOYEE_NUMBER_PREFIX', paramName: '員工編號前綴', paramValue: 'EMP', paramType: 'STRING', module: 'HR02', category: 'BUSINESS', description: '員工編號的前綴字串', defaultValue: 'EMP', encrypted: false, updatedAt: null, updatedBy: null },
    { paramCode: 'EMPLOYEE_NUMBER_FORMAT', paramName: '員工編號格式', paramValue: 'YYYYMM-NNNN', paramType: 'STRING', module: 'HR02', category: 'BUSINESS', description: '員工編號格式', defaultValue: 'YYYYMM-NNNN', encrypted: false, updatedAt: null, updatedBy: null },
    { paramCode: 'EMPLOYEE_NUMBER_SEQ_DIGITS', paramName: '員工編號流水號位數', paramValue: '4', paramType: 'INTEGER', module: 'HR02', category: 'BUSINESS', description: '流水號補零位數', defaultValue: '4', encrypted: false, updatedAt: null, updatedBy: null },
  ],
  getFeatures: async (): Promise<FeatureToggleDto[]> => [
    { featureCode: 'LATE_CHECK', featureName: '遲到判定', module: 'HR03', enabled: true, description: '啟用考勤遲到自動判定功能', updatedAt: null, updatedBy: null },
    { featureCode: 'SALARY_ADVANCE', featureName: '薪資預借', module: 'HR04', enabled: true, description: '啟用薪資預借申請功能', updatedAt: null, updatedBy: null },
    { featureCode: 'LDAP_AUTH', featureName: 'LDAP 認證', module: 'HR01', enabled: false, description: '啟用 LDAP/AD 企業登入整合', updatedAt: null, updatedBy: null },
  ],
  getJobs: async (): Promise<ScheduledJobConfigDto[]> => [
    { jobCode: 'ABSENT_DETECTION', jobName: '曠職自動判定', module: 'HR03', cronExpression: '0 0 19 * * ?', enabled: true, description: '每日 19:00 掃描', lastExecutedAt: null, lastExecutionStatus: null, lastErrorMessage: null, consecutiveFailures: 0, updatedAt: null, updatedBy: null },
    { jobCode: 'PAYROLL_MONTHLY_CLOSE', jobName: '薪資月結', module: 'HR04', cronExpression: '0 0 2 1 * ?', enabled: true, description: '每月 1 日凌晨 2:00', lastExecutedAt: null, lastExecutionStatus: null, lastErrorMessage: null, consecutiveFailures: 0, updatedAt: null, updatedBy: null },
  ],
};

/**
 * 系統管理 API
 */
export const SystemApi = {
  /** 查詢所有系統參數 */
  getParameters: async (): Promise<SystemParameterDto[]> => {
    if (MockConfig.isEnabled('AUTH')) return MockSystemApi.getParameters();
    const raw = await apiClient.get<SystemParameterDto[]>('/system/parameters');
    return Array.isArray(raw) ? raw.map(r => adaptParameterDto(r as unknown as Record<string, unknown>)) : [];
  },

  /** 更新系統參數 */
  updateParameter: async (paramCode: string, paramValue: string): Promise<SystemParameterDto> => {
    const raw = await apiClient.put<SystemParameterDto>(`/system/parameters/${paramCode}`, { paramValue });
    return adaptParameterDto(raw as unknown as Record<string, unknown>);
  },

  /** 查詢所有功能開關 */
  getFeatures: async (): Promise<FeatureToggleDto[]> => {
    if (MockConfig.isEnabled('AUTH')) return MockSystemApi.getFeatures();
    const raw = await apiClient.get<FeatureToggleDto[]>('/system/features');
    return Array.isArray(raw) ? raw.map(r => adaptToggleDto(r as unknown as Record<string, unknown>)) : [];
  },

  /** 切換功能開關 */
  toggleFeature: async (featureCode: string, enabled?: boolean): Promise<FeatureToggleDto> => {
    const body = enabled !== undefined ? { enabled } : {};
    const raw = await apiClient.put<FeatureToggleDto>(`/system/features/${featureCode}/toggle`, body);
    return adaptToggleDto(raw as unknown as Record<string, unknown>);
  },

  /** 查詢所有排程任務 */
  getJobs: async (): Promise<ScheduledJobConfigDto[]> => {
    if (MockConfig.isEnabled('AUTH')) return MockSystemApi.getJobs();
    const raw = await apiClient.get<ScheduledJobConfigDto[]>('/system/jobs');
    return Array.isArray(raw) ? raw.map(r => adaptJobDto(r as unknown as Record<string, unknown>)) : [];
  },

  /** 更新排程任務配置 */
  updateJob: async (jobCode: string, data: { cronExpression: string; enabled: boolean }): Promise<ScheduledJobConfigDto> => {
    const raw = await apiClient.put<ScheduledJobConfigDto>(`/system/jobs/${jobCode}`, data);
    return adaptJobDto(raw as unknown as Record<string, unknown>);
  },
};
