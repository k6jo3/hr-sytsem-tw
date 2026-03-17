import {
    LockOutlined,
    LogoutOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    UserOutlined
} from '@ant-design/icons';
import { logout } from '@store/authSlice';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { Avatar, Button, Dropdown, Layout, Menu, Space, type MenuProps } from 'antd';
import React, { useCallback, useMemo, useState, type ReactNode } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { MENU_CONFIG } from '../config/menuConfig';
import { MenuFactory } from '../factory/MenuFactory';

const { Header, Sider, Content } = Layout;

interface PageLayoutProps {
  children: ReactNode;
}

/**
 * Page Layout Component
 * 提供統一的頁面佈局，包含側邊欄導航
 */
export const PageLayout: React.FC<PageLayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();
  const { user, isAuthenticated } = useAppSelector((state) => state.auth);

  // 側邊欄收合狀態管理（響應式設計）
  const [siderCollapsed, setSiderCollapsed] = useState(false);
  // 記錄是否因為螢幕斷點自動收合（區分手動操作與自動觸發）
  const [isBelowBreakpoint, setIsBelowBreakpoint] = useState(false);

  /** 手動切換 Sider 收合狀態 */
  const handleSiderCollapse = useCallback((collapsed: boolean) => {
    setSiderCollapsed(collapsed);
  }, []);

  /** 螢幕斷點觸發：小於 lg（992px）時自動收合 */
  const handleBreakpoint = useCallback((broken: boolean) => {
    setIsBelowBreakpoint(broken);
    setSiderCollapsed(broken);
  }, []);

  /** 切換漢堡按鈕 */
  const toggleSider = useCallback(() => {
    setSiderCollapsed((prev) => !prev);
  }, []);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '個人資料',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'password',
      icon: <LockOutlined />,
      label: '修改密碼',
      onClick: () => navigate('/profile/password'),
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '登出',
      onClick: handleLogout,
    },
  ];

  const userRoles = useMemo(() => user?.roles ?? [], [user?.roles]);

  const menuItems = useMemo(
    () => MenuFactory.createMenuItems(MENU_CONFIG, userRoles),
    [userRoles]
  );

  const selectedKeys = useMemo(
    () => {
      const key = MenuFactory.findSelectedKey(location.pathname, MENU_CONFIG);
      return key ? [key] : [];
    },
    [location.pathname]
  );

  const defaultOpenKeys = useMemo(
    () => MenuFactory.findOpenKeys(location.pathname, MENU_CONFIG),
    [location.pathname]
  );

  const handleMenuClick: MenuProps['onClick'] = (e) => {
    navigate(e.key);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: '#fff',
          padding: '0 24px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {/* 漢堡按鈕：小螢幕時顯示，可手動展開/收合 Sider */}
          {isBelowBreakpoint && (
            <Button
              type="text"
              icon={siderCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={toggleSider}
              style={{ fontSize: 18 }}
              aria-label={siderCollapsed ? '展開選單' : '收合選單'}
            />
          )}
          <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#667eea' }}>
            HR System 3.0
          </span>
        </div>
        {isAuthenticated && user && (
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#667eea' }} />
              <span>{user.displayName}</span>
            </Space>
          </Dropdown>
        )}
      </Header>
      <Layout>
        <Sider
          width={250}
          collapsible
          breakpoint="lg"
          collapsedWidth={0}
          collapsed={siderCollapsed}
          onCollapse={handleSiderCollapse}
          onBreakpoint={handleBreakpoint}
          trigger={null}
          style={{
            background: '#fff',
            // 小螢幕時 Sider 以浮動覆蓋方式顯示
            ...(isBelowBreakpoint
              ? { position: 'fixed', height: '100vh', zIndex: 100, top: 64, left: 0 }
              : {}),
          }}
        >
          <Menu
            mode="inline"
            selectedKeys={selectedKeys}
            defaultOpenKeys={defaultOpenKeys}
            items={menuItems}
            onClick={handleMenuClick}
            style={{ borderRight: 0 }}
          />
        </Sider>
        {/* 小螢幕展開 Sider 時的遮罩層，點擊可收合 */}
        {isBelowBreakpoint && !siderCollapsed && (
          <div
            onClick={toggleSider}
            style={{
              position: 'fixed',
              inset: 0,
              zIndex: 99,
              background: 'rgba(0, 0, 0, 0.45)',
            }}
          />
        )}
        <Content style={{ padding: '24px', background: '#f0f2f5', minHeight: 280 }}>
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};
