import React from 'react';
import { Card, Typography } from 'antd';

const { Title } = Typography;

/**
 * HR04PayslipPage - 薪資單頁
 * Feature: payroll
 */
export const HR04PayslipPage: React.FC = () => {
  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Title level={2}>薪資單頁</Title>
        <p>TODO: 實作 薪資單頁 功能</p>
      </Card>
    </div>
  );
};
