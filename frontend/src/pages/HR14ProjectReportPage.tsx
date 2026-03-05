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
} from 'antd';
import {
  ProjectOutlined,
  FieldTimeOutlined,
  DollarOutlined,
  ReloadOutlined,
  BarChartOutlined,
} from '@ant-design/icons';
import { useDashboard } from '@features/report/hooks';
import type { ReportPeriod } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;

/**
 * HR14ProjectReportPage - 專案管理報表
 * Feature: report
 * Page Code: HR14-P03
 */
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
                  title="總員工數"
                  value={dashboard.kpis.totalEmployees}
                  prefix={<ProjectOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="本月加班時數"
                  value={dashboard.kpis.overtimeHoursThisMonth}
                  suffix="小時"
                  prefix={<FieldTimeOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="出勤率"
                  value={dashboard.kpis.attendanceRateDisplay}
                  prefix={<BarChartOutlined />}
                />
              </Card>
            </Col>
            <Col xs={12} md={6}>
              <Card>
                <Statistic
                  title="培訓完成率"
                  value={dashboard.kpis.trainingCompletionDisplay}
                  prefix={<DollarOutlined />}
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]}>
            {/* 部門人力配置 */}
            <Col xs={24} lg={12}>
              <Card title={<Space><BarChartOutlined /><span>部門人力配置</span></Space>}>
                {dashboard.departmentDistribution.length > 0 ? (
                  <List
                    size="small"
                    dataSource={dashboard.departmentDistribution}
                    renderItem={(item) => (
                      <List.Item>
                        <div style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <Text>{item.departmentName}</Text>
                            <Tag color="blue">{item.employeeCount}人</Tag>
                          </div>
                          <Progress
                            percent={item.percentage * 100}
                            showInfo={false}
                            strokeColor="#667eea"
                          />
                        </div>
                      </List.Item>
                    )}
                  />
                ) : (
                  <Empty description="無資料" />
                )}
              </Card>
            </Col>

            {/* 薪資結構分析 */}
            <Col xs={24} lg={12}>
              <Card title={<Space><DollarOutlined /><span>薪資結構分析</span></Space>}>
                {dashboard.salaryDistribution.length > 0 ? (
                  <List
                    size="small"
                    dataSource={dashboard.salaryDistribution}
                    renderItem={(item) => (
                      <List.Item>
                        <div style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <Text>{item.range}</Text>
                            <Text type="secondary">{item.count}人 ({item.percentageDisplay})</Text>
                          </div>
                          <Progress
                            percent={item.percentage * 100}
                            showInfo={false}
                            strokeColor="#52c41a"
                          />
                        </div>
                      </List.Item>
                    )}
                  />
                ) : (
                  <Empty description="無薪資資料" />
                )}
              </Card>
            </Col>
          </Row>

          {/* 出勤趨勢 */}
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={24}>
              <Card title={<Space><FieldTimeOutlined /><span>出勤趨勢</span></Space>}>
                {dashboard.attendanceStats.length > 0 ? (
                  <div style={{ height: 200, display: 'flex', alignItems: 'flex-end', gap: 8 }}>
                    {dashboard.attendanceStats.slice(0, 12).map((item, index) => {
                      const maxVal = Math.max(
                        ...dashboard.attendanceStats.map((a) => a.presentCount),
                        1
                      );
                      return (
                        <div key={index} style={{ flex: 1, textAlign: 'center' }}>
                          <div
                            style={{
                              height: `${(item.presentCount / maxVal) * 170}px`,
                              backgroundColor: '#667eea',
                              borderRadius: 4,
                              minHeight: 10,
                              marginBottom: 4,
                            }}
                          />
                          <Text style={{ fontSize: 10 }}>{item.dateLabel}</Text>
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <Empty description="無出勤趨勢資料" />
                )}
              </Card>
            </Col>
          </Row>
        </>
      )}
    </div>
  );
};
