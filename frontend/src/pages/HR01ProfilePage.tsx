import { UserOutlined } from '@ant-design/icons';
import { useAppSelector } from '@store/hooks';
import { Avatar, Card, Descriptions, Spin, Tag, Typography } from 'antd';
import React from 'react';

const { Title } = Typography;

/**
 * 個人資料頁面 (HR01-P03)
 * 顯示目前登入使用者的基本資訊
 */
export const HR01ProfilePage: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);

  if (!user) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 800, margin: '0 auto' }}>
      <Title level={3}>個人資料</Title>
      <Card>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
          <Avatar
            size={64}
            icon={<UserOutlined />}
            src={user.avatarUrl}
            style={{ backgroundColor: '#667eea', marginRight: 16 }}
          />
          <div>
            <Title level={4} style={{ margin: 0 }}>{user.displayName}</Title>
            <span style={{ color: '#999' }}>{user.email}</span>
          </div>
        </div>
        <Descriptions bordered column={1}>
          <Descriptions.Item label="使用者帳號">{user.username}</Descriptions.Item>
          <Descriptions.Item label="電子信箱">{user.email}</Descriptions.Item>
          <Descriptions.Item label="顯示名稱">{user.displayName}</Descriptions.Item>
          <Descriptions.Item label="帳號狀態">
            <Tag color={user.statusColor}>{user.statusLabel}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="角色">
            {user.roles.map((role) => (
              <Tag key={role} color="blue">{role}</Tag>
            ))}
          </Descriptions.Item>
          {user.lastLoginAtDisplay && (
            <Descriptions.Item label="上次登入">{user.lastLoginAtDisplay}</Descriptions.Item>
          )}
        </Descriptions>
      </Card>
    </div>
  );
};

export default HR01ProfilePage;
