import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Checkbox, Form, Input, Typography } from 'antd';
import React from 'react';
import type { LoginRequest } from '../api/AuthTypes';
import { useLogin } from '../hooks/useLogin';

const { Title, Text } = Typography;

/**
 * LoginForm 元件屬性
 */
interface LoginFormProps {
  /** 登入成功後的回調 */
  onSuccess?: () => void;
  /** 標題 (可選) */
  title?: string;
}

/**
 * 登入表單元件
 * 使用 Ant Design 5 Form 元件與 useLogin Hook
 */
export const LoginForm: React.FC<LoginFormProps> = ({
  onSuccess,
  title = '歡迎登入',
}) => {
  const { login, isLoading, error, clearError } = useLogin();
  const [form] = Form.useForm<LoginRequest>();

  const handleSubmit = async (values: LoginRequest) => {
    const success = await login(values);
    if (success && onSuccess) {
      onSuccess();
    }
  };

  const handleValuesChange = () => {
    if (error) {
      clearError();
    }
  };

  return (
    <Card
      style={{
        maxWidth: 400,
        margin: '0 auto',
        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
      }}
    >
      <div style={{ textAlign: 'center', marginBottom: 24 }}>
        <Title level={3} style={{ marginBottom: 8 }}>
          {title}
        </Title>
        <Text type="secondary">人力資源暨專案管理系統</Text>
      </div>

      {error && (
        <Alert
          message={error}
          type="error"
          showIcon
          closable
          onClose={clearError}
          style={{ marginBottom: 16 }}
        />
      )}

      <Form
        form={form}
        name="login"
        onFinish={handleSubmit}
        onValuesChange={handleValuesChange}
        autoComplete="off"
        layout="vertical"
        size="large"
      >
        <Form.Item
          name="username"
          rules={[
            { required: true, message: '請輸入帳號' },
            { min: 3, message: '帳號至少 3 個字元' },
          ]}
        >
          <Input
            prefix={<UserOutlined />}
            placeholder="帳號"
            autoComplete="username"
          />
        </Form.Item>

        <Form.Item
          name="password"
          rules={[
            { required: true, message: '請輸入密碼' },
            { min: 6, message: '密碼至少 6 個字元' },
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="密碼"
            autoComplete="current-password"
          />
        </Form.Item>

        <Form.Item name="rememberMe" valuePropName="checked">
          <Checkbox>記住我</Checkbox>
        </Form.Item>

        <Form.Item style={{ marginBottom: 0 }}>
          <Button
            type="primary"
            htmlType="submit"
            loading={isLoading}
            block
            style={{ height: 44 }}
          >
            登入
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default LoginForm;
