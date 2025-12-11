import React, { type ReactNode } from 'react';
import { Layout, Menu, type MenuProps, Avatar, Dropdown, Space } from 'antd';
import {
  HomeOutlined,
  TeamOutlined,
  ClockCircleOutlined,
  DollarOutlined,
  SafetyOutlined,
  ProjectOutlined,
  FieldTimeOutlined,
  TrophyOutlined,
  UserAddOutlined,
  BookOutlined,
  AuditOutlined,
  BellOutlined,
  FileOutlined,
  BarChartOutlined,
  UserOutlined,
  SafetyCertificateOutlined,
  LockOutlined,
  LogoutOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAppSelector, useAppDispatch } from '@store/hooks';
import { logout } from '@store/authSlice';

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

  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <HomeOutlined />,
      label: '首頁',
    },
    {
      key: 'admin',
      icon: <SettingOutlined />,
      label: '系統管理',
      children: [
        { key: '/admin/users', icon: <UserOutlined />, label: '使用者管理' },
        { key: '/admin/roles', icon: <SafetyCertificateOutlined />, label: '角色權限管理' },
        { key: '/admin/employees', icon: <TeamOutlined />, label: '員工管理' },
      ],
    },
    {
      key: '/attendance',
      icon: <ClockCircleOutlined />,
      label: '考勤管理',
      children: [
        { key: '/attendance/check-in', label: '打卡' },
        { key: '/attendance/leaves', label: '請假管理' },
      ],
    },
    {
      key: '/payroll',
      icon: <DollarOutlined />,
      label: '薪資管理',
    },
    {
      key: '/insurance',
      icon: <SafetyOutlined />,
      label: '保險管理',
    },
    {
      key: '/projects',
      icon: <ProjectOutlined />,
      label: '專案管理',
    },
    {
      key: '/timesheet',
      icon: <FieldTimeOutlined />,
      label: '工時管理',
    },
    {
      key: '/performance',
      icon: <TrophyOutlined />,
      label: '績效管理',
    },
    {
      key: '/recruitment',
      icon: <UserAddOutlined />,
      label: '招募管理',
    },
    {
      key: '/training',
      icon: <BookOutlined />,
      label: '訓練管理',
    },
    {
      key: '/workflow',
      icon: <AuditOutlined />,
      label: '簽核流程',
    },
    {
      key: '/notifications',
      icon: <BellOutlined />,
      label: '通知',
    },
    {
      key: '/documents',
      icon: <FileOutlined />,
      label: '文件管理',
    },
    {
      key: '/reports',
      icon: <BarChartOutlined />,
      label: '報表分析',
    },
  ];

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
              <span>{user.fullName}</span>
            </Space>
          </Dropdown>
        )}
      </Header>
      <Layout>
        <Sider width={250} style={{ background: '#fff' }}>
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
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
