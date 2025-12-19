import React from 'react';
import { Tag } from 'antd';
import type { ActiveStatus, ApprovalStatus, EmployeeStatus } from '../types';

/**
 * 狀態標籤配置
 */
interface StatusConfig {
  color: string;
  text: string;
}

/**
 * 啟用狀態配置
 */
const ACTIVE_STATUS_CONFIG: Record<ActiveStatus, StatusConfig> = {
  ACTIVE: { color: 'green', text: '啟用' },
  INACTIVE: { color: 'default', text: '停用' },
};

/**
 * 審核狀態配置
 */
const APPROVAL_STATUS_CONFIG: Record<ApprovalStatus, StatusConfig> = {
  PENDING: { color: 'processing', text: '待審核' },
  APPROVED: { color: 'success', text: '已核准' },
  REJECTED: { color: 'error', text: '已駁回' },
  CANCELLED: { color: 'default', text: '已取消' },
};

/**
 * 員工狀態配置
 */
const EMPLOYEE_STATUS_CONFIG: Record<EmployeeStatus, StatusConfig> = {
  ACTIVE: { color: 'green', text: '在職' },
  RESIGNED: { color: 'default', text: '離職' },
  ON_LEAVE: { color: 'orange', text: '留職停薪' },
  TERMINATED: { color: 'red', text: '終止' },
};

/**
 * 狀態標籤 Props
 */
export interface StatusTagProps {
  /** 狀態類型 */
  type: 'active' | 'approval' | 'employee';
  /** 狀態值 */
  status: ActiveStatus | ApprovalStatus | EmployeeStatus;
}

/**
 * 狀態標籤元件
 * 根據不同狀態類型顯示對應的標籤樣式
 *
 * @example
 * ```tsx
 * <StatusTag type="active" status="ACTIVE" />
 * <StatusTag type="approval" status="PENDING" />
 * <StatusTag type="employee" status="RESIGNED" />
 * ```
 */
export const StatusTag: React.FC<StatusTagProps> = ({ type, status }) => {
  let config: StatusConfig | undefined;

  switch (type) {
    case 'active':
      config = ACTIVE_STATUS_CONFIG[status as ActiveStatus];
      break;
    case 'approval':
      config = APPROVAL_STATUS_CONFIG[status as ApprovalStatus];
      break;
    case 'employee':
      config = EMPLOYEE_STATUS_CONFIG[status as EmployeeStatus];
      break;
  }

  if (!config) {
    return <Tag>{status}</Tag>;
  }

  return <Tag color={config.color}>{config.text}</Tag>;
};

export default StatusTag;
