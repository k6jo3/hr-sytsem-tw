/**
 * HR14ReportDashboardPage - 報表儀表板頁面
 * Domain Code: HR14
 * Feature: report
 */

import React, { useState } from 'react';
import {
  Card,
  Typography,
  Row,
  Col,
  Statistic,
  Tabs,
  Table,
  Tag,
  Button,
  Space,
  Select,
  Spin,
  Empty,
  Progress,
  Tooltip,
  List,
  Modal,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  DashboardOutlined,
  TeamOutlined,
  ClockCircleOutlined,
  RiseOutlined,
  FallOutlined,
  FileTextOutlined,
  DownloadOutlined,
  DeleteOutlined,
  PlusOutlined,
  ReloadOutlined,
  BarChartOutlined,
  PieChartOutlined,
  LineChartOutlined,
  CalendarOutlined,
  SyncOutlined,
} from '@ant-design/icons';
import {
  useDashboard,
  useReportDefinitions,
  useReports,
  useGenerateReport,
  useDownloadReport,
  useDeleteReport,
} from '@features/report/hooks';
import type {
  DashboardKpiViewModel,
  ReportDefinitionViewModel,
  ReportViewModel,
} from '@features/report/model/ReportViewModel';
import type { ReportPeriod, ReportFormat } from '@features/report/api/ReportTypes';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;
const { confirm } = Modal;

// ========== KPI Cards Component ==========

interface KpiCardsProps {
  kpis: DashboardKpiViewModel;
}

