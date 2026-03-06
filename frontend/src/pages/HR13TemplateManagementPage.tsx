/**
 * HR13TemplateManagementPage - 文件範本管理
 * Feature: document
 * Page Code: HR13-P02
 */

import React from 'react';
import { Typography } from 'antd';
import { FileAddOutlined } from '@ant-design/icons';
import { DocumentTemplateManager } from '@features/document/components';

const { Title, Text } = Typography;

export const HR13TemplateManagementPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <div>
          <Title level={2} style={{ margin: 0 }}>
            <FileAddOutlined /> 文件範本管理
          </Title>
          <Text type="secondary">管理系統文件範本與變數設定</Text>
        </div>
      </div>

      <DocumentTemplateManager />
    </div>
  );
};
