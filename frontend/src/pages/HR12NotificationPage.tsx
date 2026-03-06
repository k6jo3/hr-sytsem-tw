import React from 'react';
import { Typography, Badge } from 'antd';
import { BellOutlined } from '@ant-design/icons';
import { NotificationList } from '../features/notification/components';
import { useMyNotifications } from '../features/notification/hooks';

const { Title, Text } = Typography;

/**
 * HR12NotificationPage - 我的通知頁面
 * Feature: notification
 * Page Code: HR12-P02
 */
export const HR12NotificationPage: React.FC = () => {
  const { unreadCount } = useMyNotifications();

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>
          <BellOutlined /> 我的通知
          {unreadCount > 0 && (
            <Badge count={unreadCount} style={{ marginLeft: 8 }} />
          )}
        </Title>
        <Text type="secondary">查看和管理您的通知訊息</Text>
      </div>

      <NotificationList />
    </div>
  );
};
