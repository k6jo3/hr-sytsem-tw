/**
 * 待辦任務面板元件
 * Domain Code: HR11
 */

import {
  CheckOutlined,
  ClockCircleOutlined,
  CloseOutlined,
  ExclamationCircleOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import {
  Alert,
  Button,
  Card,
  Col,
  Empty,
  Input,
  message,
  Modal,
  Row,
  Space,
  Spin,
  Statistic,
  Table,
  Tag,
  Tooltip,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import { usePendingTasks } from '../hooks';
import type { ApprovalTaskViewModel } from '../model/WorkflowViewModel';

const { TextArea } = Input;

export const PendingTasksPanel: React.FC = () => {
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [selectedTask, setSelectedTask] = useState<ApprovalTaskViewModel | null>(null);
  const [rejectReason, setRejectReason] = useState('');

  const { tasks, summary, loading, error, refresh, approveTask, rejectTask } = usePendingTasks();

  const handleApprove = async (task: ApprovalTaskViewModel) => {
    Modal.confirm({
      title: '確認核准',
      content: `確定核准「${task.applicantName}」的「${task.businessTypeLabel}」？`,
      okText: '確認',
      cancelText: '取消',
      onOk: async () => {
        const result = await approveTask(task.taskId);
        if (result.success) {
          message.success(result.message);
        } else {
          message.error(result.message);
        }
      },
    });
  };

  const handleRejectClick = (task: ApprovalTaskViewModel) => {
    setSelectedTask(task);
    setRejectReason('');
    setRejectModalVisible(true);
  };

  const handleRejectConfirm = async () => {
    if (!selectedTask) return;
    if (!rejectReason.trim()) {
      message.warning('請輸入駁回原因');
      return;
    }
    const result = await rejectTask(selectedTask.taskId, { comments: rejectReason });
    if (result.success) {
      message.success(result.message);
      setRejectModalVisible(false);
      setSelectedTask(null);
      setRejectReason('');
    } else {
      message.error(result.message);
    }
  };

  const columns: ColumnsType<ApprovalTaskViewModel> = [
    {
      title: '申請類型',
      dataIndex: 'businessTypeLabel',
      key: 'businessTypeLabel',
      width: 120,
      render: (text: string) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '申請人',
      dataIndex: 'applicantName',
      key: 'applicantName',
      width: 100,
    },
    {
      title: '摘要',
      dataIndex: 'businessSummary',
      key: 'businessSummary',
      ellipsis: true,
    },
    {
      title: '審核節點',
      dataIndex: 'nodeName',
      key: 'nodeName',
      width: 120,
    },
    {
      title: '提交時間',
      dataIndex: 'createdAtDisplay',
      key: 'createdAtDisplay',
      width: 150,
    },
    {
      title: '到期日',
      dataIndex: 'dueDateDisplay',
      key: 'dueDateDisplay',
      width: 120,
      render: (text: string, record: ApprovalTaskViewModel) => (
        <span style={{ color: record.isOverdue ? '#ff4d4f' : undefined }}>
          {text || '-'}
        </span>
      ),
    },
    {
      title: '狀態',
      key: 'status',
      width: 120,
      render: (_: unknown, record: ApprovalTaskViewModel) => (
        <Space>
          {record.isOverdue && (
            <Tag color="error" icon={<ExclamationCircleOutlined />}>逾期</Tag>
          )}
          <Tag color={record.statusColor}>{record.statusLabel}</Tag>
        </Space>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_: unknown, record: ApprovalTaskViewModel) => (
        <Space>
          {record.canApprove && (
            <Tooltip title="核准此申請">
              <Button
                type="primary"
                size="small"
                icon={<CheckOutlined />}
                onClick={() => handleApprove(record)}
              >
                核准
              </Button>
            </Tooltip>
          )}
          {record.canReject && (
            <Tooltip title="駁回此申請">
              <Button
                danger
                size="small"
                icon={<CloseOutlined />}
                onClick={() => handleRejectClick(record)}
              >
                駁回
              </Button>
            </Tooltip>
          )}
        </Space>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <Alert message="載入失敗" description={error} type="error" showIcon action={<Button onClick={refresh}>重試</Button>} />;
  }

  return (
    <>
      {summary && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card size="small">
              <Statistic title="待處理" value={summary.totalPending} prefix={<FileTextOutlined />} valueStyle={{ color: '#1890ff' }} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic title="已逾期" value={summary.overdueCount} prefix={<ExclamationCircleOutlined />} valueStyle={{ color: '#ff4d4f' }} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic title="今日到期" value={summary.dueTodayCount} prefix={<ClockCircleOutlined />} valueStyle={{ color: '#faad14' }} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic title="正常" value={summary.normalCount} prefix={<CheckOutlined />} valueStyle={{ color: '#52c41a' }} />
            </Card>
          </Col>
        </Row>
      )}

      {tasks.length === 0 ? (
        <Empty description="目前沒有待辦任務" style={{ padding: '50px 0' }} />
      ) : (
        <Table columns={columns} dataSource={tasks} rowKey="taskId" pagination={{ pageSize: 10 }} />
      )}

      <Modal
        title="駁回申請"
        open={rejectModalVisible}
        onOk={handleRejectConfirm}
        onCancel={() => setRejectModalVisible(false)}
        okText="確認駁回"
        cancelText="取消"
        okButtonProps={{ danger: true }}
      >
        <div style={{ marginBottom: 16 }}>
          申請類型：{selectedTask?.businessTypeLabel}<br />
          申請人：{selectedTask?.applicantName}<br />
          摘要：{selectedTask?.businessSummary}
        </div>
        <TextArea
          rows={4}
          placeholder="請輸入駁回原因（必填）..."
          value={rejectReason}
          onChange={(e) => setRejectReason(e.target.value)}
        />
      </Modal>
    </>
  );
};
