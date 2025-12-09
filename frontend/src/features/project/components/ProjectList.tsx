import React from 'react';
import { Table, Tag, Progress, Space, Button } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ProjectViewModel } from '../model/ProjectViewModel';

export interface ProjectListProps {
  projects: ProjectViewModel[];
  loading?: boolean;
  total: number;
  page: number;
  pageSize: number;
  onPageChange: (page: number, pageSize: number) => void;
  onAdd?: () => void;
  onRefresh?: () => void;
  onRowClick?: (project: ProjectViewModel) => void;
}

/**
 * 專案列表組件
 */
export const ProjectList: React.FC<ProjectListProps> = ({
  projects,
  loading = false,
  total,
  page,
  pageSize,
  onPageChange,
  onAdd,
  onRefresh,
  onRowClick,
}) => {
  const columns: ColumnsType<ProjectViewModel> = [
    {
      title: '專案代碼',
      dataIndex: 'projectCode',
      key: 'projectCode',
      width: 150,
      fixed: 'left',
    },
    {
      title: '專案名稱',
      dataIndex: 'projectName',
      key: 'projectName',
      width: 250,
    },
    {
      title: '客戶',
      dataIndex: 'customerName',
      key: 'customerName',
      width: 200,
    },
    {
      title: '類型',
      dataIndex: 'projectTypeLabel',
      key: 'projectType',
      width: 100,
      render: (_: string, record: ProjectViewModel) => (
        <Tag color={record.projectTypeColor}>{record.projectTypeLabel}</Tag>
      ),
    },
    {
      title: '預算',
      dataIndex: 'budgetAmountDisplay',
      key: 'budget',
      width: 120,
      align: 'right',
    },
    {
      title: '實際成本',
      dataIndex: 'actualCostDisplay',
      key: 'actualCost',
      width: 120,
      align: 'right',
      render: (_: string, record: ProjectViewModel) => (
        <span style={{ color: record.isOverBudget ? '#ff4d4f' : undefined }}>
          {record.actualCostDisplay}
        </span>
      ),
    },
    {
      title: '成本使用率',
      dataIndex: 'costUtilizationDisplay',
      key: 'costUtilization',
      width: 120,
      align: 'right',
      render: (_: string, record: ProjectViewModel) => (
        <span style={{ color: record.isOverBudget ? '#ff4d4f' : undefined }}>
          {record.costUtilizationDisplay}
        </span>
      ),
    },
    {
      title: '進度',
      dataIndex: 'progress',
      key: 'progress',
      width: 150,
      render: (_: number, record: ProjectViewModel) => (
        <Progress percent={record.progress} size="small" />
      ),
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      width: 100,
      render: (_: string, record: ProjectViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Space>
          {onAdd && (
            <Button type="primary" icon={<PlusOutlined />} onClick={onAdd}>
              新增專案
            </Button>
          )}
        </Space>
        <Space>
          {onRefresh && (
            <Button icon={<ReloadOutlined />} onClick={onRefresh}>
              重新整理
            </Button>
          )}
        </Space>
      </div>

      <Table
        dataSource={projects}
        columns={columns}
        loading={loading}
        rowKey="id"
        pagination={{
          current: page,
          pageSize,
          total,
          onChange: onPageChange,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 個專案`,
        }}
        onRow={(record) => ({
          onClick: () => onRowClick?.(record),
          style: { cursor: onRowClick ? 'pointer' : 'default' },
        })}
        scroll={{ x: 1200 }}
      />
    </div>
  );
};
