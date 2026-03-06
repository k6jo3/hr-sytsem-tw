/**
 * 系統管理 ViewModel Factory
 * DTO → ViewModel 轉換
 * Domain Code: HR01
 */

import type { FeatureToggleDto, ScheduledJobConfigDto, SystemParameterDto } from '../api/SystemApi';
import type { FeatureToggleViewModel, ScheduledJobViewModel, SystemParameterViewModel } from '../model/SystemViewModel';

/** 模組代碼 → 中文標籤 */
const MODULE_LABELS: Record<string, string> = {
  GLOBAL: '全域',
  HR01: '身分認證',
  HR02: '組織管理',
  HR03: '考勤管理',
  HR04: '薪資管理',
  HR05: '保險管理',
  HR06: '專案管理',
  HR07: '工時管理',
  HR08: '績效管理',
  HR09: '招募管理',
  HR10: '訓練管理',
  HR11: '簽核流程',
  HR12: '通知服務',
  HR13: '文件管理',
  HR14: '報表中心',
};

/** 分類代碼 → 中文標籤 */
const CATEGORY_LABELS: Record<string, string> = {
  SECURITY: '安全性',
  BUSINESS: '業務規則',
  UI: '介面設定',
  SYSTEM: '系統設定',
};

/** 分類 → Ant Design Tag 顏色 */
const CATEGORY_COLORS: Record<string, string> = {
  SECURITY: 'red',
  BUSINESS: 'blue',
  UI: 'green',
  SYSTEM: 'orange',
};

/** 排程狀態 → 標籤與顏色 */
const STATUS_MAP: Record<string, { label: string; color: string }> = {
  SUCCESS: { label: '成功', color: 'success' },
  FAILED: { label: '失敗', color: 'error' },
  RUNNING: { label: '執行中', color: 'processing' },
};

/** 格式化日期時間 */
function formatDateTime(dateStr: string | null): string {
  if (!dateStr) return '-';
  try {
    const d = new Date(dateStr);
    return d.toLocaleString('zh-TW', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit',
    });
  } catch {
    return dateStr;
  }
}

/** Cron 表達式簡單描述 */
function describeCron(cron: string): string {
  const cronMap: Record<string, string> = {
    '0 0 19 * * ?': '每日 19:00',
    '0 0 1 1 1 ?': '每年 1/1 01:00',
    '0 30 8 * * ?': '每日 08:30',
    '0 0 2 1 * ?': '每月 1 日 02:00',
  };
  return cronMap[cron] || cron;
}

export class SystemViewModelFactory {

  /** 系統參數 DTO → ViewModel */
  static createParameterViewModel(dto: SystemParameterDto): SystemParameterViewModel {
    return {
      paramCode: dto.paramCode,
      paramName: dto.paramName,
      paramValue: dto.paramValue,
      paramType: dto.paramType,
      module: dto.module,
      moduleLabel: MODULE_LABELS[dto.module] ?? dto.module,
      category: dto.category,
      categoryLabel: CATEGORY_LABELS[dto.category] ?? dto.category,
      categoryColor: CATEGORY_COLORS[dto.category] ?? 'default',
      description: dto.description,
      defaultValue: dto.defaultValue,
      isEncrypted: dto.encrypted,
      isModified: dto.paramValue !== dto.defaultValue,
      updatedAtDisplay: formatDateTime(dto.updatedAt),
      updatedBy: dto.updatedBy ?? '-',
    };
  }

  /** 批量轉換系統參數 */
  static createParameterList(dtos: SystemParameterDto[]): SystemParameterViewModel[] {
    return dtos.map(dto => this.createParameterViewModel(dto));
  }

  /** 功能開關 DTO → ViewModel */
  static createToggleViewModel(dto: FeatureToggleDto): FeatureToggleViewModel {
    return {
      featureCode: dto.featureCode,
      featureName: dto.featureName,
      module: dto.module,
      moduleLabel: MODULE_LABELS[dto.module] ?? dto.module,
      enabled: dto.enabled,
      description: dto.description,
      updatedAtDisplay: formatDateTime(dto.updatedAt),
      updatedBy: dto.updatedBy ?? '-',
    };
  }

  /** 批量轉換功能開關 */
  static createToggleList(dtos: FeatureToggleDto[]): FeatureToggleViewModel[] {
    return dtos.map(dto => this.createToggleViewModel(dto));
  }

  /** 排程任務 DTO → ViewModel */
  static createJobViewModel(dto: ScheduledJobConfigDto): ScheduledJobViewModel {
    const statusInfo = dto.lastExecutionStatus
      ? STATUS_MAP[dto.lastExecutionStatus] ?? { label: dto.lastExecutionStatus, color: 'default' }
      : { label: '-', color: 'default' };

    return {
      jobCode: dto.jobCode,
      jobName: dto.jobName,
      module: dto.module,
      moduleLabel: MODULE_LABELS[dto.module] ?? dto.module,
      cronExpression: dto.cronExpression,
      cronDescription: describeCron(dto.cronExpression),
      enabled: dto.enabled,
      description: dto.description,
      lastExecutedAtDisplay: formatDateTime(dto.lastExecutedAt),
      lastExecutionStatus: dto.lastExecutionStatus,
      statusLabel: statusInfo.label,
      statusColor: statusInfo.color,
      lastErrorMessage: dto.lastErrorMessage,
      consecutiveFailures: dto.consecutiveFailures,
      needsAlert: dto.consecutiveFailures >= 3,
      updatedAtDisplay: formatDateTime(dto.updatedAt),
      updatedBy: dto.updatedBy ?? '-',
    };
  }

  /** 批量轉換排程任務 */
  static createJobList(dtos: ScheduledJobConfigDto[]): ScheduledJobViewModel[] {
    return dtos.map(dto => this.createJobViewModel(dto));
  }
}
