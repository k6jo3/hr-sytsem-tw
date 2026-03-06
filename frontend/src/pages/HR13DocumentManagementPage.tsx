/**
 * HR13DocumentManagementPage - 文件管理（管理員）
 * Feature: document
 * Page Code: HR13-P01
 */

import React from 'react';
import { Typography, Space } from 'antd';
import { SettingOutlined } from '@ant-design/icons';
import { DocumentAdminTable } from '@features/document/components';

const { Title, Text } = Typography;

export const HR13DocumentManagementPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>
              <SettingOutlined /> 文件管理
            </Title>
            <Text type="secondary">管理所有員工的文件資料</Text>
          </div>
        </Space>
      </div>

      <DocumentAdminTable />
    </div>
  );
};
