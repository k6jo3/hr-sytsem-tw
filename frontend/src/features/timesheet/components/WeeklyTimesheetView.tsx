import { DeleteOutlined, EditOutlined, SendOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Empty, Modal, Popconfirm, Skeleton, Space, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';
import type { TimesheetEntryViewModel, WeeklyTimesheetSummary } from '../model/TimesheetViewModel';

const { Title, Text } = Typography;

export interface WeeklyTimesheetViewProps {
  summary: WeeklyTimesheetSummary | null;
  loading?: boolean;
  onSubmit: () => void;
  onEdit: (record: TimesheetEntryViewModel) => void;
  onDelete: (id: string) => void;
}

export const WeeklyTimesheetView: React.FC<WeeklyTimesheetViewProps> = ({
  summary,
  loading = false,
  onSubmit,
  onEdit,
  onDelete,
}) => {
  // 送出審核前的二次確認對話框
  const handleSubmitClick = () => {
    Modal.confirm({
      title: '確認送出工時',
      content: '送出後將進入主管審核流程，送出前請確認所有工時資料正確。確定要送出嗎？',
      okText: '確認送出',
      cancelText: '取消',
      onOk: () => onSubmit(),
    });
  };

  if (loading) {
    return <Card><Skeleton active /></Card>;
  }

  if (!summary) {
    return <Card><Empty description="無工時資料" /></Card>;
  }

  const columns: ColumnsType<TimesheetEntryViewModel> = [
    { title: '日期', dataIndex: 'workDateDisplay', key: 'workDateDisplay', width: 80 },
    { title: '專案', dataIndex: 'projectDisplay', key: 'projectDisplay', width: 150 },
    { title: 'WBS', dataIndex: 'wbsDisplay', key: 'wbsDisplay', width: 150 },
    { title: '時數', dataIndex: 'hours', key: 'hours', width: 80, align: 'right' as const },
    { title: '工作說明', dataIndex: 'description', key: 'description', ellipsis: true },
    {
      title: '操作',
      key: 'actions',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="text" 
            icon={<EditOutlined />} 
            disabled={!record.canEdit}
            onClick={() => onEdit(record)}
          />
          <Popconfirm
            title="確定要刪除這筆工時記錄嗎？"
            onConfirm={() => onDelete(record.id)}
            disabled={!record.canDelete}
          >
            <Button 
              type="text" 
              danger 
              icon={<DeleteOutlined />} 
              disabled={!record.canDelete}
            />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space align="baseline">
            <Title level={4} style={{ margin: 0 }}>週工時 {summary.weekDisplay}</Title>
            <Tag color={summary.statusColor}>
              {summary.statusLabel}
            </Tag>
            <Text strong>總計: {summary.totalHours} 小時</Text>
          </Space>
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleSubmitClick}
            disabled={!summary.canSubmit}
          >
            送出審核
          </Button>
        </div>

        {summary.rejectionReason && (
          <Alert
            message="駁回原因"
            description={summary.rejectionReason}
            type="error"
            showIcon
          />
        )}

        {summary.entries.length === 0 ? (
          <Empty description="本週尚無工時記錄" />
        ) : (
          <Table
            rowKey="id"
            dataSource={summary.entries}
            columns={columns}
            pagination={false}
            size="small"
          />
        )}
      </Space>
    </Card>
  );
};
