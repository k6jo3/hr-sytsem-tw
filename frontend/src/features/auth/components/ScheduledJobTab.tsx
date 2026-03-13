/**
 * 排程管理 Tab
 * Domain Code: HR01
 */

import {
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  PauseCircleOutlined,
  PlayCircleOutlined,
} from '@ant-design/icons';
import { Badge, Button, Modal, Popconfirm, Space, Table, Tag, Tooltip, Typography, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import type { ScheduledJobViewModel } from '../model/SystemViewModel';

const { Text, Paragraph } = Typography;

interface ScheduledJobTabProps {
  jobs: ScheduledJobViewModel[];
  loading: boolean;
  onRefresh: () => Promise<void>;
  onToggleEnabled: (jobCode: string, cronExpression: string, enabled: boolean) => Promise<void>;
}

export const ScheduledJobTab: React.FC<ScheduledJobTabProps> = ({
  jobs, loading, onRefresh, onToggleEnabled,
}) => {
  const [toggling, setToggling] = useState<string | null>(null);

  /** 切換排程啟用/停用 */
  const handleToggle = async (record: ScheduledJobViewModel) => {
    setToggling(record.jobCode);
    try {
      await onToggleEnabled(record.jobCode, record.cronExpression, !record.enabled);
      message.success(`排程「${record.jobName}」已${record.enabled ? '停用' : '啟用'}`);
    } catch {
      message.error(`操作失敗`);
    } finally {
      setToggling(null);
    }
  };

  /** 顯示錯誤詳情 Modal */
  const showErrorDetail = (record: ScheduledJobViewModel) => {
    Modal.error({
      title: `排程「${record.jobName}」錯誤詳情`,
      width: 600,
      content: (
        <div>
          <Paragraph>
            <Text strong>連續失敗次數：</Text>
            <Text type="danger">{record.consecutiveFailures} 次</Text>
          </Paragraph>
          <Paragraph>
            <Text strong>最近執行狀態：</Text>
            <Tag color={record.statusColor}>{record.statusLabel}</Tag>
          </Paragraph>
          <Paragraph>
            <Text strong>錯誤訊息：</Text>
          </Paragraph>
          <Paragraph>
            <Text code style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
              {record.lastErrorMessage || '無錯誤訊息'}
            </Text>
          </Paragraph>
        </div>
      ),
    });
  };

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
      width: 200,
      render: (_: unknown, record: ScheduledJobViewModel) => (
        <div>
          <div>{record.lastExecutedAtDisplay}</div>
          {record.lastExecutionStatus && (
            <Tag color={record.statusColor}>{record.statusLabel}</Tag>
          )}
          {record.needsAlert && (
            <Tooltip title={`連續失敗 ${record.consecutiveFailures} 次，點擊查看詳情`}>
              <Tag
                color="error"
                style={{ cursor: 'pointer' }}
                onClick={() => showErrorDetail(record)}
              >
                <ExclamationCircleOutlined /> 需關注
              </Tag>
            </Tooltip>
          )}
          {record.lastErrorMessage && !record.needsAlert && (
            <Tag
              color="warning"
              style={{ cursor: 'pointer' }}
              onClick={() => showErrorDetail(record)}
            >
              查看錯誤
            </Tag>
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
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: unknown, record: ScheduledJobViewModel) => (
        <Space>
          <Popconfirm
            title={`確定要${record.enabled ? '停用' : '啟用'}「${record.jobName}」排程？`}
            description={record.enabled ? '停用後排程將不再自動執行' : '啟用後排程將依 Cron 設定自動執行'}
            onConfirm={() => handleToggle(record)}
            okText="確定"
            cancelText="取消"
          >
            <Button
              type="link"
              size="small"
              loading={toggling === record.jobCode}
              icon={record.enabled ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
              danger={record.enabled}
            >
              {record.enabled ? '停用' : '啟用'}
            </Button>
          </Popconfirm>
          {(record.lastErrorMessage || record.consecutiveFailures > 0) && (
            <Button
              type="link"
              size="small"
              icon={<CheckCircleOutlined />}
              onClick={() => showErrorDetail(record)}
            >
              詳情
            </Button>
          )}
        </Space>
      ),
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
