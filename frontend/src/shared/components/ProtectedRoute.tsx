import React, { type ReactNode, useRef } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAppSelector } from '@store/hooks';
import { message } from 'antd';
import { PageLayout } from './PageLayout';

interface ProtectedRouteProps {
  children: ReactNode;
  /**
   * 是否需要包裹 PageLayout
   * @default true
   */
  requireLayout?: boolean;
  /**
   * 可存取此路由的角色清單
   * 未設定時所有已登入使用者皆可存取
   */
  requiredRoles?: string[];
}

/**
 * Protected Route Component
 * 保護需要身份認證的路由，並支援角色權限控制
 * 未登入時重定向到登入頁面
 * 角色不符時顯示提示訊息並重定向到 /profile（避免環形重導）
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requireLayout = true,
  requiredRoles,
}) => {
  const location = useLocation();
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);
  // 使用 ref 避免重複顯示警告訊息
  const hasWarnedRef = useRef(false);

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 角色權限檢查
  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles = user?.roles ?? [];
    const hasAccess = requiredRoles.some((role) => userRoles.includes(role));
    if (!hasAccess) {
      // 顯示權限不足提示（避免重複觸發）
      if (!hasWarnedRef.current) {
        hasWarnedRef.current = true;
        message.warning('您沒有存取此頁面的權限');
      }
      return <Navigate to="/profile" replace />;
    }
  }

  if (requireLayout) {
    return <PageLayout>{children}</PageLayout>;
  }

  return <>{children}</>;
};
