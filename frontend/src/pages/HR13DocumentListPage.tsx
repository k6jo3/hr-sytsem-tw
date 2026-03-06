/**
 * HR13DocumentListPage - 文件管理頁面
 * Domain Code: HR13
 * Feature: document
 */

import React, { useState } from 'react';
import { Card, Typography, Tabs } from 'antd';
import { FileTextOutlined } from '@ant-design/icons';
import { DocumentListPanel, DocumentRequestPanel } from '@features/document/components';

const { Title } = Typography;

export const HR13DocumentListPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('documents');

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Title level={2}>
          <FileTextOutlined style={{ marginRight: 12 }} />
          文件管理
        </Title>

        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            { key: 'documents', label: '我的文件', children: <DocumentListPanel /> },
            { key: 'request', label: '申請文件', children: <DocumentRequestPanel /> },
          ]}
        />
      </Card>
    </div>
  );
};
