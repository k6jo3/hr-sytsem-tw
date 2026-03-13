import {
    LockOutlined,
    LogoutOutlined,
    UserOutlined
} from '@ant-design/icons';
import { logout } from '@store/authSlice';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { Avatar, Dropdown, Layout, Menu, Space, type MenuProps } from 'antd';
import React, { useMemo, type ReactNode } from 'react';
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
        <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#667eea' }}>
          HR System 3.0
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
        <Sider width={250} style={{ background: '#fff' }}>
          <Menu
            mode="inline"
            selectedKeys={selectedKeys}
            defaultOpenKeys={defaultOpenKeys}
            items={menuItems}
            onClick={handleMenuClick}
            style={{ borderRight: 0 }}
          />
        </Sider>
        <Content style={{ padding: '24px', background: '#f0f2f5', minHeight: 280 }}>
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};
