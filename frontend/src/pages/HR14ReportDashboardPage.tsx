/**
 * HR14ReportDashboardPage - 報表儀表板頁面
 * Domain Code: HR14
 * Feature: report
 */

import React, { useState } from 'react';
import {
  Card,
  Typography,
  Tabs,
  Select,
  Spin,
  Empty,
  Space,
  Button,
  Row,
  Col,
} from 'antd';
import {
  DashboardOutlined,
  LineChartOutlined,
  PieChartOutlined,
  BarChartOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import { DashboardKPICards, ReportChart, ReportCatalog, ReportDataTable } from '@features/report/components';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;

// ========== Dashboard Tab ==========

const DashboardTab: React.FC = () => {
  const [period, setPeriod] = useState<ReportPeriod>('MONTHLY');
  const { data: dashboard, isLoading, refetch } = useDashboard({ period });

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: 48 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!dashboard) {
    return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暫無報表資料" />;
  }

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Select value={period} onChange={setPeriod} style={{ width: 120 }}>
          <Option value="DAILY">每日</Option>
          <Option value="WEEKLY">每週</Option>
          <Option value="MONTHLY">每月</Option>
          <Option value="QUARTERLY">每季</Option>
          <Option value="YEARLY">每年</Option>
        </Select>
        <Button icon={<ReloadOutlined />} onClick={() => refetch()}>
          重新整理
        </Button>
      </div>

      <DashboardKPICards kpis={dashboard.kpis} />

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <LineChartOutlined />
                <span>人員趨勢</span>
              </Space>
            }
          >
            <ReportChart
              type="bar"
              data={dashboard.headcountTrend.map((item) => ({
                name: item.monthLabel,
                value: item.headcount,
              }))}
              height={200}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <PieChartOutlined />
                <span>部門分佈</span>
              </Space>
            }
          >
            <ReportChart
              type="pie"
              data={dashboard.departmentDistribution.map((item) => ({
                name: item.departmentName,
                value: item.employeeCount,
              }))}
              height={200}
              color={['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2']}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                <span>出勤統計</span>
              </Space>
            }
          >
            <ReportChart
              type="bar"
              data={dashboard.attendanceStats.slice(0, 7).map((item) => ({
                name: item.dateLabel,
                value: item.presentCount,
              }))}
              height={200}
              color="#52c41a"
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                <span>薪資分佈</span>
              </Space>
            }
          >
            <ReportChart
              type="bar"
              data={dashboard.salaryDistribution.map((item) => ({
                name: item.range,
                value: item.count,
              }))}
              height={200}
              color="#faad14"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

// ========== Main Page Component ==========

export const HR14ReportDashboardPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('dashboard');

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Title level={2}>
          <DashboardOutlined style={{ marginRight: 12 }} />
          報表分析
        </Title>

        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="儀表板" key="dashboard">
            <DashboardTab />
          </TabPane>
          <TabPane tab="報表目錄" key="catalog">
            <ReportCatalog />
          </TabPane>
          <TabPane tab="產生記錄" key="history">
            <ReportDataTable />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
