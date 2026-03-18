import type { LoginFormData } from '@features/auth/api/AuthTypes';
import { ForgotPasswordModal } from '@features/auth/components/ForgotPasswordModal';
import { LoginForm } from '@features/auth/components/LoginForm';
import { useLogin } from '@features/auth/hooks/useLogin';
import { useAppSelector } from '@store/hooks';
import { CheckCircleOutlined } from '@ant-design/icons';
import { Typography, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

/**
 * 取得登入後的預設首頁路徑
 * 所有角色統一導向 Dashboard，Dashboard 會根據角色顯示對應的快捷操作與統計數據
 */
const getDefaultPathByRoles = (_roles: string[]): string => {
  return '/dashboard';
};

/** 品牌區功能亮點 */
const FEATURE_HIGHLIGHTS = [
  '14 大模組全面覆蓋人資管理',
  '智慧薪資計算與勞健保試算',
  '多層簽核流程與即時通知',
  '專案管理與工時追蹤整合',
];

/** 響應式斷點（px） */
const MOBILE_BREAKPOINT = 768;

/**
 * HR01-P01 - 登入頁面
 * Domain Code: HR01
 * 左右分割佈局：左側品牌展示區 + 右側登入表單區
 */
export const HR01LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, loading, error } = useLogin();
  const { isAuthenticated, user: currentUser } = useAppSelector((state) => state.auth);
  const [forgotPasswordVisible, setForgotPasswordVisible] = React.useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < MOBILE_BREAKPOINT);

  // 監聽視窗大小變化，判斷是否為行動裝置
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < MOBILE_BREAKPOINT);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

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
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* 左側品牌展示區 — 小螢幕時隱藏 */}
      {!isMobile && (
        <div
          style={{
            flex: '0 0 60%',
            background: 'linear-gradient(135deg, #4a5a82 0%, #3a4a6e 50%, #2d3a5c 100%)',
            boxShadow: '6px 0 20px rgba(0, 0, 0, 0.12)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            padding: 48,
            position: 'relative',
          }}
        >
          {/* Logo 區域 */}
          <div
            style={{
              width: 72,
              height: 72,
              borderRadius: '50%',
              background: 'rgba(255, 255, 255, 0.1)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: 24,
              border: '2px solid rgba(255, 255, 255, 0.2)',
            }}
          >
            <span style={{ fontSize: 32, color: '#fff', fontWeight: 700 }}>HR</span>
          </div>

          {/* 系統名稱 */}
          <Title
            level={1}
            style={{
              color: '#fff',
              fontSize: 36,
              marginBottom: 8,
              fontWeight: 600,
            }}
          >
            HR System
          </Title>

          {/* Slogan */}
          <Text
            style={{
              color: '#fff',
              fontSize: 20,
              fontWeight: 300,
              marginBottom: 48,
            }}
          >
            智慧人力資源管理平台
          </Text>

          {/* 功能亮點列表 */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {FEATURE_HIGHLIGHTS.map((text) => (
              <div
                key={text}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 12,
                }}
              >
                <CheckCircleOutlined
                  style={{ color: 'rgba(255, 255, 255, 0.7)', fontSize: 18 }}
                />
                <span style={{ color: 'rgba(255, 255, 255, 0.8)', fontSize: 15 }}>
                  {text}
                </span>
              </div>
            ))}
          </div>

          {/* 底部版本資訊 */}
          <div
            style={{
              position: 'absolute',
              bottom: 32,
              color: 'rgba(255, 255, 255, 0.4)',
              fontSize: 13,
            }}
          >
            Version 3.0 | &copy; 2026 HR System
          </div>
        </div>
      )}

      {/* 右側登入表單區 */}
      <div
        style={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          padding: 48,
          background: '#f8f9fc',
        }}
      >
        <div style={{ maxWidth: 400, width: '100%', margin: '0 auto' }}>
          {/* 歡迎文字 */}
          <Title level={2} style={{ color: '#4a5a82', marginBottom: 8 }}>
            歡迎回來
          </Title>
          <Text type="secondary" style={{ display: 'block', marginBottom: 32 }}>
            請登入您的帳號以繼續
          </Text>

          {/* 登入表單 */}
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
        </div>
      </div>
    </div>
  );
};
