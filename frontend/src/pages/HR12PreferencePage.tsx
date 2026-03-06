import React from 'react';
import { Typography } from 'antd';
import { SettingOutlined } from '@ant-design/icons';
import { NotificationPreferenceForm } from '../features/notification/components';

const { Title, Text } = Typography;

/**
 * HR12PreferencePage - 通知偏好設定頁面
 * Feature: notification
 * Page Code: HR12-P03
 */
export const HR12PreferencePage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>
          <SettingOutlined /> 通知偏好設定
        </Title>
        <Text type="secondary">管理您的通知接收偏好與靜音時段</Text>
      </div>

      <NotificationPreferenceForm />
    </div>
  );
};
