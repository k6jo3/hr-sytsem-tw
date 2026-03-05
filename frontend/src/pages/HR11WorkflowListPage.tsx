import {
  ClockCircleOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import { Card, Space, Tabs, Typography } from 'antd';
import React, { useState } from 'react';
import { MyApplicationsPanel, PendingTasksPanel } from '../features/workflow/components';

const { Title, Text } = Typography;

/**
 * HR11-P03: 我的待辦與申請清單
 * Feature: workflow
 */
export const HR11WorkflowListPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('pending');

  const tabItems = [
    {
      key: 'pending',
      label: (
        <span>
          <ClockCircleOutlined /> 我的待辦
        </span>
      ),
      children: <PendingTasksPanel />,
    },
    {
      key: 'applications',
      label: (
        <span>
          <FileTextOutlined /> 我的申請
        </span>
      ),
      children: <MyApplicationsPanel />,
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>簽核流程</Title>
            <Text type="secondary">管理您的待辦任務和申請記錄</Text>
          </div>
        </Space>
      </div>

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
      </Card>
    </div>
  );
};
