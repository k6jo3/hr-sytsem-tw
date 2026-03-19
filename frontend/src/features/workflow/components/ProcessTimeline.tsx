/**
 * 流程進度時間軸元件
 * Domain Code: HR11
 */

import { CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { Drawer, Empty, Spin, Steps, Tag, Typography } from 'antd';
import React, { useCallback, useEffect, useState } from 'react';
import { WorkflowApi } from '../api';
import { WorkflowViewModelFactory } from '../factory/WorkflowViewModelFactory';
import type { ApprovalTaskViewModel, WorkflowInstanceViewModel } from '../model/WorkflowViewModel';

const { Text, Title } = Typography;

interface ProcessTimelineProps {
  instanceId: string | null;
  onClose: () => void;
}

export const ProcessTimeline: React.FC<ProcessTimelineProps> = ({ instanceId, onClose }) => {
  const [instance, setInstance] = useState<WorkflowInstanceViewModel | null>(null);
  const [tasks, setTasks] = useState<ApprovalTaskViewModel[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchDetail = useCallback(async (id: string) => {
    setLoading(true);
    try {
      const response = await WorkflowApi.getInstance(id);
      setInstance(WorkflowViewModelFactory.createInstanceViewModel(response.instance));
      setTasks(WorkflowViewModelFactory.createTaskList(response.tasks));
    } catch {
      setInstance(null);
      setTasks([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (instanceId) {
      fetchDetail(instanceId);
    }
  }, [instanceId, fetchDetail]);

  const getStepStatus = (task: ApprovalTaskViewModel): 'finish' | 'process' | 'error' | 'wait' => {
    if (task.status === 'APPROVED') return 'finish';
    if (task.status === 'REJECTED') return 'error';
    if (task.isPending) return 'process';
    return 'wait';
  };

  const getStepIcon = (task: ApprovalTaskViewModel) => {
    if (task.status === 'APPROVED') return <CheckCircleOutlined />;
    if (task.status === 'REJECTED') return <CloseCircleOutlined />;
    if (task.isPending) return <ClockCircleOutlined />;
    return <MinusCircleOutlined />;
  };

  return (
    <Drawer
      title="流程進度追蹤"
      open={!!instanceId}
      onClose={onClose}
      width={480}
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      ) : !instance ? (
        <Empty description="無法載入流程資料" />
      ) : (
        <>
          <div style={{ marginBottom: 24, padding: 16, background: '#fafafa', borderRadius: 8 }}>
            <Title level={5} style={{ margin: 0 }}>{instance.flowName}</Title>
            <div style={{ marginTop: 8 }}>
              <Text type="secondary">申請人：</Text>
              <Text>{instance.applicantName}</Text>
            </div>
            <div style={{ marginTop: 4 }}>
              <Text type="secondary">申請類型：</Text>
              <Tag color="blue">{instance.businessTypeLabel}</Tag>
            </div>
            <div style={{ marginTop: 4 }}>
              <Text type="secondary">提交時間：</Text>
              <Text>{instance.startedAtDisplay}</Text>
            </div>
            <div style={{ marginTop: 4 }}>
              <Text type="secondary">狀態：</Text>
              <Tag color={instance.statusColor}>{instance.statusLabel}</Tag>
              {instance.duration && <Text type="secondary">（耗時 {instance.duration}）</Text>}
            </div>
          </div>

          {tasks.length === 0 ? (
            <Empty description="暫無審核紀錄" />
          ) : (
            <Steps
              direction="vertical"
              current={tasks.findIndex((t) => t.isPending)}
              items={tasks.map((task) => ({
                title: (
                  <span>
                    {task.nodeName}
                    <Tag color={task.statusColor} style={{ marginLeft: 8 }}>{task.statusLabel}</Tag>
                  </span>
                ),
                description: (
                  <div>
                    <div>審核人：{task.assigneeName}</div>
                    {task.delegatedToName && <div>代理人：{task.delegatedToName}</div>}
                    {task.completedAtDisplay && <div>完成時間：{task.completedAtDisplay}</div>}
                    {task.comments && <div>意見：{task.comments}</div>}
                  </div>
                ),
                status: getStepStatus(task),
                icon: getStepIcon(task),
              }))}
            />
          )}
        </>
      )}
    </Drawer>
  );
};
