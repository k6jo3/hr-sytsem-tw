import { Progress, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';
import type { TaskViewModel } from '../model/ProjectViewModel';

const { Text } = Typography;

interface ProjectWbsTreeProps {
  tasks: TaskViewModel[];
  loading?: boolean;
}

/**
 * 專案 WBS 工項樹狀組件
 */
export const ProjectWbsTree: React.FC<ProjectWbsTreeProps> = ({ tasks, loading }) => {
  const columns: ColumnsType<TaskViewModel> = [
    {
      title: '工項名稱',
      dataIndex: 'taskName',
      key: 'taskName',
      render: (text: string, record: TaskViewModel) => (
        <span>
          <Text strong style={{ marginRight: 8 }}>{record.taskCode}</Text>
          {text}
        </span>
      ),
    },
    {
      title: '預估工時',
      dataIndex: 'estimatedHours',
      key: 'estimatedHours',
      width: 100,
      align: 'right',
      render: (val: number) => `${val}h`,
    },
    {
      title: '已投工時',
      dataIndex: 'actualHours',
      key: 'actualHours',
      width: 100,
      align: 'right',
      render: (val: number) => `${val}h`,
    },
    {
      title: '進度',
      dataIndex: 'progress',
      key: 'progress',
      width: 180,
      render: (val: number) => (
        <Progress percent={val} size="small" />
      ),
    },
    {
      title: '負責人',
      dataIndex: 'assigneeName',
      key: 'assigneeName',
      width: 120,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      width: 100,
      render: (text: string, record: TaskViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={tasks}
      rowKey="id"
      loading={loading}
      pagination={false}
      expandable={{ defaultExpandAllRows: true }}
      size="small"
      scroll={{ x: 800 }}
    />
  );
};
