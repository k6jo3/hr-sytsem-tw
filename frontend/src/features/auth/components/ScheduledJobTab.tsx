/**
 * 排程管理 Tab
 * Domain Code: HR01
 */

import { Badge, Button, Table, Tag, Tooltip, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';
import type { ScheduledJobViewModel } from '../model/SystemViewModel';

const { Text } = Typography;

interface ScheduledJobTabProps {
  jobs: ScheduledJobViewModel[];
  loading: boolean;
  onRefresh: () => Promise<void>;
}

export const ScheduledJobTab: React.FC<ScheduledJobTabProps> = ({
  jobs, loading, onRefresh,
}) => {

  const columns: ColumnsType<ScheduledJobViewModel> = [
    {
      title: '任務代碼',
      dataIndex: 'jobCode',
      key: 'jobCode',
      width: 200,
      render: (text: string) => <Text code>{text}</Text>,
    },
    {
      title: '任務名稱',
      dataIndex: 'jobName',
      key: 'jobName',
      width: 150,
    },
    {
      title: '模組',
      dataIndex: 'moduleLabel',
      key: 'moduleLabel',
      width: 100,
    },
    {
      title: '排程',
      key: 'cron',
      width: 200,
      render: (_: unknown, record: ScheduledJobViewModel) => (
        <Tooltip title={record.cronExpression}>
          <Text>{record.cronDescription}</Text>
        </Tooltip>
      ),
    },
    {
      title: '狀態',
      key: 'enabled',
      width: 80,
      render: (_: unknown, record: ScheduledJobViewModel) => (
        <Badge
          status={record.enabled ? 'success' : 'default'}
          text={record.enabled ? '啟用' : '停用'}
        />
      ),
    },
    {
      title: '最近執行',
      key: 'lastExecution',
      width: 180,
      render: (_: unknown, record: ScheduledJobViewModel) => (
        <div>
          <div>{record.lastExecutedAtDisplay}</div>
          {record.lastExecutionStatus && (
            <Tag color={record.statusColor}>{record.statusLabel}</Tag>
          )}
          {record.needsAlert && (
            <Tooltip title={`連續失敗 ${record.consecutiveFailures} 次`}>
              <Tag color="error">需關注</Tag>
            </Tooltip>
          )}
        </div>
      ),
    },
    {
      title: '說明',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, textAlign: 'right' }}>
        <Button size="small" onClick={onRefresh}>重新整理</Button>
      </div>
      <Table
        columns={columns}
        dataSource={jobs}
        rowKey="jobCode"
        pagination={false}
        size="small"
        loading={loading}
      />
    </div>
  );
};
