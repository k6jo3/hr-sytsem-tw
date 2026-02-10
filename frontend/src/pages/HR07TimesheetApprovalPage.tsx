import {
    CheckCircleOutlined,
    CloseCircleOutlined,
    FileSearchOutlined
} from '@ant-design/icons';
import {
    Alert,
    Button,
    Card,
    Input,
    message,
    Modal,
    Space,
    Table,
    Tag,
    Tooltip,
    Typography
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect, useState } from 'react';
import { TimesheetDetailModal } from '../features/timesheet/components/TimesheetDetailModal';
import { useTimesheetApproval } from '../features/timesheet/hooks/useTimesheetApproval';
import type { WeeklyTimesheetSummary } from '../features/timesheet/model/TimesheetViewModel';

const { Title, Text } = Typography;
const { TextArea } = Input;

/**
 * HR07-P02: 工時審核頁面
 */
export const HR07TimesheetApprovalPage: React.FC = () => {
  const {
    pendingTimesheets,
    total,
    loading,
    error,
    fetchPendingApprovals,
    handleApprove,
    handleReject,
    handleBatchApprove
  } = useTimesheetApproval();

  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [rejectingId, setRejectingId] = useState<string | null>(null);
  const [viewingTimesheet, setViewingTimesheet] = useState<{id: string, employeeId: string} | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    fetchPendingApprovals(undefined, page, pageSize);
  }, [fetchPendingApprovals, page, pageSize]);

  const onSelectChange = (newSelectedRowKeys: React.Key[]) => {
    setSelectedRowKeys(newSelectedRowKeys);
  };

  const showRejectModal = (id: string) => {
    setRejectingId(id);
    setRejectionReason('');
    setRejectModalVisible(true);
  };

  const showDetailModal = (record: WeeklyTimesheetSummary) => {
    setViewingTimesheet({ id: record.id, employeeId: record.employeeId });
    setDetailModalVisible(true);
  };

  const handleConfirmReject = async () => {
    if (!rejectionReason.trim()) {
      message.warning('請輸入駁回原因');
      return;
    }
    if (rejectingId) {
      await handleReject(rejectingId, rejectionReason);
      setRejectModalVisible(false);
    }
  };

  const columns: ColumnsType<WeeklyTimesheetSummary> = [
    {
      title: '提交人',
      dataIndex: 'employeeName',
      key: 'employeeName',
      width: 120,
    },
    {
      title: '週次',
      dataIndex: 'weekDisplay',
      key: 'weekDisplay',
      width: 150,
    },
    {
      title: '總工時',
      dataIndex: 'totalHours',
      key: 'totalHours',
      width: 100,
      align: 'right' as const,
      render: (val: number) => <Text strong>{val}h</Text>,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text: string, record: WeeklyTimesheetSummary) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="核准">
            <Button 
              type="text" 
              icon={<CheckCircleOutlined style={{ color: '#52c41a' }} />} 
              onClick={() => handleApprove(record.id)}
            />
          </Tooltip>
          <Tooltip title="駁回">
            <Button 
              type="text" 
              icon={<CloseCircleOutlined style={{ color: '#f5222d' }} />} 
              onClick={() => showRejectModal(record.id)}
            />
          </Tooltip>
          <Tooltip title="查看詳情與勾稽器">
            <Button 
              type="text" 
              icon={<FileSearchOutlined />} 
              onClick={() => showDetailModal(record)}
            />
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={2}>工時審核</Title>
          <Space>
            <Button 
              type="primary" 
              disabled={selectedRowKeys.length === 0}
              onClick={() => handleBatchApprove(selectedRowKeys as string[])}
            >
              批次核准 ({selectedRowKeys.length})
            </Button>
          </Space>
        </div>

        {error && <Alert message="錯誤" description={error} type="error" showIcon />}

        <Card>
          <Table
            rowSelection={{
              selectedRowKeys,
              onChange: onSelectChange,
            }}
            columns={columns}
            dataSource={pendingTimesheets}
            rowKey="id"
            loading={loading}
            pagination={{
              total,
              current: page,
              pageSize,
              onChange: (p, ps) => {
                setPage(p);
                setPageSize(ps);
              },
              showTotal: (total) => `共 ${total} 筆待審核`
            }}
          />
        </Card>
      </Space>

      <TimesheetDetailModal
        visible={detailModalVisible}
        timesheetId={viewingTimesheet?.id || null}
        employeeId={viewingTimesheet?.employeeId || null}
        onCancel={() => setDetailModalVisible(false)}
      />

      <Modal
        title="駁回工時申請"
        open={rejectModalVisible}
        onOk={handleConfirmReject}
        onCancel={() => setRejectModalVisible(false)}
        okText="確認駁回"
        okButtonProps={{ danger: true }}
      >
        <div style={{ marginBottom: 16 }}>
          <Text type="secondary">請填寫駁回原因，幫助員工修正回報內容：</Text>
        </div>
        <TextArea
          rows={4}
          value={rejectionReason}
          onChange={(e) => setRejectionReason(e.target.value)}
          placeholder="例如：12/01 的外部會議工時報錯專案，請修正後重新送審。"
        />
      </Modal>
    </div>
  );
};

export default HR07TimesheetApprovalPage;
