import {
    BarChartOutlined,
    ExclamationCircleOutlined,
    FieldTimeOutlined,
    PieChartOutlined,
    ProjectOutlined,
    ReloadOutlined,
    TeamOutlined
} from '@ant-design/icons';
import {
    Alert,
    Button,
    Card,
    Col,
    DatePicker,
    Empty,
    Layout,
    List,
    Progress,
    Row,
    Space,
    Statistic,
    Table,
    Tag,
    Typography
} from 'antd';
import dayjs, { Dayjs } from 'dayjs';
import React, { useState } from 'react';
import { useTimesheetReport } from '../features/timesheet/hooks/useTimesheetReport';

const { Content } = Layout;
const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

/**
 * HR07-P03: 工時統計報表頁面
 */
export const HR07TimesheetReportPage: React.FC = () => {
  const [dates, setDates] = useState<[Dayjs, Dayjs]>([
    dayjs().startOf('month'),
    dayjs().endOf('month')
  ]);

  const startDateStr = dates[0].format('YYYY-MM-DD');
  const endDateStr = dates[1].format('YYYY-MM-DD');

  const { summary, loading, error, refresh } = useTimesheetReport(startDateStr, endDateStr);

  const handleDateChange = (val: any) => {
    if (val && val[0] && val[1]) {
      setDates([val[0], val[1]]);
    }
  };

  const getUnreportedColumns = () => [
    { title: '姓名', dataIndex: 'name', key: 'name' },
    {
      title: '狀態',
      key: 'status',
      render: () => <Tag color="warning">未回報</Tag>
    },
  ];

  if (error) {
    return (
      <Content style={{ padding: 24 }}>
        <Alert message="載入失敗" description={error} type="error" showIcon />
      </Content>
    );
  }

  return (
    <Content style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space align="baseline">
            <BarChartOutlined style={{ fontSize: 24, color: '#1890ff' }} />
            <Title level={2}>工時統計報表</Title>
          </Space>
          <Space>
            <RangePicker 
              value={dates} 
              onChange={handleDateChange} 
              allowClear={false}
            />
            <Button icon={<ReloadOutlined />} onClick={refresh}>重新整理</Button>
          </Space>
        </div>

        <Row gutter={[16, 16]}>
          <Col xs={24} sm={8}>
            <Card bordered={false} className="stat-card">
              <Statistic
                title="總報工時數"
                value={summary?.total_hours || 0}
                precision={1}
                prefix={<FieldTimeOutlined />}
                suffix="h"
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card bordered={false} className="stat-card">
              <Statistic
                title="參與專案數"
                value={summary?.project_hours.length || 0}
                prefix={<ProjectOutlined />}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card bordered={false} className="stat-card">
              <Statistic
                title="未回報人數"
                value={summary?.unreported_employees.length || 0}
                prefix={<TeamOutlined />}
                valueStyle={{ color: (summary?.unreported_employees.length || 0) > 0 ? '#f5222d' : '#8c8c8c' }}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]}>
          <Col xs={24} lg={12}>
            <Card 
              title={<Space><ProjectOutlined /><span>專案工時分佈</span></Space>} 
              loading={loading}
              className="chart-card"
            >
              {summary && summary.project_hours.length > 0 ? (
                <List
                  dataSource={summary.project_hours}
                  renderItem={(item) => {
                    const percent = (item.hours / summary.total_hours) * 100;
                    return (
                      <List.Item>
                        <div style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <Text strong>{item.project_name}</Text>
                            <Text type="secondary">{item.hours}h ({percent.toFixed(1)}%)</Text>
                          </div>
                          <Progress percent={percent} showInfo={false} strokeColor="#1890ff" />
                        </div>
                      </List.Item>
                    );
                  }}
                />
              ) : (
                <Empty description="選擇區間內無專案工時" />
              )}
            </Card>
          </Col>
          <Col xs={24} lg={12}>
            <Card 
              title={<Space><PieChartOutlined /><span>部門佔比</span></Space>} 
              loading={loading}
              className="chart-card"
            >
              {summary && summary.department_hours.length > 0 ? (
                <List
                  dataSource={summary.department_hours}
                  renderItem={(item) => {
                    const percent = (item.hours / summary.total_hours) * 100;
                    return (
                      <List.Item>
                        <div style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <Text>{item.department_name}</Text>
                            <Text type="secondary">{item.hours}h</Text>
                          </div>
                          <Progress percent={percent} showInfo={false} strokeColor="#722ed1" />
                        </div>
                      </List.Item>
                    );
                  }}
                />
              ) : (
                <Empty description="選擇區間內無部門工時" />
              )}
            </Card>
          </Col>
        </Row>

        <Card 
          title={<Space><ExclamationCircleOutlined /><span>未回報清單</span></Space>}
          loading={loading}
        >
          <Table 
            dataSource={summary?.unreported_employees || []} 
            columns={getUnreportedColumns()} 
            rowKey="id"
            size="small"
            pagination={{ pageSize: 5 }}
            locale={{ emptyText: '本區間全員已完成報工' }}
          />
        </Card>
      </Space>

      <style dangerouslySetInnerHTML={{ __html: `
        .stat-card {
          box-shadow: 0 4px 12px rgba(0,0,0,0.05);
          border-radius: 8px;
          transition: transform 0.3s;
        }
        .stat-card:hover {
          transform: translateY(-4px);
        }
        .chart-card {
          border-radius: 8px;
          height: 100%;
        }
        .ant-statistic-title {
          font-size: 14px;
          color: #8c8c8c;
        }
      `}} />
    </Content>
  );
};

export default HR07TimesheetReportPage;
