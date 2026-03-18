import { CheckCircleFilled, CloseCircleFilled, ExclamationCircleFilled, FileSearchOutlined } from '@ant-design/icons';
import { Alert, Card, Empty, Space, Table, Tag, Typography } from 'antd';
import dayjs from 'dayjs';
import React from 'react';
import type { DailyAuditResult, WeeklyAuditSummary } from '../model/TimesheetAuditModel';

const { Text } = Typography;

interface TimesheetAuditResultProps {
  summary: WeeklyAuditSummary | null;
  loading?: boolean;
}

/**
 * HR07-C01: 工時考勤勾稽結果組件
 */
export const TimesheetAuditResult: React.FC<TimesheetAuditResultProps> = ({ summary, loading }) => {
  if (!summary) {
    return (
      <Card title="工時與考勤勾稽驗證" size="small">
        <Empty
          image={<FileSearchOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />}
          description="選擇工時週別後顯示勾稽結果"
        />
      </Card>
    );
  }

  const columns = [
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
      width: 90,
      render: (date: string) => dayjs(date).format('MM/DD(dd)'),
    },
    {
      title: '報工',
      dataIndex: 'timesheetHours',
      key: 'timesheetHours',
      width: 50,
      align: 'center' as const,
      render: (h: number) => `${h}h`,
    },
    {
      title: '在場',
      dataIndex: 'attendanceHours',
      key: 'attendanceHours',
      width: 50,
      align: 'center' as const,
      render: (h: number) => `${h}h`,
    },
    {
      title: '請假',
      dataIndex: 'leaveHours',
      key: 'leaveHours',
      width: 50,
      align: 'center' as const,
      render: (h: number) => h > 0 ? <Tag color="orange">{h}h</Tag> : '-',
    },
    {
      title: '結果',
      key: 'status',
      width: 45,
      align: 'center' as const,
      render: (_: any, record: DailyAuditResult) => {
        if (record.status === 'OK') {
          return <CheckCircleFilled style={{ color: '#52c41a' }} />;
        } else if (record.status === 'WARN') {
          return <ExclamationCircleFilled style={{ color: '#faad14' }} />;
        } else {
          return <CloseCircleFilled style={{ color: '#f5222d' }} />;
        }
      },
    },
    {
      title: '說明',
      dataIndex: 'message',
      key: 'message',
      ellipsis: true,
      render: (msg: string) => <Text type="secondary" style={{ fontSize: '12px' }}>{msg || '-'}</Text>,
    },
  ];

  return (
    <Card 
      title="工時與考勤勾稽驗證" 
      size="small" 
      loading={loading}
      className={summary.hasMismatch ? 'audit-mismatch' : ''}
    >
      <Space direction="vertical" style={{ width: '100%' }}>
        {summary.hasMismatch ? (
          <Alert
            message="檢核異常"
            description="部分日期的報工時數與考勤記錄不符，請確認是否漏打卡或報錯專案。"
            type="error"
            showIcon
          />
        ) : (
          <Alert message="檢核通過" type="success" showIcon />
        )}

        <Table
          dataSource={summary.dailyResults}
          columns={columns}
          pagination={false}
          size="small"
          rowKey="date"
          scroll={{ x: 'max-content' }}
        />
      </Space>
    </Card>
  );
};
