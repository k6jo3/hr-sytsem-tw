import { DatePicker, Form, InputNumber, Modal, Select } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect } from 'react';
import { useEmployees } from '../../organization/hooks/useEmployees';
import type { AddProjectMemberRequest } from '../api/ProjectTypes';

interface ProjectMemberModalProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: AddProjectMemberRequest) => Promise<void>;
  loading?: boolean;
}

/**
 * HR06-M02: 新增專案成員對話框
 */
export const ProjectMemberModal: React.FC<ProjectMemberModalProps> = ({
  visible,
  onCancel,
  onSubmit,
  loading,
}) => {
  const [form] = Form.useForm();
  
  // 載入員工列表供選擇
  const { employees } = useEmployees({ page_size: 100 });

  useEffect(() => {
    if (visible) {
      form.resetFields();
      form.setFieldsValue({
        join_date: dayjs()
      });
    }
  }, [visible, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      await onSubmit({
        employee_id: values.employeeId,
        role: Array.isArray(values.role) ? values.role.join(', ') : values.role,
        allocated_hours: values.allocatedHours,
        join_date: values.join_date.format('YYYY-MM-DD'),
      });
      onCancel();
    } catch (error) {
      // 驗證失敗
    }
  };

  return (
    <Modal
      title="新增專案成員"
      open={visible}
      onCancel={onCancel}
      onOk={handleOk}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="employeeId"
          label="員工"
          rules={[{ required: true, message: '請選擇員工' }]}
        >
          <Select 
            placeholder="請選擇員工" 
            showSearch
            optionFilterProp="children"
            filterOption={(input, option) =>
              (option?.children as unknown as string).toLowerCase().includes(input.toLowerCase())
            }
          >
            {employees.map(emp => (
              <Select.Option key={emp.id} value={emp.id}>
                {emp.fullName} ({emp.employeeNumber})
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="role"
          label="專案角色"
          rules={[{ required: true, message: '請選擇或輸入角色' }]}
        >
          <Select
            mode="tags"
            placeholder="例如: Developer, PM, QA"
            maxTagCount={1}
            tokenSeparators={[',']}
          >
            <Select.Option value="Project Manager">Project Manager</Select.Option>
            <Select.Option value="Developer">Developer</Select.Option>
            <Select.Option value="QA Engineer">QA Engineer</Select.Option>
            <Select.Option value="Designer">Designer</Select.Option>
            <Select.Option value="Business Analyst">Business Analyst</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="allocatedHours"
          label="分配工時 (小時)"
          rules={[{ required: true, message: '請輸入分配工時' }]}
        >
          <InputNumber style={{ width: '100%' }} min={0} step={1} />
        </Form.Item>

        <Form.Item
          name="join_date"
          label="加入日期"
          rules={[{ required: true, message: '請選擇加入日期' }]}
        >
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
};
