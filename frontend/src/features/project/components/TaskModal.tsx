import { Form, Input, InputNumber, Modal, Select } from 'antd';
import React, { useEffect } from 'react';
import { useEmployees } from '../../organization/hooks/useEmployees';
import type { TaskViewModel } from '../model/ProjectViewModel';

const { TextArea } = Input;

interface TaskModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  projectId: string;
  parentTask?: TaskViewModel | null;
  editingTask?: TaskViewModel | null;
  loading?: boolean;
  onSubmit: (values: any) => Promise<boolean>;
}

/**
 * HR06-M03: 工項編輯對話框
 */
export const TaskModal: React.FC<TaskModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  projectId,
  parentTask,
  editingTask,
  loading,
  onSubmit,
}) => {
  const [form] = Form.useForm();
  const { employees } = useEmployees({ page_size: 100 });

  useEffect(() => {
    if (visible) {
      if (editingTask) {
        form.setFieldsValue({
          task_code: editingTask.taskCode,
          task_name: editingTask.taskName,
          estimated_hours: editingTask.estimatedHours,
          assignee_id: editingTask.assigneeId, // Assuming ID is available
          status: editingTask.status,
          progress: editingTask.progress,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({
          status: 'NOT_STARTED',
          progress: 0,
        });
      }
    }
  }, [visible, editingTask, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        project_id: projectId,
        parent_task_id: parentTask?.id || editingTask?.parentTaskId || null,
      };
      
      const success = await onSubmit(payload);
      if (success) {
        onSuccess();
      }
    } catch (error) {
      // 驗證失敗
    }
  };

  return (
    <Modal
      title={editingTask ? '編輯工項' : parentTask ? `新增子工項 (於 ${parentTask.taskName})` : '新增主工項'}
      open={visible}
      onCancel={onCancel}
      onOk={handleOk}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="task_code"
          label="工項代碼"
          rules={[{ required: true, message: '請輸入工項代碼' }]}
        >
          <Input placeholder="例如: WBS-1.1" disabled={!!editingTask} />
        </Form.Item>
        
        <Form.Item
          name="task_name"
          label="工項名稱"
          rules={[{ required: true, message: '請輸入工項名稱' }]}
        >
          <Input placeholder="例如: 需求訪談" />
        </Form.Item>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Form.Item
            name="estimated_hours"
            label="預估工時 (h)"
            rules={[{ required: true, message: '請輸入預估工時' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>

          <Form.Item name="assignee_id" label="負責人">
            <Select placeholder="指派成員" allowClear>
              {employees.map(e => (
                <Select.Option key={e.id} value={e.id}>{e.fullName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        {editingTask && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
            <Form.Item name="status" label="狀態" rules={[{ required: true }]}>
              <Select>
                <Select.Option value="NOT_STARTED">未開始</Select.Option>
                <Select.Option value="IN_PROGRESS">進行中</Select.Option>
                <Select.Option value="COMPLETED">已完成</Select.Option>
                <Select.Option value="BLOCKED">阻塞中</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item name="progress" label="進度 (%)" rules={[{ required: true }]}>
              <InputNumber style={{ width: '100%' }} min={0} max={100} />
            </Form.Item>
          </div>
        )}

        <Form.Item name="description" label="工項描述">
          <TextArea rows={3} placeholder="請輸入工項詳細說明..." />
        </Form.Item>
      </Form>
    </Modal>
  );
};
