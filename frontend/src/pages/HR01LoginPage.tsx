import React from 'react';
import { Card, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { LoginForm } from '@features/auth/components/LoginForm';
import { useLogin } from '@features/auth/hooks/useLogin';
import type { LoginFormData } from '@features/auth/api/AuthTypes';

const { Title, Text } = Typography;

/**
 * HR01-P01 - 登入頁面
 * Domain Code: HR01
 */
export const HR01LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, loading, error } = useLogin();

  const handleSubmit = async (data: LoginFormData) => {
    try {
      await login(data);
      message.success('登入成功！');
      // 登入後導向打卡頁面（員工自助服務）
      // TODO: 根據使用者角色導向不同頁面 (admin -> /admin/employees, employee -> /attendance/check-in)
      navigate('/attendance/check-in');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '登入失敗，請檢查帳號密碼';
      message.error(errorMessage);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '20px',
    }}>
      <Card
        style={{
          width: '100%',
          maxWidth: 400,
          boxShadow: '0 10px 40px rgba(0, 0, 0, 0.1)',
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={2} style={{ color: '#667eea', marginBottom: 8 }}>
            HR System 3.0
          </Title>
          <Text type="secondary">企業級人力資源管理系統</Text>
        </div>

        <LoginForm
          onSubmit={handleSubmit}
          loading={loading}
          error={error?.message ?? null}
        />

        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Text type="secondary" style={{ fontSize: 12 }}>
            Version 3.0 | © 2024 HR System
          </Text>
        </div>
      </Card>
    </div>
  );
};
