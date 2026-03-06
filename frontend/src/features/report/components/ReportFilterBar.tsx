/**
 * 報表篩選列元件
 * Domain Code: HR14
 * 期間選擇 + 重新整理按鈕
 */

import React from 'react';
import { Select, Button, Space } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import type { ReportPeriod } from '../api/ReportTypes';

interface ReportFilterBarProps {
  period: ReportPeriod;
  onPeriodChange: (period: ReportPeriod) => void;
  onRefresh: () => void;
  loading?: boolean;
}

export const ReportFilterBar: React.FC<ReportFilterBarProps> = ({
  period,
  onPeriodChange,
  onRefresh,
  loading,
}) => {
  return (
    <Space>
      <Select value={period} onChange={onPeriodChange} style={{ width: 120 }}>
        <Select.Option value="MONTHLY">每月</Select.Option>
        <Select.Option value="QUARTERLY">每季</Select.Option>
        <Select.Option value="YEARLY">每年</Select.Option>
      </Select>
      <Button icon={<ReloadOutlined />} onClick={onRefresh} loading={loading}>
        重新整理
      </Button>
    </Space>
  );
};
