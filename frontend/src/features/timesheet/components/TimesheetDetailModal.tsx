import { Divider, Modal, Space, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { TimesheetApi } from '../api/TimesheetApi';
import { TimesheetViewModelFactory } from '../factory/TimesheetViewModelFactory';
import { useTimesheetAudit } from '../hooks/useTimesheetAudit';
import type { WeeklyTimesheetSummary } from '../model/TimesheetViewModel';
import { TimesheetAuditResult } from './TimesheetAuditResult';

const { Title, Text } = Typography;

interface TimesheetDetailModalProps {
  open: boolean;
  timesheetId: string | null;
  employeeId: string | null;
  onCancel: () => void;
}

/**
 * HR07-M03: 工時明細與勾稽詳情對話框
 */
export const TimesheetDetailModal: React.FC<TimesheetDetailModalProps> = ({
  open,
  timesheetId,
  employeeId,
  onCancel,
}) => {
  const [summary, setSummary] = useState<WeeklyTimesheetSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const { auditSummary, loading: auditLoading, calculateAudit } = useTimesheetAudit();

  useEffect(() => {
    if (open && timesheetId) {
      fetchDetails();
    }
  }, [open, timesheetId]);

  const fetchDetails = async () => {
    if (!timesheetId) return;
    setLoading(true);
    try {
      const response = await TimesheetApi.getWeeklyTimesheet({ id: timesheetId });
      const vm = TimesheetViewModelFactory.createWeeklySummary(response.timesheet);
      setSummary(vm);
      
      if (employeeId) {
        calculateAudit(vm, employeeId);
      }
    } catch (err) {
      console.error('Failed to fetch timesheet details:', err);
    } finally {
      setLoading(false);
    }
  };

  const entryColumns = [
    { title: '日期', dataIndex: 'workDateDisplay', key: 'workDate', width: 120 },
    { title: '專案', dataIndex: 'projectDisplay', key: 'project', width: 200 },
    { title: 'WBS', dataIndex: 'wbsDisplay', key: 'wbs', width: 200 },
    { title: '時數', dataIndex: 'hours', key: 'hours', width: 80, align: 'right' as const, render: (h: number) => `${h}h` },
    { title: '說明', dataIndex: 'description', key: 'description' },
  ];

  return (
    <Modal
      title="工時明細與勾稽驗證"
      open={open}
      onCancel={onCancel}
      footer={null}
      width={1000}
      destroyOnClose
    >
      {summary && (
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Title level={4}>{summary.weekDisplay} 工時摘要</Title>
            <Space split={<Divider type="vertical" />}>
              <Text>員工: <Text strong>{summary.employeeName}</Text></Text>
              <Text>總工時: <Text strong>{summary.totalHours} 小時</Text></Text>
              <Text>狀態: <Tag color={summary.statusColor}>{summary.statusLabel}</Tag></Text>
            </Space>
          </div>

          <TimesheetAuditResult summary={auditSummary} loading={auditLoading} />

          <div>
            <Title level={5}>工作明細</Title>
            <Table
              dataSource={summary.entries}
              columns={entryColumns}
              pagination={false}
              size="small"
              rowKey="id"
              loading={loading}
            />
          </div>
        </Space>
      )}
    </Modal>
  );
};
