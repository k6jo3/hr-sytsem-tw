import React from 'react';
import { Typography } from 'antd';
import { MailOutlined } from '@ant-design/icons';
import { TemplateEditor } from '../features/notification/components';

const { Title, Text } = Typography;

/**
 * HR12TemplateManagementPage - 通知範本管理頁面
 * Feature: notification
 * Page Code: HR12-P01
 */
export const HR12TemplateManagementPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>
          <MailOutlined /> 通知範本管理
        </Title>
        <Text type="secondary">管理系統通知的訊息範本</Text>
      </div>

      <TemplateEditor />
    </div>
  );
};
