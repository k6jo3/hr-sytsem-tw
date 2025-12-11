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
}

/**
 * Protected Route Component
 * 保護需要身份認證的路由
 * 未登入時重定向到登入頁面
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requireLayout = true,
}) => {
  const location = useLocation();
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  if (!isAuthenticated) {
    // 保存當前路徑，登入後可以重定向回來
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requireLayout) {
    return <PageLayout>{children}</PageLayout>;
  }

  return <>{children}</>;
};
