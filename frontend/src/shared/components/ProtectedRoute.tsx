import React, { type ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAppSelector } from '@store/hooks';
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
 * 角色不符時重定向到 /dashboard
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requireLayout = true,
  requiredRoles,
}) => {
  const location = useLocation();
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 角色權限檢查
  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles = user?.roles ?? [];
    const hasAccess = requiredRoles.some((role) => userRoles.includes(role));
    if (!hasAccess) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  if (requireLayout) {
    return <PageLayout>{children}</PageLayout>;
  }

  return <>{children}</>;
};
