/**
 * Role Card Component (角色卡片元件)
 * Domain Code: HR01
 */

import React from 'react';
import { Card, Tag, Typography, Space, Badge } from 'antd';
import { EditOutlined, UserOutlined } from '@ant-design/icons';
import type { RoleDto } from '../api/AuthTypes';

const { Text, Paragraph } = Typography;

export interface RoleCardProps {
  role: RoleDto;
  selected: boolean;
  onClick: (role: RoleDto) => void;
  onEdit: (role: RoleDto) => void;
}

/**
 * 角色卡片元件
 */
export const RoleCard: React.FC<RoleCardProps> = ({
  role,
  selected,
  onClick,
  onEdit,
}) => {
  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit(role);
  };

  return (
    <Card
      hoverable
      size="small"
      onClick={() => onClick(role)}
      style={{
        marginBottom: 8,
        borderColor: selected ? '#1890ff' : undefined,
        backgroundColor: selected ? '#e6f7ff' : undefined,
      }}
      actions={[
        <EditOutlined key="edit" onClick={handleEdit} />,
      ]}
    >
      <Space direction="vertical" size="small" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Text strong>{role.role_name}</Text>
          <Space size={4}>
            {role.is_system && <Tag color="purple">系統</Tag>}
            <Tag color={role.is_active ? 'green' : 'default'}>
              {role.is_active ? '啟用' : '停用'}
            </Tag>
          </Space>
        </div>

        {role.description && (
          <Paragraph
            type="secondary"
            ellipsis={{ rows: 2 }}
            style={{ marginBottom: 0, fontSize: 12 }}
          >
            {role.description}
          </Paragraph>
        )}

        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <Badge
            count={role.user_count}
            showZero
            style={{ backgroundColor: '#1890ff' }}
          />
          <Text type="secondary" style={{ fontSize: 12 }}>
            <UserOutlined /> {role.user_count} 位使用者
          </Text>
        </div>
      </Space>
    </Card>
  );
};
