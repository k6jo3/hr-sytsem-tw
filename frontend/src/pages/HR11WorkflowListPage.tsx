import {
    CheckOutlined,
    ClockCircleOutlined,
    CloseOutlined,
    ExclamationCircleOutlined,
    FileTextOutlined,
    ReloadOutlined,
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
    Tabs,
    Tag,
    Typography,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import { useMyApplications, usePendingTasks } from '../features/workflow/hooks';
import type { ApprovalTaskViewModel, WorkflowInstanceViewModel } from '../features/workflow/model/WorkflowViewModel';

const { Title, Text } = Typography;
const { TextArea } = Input;

/**
 * HR11-P02: 我的待辦與申請清單
 * Feature: workflow
 */
export const HR11WorkflowListPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('pending');
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [selectedTask, setSelectedTask] = useState<ApprovalTaskViewModel | null>(null);
  const [rejectReason, setRejectReason] = useState('');

  const {
    tasks,
    summary,
    loading: tasksLoading,
    error: tasksError,
    refresh: refreshTasks,
    approveTask,
    rejectTask,
  } = usePendingTasks();

  const {
    applications,
    loading: applicationsLoading,
    error: applicationsError,
    refresh: refreshApplications,
  } = useMyApplications();

  const handleApprove = async (task: ApprovalTaskViewModel) => {
    const result = await approveTask(task.taskId);
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
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

  const taskColumns: ColumnsType<ApprovalTaskViewModel> = [
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
      title: '提交時間',
      dataIndex: 'createdAtDisplay',
      key: 'createdAtDisplay',
      width: 150,
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_: unknown, record: ApprovalTaskViewModel) => (
        <Space>
          {record.isOverdue && (
            <Tag color="error" icon={<ExclamationCircleOutlined />}>
              逾期
            </Tag>
          )}
          <Tag color={record.statusColor}>{record.statusLabel}</Tag>
        </Space>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: ApprovalTaskViewModel) => (
        <Space>
          {record.canApprove && (
            <Button
              type="primary"
              size="small"
              icon={<CheckOutlined />}
              onClick={() => handleApprove(record)}
            >
              核准
            </Button>
          )}
          {record.canReject && (
            <Button
              danger
              size="small"
              icon={<CloseOutlined />}
              onClick={() => handleRejectClick(record)}
            >
              駁回
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const applicationColumns: ColumnsType<WorkflowInstanceViewModel> = [
    {
      title: '申請類型',
      dataIndex: 'businessTypeLabel',
      key: 'businessTypeLabel',
      width: 120,
      render: (text: string) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '流程名稱',
      dataIndex: 'flowName',
      key: 'flowName',
      width: 150,
    },
    {
      title: '目前節點',
      dataIndex: 'currentNodeName',
      key: 'currentNodeName',
      width: 120,
    },
    {
      title: '提交時間',
      dataIndex: 'startedAtDisplay',
      key: 'startedAtDisplay',
      width: 150,
    },
    {
      title: '完成時間',
      dataIndex: 'completedAtDisplay',
      key: 'completedAtDisplay',
      width: 150,
      render: (text: string) => text || '-',
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_: unknown, record: WorkflowInstanceViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '耗時',
      dataIndex: 'duration',
      key: 'duration',
      width: 80,
      render: (text: string) => text || '-',
    },
  ];

  const renderPendingTasks = () => {
    if (tasksLoading) {
      return (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      );
    }

    if (tasksError) {
      return <Alert message="載入失敗" description={tasksError} type="error" showIcon />;
    }

    return (
      <>
        {summary && (
          <Row gutter={16} style={{ marginBottom: 24 }}>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="待處理"
                  value={summary.totalPending}
                  prefix={<FileTextOutlined />}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="已逾期"
                  value={summary.overdueCount}
                  prefix={<ExclamationCircleOutlined />}
                  valueStyle={{ color: '#ff4d4f' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="今日到期"
                  value={summary.dueTodayCount}
                  prefix={<ClockCircleOutlined />}
                  valueStyle={{ color: '#faad14' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="正常"
                  value={summary.normalCount}
                  prefix={<CheckOutlined />}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
          </Row>
        )}

        {tasks.length === 0 ? (
          <Empty description="目前沒有待辦任務" style={{ padding: '50px 0' }} />
        ) : (
          <Table
            columns={taskColumns}
            dataSource={tasks}
            rowKey="taskId"
            pagination={{ pageSize: 10 }}
          />
        )}
      </>
    );
  };

  const renderMyApplications = () => {
    if (applicationsLoading) {
      return (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      );
    }

    if (applicationsError) {
      return <Alert message="載入失敗" description={applicationsError} type="error" showIcon />;
    }

    if (applications.length === 0) {
      return <Empty description="目前沒有申請記錄" style={{ padding: '50px 0' }} />;
    }

    return (
      <Table
        columns={applicationColumns}
        dataSource={applications}
        rowKey="instanceId"
        pagination={{ pageSize: 10 }}
      />
    );
  };

  const tabItems = [
    {
      key: 'pending',
      label: (
        <span>
          <ClockCircleOutlined />
          我的待辦 {summary && summary.totalPending > 0 && `(${summary.totalPending})`}
        </span>
      ),
      children: renderPendingTasks(),
    },
    {
      key: 'applications',
      label: (
        <span>
          <FileTextOutlined />
          我的申請
        </span>
      ),
      children: renderMyApplications(),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>
              我的待辦與申請清單
            </Title>
            <Text type="secondary">管理您的待辦任務和申請記錄</Text>
          </div>
          <Button
            icon={<ReloadOutlined />}
            onClick={activeTab === 'pending' ? refreshTasks : refreshApplications}
          >
            重新整理
          </Button>
        </Space>
      </div>

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
      </Card>

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
          <Text>
            申請類型：{selectedTask?.businessTypeLabel}
            <br />
            申請人：{selectedTask?.applicantName}
          </Text>
        </div>
        <TextArea
          rows={4}
          placeholder="請輸入駁回原因..."
          value={rejectReason}
          onChange={(e) => setRejectReason(e.target.value)}
        />
      </Modal>
    </div>
  );
};
