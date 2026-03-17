/**
 * LoginForm - 登入表單元件
 * Domain Code: HR01
 * Page Code: HR01-P01
 */

import React from 'react';
import { Form, Input, Button, Checkbox, Alert, Typography } from 'antd';
import {
  UserOutlined,
  LockOutlined,
} from '@ant-design/icons';
import type { LoginFormData } from '../api/AuthTypes';

const { Link } = Typography;

interface LoginFormProps {
  onSubmit: (data: LoginFormData) => void;
  onForgotPassword?: () => void;
  loading?: boolean;
  error?: string | null;
  showForgotPassword?: boolean;
}

/**
 * 登入表單元件
 * 包含帳號密碼登入、忘記密碼功能
 */
export const LoginForm: React.FC<LoginFormProps> = ({
  onSubmit,
  onForgotPassword,
  loading = false,
  error,
  showForgotPassword = true,
}) => {
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
          rules={[{ required: true, message: '請輸入帳號' }]}
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
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Form.Item name="remember" valuePropName="checked" noStyle>
              <Checkbox disabled={loading} aria-label="記住我">
                記住我
              </Checkbox>
            </Form.Item>
            {showForgotPassword && (
              <Link
                onClick={onForgotPassword}
                style={{ cursor: 'pointer' }}
              >
                忘記密碼？
              </Link>
            )}
          </div>
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

      {/* 版權資訊已移至登入頁左側品牌區 */}
    </div>
  );
};

export default LoginForm;
