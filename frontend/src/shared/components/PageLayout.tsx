import {
    BellOutlined,
    LockOutlined,
    LogoutOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    UserOutlined
} from '@ant-design/icons';
import { logout } from '@store/authSlice';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { Avatar, Badge, Button, Dropdown, Layout, Menu, Space, type MenuProps } from 'antd';
import React, { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react';
import { apiClient } from '@shared/api';
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

  // 未讀通知數量
  const [unreadCount, setUnreadCount] = useState(0);

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

  // 載入時取得未讀通知數量（靜默模式，失敗不影響頁面）
  useEffect(() => {
    if (isAuthenticated) {
      apiClient.get<any>('/notifications/unread-count')
        .then((data) => {
          setUnreadCount(data?.unreadCount ?? data?.unread_count ?? 0);
        })
        .catch(() => { /* 靜默失敗，不影響主要功能 */ });
    }
  }, [isAuthenticated]);

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
          background: '#f5f6fa',
          padding: '0 24px',
          boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {/* 漢堡按鈕：桌面與手機皆顯示，可手動展開/收合 Sider */}
          <Button
            type="text"
            icon={siderCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={toggleSider}
            style={{ fontSize: 18 }}
            aria-label={siderCollapsed ? '展開選單' : '收合選單'}
          />
          <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#4a5a82' }}>
            HR System 3.0
          </span>
        </div>
        {isAuthenticated && user && (
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            {/* 通知鈴鐺 */}
            <Badge count={unreadCount} size="small" offset={[-2, 2]}>
              <Button
                type="text"
                icon={<BellOutlined style={{ fontSize: 18 }} />}
                onClick={() => navigate('/profile/notifications')}
                aria-label="通知"
                style={{ color: '#595959' }}
              />
            </Badge>

            {/* 使用者下拉選單 */}
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#667eea' }} />
                <span>{user.displayName}</span>
              </Space>
            </Dropdown>
          </div>
        )}
      </Header>
      <Layout>
        <Sider
          width={250}
          collapsible
          breakpoint="lg"
          collapsedWidth={isBelowBreakpoint ? 0 : 80}
          collapsed={siderCollapsed}
          onCollapse={handleSiderCollapse}
          onBreakpoint={handleBreakpoint}
          trigger={null}
          style={{
            background: '#4a5a82',
            boxShadow: '4px 0 12px rgba(0, 0, 0, 0.08)',
            // 手機展開時以浮動覆蓋方式顯示
            ...(isBelowBreakpoint && !siderCollapsed
              ? { position: 'fixed', height: '100vh', zIndex: 100, top: 64, left: 0 }
              : {}),
          }}
        >
          <Menu
            mode="inline"
            theme="dark"
            selectedKeys={selectedKeys}
            defaultOpenKeys={defaultOpenKeys}
            items={menuItems}
            onClick={handleMenuClick}
            style={{
              borderRight: 0,
              background: '#4a5a82',
            }}
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
        <Layout style={{ background: '#e8ebf0' }}>
          <Content style={{ padding: '24px', minHeight: 280 }}>
            {children}
          </Content>
          <div style={{
            textAlign: 'center',
            padding: '12px 24px',
            color: '#999',
            fontSize: 13,
          }}>
            © 2026 HR System 3.0 — 台灣科技股份有限公司
          </div>
        </Layout>
      </Layout>
    </Layout>
  );
};
