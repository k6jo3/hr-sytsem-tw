import React, { useState } from 'react';
import { Form, Input, Button, Checkbox, Alert } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import type { LoginFormData } from '../api/AuthTypes';

interface LoginFormProps {
  onSubmit: (data: LoginFormData) => void;
  loading?: boolean;
  error?: string | null;
}

/**
 * 登入表單元件
 * Domain Code: HR01
 */
export const LoginForm: React.FC<LoginFormProps> = ({ onSubmit, loading = false, error }) => {
  const [form] = Form.useForm();

  const handleSubmit = (values: { username: string; password: string; remember?: boolean }) => {
    onSubmit({
      username: values.username,
      password: values.password,
      remember: values.remember ?? false,
    });
  };

  return (
    <div>
      {error && (
        <Alert
          message={error}
          type="error"
          showIcon
          closable
          style={{ marginBottom: 16 }}
        />
      )}
      
      <Form
        form={form}
        name="login-form"
        onFinish={handleSubmit}
        autoComplete="off"
        size="large"
      >
        <Form.Item
          name="username"
          rules={[
            { required: true, message: '請輸入帳號' },
          ]}
        >
          <Input
            prefix={<UserOutlined />}
            placeholder="帳號"
            aria-label="帳號"
            disabled={loading}
          />
        </Form.Item>

        <Form.Item
          name="password"
          rules={[
            { required: true, message: '請輸入密碼' },
            { min: 6, message: '密碼長度至少6個字元' },
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="密碼"
            aria-label="密碼"
            disabled={loading}
          />
        </Form.Item>

        <Form.Item>
          <Form.Item name="remember" valuePropName="checked" noStyle>
            <Checkbox disabled={loading} aria-label="記住我">
              記住我
            </Checkbox>
          </Form.Item>
        </Form.Item>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            disabled={loading}
            block
            aria-label={loading ? '登入中' : '登入'}
          >
            {loading ? '登入中...' : '登入'}
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};
