import { DeleteOutlined, EditOutlined, InfoCircleOutlined } from '@ant-design/icons';
import { Button, Space, Table, Tag, Tooltip } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';
import { CustomerViewModel } from '../model/CustomerViewModel';

interface CustomerListProps {
  customers: CustomerViewModel[];
  loading: boolean;
  total: number;
  page: number;
  pageSize: number;
  onPageChange: (page: number, pageSize: number) => void;
  onEdit: (customer: CustomerViewModel) => void;
  onDelete?: (id: string) => void;
}

/**
 * 客戶列表元件
 */
export const CustomerList: React.FC<CustomerListProps> = ({
  customers,
  loading,
  total,
  page,
  pageSize,
  onPageChange,
  onEdit,
  onDelete,
}) => {
  const columns: ColumnsType<CustomerViewModel> = [
    {
      title: '客戶代碼',
      dataIndex: 'customerCode',
      key: 'customerCode',
      width: 120,
    },
    {
      title: '客戶名稱',
      dataIndex: 'customerName',
      key: 'customerName',
      ellipsis: true,
    },
    {
      title: '統一編號',
      dataIndex: 'taxId',
      key: 'taxId',
      width: 120,
    },
    {
      title: '產業',
      dataIndex: 'industry',
      key: 'industry',
      width: 120,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text, record) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="編輯">
            <Button 
              type="text" 
              icon={<EditOutlined />} 
              onClick={() => onEdit(record)}
            />
          </Tooltip>
          <Tooltip title="詳情">
            <Button 
              type="text" 
              icon={<InfoCircleOutlined />} 
            />
          </Tooltip>
          {onDelete && (
            <Tooltip title="刪除">
              <Button 
                type="text" 
                danger 
                icon={<DeleteOutlined />} 
                onClick={() => onDelete(record.id)}
              />
            </Tooltip>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={customers}
      rowKey="id"
      loading={loading}
      pagination={{
        current: page,
        pageSize: pageSize,
        total: total,
        onChange: onPageChange,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 筆`,
      }}
      scroll={{ x: 1000 }}
    />
  );
};
