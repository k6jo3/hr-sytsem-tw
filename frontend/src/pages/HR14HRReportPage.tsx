import React, { useState } from 'react';
import {
  Card,
  Typography,
  Row,
  Col,
  Table,
  Tag,
  Select,
  Statistic,
  Spin,
  Empty,
  Space,
  Button,
  Progress,
  List,
} from 'antd';
import {
  TeamOutlined,
  UserOutlined,
  ClockCircleOutlined,
  BarChartOutlined,
  ReloadOutlined,
  FallOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;

/**
 * HR14HRReportPage - 人力資源報表
 * Feature: report
 * Page Code: HR14-P02
 */
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
          <Space>
            <Select value={period} onChange={setPeriod} style={{ width: 120 }}>
              <Select.Option value="MONTHLY">每月</Select.Option>
              <Select.Option value="QUARTERLY">每季</Select.Option>
              <Select.Option value="YEARLY">每年</Select.Option>
            </Select>
            <Button icon={<ReloadOutlined />} onClick={() => refetch()}>
              重新整理
            </Button>
          </Space>
        </Space>
      </div>

      {!dashboard ? (
        <Empty description="無資料" />
      ) : (
        <>
          {/* KPI 卡片 */}
          <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
            <Col xs={12} sm={8} md={6}>
              <Card>
                <Statistic
                  title="總員工數"
                  value={dashboard.kpis.totalEmployees}
                  prefix={<TeamOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6}>
              <Card>
                <Statistic
                  title="在職人數"
                  value={dashboard.kpis.activeEmployees}
                  valueStyle={{ color: '#3f8600' }}
                  prefix={<UserOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6}>
              <Card>
                <Statistic
                  title="出勤率"
                  value={dashboard.kpis.attendanceRateDisplay}
                  prefix={<ClockCircleOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6}>
              <Card>
                <Statistic
                  title="離職率"
                  value={dashboard.kpis.turnoverRateDisplay}
                  prefix={<FallOutlined />}
                  valueStyle={{ color: dashboard.kpis.turnoverRate > 0.1 ? '#cf1322' : undefined }}
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]}>
            {/* 部門人員分佈 */}
            <Col xs={24} lg={12}>
              <Card
                title={<Space><BarChartOutlined /><span>部門人員分佈</span></Space>}
              >
                {dashboard.departmentDistribution.length > 0 ? (
                  <List
                    size="small"
                    dataSource={dashboard.departmentDistribution}
                    renderItem={(item) => (
                      <List.Item>
                        <div style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <Text>{item.departmentName}</Text>
                            <Text type="secondary">
                              {item.employeeCount}人 ({item.percentageDisplay})
                            </Text>
                          </div>
                          <Progress
                            percent={item.percentage * 100}
                            showInfo={false}
                            strokeColor="#1890ff"
                          />
                        </div>
                      </List.Item>
                    )}
                  />
                ) : (
                  <Empty description="無部門資料" />
                )}
              </Card>
            </Col>

            {/* 出勤統計 */}
            <Col xs={24} lg={12}>
              <Card
                title={<Space><ClockCircleOutlined /><span>出勤統計</span></Space>}
              >
                {dashboard.attendanceStats.length > 0 ? (
                  <Table
                    size="small"
                    dataSource={dashboard.attendanceStats.slice(0, 10)}
                    rowKey="dateLabel"
                    pagination={false}
                    columns={[
                      { title: '日期/員工', dataIndex: 'dateLabel', key: 'dateLabel' },
                      {
                        title: '出勤',
                        dataIndex: 'presentCount',
                        key: 'presentCount',
                        width: 80,
                        render: (v: number) => <Tag color="green">{v}</Tag>,
                      },
                      {
                        title: '缺勤',
                        dataIndex: 'absentCount',
                        key: 'absentCount',
                        width: 80,
                        render: (v: number) => v > 0 ? <Tag color="red">{v}</Tag> : <Tag>{v}</Tag>,
                      },
                      {
                        title: '遲到',
                        dataIndex: 'lateCount',
                        key: 'lateCount',
                        width: 80,
                        render: (v: number) => v > 0 ? <Tag color="orange">{v}</Tag> : <Tag>{v}</Tag>,
                      },
                      {
                        title: '出勤率',
                        dataIndex: 'attendanceRateDisplay',
                        key: 'attendanceRateDisplay',
                        width: 100,
                      },
                    ]}
                  />
                ) : (
                  <Empty description="無出勤資料" />
                )}
              </Card>
            </Col>
          </Row>

          {/* 人員趨勢 */}
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={24}>
              <Card
                title={<Space><BarChartOutlined /><span>人員趨勢</span></Space>}
              >
                {dashboard.headcountTrend.length > 0 ? (
                  <div style={{ height: 220, display: 'flex', alignItems: 'flex-end', gap: 4, padding: '0 16px' }}>
                    {dashboard.headcountTrend.map((item) => {
                      const maxVal = Math.max(...dashboard.headcountTrend.map((t) => t.headcount), 1);
                      return (
                        <div key={item.month} style={{ flex: 1, textAlign: 'center' }}>
                          <div
                            style={{
                              height: `${(item.headcount / maxVal) * 180}px`,
                              backgroundColor: '#1890ff',
                              borderRadius: 4,
                              minHeight: 20,
                              marginBottom: 8,
                            }}
                          />
                          <Text style={{ fontSize: 11 }}>{item.monthLabel}</Text>
                          <br />
                          <Text type="secondary" style={{ fontSize: 10 }}>{item.headcount}</Text>
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <Empty description="無趨勢資料" />
                )}
              </Card>
            </Col>
          </Row>
        </>
      )}
    </div>
  );
};
