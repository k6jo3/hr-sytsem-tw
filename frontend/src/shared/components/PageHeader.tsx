import { HomeOutlined } from '@ant-design/icons';
import { Breadcrumb, Space, Typography } from 'antd';
import React from 'react';
import { Link } from 'react-router-dom';

const { Title, Text } = Typography;

export interface BreadcrumbItem {
  /** 麵包屑顯示文字 */
  title: string;
  /** 可選路徑，最後一項通常不需要 path（表示當前頁） */
  path?: string;
}

interface PageHeaderProps {
  /** 頁面標題 */
  title: string;
  /** 副標題說明 */
  subtitle?: string;
  /** 麵包屑路徑，不含首頁（自動加入） */
  breadcrumbs?: BreadcrumbItem[];
  /** 右側操作按鈕區域 */
  extra?: React.ReactNode;
}

/**
 * 統一頁面標題元件
 * 包含麵包屑導航、標題、副標題及操作按鈕區域
 */
export const PageHeader: React.FC<PageHeaderProps> = ({
  title,
  subtitle,
  breadcrumbs = [],
  extra,
}) => {
  const breadcrumbItems = [
    {
      title: (
        <Link to="/dashboard">
          <HomeOutlined /> 首頁
        </Link>
      ),
    },
    ...breadcrumbs.map((item, index) => ({
      title:
        item.path && index < breadcrumbs.length - 1 ? (
          <Link to={item.path}>{item.title}</Link>
        ) : (
          item.title
        ),
    })),
  ];

  return (
    <div style={{ marginBottom: 24 }}>
      <Breadcrumb items={breadcrumbItems} style={{ marginBottom: 12 }} />
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
        }}
      >
        <div>
          <Title level={3} style={{ margin: 0 }}>
            {title}
          </Title>
          {subtitle && (
            <Text type="secondary" style={{ marginTop: 4, display: 'block' }}>
              {subtitle}
            </Text>
          )}
        </div>
        {extra && <Space>{extra}</Space>}
      </div>
    </div>
  );
};