const KpiCards: React.FC<KpiCardsProps> = ({ kpis }) => {
  return (
    <Row gutter={[16, 16]}>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic
            title="總員工數"
            value={kpis.totalEmployees}
            prefix={<TeamOutlined />}
          />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic
            title="在職人數"
            value={kpis.activeEmployees}
            valueStyle={{ color: '#3f8600' }}
          />
        </Card>
      </Col>
      <Col xs={12} sm={8} md={6} lg={4}>
        <Card>
          <Statistic
            title="本月新進"
            value={kpis.newHiresThisMonth}
            prefix={<RiseOutlined />}
            valueStyle={{ color: '#3f8600' }}
          />
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
          <Statistic
            title="出勤率"
            value={kpis.attendanceRateDisplay}
            prefix={<ClockCircleOutlined />}
          />
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
    return <Empty description="無資料" />;
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

      <KpiCards kpis={dashboard.kpis} />

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
            {dashboard.headcountTrend.length > 0 ? (
              <div style={{ height: 200, display: 'flex', alignItems: 'flex-end', gap: 8 }}>
                {dashboard.headcountTrend.map((item) => (
                  <Tooltip
                    key={item.month}
                    title={`${item.monthLabel}: ${item.headcount}人`}
                  >
                    <div
                      style={{
                        flex: 1,
                        height: `${(item.headcount / Math.max(...dashboard.headcountTrend.map(t => t.headcount))) * 180}px`,
                        backgroundColor: '#1890ff',
                        borderRadius: 4,
                        minHeight: 20,
                      }}
                    />
                  </Tooltip>
                ))}
              </div>
            ) : (
              <Empty description="無資料" />
            )}
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
            {dashboard.departmentDistribution.length > 0 ? (
              <List
                size="small"
                dataSource={dashboard.departmentDistribution}
                renderItem={(item) => (
                  <List.Item>
                    <div style={{ width: '100%' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                        <Text>{item.departmentName}</Text>
                        <Text type="secondary">{item.employeeCount}人 ({item.percentageDisplay})</Text>
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
              <Empty description="無資料" />
            )}
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
            {dashboard.attendanceStats.length > 0 ? (
              <List
                size="small"
                dataSource={dashboard.attendanceStats.slice(0, 7)}
                renderItem={(item) => (
                  <List.Item>
                    <Text>{item.dateLabel}</Text>
                    <Space>
                      <Tag color="green">出勤 {item.presentCount}</Tag>
                      <Tag color="red">缺勤 {item.absentCount}</Tag>
                      <Tag color="orange">遲到 {item.lateCount}</Tag>
                      <Text type="secondary">{item.attendanceRateDisplay}</Text>
                    </Space>
                  </List.Item>
                )}
              />
            ) : (
              <Empty description="無資料" />
            )}
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
              <Empty description="無資料" />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

// ========== Report Catalog Tab ==========

const ReportCatalogTab: React.FC = () => {
  const { data, isLoading } = useReportDefinitions();
  const { mutate: generateReport, isPending: isGenerating } = useGenerateReport();

  const handleGenerate = (definition: ReportDefinitionViewModel) => {
    const format = definition.availableFormats[0] || 'PDF';
    generateReport({
      report_definition_id: definition.definitionId,
      format: format as ReportFormat,
      parameters: {},
    });
  };

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: 48 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <Text type="secondary" style={{ display: 'block', marginBottom: 24 }}>
        選擇報表類型，快速產生所需報表
      </Text>

      {data && data.definitions.length > 0 ? (
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4 }}
          dataSource={data.definitions}
          renderItem={(item) => (
            <List.Item>
              <Card
                hoverable
                onClick={() => handleGenerate(item)}
              >
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: 32, marginBottom: 12 }}>
                    <FileTextOutlined />
                  </div>
                  <Title level={5} style={{ marginBottom: 4 }}>
                    {item.reportName}
                  </Title>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    {item.description}
                  </Text>
                  <div style={{ marginTop: 8 }}>
                    <Text type="secondary" style={{ fontSize: 11 }}>
                      格式: {item.availableFormatsDisplay}
                    </Text>
                  </div>
                </div>
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暫無可用報表" />
      )}
    </div>
  );
};

// ========== Report History Tab ==========

const ReportHistoryTab: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1);

  const { data, isLoading, refetch } = useReports({
    page: currentPage,
    pageSize: 10,
  });

  const { mutate: downloadReport } = useDownloadReport();
  const { mutate: deleteReport, isPending: isDeleting } = useDeleteReport();

  const handleDelete = (report: ReportViewModel) => {
    confirm({
      title: '確認刪除',
      content: `確定要刪除報表「${report.reportName}」嗎？`,
      okText: '確認',
      cancelText: '取消',
      okType: 'danger',
      onOk: () => {
        deleteReport(report.reportId);
      },
    });
  };

  const columns: ColumnsType<ReportViewModel> = [
    {
      title: '報表名稱',
      dataIndex: 'reportName',
      key: 'reportName',
      render: (text, record) => (
        <Space>
          <FileTextOutlined />
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: '類型',
      dataIndex: 'reportTypeLabel',
      key: 'reportTypeLabel',
      width: 150,
    },
    {
      title: '格式',
      dataIndex: 'formatLabel',
      key: 'formatLabel',
      width: 80,
      render: (text) => <Tag>{text}</Tag>,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text, record) => (
        <Tag
          icon={record.isProcessing ? <SyncOutlined spin /> : undefined}
          color={record.statusColor}
        >
          {text}
        </Tag>
      ),
    },
    {
      title: '產生時間',
      dataIndex: 'generatedAtDisplay',
      key: 'generatedAtDisplay',
      width: 150,
      render: (text) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          {record.canDownload && (
            <Tooltip title="下載">
              <Button
                type="primary"
                size="small"
                icon={<DownloadOutlined />}
                onClick={() => downloadReport(record.reportId)}
              >
                下載
              </Button>
            </Tooltip>
          )}
          <Tooltip title="刪除">
            <Button
              type="text"
              size="small"
              danger
              icon={<DeleteOutlined />}
              loading={isDeleting}
              onClick={() => handleDelete(record)}
            />
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button icon={<ReloadOutlined />} onClick={() => refetch()}>
          重新整理
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data?.reports}
        rowKey="reportId"
        loading={isLoading}
        pagination={{
          current: currentPage,
          pageSize: 10,
          total: data?.pagination.total,
          showSizeChanger: false,
          showTotal: (total) => `共 ${total} 筆`,
          onChange: (page) => setCurrentPage(page),
        }}
      />
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
            <ReportCatalogTab />
          </TabPane>
          <TabPane tab="產生記錄" key="history">
            <ReportHistoryTab />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
