/**
 * User Table Component (使用者表格元件)
 * Domain Code: HR01
 */

import React from 'react';
import {
  Table,
  Space,
  Button,
  Avatar,
  Tag,
  Dropdown,
  Popconfirm,
  Tooltip,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  EditOutlined,
  LockOutlined,
  UnlockOutlined,
  DeleteOutlined,
  KeyOutlined,
  MoreOutlined,
  UserOutlined,
  StopOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';
import type { UserViewModel } from '../hooks/useUsers';

export interface UserTableProps {
  users: UserViewModel[];
  loading: boolean;
  selectedRowKeys: React.Key[];
  onSelectionChange: (keys: React.Key[]) => void;
  onEdit: (user: UserViewModel) => void;
  onDeactivate: (userId: string) => void;
  onActivate: (userId: string) => void;
  onUnlock: (userId: string) => void;
  onDelete: (userId: string) => void;
  onResetPassword: (userId: string) => void;
}

/**
 * 使用者表格元件
 */
export const UserTable: React.FC<UserTableProps> = ({
  users,
  loading,
  selectedRowKeys,
  onSelectionChange,
  onEdit,
  onDeactivate,
  onActivate,
  onUnlock,
  onDelete,
  onResetPassword,
}) => {
  const getStatusColor = (status: string): string => {
    const colors: Record<string, string> = {
      ACTIVE: 'green',
      INACTIVE: 'default',
      LOCKED: 'red',
      DELETED: 'default',
    };
    return colors[status] || 'default';
  };

  const getStatusLabel = (status: string): string => {
    const labels: Record<string, string> = {
      ACTIVE: '啟用',
      INACTIVE: '停用',
      LOCKED: '鎖定',
      DELETED: '已刪除',
    };
    return labels[status] || status;
  };

  const columns: ColumnsType<UserViewModel> = [
    {
      title: '使用者',
      key: 'user',
      width: 250,
      render: (_, record) => (
        <Space>
          <Avatar
            src={record.avatarUrl}
            icon={!record.avatarUrl && <UserOutlined />}
            size={40}
          />
          <div>
            <div style={{ fontWeight: 500 }}>{record.displayName}</div>
            <div style={{ fontSize: 12, color: '#8c8c8c' }}>{record.email}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '使用者名稱',
      dataIndex: 'username',
      key: 'username',
      width: 150,
    },
    {
      title: '角色',
      key: 'roles',
      width: 200,
      render: (_, record) => (
        <Space wrap>
          {record.roles.slice(0, 3).map((role) => (
            <Tag key={role.id} color="blue">
              {role.name}
            </Tag>
          ))}
          {record.roles.length > 3 && (
            <Tooltip title={record.roles.slice(3).map(r => r.name).join(', ')}>
              <Tag>+{record.roles.length - 3}</Tag>
            </Tooltip>
          )}
        </Space>
      ),
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_, record) => (
        <Tag color={getStatusColor(record.status)}>
          {getStatusLabel(record.status)}
        </Tag>
      ),
    },
    {
      title: '最後登入',
      dataIndex: 'lastLoginDisplay',
      key: 'lastLoginAt',
      width: 150,
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => {
        const menuItems = [
          {
            key: 'resetPassword',
            icon: <KeyOutlined />,
            label: '重置密碼',
            onClick: () => onResetPassword(record.id),
          },
        ];

        if (record.status === 'LOCKED') {
          menuItems.push({
            key: 'unlock',
            icon: <UnlockOutlined />,
            label: '解鎖帳號',
            onClick: () => onUnlock(record.id),
          });
        }

        if (record.status === 'ACTIVE') {
          menuItems.push({
            key: 'deactivate',
            icon: <StopOutlined />,
            label: '停用',
            onClick: () => onDeactivate(record.id),
          });
        }

        if (record.status === 'INACTIVE') {
          menuItems.push({
            key: 'activate',
            icon: <CheckCircleOutlined />,
            label: '啟用',
            onClick: () => onActivate(record.id),
          });
        }

        menuItems.push({
          key: 'delete',
          icon: <DeleteOutlined />,
          label: '刪除',
          danger: true,
          onClick: () => onDelete(record.id),
        } as typeof menuItems[0]);

        return (
          <Space size="small">
            <Tooltip title="編輯">
              <Button
                type="text"
                size="small"
                icon={<EditOutlined />}
                onClick={() => onEdit(record)}
              />
            </Tooltip>
            <Dropdown
              menu={{ items: menuItems }}
              trigger={['click']}
            >
              <Button
                type="text"
                size="small"
                icon={<MoreOutlined />}
              />
            </Dropdown>
          </Space>
        );
      },
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectionChange,
    getCheckboxProps: (record: UserViewModel) => ({
      disabled: record.status === 'DELETED',
    }),
  };

  return (
    <Table<UserViewModel>
      columns={columns}
      dataSource={users}
      rowKey="id"
      rowSelection={rowSelection}
      loading={loading}
      pagination={false}
      scroll={{ x: 1000 }}
      size="middle"
    />
  );
};
