/**
 * HR14HRReportPage - 人力資源報表
 * Feature: report
 * Page Code: HR14-P02
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
  TeamOutlined,
  BarChartOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import { ReportFilterBar, ReportChart } from '@features/report/components';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;

export const HR14HRReportPage: React.FC = () => {
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
              <TeamOutlined /> 人力資源報表
            </Title>
            <Text type="secondary">員工結構、出勤統計、離職分析</Text>
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
        <Empty description="無資料" />
      ) : (
        <>
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={12}>
              <Card title={<Space><BarChartOutlined /><span>部門人員分佈</span></Space>}>
                <ReportChart
                  type="pie"
                  data={dashboard.departmentDistribution.map((item) => ({
                    name: item.departmentName,
                    value: item.employeeCount,
                  }))}
                  height={280}
                  color={['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2']}
                />
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card title={<Space><ClockCircleOutlined /><span>出勤統計</span></Space>}>
                <ReportChart
                  type="bar"
                  data={dashboard.attendanceStats.slice(0, 10).map((item) => ({
                    name: item.dateLabel,
                    value: item.presentCount,
                  }))}
                  height={280}
                  color="#52c41a"
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={24}>
              <Card title={<Space><BarChartOutlined /><span>人員趨勢</span></Space>}>
                <ReportChart
                  type="line"
                  data={dashboard.headcountTrend.map((item) => ({
                    name: item.monthLabel,
                    value: item.headcount,
                  }))}
                  height={280}
                  color="#1890ff"
                />
              </Card>
            </Col>
          </Row>
        </>
      )}
    </div>
  );
};
