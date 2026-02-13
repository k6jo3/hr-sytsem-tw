import {
    AuditOutlined,
    BarChartOutlined,
    BookOutlined,
    ClockCircleOutlined,
    DollarOutlined,
    FieldTimeOutlined,
    FileOutlined,
    HomeOutlined,
    LockOutlined,
    LogoutOutlined,
    ProjectOutlined,
    SafetyOutlined,
    TeamOutlined,
    TrophyOutlined,
    UserAddOutlined,
    UserOutlined
} from '@ant-design/icons';
import { logout } from '@store/authSlice';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { Avatar, Dropdown, Layout, Menu, Space, type MenuProps } from 'antd';
import React, { type ReactNode } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

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
    { key: '/dashboard', icon: <HomeOutlined />, label: '首頁儀表板' },
    {
      key: 'iam',
      icon: <LockOutlined />,
      label: '帳號與權限',
      children: [
        { key: '/admin/users', label: '使用者管理' },
        { key: '/admin/roles', label: '角色權限分配' },
      ],
    },
    {
      key: 'org',
      icon: <TeamOutlined />,
      label: '組織與員工',
      children: [
        { key: '/admin/organization', label: '部門與編制' },
        { key: '/admin/employees', label: '員工基本資料' },
      ],
    },
    {
      key: 'attendance',
      icon: <ClockCircleOutlined />,
      label: '考勤管理',
      children: [
        { key: '/attendance/check-in', label: '每日打卡' },
        { key: '/attendance/leave/apply', label: '請假加班申請' },
        { key: '/attendance/my-records', label: '我的考勤日誌' },
        { key: '/admin/attendance/approvals', label: '考勤例外審核' },
      ],
    },
    {
      key: 'payroll',
      icon: <DollarOutlined />,
      label: '薪資核算',
      children: [
        { key: '/admin/payroll/runs', label: '計薪作業中心' },
        { key: '/profile/payslips', label: '我的電子薪資單' },
        { key: '/admin/payroll/structures', label: '薪資結構設定' },
      ],
    },
    {
      key: 'insurance',
      icon: <SafetyOutlined />,
      label: '保險管理',
      children: [
        { key: '/admin/insurance/enrollments', label: '勞健保加退保' },
        { key: '/admin/insurance/calculator', label: '保費試算工具' },
      ],
    },
    {
      key: 'projects',
      icon: <ProjectOutlined />,
      label: '專案管理',
      children: [
        { key: '/admin/projects', label: '專案與客戶維護' },
        { key: '/admin/projects/customers', label: '合作客戶管理' },
      ],
    },
    {
      key: 'timesheet',
      icon: <FieldTimeOutlined />,
      label: '工時申報',
      children: [
        { key: '/profile/timesheets', label: '每週工時報表' },
        { key: '/admin/timesheets/approval', label: '工時審核看板' },
      ],
    },
    {
      key: 'performance',
      icon: <TrophyOutlined />,
      label: '績效考核',
      children: [
        { key: '/admin/performance/cycles', label: '考核週期管理' },
        { key: '/profile/performance', label: '我的評核表' },
      ],
    },
    { key: '/admin/recruitment', icon: <UserAddOutlined />, label: '招募管理' },
    { key: '/admin/training', icon: <BookOutlined />, label: '教育訓練' },
    { key: '/admin/workflow', icon: <AuditOutlined />, label: '簽核流程' },
    { key: '/admin/notifications', icon: <HomeOutlined />, label: '訊息通知' },
    { key: '/admin/documents', icon: <FileOutlined />, label: '文件管理' },
    { key: '/admin/reports', icon: <BarChartOutlined />, label: '報表中心' },
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
              <span>{user.username}</span>
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
