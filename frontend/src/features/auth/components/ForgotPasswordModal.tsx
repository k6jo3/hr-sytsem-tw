/**
 * ForgotPasswordModal - 忘記密碼彈窗
 * Domain Code: HR01
 */

import { MailOutlined } from '@ant-design/icons';
import { Form, Input, message, Modal } from 'antd';
import React, { useState } from 'react';
import { AuthApi } from '../api/AuthApi'; // Ensure AuthApi is imported correctly

interface ForgotPasswordModalProps {
  open: boolean;
  onCancel: () => void;
}

export const ForgotPasswordModal: React.FC<ForgotPasswordModalProps> = ({
  open,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: { email: string }) => {
    setLoading(true);
    try {
      await AuthApi.forgotPassword(values.email);
      message.success('已發送重置密碼連結至您的信箱，請查收。');
      form.resetFields();
      onCancel();
    } catch (error) {
      // Security: Do not reveal if email exists or not, but for UX we might show generic error if network fails
      // However, usually backend should return success even if email not found (security practice)
      // If backend returns error, handle it.
      const msg = error instanceof Error ? error.message : '發送失敗，請稍後再試';
      message.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="忘記密碼"
      open={open}
      onCancel={onCancel}
      onOk={form.submit}
      confirmLoading={loading}
      okText="發送重置連結"
      cancelText="取消"
    >
      <div style={{ marginBottom: 24 }}>
        <p>請輸入您的註冊 Email，系統將發送密碼重置連結給您。</p>
      </div>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
      >
        <Form.Item
          name="email"
          label="Email"
          rules={[
            { required: true, message: '請輸入 Email' },
            { type: 'email', message: '請輸入有效的 Email 格式' },
          ]}
        >
          <Input 
            prefix={<MailOutlined />} 
            placeholder="example@company.com" 
            autoComplete="email"
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
