import React from 'react';
import { Card, Button, Table, Typography, Tag, Skeleton, Empty } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { WeeklyTimesheetSummary, TimesheetEntryViewModel } from '../model/TimesheetViewModel';

const { Title, Text } = Typography;

export interface WeeklyTimesheetViewProps {
  summary: WeeklyTimesheetSummary | null;
  loading?: boolean;
  onSubmit: () => void;
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
}

export const WeeklyTimesheetView: React.FC<WeeklyTimesheetViewProps> = ({
  summary,
  loading = false,
  onSubmit,
}) => {
  if (loading) {
    return <Card><Skeleton active /></Card>;
  }

  if (!summary) {
    return <Card><Empty description="無工時資料" /></Card>;
  }

  const columns: ColumnsType<TimesheetEntryViewModel> = [
    { title: '日期', dataIndex: 'workDateDisplay', key: 'workDateDisplay', width: 80 },
    { title: '專案', dataIndex: 'projectDisplay', key: 'projectDisplay' },
    { title: '工時', dataIndex: 'hours', key: 'hours', width: 80 },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text: string, record: TimesheetEntryViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
  ];

  return (
    <Card>
      <div style={{ marginBottom: 16 }}>
        <Title level={4}>週工時 {summary.weekDisplay}</Title>
        <Text>總工時: {summary.totalHours} 小時</Text>
        <Tag color={summary.statusColor} style={{ marginLeft: 8 }}>
          {summary.statusLabel}
        </Tag>
      </div>

      {summary.entries.length === 0 ? (
        <Empty description="尚無工時記錄" />
      ) : (
        <Table
          rowKey="id"
          dataSource={summary.entries}
          columns={columns}
          pagination={false}
        />
      )}

      <div style={{ marginTop: 16 }}>
        <Button
          type="primary"
          onClick={onSubmit}
          disabled={!summary.canSubmit}
        >
          提交工時
        </Button>
      </div>
    </Card>
  );
};
