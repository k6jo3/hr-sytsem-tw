import React from 'react';
import { Button, Card, Space, Table, Tag, Typography } from 'antd';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';

const { Title } = Typography;

/**
 * EmployeeList Props
 */
export interface EmployeeListProps {
  employees: EmployeeViewModel[];
  loading: boolean;
  total: number;
  onRefresh: () => void;
  onAdd: () => void;
  onPageChange?: (page: number, pageSize: number) => void;
}

/**
 * EmployeeList Component
 * 員工列表元件
 */
export const EmployeeList: React.FC<EmployeeListProps> = ({
  employees,
  loading,
  total,
  onRefresh,
  onAdd,
  onPageChange,
}) => {
  const columns: ColumnsType<EmployeeViewModel> = [
    {
      title: '員工編號',
      dataIndex: 'employeeNumber',
      key: 'employeeNumber',
      width: 120,
    },
    {
      title: '姓名',
      dataIndex: 'fullName',
      key: 'fullName',
      width: 120,
    },
    {
      title: '部門',
      dataIndex: 'departmentName',
      key: 'departmentName',
      width: 150,
    },
    {
      title: '職位',
      dataIndex: 'position',
      key: 'position',
      width: 150,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text: string, record: EmployeeViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '到職日',
      dataIndex: 'hireDate',
      key: 'hireDate',
      width: 120,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      ellipsis: true,
    },
  ];

  const handleTableChange = (pagination: TablePaginationConfig) => {
    if (onPageChange && pagination.current && pagination.pageSize) {
      onPageChange(pagination.current, pagination.pageSize);
    }
  };

  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 16,
        }}
      >
        <Title level={4} style={{ margin: 0 }}>
          員工列表
        </Title>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={onRefresh}>
            重新整理
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={onAdd}>
            新增員工
          </Button>
        </Space>
      </div>
      <Card>
        <Table
          rowKey="id"
          dataSource={employees}
          columns={columns}
          loading={loading}
          pagination={{
            total,
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 筆`,
          }}
          onChange={handleTableChange}
        />
      </Card>
    </div>
  );
};
