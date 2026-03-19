import { DownloadOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Col, DatePicker, Form, message, Row, Select, Space, Statistic, Table, Typography } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { AttendanceReportApi } from '../features/attendance/api/AttendanceReportApi';
import type { GetMonthlyReportResponse } from '../features/attendance/api/AttendanceTypes';

const { Title, Text } = Typography;
const { Option } = Select;

export const HR03AttendanceReportPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [reportData, setReportData] = useState<GetMonthlyReportResponse | null>(null);
  const [form] = Form.useForm();

  const handleSearch = async (values: any) => {
    setLoading(true);
    try {
      const date = values.month || dayjs();
      const res = await AttendanceReportApi.getMonthlyReport({
        year: date.year(),
        month: date.month() + 1,
        departmentId: values.departmentId
      });
      // 確保回應結構完整，避免缺少 summary 時崩潰
      if (res && res.summary) {
        setReportData(res);
      } else {
        setReportData({
          year: date.year(),
          month: date.month() + 1,
          summary: { totalEmployees: 0, averageAttendanceRate: 0, totalLateCount: 0, totalOvertimeHours: 0 },
          items: Array.isArray(res?.items) ? res.items : [],
        } as GetMonthlyReportResponse);
      }
    } catch (error: any) {
      const status = error?.response?.status;
      if (status === 404) {
        // 無報表資料不算錯誤，顯示空狀態
        setReportData(null);
      } else {
        console.error('[AttendanceReport] 載入報表失敗:', error);
        message.error('載入報表失敗，請檢查網路連線或稍後再試');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handleSearch({ month: dayjs() });
  }, []);

  const columns = [
    { title: '工號', dataIndex: 'employeeNumber', key: 'employeeNumber' },
    { title: '姓名', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '部門', dataIndex: 'departmentName', key: 'departmentName' },
    { title: '應出勤', dataIndex: 'scheduledDays', key: 'scheduledDays', render: (val: any) => `${val}天` },
    { title: '實出勤', dataIndex: 'actualDays', key: 'actualDays', render: (val: any) => <Text type="success">{val}天</Text> },
    { title: '遲到', dataIndex: 'lateCount', key: 'lateCount', render: (val: any) => val > 0 ? <Text type="danger">{val}次</Text> : val },
    { title: '早退', dataIndex: 'earlyLeaveCount', key: 'earlyLeaveCount', render: (val: any) => val > 0 ? <Text type="danger">{val}次</Text> : val },
    { title: '請假', dataIndex: 'leaveDays', key: 'leaveDays', render: (val: any) => `${val}天` },
    { title: '加班', dataIndex: 'overtimeHours', key: 'overtimeHours', render: (val: any) => `${val}h` },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>部門差勤報表</Title>
      
      <Card style={{ marginBottom: 24 }}>
        <Form form={form} layout="inline" onFinish={handleSearch} initialValues={{ month: dayjs() }}>
          <Form.Item name="month" label="選擇月份">
            <DatePicker picker="month" />
          </Form.Item>
          <Form.Item name="departmentId" label="部門">
            <Select placeholder="所有部門" style={{ width: 150 }} allowClear>
              <Option value="dept-001">開發部</Option>
              <Option value="dept-002">人事部</Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" icon={<SearchOutlined />} htmlType="submit" loading={loading}>
                查詢
              </Button>
              <Button icon={<DownloadOutlined />}>匯出 Excel</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>

      {reportData && (
        <>
          <Row gutter={16} style={{ marginBottom: 24 }}>
            <Col span={6}>
              <Card><Statistic title="總員工人數" value={reportData.summary.totalEmployees} /></Card>
            </Col>
            <Col span={6}>
              <Card><Statistic title="平均出勤率" value={reportData.summary.averageAttendanceRate * 100} precision={1} suffix="%" /></Card>
            </Col>
            <Col span={6}>
              <Card><Statistic title="總遲到次數" value={reportData.summary.totalLateCount} valueStyle={{ color: '#cf1322' }} /></Card>
            </Col>
            <Col span={6}>
              <Card><Statistic title="總加班時數" value={reportData.summary.totalOvertimeHours} suffix="h" /></Card>
            </Col>
          </Row>

          <Card title={`報表詳情 - ${reportData.year}/${reportData.month}`}>
            <Table
              columns={columns}
              dataSource={reportData.items}
              rowKey="employeeId"
              loading={loading}
              scroll={{ x: 'max-content' }}
              pagination={{ pageSize: 20 }}
              locale={{ emptyText: '該月份暫無差勤報表資料' }}
            />
          </Card>
        </>
      )}
    </div>
  );
};

export default HR03AttendanceReportPage;
