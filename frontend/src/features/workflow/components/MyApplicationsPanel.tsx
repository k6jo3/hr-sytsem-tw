/**
 * 我的申請面板元件
 * Domain Code: HR11
 */

import { EyeOutlined } from '@ant-design/icons';
import { Alert, Button, Empty, Select, Space, Spin, Table, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import { useMyApplications } from '../hooks';
import type { WorkflowInstanceViewModel } from '../model/WorkflowViewModel';
import { ProcessTimeline } from './ProcessTimeline';

export const MyApplicationsPanel: React.FC = () => {
  const { applications, loading, error, refresh } = useMyApplications();
  const [statusFilter, setStatusFilter] = useState<string | undefined>(undefined);
  const [timelineInstanceId, setTimelineInstanceId] = useState<string | null>(null);

  const filteredApplications = statusFilter
    ? applications.filter((app) => app.status === statusFilter)
    : applications;

  const columns: ColumnsType<WorkflowInstanceViewModel> = [
    {
      title: '申請類型',
      dataIndex: 'businessTypeLabel',
      key: 'businessTypeLabel',
      width: 120,
      render: (text: string) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '流程名稱',
      dataIndex: 'flowName',
      key: 'flowName',
      width: 150,
    },
    {
      title: '目前節點',
      dataIndex: 'currentNodeName',
      key: 'currentNodeName',
      width: 120,
    },
    {
      title: '提交時間',
      dataIndex: 'startedAtDisplay',
      key: 'startedAtDisplay',
      width: 150,
    },
    {
      title: '完成時間',
      dataIndex: 'completedAtDisplay',
      key: 'completedAtDisplay',
      width: 150,
      render: (text: string) => text || '-',
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_: unknown, record: WorkflowInstanceViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '耗時',
      dataIndex: 'duration',
      key: 'duration',
      width: 80,
      render: (text: string) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: WorkflowInstanceViewModel) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => setTimelineInstanceId(record.instanceId)}
        >
          進度
        </Button>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <Alert message="載入失敗" description={error} type="error" showIcon action={<Button onClick={refresh}>重試</Button>} />;
  }

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Select
          placeholder="篩選狀態"
          allowClear
          style={{ width: 160 }}
          value={statusFilter}
          onChange={setStatusFilter}
          options={[
            { value: 'RUNNING', label: '審核中' },
            { value: 'COMPLETED', label: '已核准' },
            { value: 'REJECTED', label: '已駁回' },
            { value: 'CANCELLED', label: '已取消' },
          ]}
        />
      </Space>

      {filteredApplications.length === 0 ? (
        <Empty description="暫無申請記錄" style={{ padding: '50px 0' }} />
      ) : (
        <Table
          columns={columns}
          dataSource={filteredApplications}
          rowKey="instanceId"
          scroll={{ x: 'max-content' }}
          pagination={{ pageSize: 10 }}
        />
      )}

      <ProcessTimeline
        instanceId={timelineInstanceId}
        onClose={() => setTimelineInstanceId(null)}
      />
    </>
  );
};
