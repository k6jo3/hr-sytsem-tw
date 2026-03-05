/**
 * 流程定義管理列表元件
 * Domain Code: HR11
 */

import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Empty, Space, Spin, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';
import { useWorkflowDefinitions } from '../hooks';
import type { WorkflowDefinitionViewModel } from '../model/WorkflowViewModel';

const { Title, Text } = Typography;

interface WorkflowDefinitionListProps {
  onDesign?: (definitionId: string) => void;
  onCreate?: () => void;
}

export const WorkflowDefinitionList: React.FC<WorkflowDefinitionListProps> = ({
  onDesign,
  onCreate,
}) => {
  const { definitions, loading, error, refresh } = useWorkflowDefinitions();

  const columns: ColumnsType<WorkflowDefinitionViewModel> = [
    {
      title: '流程名稱',
      dataIndex: 'flowName',
      key: 'flowName',
      width: 200,
    },
    {
      title: '流程類型',
      dataIndex: 'flowTypeLabel',
      key: 'flowTypeLabel',
      width: 120,
      render: (text: string) => <Tag color="purple">{text}</Tag>,
    },
    {
      title: '節點數',
      dataIndex: 'nodeCount',
      key: 'nodeCount',
      width: 80,
      align: 'center',
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 80,
      align: 'center',
      render: (v: number) => `v${v}`,
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_: unknown, record: WorkflowDefinitionViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '建立時間',
      dataIndex: 'createdAtDisplay',
      key: 'createdAtDisplay',
      width: 150,
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: unknown, record: WorkflowDefinitionViewModel) => (
        <Button
          type="link"
          size="small"
          onClick={() => onDesign?.(record.definitionId)}
        >
          編輯設計
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
    <Card>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Title level={4} style={{ margin: 0 }}>流程定義管理</Title>
          <Text type="secondary">管理所有簽核流程的定義與設計</Text>
        </div>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={refresh}>重新整理</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={onCreate}>新增流程</Button>
        </Space>
      </div>

      {definitions.length === 0 ? (
        <Empty description="目前沒有流程定義" />
      ) : (
        <Table
          columns={columns}
          dataSource={definitions}
          rowKey="definitionId"
          pagination={{ pageSize: 10 }}
        />
      )}
    </Card>
  );
};
