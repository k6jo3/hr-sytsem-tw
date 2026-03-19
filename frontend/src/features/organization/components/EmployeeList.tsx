import { Button, Card, Space, Table, Tag } from 'antd';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';


/**
 * EmployeeList Props
 */
export interface EmployeeListProps {
  employees: EmployeeViewModel[];
  loading: boolean;
  total: number;
  currentPage?: number;
  pageSize?: number;
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
  currentPage,
  pageSize: pageSizeProp,
  onRefresh,
  onAdd,
  onPageChange,
}) => {
  const navigate = useNavigate();

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
      <Card
        title="員工列表"
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={onRefresh}>
              重新整理
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={onAdd}>
              新增員工
            </Button>
          </Space>
        }
      >
        <Table
          rowKey="id"
          dataSource={employees}
          columns={columns}
          loading={loading}
          pagination={{
            total,
            current: currentPage,
            pageSize: pageSizeProp ?? 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 筆`,
          }}
          onChange={handleTableChange}
          onRow={(record) => ({
            onClick: () => {
              navigate(`/admin/employees/${record.id}`);
            },
            style: { cursor: 'pointer' },
          })}
        />
      </Card>
    </div>
  );
};
