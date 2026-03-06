/**
 * 儀表板 KPI 統計卡片元件
 * Domain Code: HR14
 */

import React from 'react';
import { Card, Col, Row, Statistic } from 'antd';
import {
  TeamOutlined,
  RiseOutlined,
  FallOutlined,
  ClockCircleOutlined,
  CalendarOutlined,
} from '@ant-design/icons';
import type { DashboardKpiViewModel } from '../model/ReportViewModel';

interface DashboardKPICardsProps {
  kpis: DashboardKpiViewModel;
}

export const DashboardKPICards: React.FC<DashboardKPICardsProps> = ({ kpis }) => {
  return (
    <Row gutter={[16, 16]}>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic title="總員工數" value={kpis.totalEmployees} prefix={<TeamOutlined />} />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic title="在職人數" value={kpis.activeEmployees} valueStyle={{ color: '#3f8600' }} />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic title="本月新進" value={kpis.newHiresThisMonth} prefix={<RiseOutlined />} valueStyle={{ color: '#3f8600' }} />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic
            title="離職率"
            value={kpis.turnoverRateDisplay}
            prefix={<FallOutlined />}
            valueStyle={{ color: kpis.turnoverRate > 0.1 ? '#cf1322' : undefined }}
          />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic title="出勤率" value={kpis.attendanceRateDisplay} prefix={<ClockCircleOutlined />} />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic
            title="待簽假單"
            value={kpis.pendingLeaveRequests}
            prefix={<CalendarOutlined />}
            valueStyle={{ color: kpis.pendingLeaveRequests > 0 ? '#faad14' : undefined }}
          />
        </Card>
      </Col>
    </Row>
  );
};
