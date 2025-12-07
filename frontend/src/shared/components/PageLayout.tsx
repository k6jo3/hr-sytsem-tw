import {
    LogoutOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    SettingOutlined,
    UserOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Avatar, Dropdown, Layout, Menu, Space, Typography } from 'antd';
import React from 'react';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

/**
 * PageLayout 元件屬性
 */
interface PageLayoutProps {
  /** 子元件 */
  children: React.ReactNode;
  /** 使用者名稱 */
  username?: string;
  /** 側邊欄選單項目 */
  menuItems?: MenuProps['items'];
  /** 當前選中的選單 key */
  selectedKey?: string;
  /** 選單點擊回調 */
  onMenuClick?: (key: string) => void;
  /** 登出回調 */
  onLogout?: () => void;
}

/**
 * 頁面佈局共用元件
 * 包含 Header、Sider、Content 三層結構
 */
export const PageLayout: React.FC<PageLayoutProps> = ({
  children,
  username = '使用者',
  menuItems = [],
  selectedKey = '',
  onMenuClick,
  onLogout,
}) => {
  const [collapsed, setCollapsed] = React.useState(false);

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '個人設定',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '登出',
      onClick: onLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        style={{
          overflow: 'auto',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
        }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: collapsed ? 16 : 18,
            fontWeight: 600,
          }}
        >
          {collapsed ? 'HR' : 'HR System'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => onMenuClick?.(key)}
        />
      </Sider>
      <Layout style={{ marginLeft: collapsed ? 80 : 200, transition: 'margin-left 0.2s' }}>
        <Header
          style={{
            padding: '0 24px',
            background: '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
          }}
        >
          <Space>
            {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
              onClick: () => setCollapsed(!collapsed),
              style: { fontSize: 18, cursor: 'pointer' },
            })}
          </Space>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} />
              <Text>{username}</Text>
            </Space>
          </Dropdown>
        </Header>
        <Content
          style={{
            margin: 24,
            padding: 24,
            background: '#fff',
            borderRadius: 8,
            minHeight: 280,
          }}
        >
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};

export default PageLayout;
