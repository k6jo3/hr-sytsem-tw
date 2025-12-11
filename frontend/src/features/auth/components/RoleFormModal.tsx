/**
 * Role Form Modal Component (角色表單對話框元件)
 * Domain Code: HR01
 */

import React, { useEffect } from 'react';
import { Modal, Form, Input } from 'antd';
import type { RoleDto, CreateRoleRequest, UpdateRoleRequest } from '../api/AuthTypes';

const { TextArea } = Input;

export interface RoleFormModalProps {
  open: boolean;
  loading: boolean;
  role: RoleDto | null;
  onSubmit: (values: CreateRoleRequest | UpdateRoleRequest) => void;
  onCancel: () => void;
}

interface RoleFormValues {
  role_code: string;
  role_name: string;
  description?: string;
}

/**
 * 角色表單對話框
 */
export const RoleFormModal: React.FC<RoleFormModalProps> = ({
  open,
  loading,
  role,
  onSubmit,
  onCancel,
}) => {
  const [form] = Form.useForm<RoleFormValues>();
  const isEdit = !!role;

  // 當 role 變更時，重置表單
  useEffect(() => {
    if (open) {
      if (role) {
        form.setFieldsValue({
          role_code: role.role_code,
          role_name: role.role_name,
          description: role.description,
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, role, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (isEdit) {
        const updateData: UpdateRoleRequest = {
          role_name: values.role_name,
          description: values.description,
        };
        onSubmit(updateData);
      } else {
        const createData: CreateRoleRequest = {
          role_code: values.role_code,
          role_name: values.role_name,
          description: values.description,
          permission_ids: [],
        };
        onSubmit(createData);
      }
    } catch {
      // Form validation failed
    }
  };

  return (
    <Modal
      title={isEdit ? '編輯角色' : '新增角色'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={500}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        <Form.Item
          name="role_code"
          label="角色代碼"
          rules={[
            { required: true, message: '請輸入角色代碼' },
            { max: 50, message: '角色代碼最多 50 個字元' },
            { pattern: /^[A-Z0-9_]+$/, message: '只能使用大寫字母、數字和底線' },
          ]}
        >
          <Input
            placeholder="例如：HR_ADMIN"
            disabled={isEdit}
          />
        </Form.Item>

        <Form.Item
          name="role_name"
          label="角色名稱"
          rules={[
            { required: true, message: '請輸入角色名稱' },
            { max: 100, message: '角色名稱最多 100 個字元' },
          ]}
        >
          <Input placeholder="例如：人資管理員" />
        </Form.Item>

        <Form.Item
          name="description"
          label="描述"
        >
          <TextArea
            rows={3}
            placeholder="請輸入角色描述（選填）"
            maxLength={500}
            showCount
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
