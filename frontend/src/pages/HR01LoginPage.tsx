import type { LoginFormData } from '@features/auth/api/AuthTypes';
import { ForgotPasswordModal } from '@features/auth/components/ForgotPasswordModal';
import { LoginForm } from '@features/auth/components/LoginForm';
import { useLogin } from '@features/auth/hooks/useLogin';
import { useAppSelector } from '@store/hooks';
import { Card, Typography, message } from 'antd';
import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

/**
 * 取得登入後的預設首頁路徑
 * 所有角色統一導向 Dashboard，Dashboard 會根據角色顯示對應的快捷操作與統計數據
 */
const getDefaultPathByRoles = (_roles: string[]): string => {
  return '/dashboard';
};

/**
 * HR01-P01 - 登入頁面
 * Domain Code: HR01
 */
export const HR01LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, loading, error } = useLogin();
  const { isAuthenticated, user: currentUser } = useAppSelector((state) => state.auth);
  const [forgotPasswordVisible, setForgotPasswordVisible] = React.useState(false);

  // 如果已經登入，重定向到首頁或原本要去的頁面
  useEffect(() => {
    if (isAuthenticated) {
      const from = (location.state as any)?.from?.pathname;
      if (from && from !== '/login') {
        navigate(from, { replace: true });
      } else {
        const defaultPath = getDefaultPathByRoles(currentUser?.roles ?? []);
        navigate(defaultPath, { replace: true });
      }
    }
  }, [isAuthenticated, navigate, location, currentUser]);

  const handleSubmit = async (data: LoginFormData) => {
    try {
      const user = await login(data);
      message.success('登入成功！');

      // 根據保存的路徑或使用者角色導向不同頁面
      const from = (location.state as any)?.from?.pathname;
      if (from && from !== '/login') {
        navigate(from, { replace: true });
      } else {
        // 依據角色導向對應首頁
        const defaultPath = getDefaultPathByRoles(user.roles);
        navigate(defaultPath, { replace: true });
      }
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
          onForgotPassword={() => setForgotPasswordVisible(true)}
        />

        <ForgotPasswordModal
          visible={forgotPasswordVisible}
          onCancel={() => setForgotPasswordVisible(false)}
        />

        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Text type="secondary" style={{ fontSize: 12 }}>
            Version 3.0 | © 2026 HR System
          </Text>
        </div>
      </Card>
    </div>
  );
};
