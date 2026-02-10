import { CalendarOutlined, LeftOutlined, PlusOutlined, RightOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, DatePicker, Layout, Row, Space, Typography } from 'antd';
import dayjs, { Dayjs } from 'dayjs';
import weekOfYear from 'dayjs/plugin/weekOfYear';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { TimesheetEntryForm, WeeklyTimesheetView } from '../features/timesheet/components';
import { TimesheetAuditResult } from '../features/timesheet/components/TimesheetAuditResult';
import { useTimesheet } from '../features/timesheet/hooks/useTimesheet';
import { useTimesheetAudit } from '../features/timesheet/hooks/useTimesheetAudit';
import type { TimesheetEntryViewModel } from '../features/timesheet/model/TimesheetViewModel';
import { RootState } from '../store';

dayjs.extend(weekOfYear);

const { Content } = Layout;
const { Title, Text } = Typography;

/**
 * HR07-P01: 我的工時回報頁面
 */
export const HR07TimesheetPage: React.FC = () => {
  const [currentDate, setCurrentDate] = useState<Dayjs>(dayjs());
  const [formVisible, setFormVisible] = useState(false);
  const [editingEntry, setEditingEntry] = useState<Partial<TimesheetEntryViewModel> | undefined>(undefined);

  const currentUser = useSelector((state: RootState) => state.auth.user);
  const employeeId = currentUser?.employeeId || '';

  // 取得該週的開始日期 (週一)
  const weekStart = currentDate.startOf('week').add(1, 'day');
  const weekStartDateStr = weekStart.format('YYYY-MM-DD');

  const {
    summary,
    loading,
    error,
    handleSaveEntry,
    handleDeleteEntry,
    handleSubmit,
  } = useTimesheet(weekStartDateStr);

  const {
    auditSummary,
    loading: auditLoading,
    calculateAudit
  } = useTimesheetAudit();

  // 當工時資料更新時，觸發勾稽驗證
  useEffect(() => {
    if (summary && employeeId) {
      calculateAudit(summary, employeeId);
    }
  }, [summary, employeeId, calculateAudit]);

  const handlePrevWeek = () => setCurrentDate(currentDate.subtract(1, 'week'));
  const handleNextWeek = () => setCurrentDate(currentDate.add(1, 'week'));
  const handleCurrentWeek = () => setCurrentDate(dayjs());

  const handleAddClick = () => {
    setEditingEntry(undefined);
    setFormVisible(true);
  };

  const handleEditClick = (record: TimesheetEntryViewModel) => {
    setEditingEntry(record);
    setFormVisible(true);
  };

  return (
    <Content style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space align="baseline">
            <CalendarOutlined style={{ fontSize: 24, color: '#1890ff' }} />
            <Title level={2}>我的工時回報</Title>
          </Space>
          <Space>
            <Button icon={<PlusOutlined />} type="primary" onClick={handleAddClick}>
              新增工時
            </Button>
          </Space>
        </div>

        {error && <Alert message="錯誤" description={error} type="error" showIcon />}

        <Card size="small">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Space>
              <Button icon={<LeftOutlined />} onClick={handlePrevWeek} />
              <Button onClick={handleCurrentWeek}>本週</Button>
              <Button icon={<RightOutlined />} onClick={handleNextWeek} />
              <DatePicker 
                picker="week" 
                value={currentDate} 
                onChange={(date) => date && setCurrentDate(date)}
                allowClear={false}
              />
              <Text strong style={{ fontSize: 16, marginLeft: 8 }}>
                {weekStart.format('YYYY/MM/DD')} - {weekStart.add(6, 'day').format('YYYY/MM/DD')}
              </Text>
            </Space>
            {summary && (
              <Space>
                <Text type="secondary">狀態:</Text>
                <span style={{ color: summary.statusColor === 'processing' ? '#1890ff' : 'inherit' }}>
                  {summary.statusLabel}
                </span>
              </Space>
            )}
          </div>
        </Card>

        <Row gutter={[24, 24]}>
          <Col lg={16} xl={18}>
            <WeeklyTimesheetView 
              summary={summary}
              loading={loading}
              onSubmit={handleSubmit}
              onEdit={handleEditClick}
              onDelete={handleDeleteEntry}
            />
          </Col>
          <Col lg={8} xl={6}>
            <TimesheetAuditResult 
              summary={auditSummary}
              loading={auditLoading}
            />
          </Col>
        </Row>

        <TimesheetEntryForm
          visible={formVisible}
          initialValues={editingEntry}
          onCancel={() => setFormVisible(false)}
          onSave={handleSaveEntry}
        />
      </Space>
    </Content>
  );
};

export default HR07TimesheetPage;
