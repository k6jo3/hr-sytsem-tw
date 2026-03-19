/**
 * Department Form Modal
 * Domain Code: HR02
 * Modal for creating and updating departments
 */

import { Form, Input, InputNumber, Modal } from 'antd';
import React, { useEffect } from 'react';
import { DepartmentDto, DepartmentRequest } from '../api/OrganizationTypes';

interface DepartmentFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSubmit: (values: DepartmentRequest) => Promise<void>;
  initialValues?: DepartmentDto | null;
  organizationId: string;
  parentId?: string;
  loading?: boolean;
}

export const DepartmentFormModal: React.FC<DepartmentFormModalProps> = ({
  open,
  onCancel,
  onSubmit,
  initialValues,
  organizationId,
  parentId,
  loading = false,
}) => {
  const [form] = Form.useForm();
  const isEdit = !!initialValues;

  useEffect(() => {
    if (open) {
      if (initialValues) {
        form.setFieldsValue({
          code: initialValues.code,
          name: initialValues.name,
          managerId: initialValues.managerId,
          sortOrder: initialValues.sortOrder,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({
            organizationId, // Hidden field if needed, or handled in submit
            parentId, // Context for new child
        });
      }
    }
  }, [open, initialValues, form, organizationId, parentId]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      await onSubmit({
        ...values,
        organizationId,
        parentId: isEdit ? initialValues?.parentId : parentId,
      });
      onCancel();
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Modal
      title={isEdit ? "編輯部門" : "新增部門"}
      open={open}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
      >
        <Form.Item
          name="code"
          label="部門代號"
          rules={[{ required: true, message: '請輸入部門代號' }]}
        >
          <Input placeholder="例如：RD01" disabled={isEdit} />
        </Form.Item>

        <Form.Item
          name="name"
          label="部門名稱"
          rules={[{ required: true, message: '請輸入部門名稱' }]}
        >
          <Input placeholder="例如：研發一部" />
        </Form.Item>

        {/* Manager Selection could be a complex component searching employees. 
            For now, we use a simple input or assume ID entry, or we can later integrate an EmployeeSelect component.
            Let's keep it simple or commented out until EmployeeSelect is ready.
         */}
        {/*
        <Form.Item
          name="managerId"
          label="部門主管"
        >
          <Input placeholder="輸入員工ID (待整合員工搜尋元件)" />
        </Form.Item>
        */}

        <Form.Item
          name="sortOrder"
          label="顯示順序"
        >
          <InputNumber min={0} step={1} style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
};
