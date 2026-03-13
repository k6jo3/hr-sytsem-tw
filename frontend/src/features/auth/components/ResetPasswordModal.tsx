/**
 * Reset Password Modal Component (重置密碼對話框元件)
 * Domain Code: HR01
 */

import React, { useEffect } from 'react';
import { Modal, Form, Input, Switch } from 'antd';

export interface ResetPasswordModalProps {
  open: boolean;
  loading: boolean;
  userId: string | null;
  onSubmit: (userId: string, newPassword: string, forceChange: boolean) => void;
  onCancel: () => void;
}

interface ResetPasswordFormValues {
  new_password: string;
  confirm_password: string;
  force_change: boolean;
}

/**
 * 重置密碼對話框
 */
export const ResetPasswordModal: React.FC<ResetPasswordModalProps> = ({
  open,
  loading,
  userId,
  onSubmit,
  onCancel,
}) => {
  const [form] = Form.useForm<ResetPasswordFormValues>();

  useEffect(() => {
    if (open) {
      form.resetFields();
      form.setFieldsValue({ force_change: true });
    }
  }, [open, form]);

  const handleSubmit = async () => {
    if (!userId) return;
    try {
      const values = await form.validateFields();
      onSubmit(userId, values.new_password, values.force_change);
    } catch {
      // 表單驗證失敗，由 Ant Design 表單元件自行顯示錯誤
    }
  };

  return (
    <Modal
      title="重置密碼"
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={400}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        <Form.Item
          name="new_password"
          label="新密碼"
          rules={[
            { required: true, message: '請輸入新密碼' },
            { min: 8, message: '密碼至少 8 個字元' },
            {
              pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
              message: '密碼必須包含大小寫字母和數字',
            },
          ]}
        >
          <Input.Password placeholder="請輸入新密碼" />
        </Form.Item>

        <Form.Item
          name="confirm_password"
          label="確認密碼"
          dependencies={['new_password']}
          rules={[
            { required: true, message: '請再次輸入密碼' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('new_password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('兩次輸入的密碼不一致'));
              },
            }),
          ]}
        >
          <Input.Password placeholder="請再次輸入密碼" />
        </Form.Item>

        <Form.Item
          name="force_change"
          label="首次登入強制變更密碼"
          valuePropName="checked"
        >
          <Switch />
        </Form.Item>
      </Form>
    </Modal>
  );
};
