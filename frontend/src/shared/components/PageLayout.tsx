import React, { type ReactNode } from 'react';
import { Layout, Menu, type MenuProps } from 'antd';
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
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

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

  const menuItems: MenuProps['items'] = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首頁',
    },
    {
      key: '/employees',
      icon: <TeamOutlined />,
      label: '員工管理',
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
      <Header style={{ display: 'flex', alignItems: 'center', background: '#fff', padding: '0 24px' }}>
        <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#667eea' }}>
          HR System 3.0
        </div>
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
