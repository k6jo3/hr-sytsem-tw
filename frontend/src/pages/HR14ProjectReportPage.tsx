/**
 * HR14ProjectReportPage - 專案管理報表
 * Feature: report
 * Page Code: HR14-P03
 */

import React, { useState } from 'react';
import {
  Card,
  Typography,
  Row,
  Col,
  Spin,
  Empty,
  Space,
} from 'antd';
import {
  ProjectOutlined,
  BarChartOutlined,
  DollarOutlined,
  FieldTimeOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import { ReportFilterBar, ReportChart } from '@features/report/components';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;

export const HR14ProjectReportPage: React.FC = () => {
  const [period, setPeriod] = useState<ReportPeriod>('MONTHLY');
  const { data: dashboard, isLoading, refetch } = useDashboard({ period });

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>
              <ProjectOutlined /> 專案管理報表
            </Title>
            <Text type="secondary">專案成本分析、稼動率、工時分配</Text>
          </div>
          <ReportFilterBar
            period={period}
            onPeriodChange={setPeriod}
            onRefresh={() => refetch()}
            loading={isLoading}
          />
        </Space>
      </div>

      {!dashboard ? (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暫無專案報表資料" />
      ) : (
        <>
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={12}>
              <Card title={<Space><BarChartOutlined /><span>部門人力配置</span></Space>}>
                <ReportChart
                  type="pie"
                  data={dashboard.departmentDistribution.map((item) => ({
                    name: item.departmentName,
                    value: item.employeeCount,
                  }))}
                  height={280}
                  color={['#667eea', '#764ba2', '#f093fb', '#4facfe', '#00f2fe', '#43e97b']}
                />
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card title={<Space><DollarOutlined /><span>薪資結構分析</span></Space>}>
                <ReportChart
                  type="bar"
                  data={dashboard.salaryDistribution.map((item) => ({
                    name: item.range,
                    value: item.count,
                  }))}
                  height={280}
                  color="#52c41a"
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={24}>
              <Card title={<Space><FieldTimeOutlined /><span>出勤趨勢</span></Space>}>
                <ReportChart
                  type="line"
                  data={dashboard.attendanceStats.slice(0, 12).map((item) => ({
                    name: item.dateLabel,
                    value: item.presentCount,
                  }))}
                  height={280}
                  color="#667eea"
                />
              </Card>
            </Col>
          </Row>
        </>
      )}
    </div>
  );
};
