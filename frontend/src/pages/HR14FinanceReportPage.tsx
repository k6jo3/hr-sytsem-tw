import React, { useState } from 'react';
import {
  Card,
  Typography,
  Row,
  Col,
  Statistic,
  Spin,
  Empty,
  Space,
  Button,
  Select,
  Progress,
  List,
  Tag,
  Table,
} from 'antd';
import {
  DollarOutlined,
  BankOutlined,
  BarChartOutlined,
  ReloadOutlined,
  PieChartOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;

/**
 * HR14FinanceReportPage - 財務報表
 * Feature: report
 * Page Code: HR14-P04
 */
export const HR14FinanceReportPage: React.FC = () => {
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
              <DollarOutlined /> 財務報表
            </Title>
            <Text type="secondary">人力成本分析、薪資結構、部門成本比較</Text>
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
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="在職人數"
                  value={dashboard.kpis.activeEmployees}
                  prefix={<BankOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="本月新進"
                  value={dashboard.kpis.newHiresThisMonth}
                  valueStyle={{ color: '#3f8600' }}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="加班時數"
                  value={dashboard.kpis.overtimeHoursThisMonth}
                  suffix="小時"
                  prefix={<BarChartOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="離職率"
                  value={dashboard.kpis.turnoverRateDisplay}
                  prefix={<DollarOutlined />}
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]}>
            {/* 薪資結構分析 */}
            <Col xs={24} lg={12}>
              <Card title={<Space><PieChartOutlined /><span>薪資結構分析</span></Space>}>
                {dashboard.salaryDistribution.length > 0 ? (
                  <Table
                    size="small"
                    dataSource={dashboard.salaryDistribution}
                    rowKey="range"
                    pagination={false}
                    columns={[
                      { title: '薪資範圍', dataIndex: 'range', key: 'range' },
                      {
                        title: '人數',
                        dataIndex: 'count',
                        key: 'count',
                        width: 80,
                        render: (v: number) => <Tag color="blue">{v}人</Tag>,
                      },
                      {
                        title: '占比',
                        dataIndex: 'percentageDisplay',
                        key: 'percentageDisplay',
                        width: 80,
                      },
                      {
                        title: '分佈',
                        key: 'bar',
                        width: 200,
                        render: (_, record: any) => (
                          <Progress
                            percent={record.percentage * 100}
                            showInfo={false}
                            strokeColor="#52c41a"
                          />
                        ),
                      },
                    ]}
                  />
                ) : (
                  <Empty description="無薪資結構資料" />
                )}
              </Card>
            </Col>

            {/* 部門人力成本 */}
            <Col xs={24} lg={12}>
              <Card title={<Space><BankOutlined /><span>部門人力配置</span></Space>}>
                {dashboard.departmentDistribution.length > 0 ? (
                  <List
                    size="small"
                    dataSource={dashboard.departmentDistribution}
                    renderItem={(item) => (
                      <List.Item
                        extra={<Tag color="green">{item.employeeCount}人</Tag>}
                      >
                        <List.Item.Meta
                          title={item.departmentName}
                          description={
                            <Progress
                              percent={item.percentage * 100}
                              size="small"
                              strokeColor="#667eea"
                            />
                          }
                        />
                      </List.Item>
                    )}
                  />
                ) : (
                  <Empty description="無部門資料" />
                )}
              </Card>
            </Col>
          </Row>

          {/* 出勤成本相關 */}
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={24}>
              <Card title={<Space><BarChartOutlined /><span>出勤統計（成本關聯）</span></Space>}>
                {dashboard.attendanceStats.length > 0 ? (
                  <Table
                    size="small"
                    dataSource={dashboard.attendanceStats.slice(0, 10)}
                    rowKey="dateLabel"
                    pagination={false}
                    columns={[
                      { title: '日期/員工', dataIndex: 'dateLabel', key: 'dateLabel' },
                      {
                        title: '出勤天數',
                        dataIndex: 'presentCount',
                        key: 'presentCount',
                        width: 100,
                        render: (v: number) => <Tag color="green">{v}</Tag>,
                      },
                      {
                        title: '缺勤天數',
                        dataIndex: 'absentCount',
                        key: 'absentCount',
                        width: 100,
                        render: (v: number) => v > 0 ? <Tag color="red">{v}</Tag> : <Tag>{v}</Tag>,
                      },
                      {
                        title: '遲到次數',
                        dataIndex: 'lateCount',
                        key: 'lateCount',
                        width: 100,
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
        </>
      )}
    </div>
  );
};
