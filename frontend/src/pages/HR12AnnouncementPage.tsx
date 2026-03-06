import React from 'react';
import { Typography } from 'antd';
import { NotificationOutlined } from '@ant-design/icons';
import { AnnouncementManager } from '../features/notification/components';

const { Title, Text } = Typography;

/**
 * HR12AnnouncementPage - 公告管理頁面
 * Feature: notification
 * Page Code: HR12-P04
 */
export const HR12AnnouncementPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>
          <NotificationOutlined /> 公告管理
        </Title>
        <Text type="secondary">發布與管理系統公告</Text>
      </div>

      <AnnouncementManager />
    </div>
  );
};
