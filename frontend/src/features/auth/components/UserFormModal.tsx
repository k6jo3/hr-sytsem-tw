/**
 * User Form Modal Component (使用者表單對話框元件)
 * Domain Code: HR01
 */

import React, { useEffect } from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Switch,
  Space,
} from 'antd';
import type { RoleDto, CreateUserRequest, UpdateUserRequest } from '../api/AuthTypes';
import type { UserListViewModel } from '../model/UserProfile';

export interface UserFormModalProps {
  open: boolean;
  loading: boolean;
  user: UserListViewModel | null;
  roles: RoleDto[];
  onSubmit: (values: CreateUserRequest | UpdateUserRequest) => void;
  onCancel: () => void;
}

interface UserFormValues {
  username: string;
  email: string;
  password?: string;
  display_name: string;
  first_name: string;
  last_name: string;
  employee_id?: string;
  role_ids: string[];
  must_change_password?: boolean;
}

/**
 * 使用者表單對話框
 */
export const UserFormModal: React.FC<UserFormModalProps> = ({
  open,
  loading,
  user,
  roles,
  onSubmit,
  onCancel,
}) => {
  const [form] = Form.useForm<UserFormValues>();
  const isEdit = !!user;

  // 當 user 變更時，重置表單
  useEffect(() => {
    if (open) {
      if (user) {
        form.setFieldsValue({
          username: user.username,
          email: user.email,
          display_name: user.displayName,
          first_name: user.firstName,
          last_name: user.lastName,
          employee_id: user.employeeId,
          role_ids: user.roles.map((r) => r.id),
        });
      } else {
        form.resetFields();
        form.setFieldsValue({
          must_change_password: true,
        });
      }
    }
  }, [open, user, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (isEdit) {
        const updateData: UpdateUserRequest = {
          email: values.email,
          display_name: values.display_name,
          first_name: values.first_name,
          last_name: values.last_name,
          employee_id: values.employee_id,
          role_ids: values.role_ids,
        };
        onSubmit(updateData);
      } else {
        const createData: CreateUserRequest = {
          username: values.username,
          email: values.email,
          password: values.password!,
          display_name: values.display_name,
          first_name: values.first_name,
          last_name: values.last_name,
          employee_id: values.employee_id,
          role_ids: values.role_ids,
          must_change_password: values.must_change_password,
        };
        onSubmit(createData);
      }
    } catch {
      // Form validation failed
    }
  };

  return (
    <Modal
      title={isEdit ? '編輯使用者' : '新增使用者'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={600}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        <Space direction="vertical" style={{ width: '100%' }} size="small">
          <Form.Item
            name="username"
            label="登入帳號"
            rules={[
              { required: true, message: '請輸入登入帳號' },
              { min: 3, message: '登入帳號至少 3 個字元' },
              { max: 50, message: '登入帳號最多 50 個字元' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: '只能使用英文字母、數字和底線' },
            ]}
          >
            <Input
              placeholder="請輸入登入帳號（英文、數字、底線）"
              disabled={isEdit}
            />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: '請輸入 Email' },
              { type: 'email', message: '請輸入有效的 Email' },
            ]}
          >
            <Input placeholder="請輸入 Email" />
          </Form.Item>

          {!isEdit && (
            <Form.Item
              name="password"
              label="密碼"
              rules={[
                { required: true, message: '請輸入密碼' },
                { min: 8, message: '密碼至少 8 個字元' },
                {
                  pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                  message: '密碼必須包含大小寫字母和數字',
                },
              ]}
            >
              <Input.Password placeholder="請輸入密碼" />
            </Form.Item>
          )}

          <Form.Item
            name="display_name"
            label="顯示名稱"
            rules={[
              { required: true, message: '請輸入顯示名稱' },
              { max: 100, message: '顯示名稱最多 100 個字元' },
            ]}
          >
            <Input placeholder="請輸入顯示名稱" />
          </Form.Item>

          <Space style={{ width: '100%' }} size="middle">
            <Form.Item
              name="last_name"
              label="姓"
              rules={[{ required: true, message: '請輸入姓' }]}
              style={{ flex: 1, marginBottom: 0 }}
            >
              <Input placeholder="姓" />
            </Form.Item>
            <Form.Item
              name="first_name"
              label="名"
              rules={[{ required: true, message: '請輸入名' }]}
              style={{ flex: 1, marginBottom: 0 }}
            >
              <Input placeholder="名" />
            </Form.Item>
          </Space>

          <Form.Item
            name="employee_id"
            label="員工編號"
            style={{ marginTop: 16 }}
          >
            <Input placeholder="請輸入關聯的員工編號（選填）" />
          </Form.Item>

          <Form.Item
            name="role_ids"
            label="角色"
            rules={[{ required: true, message: '請選擇至少一個角色' }]}
          >
            <Select
              mode="multiple"
              placeholder="請選擇角色"
              options={roles
                .filter((role) => role.is_active)
                .map((role) => ({
                  label: role.role_name,
                  value: role.id,
                }))}
            />
          </Form.Item>

          {!isEdit && (
            <Form.Item
              name="must_change_password"
              label="首次登入強制變更密碼"
              valuePropName="checked"
            >
              <Switch />
            </Form.Item>
          )}
        </Space>
      </Form>
    </Modal>
  );
};
