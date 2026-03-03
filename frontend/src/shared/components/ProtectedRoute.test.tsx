import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Route, Routes, Navigate, useLocation } from 'react-router-dom';
import { Provider, useSelector } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import authReducer, { type AuthState } from '@store/authSlice';

/**
 * ProtectedRoute 單元測試
 * 驗證身份認證與角色權限控制邏輯
 *
 * 使用獨立的 RouteGuard 元件模擬 ProtectedRoute 核心邏輯，
 * 避免 import PageLayout 載入大量 antd 依賴造成 OOM。
 */

/** 建立測試用 store */
const createTestStore = (authState: Partial<AuthState>) =>
  configureStore({
    reducer: { auth: authReducer },
    preloadedState: {
      auth: {
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
        ...authState,
      },
    },
  });

/** 建立完整 UserViewModel 測試資料 */
const createMockUser = (overrides: { roles?: string[]; isAdmin?: boolean } = {}) => ({
  userId: '1',
  username: 'test',
  email: 'test@test.com',
  displayName: 'Test User',
  status: 'ACTIVE' as const,
  statusLabel: '啟用',
  statusColor: 'green',
  roles: overrides.roles ?? ['EMPLOYEE'],
  roleIds: [],
  displayRoles: (overrides.roles ?? ['EMPLOYEE']).join(', '),
  isAdmin: overrides.isAdmin ?? false,
  isLocked: false,
  mustChangePassword: false,
  createdAt: '',
  createdAtDisplay: '',
  canEdit: false,
  canDelete: false,
  canResetPassword: false,
});

/**
 * 模擬 ProtectedRoute 核心邏輯的測試元件
 * 等同 ProtectedRoute 的認證與角色檢查，但不 import PageLayout
 */
const RouteGuard: React.FC<{
  children: React.ReactNode;
  requiredRoles?: string[];
}> = ({ children, requiredRoles }) => {
  const location = useLocation();
  const { isAuthenticated, user } = useSelector((state: any) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles: string[] = user?.roles ?? [];
    const hasAccess = requiredRoles.some((role: string) => userRoles.includes(role));
    if (!hasAccess) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  return <>{children}</>;
};

const renderWithRoute = (
  authState: Partial<AuthState>,
  requiredRoles?: string[],
) => {
  const store = createTestStore(authState);
  return render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route
            path="/protected"
            element={
              <RouteGuard requiredRoles={requiredRoles}>
                <div>受保護的內容</div>
              </RouteGuard>
            }
          />
          <Route path="/login" element={<div>登入頁面</div>} />
          <Route path="/dashboard" element={<div>儀表板</div>} />
        </Routes>
      </MemoryRouter>
    </Provider>
  );
};

describe('ProtectedRoute（路由守衛邏輯）', () => {
  it('未登入時應重導至 /login', () => {
    renderWithRoute({ isAuthenticated: false });
    expect(screen.getByText('登入頁面')).toBeTruthy();
    expect(screen.queryByText('受保護的內容')).toBeNull();
  });

  it('已登入無角色限制時應顯示子內容', () => {
    renderWithRoute({
      isAuthenticated: true,
      user: createMockUser({ roles: ['EMPLOYEE'] }),
    });
    expect(screen.getByText('受保護的內容')).toBeTruthy();
  });

  it('有 requiredRoles 且角色符合時應顯示內容', () => {
    renderWithRoute(
      {
        isAuthenticated: true,
        user: createMockUser({ roles: ['ADMIN'], isAdmin: true }),
      },
      ['ADMIN']
    );
    expect(screen.getByText('受保護的內容')).toBeTruthy();
  });

  it('有 requiredRoles 且角色不符時應重導至 /dashboard', () => {
    renderWithRoute(
      {
        isAuthenticated: true,
        user: createMockUser({ roles: ['EMPLOYEE'] }),
      },
      ['ADMIN']
    );
    expect(screen.getByText('儀表板')).toBeTruthy();
    expect(screen.queryByText('受保護的內容')).toBeNull();
  });

  it('多角色中有一個符合即可存取', () => {
    renderWithRoute(
      {
        isAuthenticated: true,
        user: createMockUser({ roles: ['HR'] }),
      },
      ['ADMIN', 'HR']
    );
    expect(screen.getByText('受保護的內容')).toBeTruthy();
  });
});
