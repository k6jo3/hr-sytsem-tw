import React, { useState } from 'react';
import {
  Card,
  Typography,
  List,
  Tag,
  Space,
  Button,
  Spin,
  Alert,
  Row,
  Col,
  Statistic,
  Badge,
  Tabs,
  Empty,
  message,
} from 'antd';
import {
  BellOutlined,
  CheckOutlined,
  ReloadOutlined,
  MailOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  NotificationOutlined,
  AuditOutlined,
  CheckCircleOutlined,
  WarningOutlined,
} from '@ant-design/icons';
import { useMyNotifications } from '../features/notification/hooks';
import type { NotificationViewModel } from '../features/notification/model/NotificationViewModel';

const { Title, Text, Paragraph } = Typography;

/**
 * 通知類型圖標映射
 */
const getNotificationIcon = (type: string) => {
  const iconMap: Record<string, React.ReactNode> = {
    APPROVAL_REQUEST: <AuditOutlined style={{ color: '#1890ff' }} />,
    APPROVAL_RESULT: <CheckCircleOutlined style={{ color: '#52c41a' }} />,
    REMINDER: <ClockCircleOutlined style={{ color: '#faad14' }} />,
    ANNOUNCEMENT: <NotificationOutlined style={{ color: '#722ed1' }} />,
    ALERT: <WarningOutlined style={{ color: '#ff4d4f' }} />,
  };
  return iconMap[type] || <BellOutlined />;
};

/**
 * 通知項目元件
 */
const NotificationItem: React.FC<{
  notification: NotificationViewModel;
  onMarkAsRead: (id: string) => void;
}> = ({ notification, onMarkAsRead }) => {
  return (
    <List.Item
      style={{
        backgroundColor: notification.isUnread ? '#f6ffed' : 'transparent',
        padding: '16px',
        borderRadius: 8,
        marginBottom: 8,
      }}
      actions={
        notification.isUnread
          ? [
              <Button
                key="read"
                type="link"
                size="small"
                onClick={() => onMarkAsRead(notification.notificationId)}
              >
                標記已讀
              </Button>,
            ]
          : []
      }
    >
      <List.Item.Meta
        avatar={
          <Badge dot={notification.isUnread}>
            <div
              style={{
                width: 40,
                height: 40,
                borderRadius: '50%',
                backgroundColor: '#f0f5ff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: 18,
              }}
            >
              {getNotificationIcon(notification.notificationType)}
            </div>
          </Badge>
        }
        title={
          <Space>
            <Text strong={notification.isUnread}>{notification.title}</Text>
            <Tag color={notification.priorityColor}>{notification.priorityLabel}</Tag>
          </Space>
        }
        description={
          <Space direction="vertical" size={4} style={{ width: '100%' }}>
            <Paragraph
              ellipsis={{ rows: 2 }}
              style={{ marginBottom: 0, color: 'rgba(0,0,0,0.65)' }}
            >
              {notification.content}
            </Paragraph>
            <Space size="middle">
              <Text type="secondary" style={{ fontSize: 12 }}>
                <ClockCircleOutlined /> {notification.timeAgo}
              </Text>
              <Tag style={{ fontSize: 11 }}>{notification.notificationTypeLabel}</Tag>
            </Space>
          </Space>
        }
      />
    </List.Item>
  );
};

/**
 * HR12NotificationPage - 我的通知頁面
 * Feature: notification
 * Page Code: HR12-P02
 */
export const HR12NotificationPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('all');
  const {
    notifications,
    summary,
    unreadCount,
    loading,
    error,
    refresh,
    markAsRead,
    markAllAsRead,
  } = useMyNotifications();

  const handleMarkAsRead = async (notificationId: string) => {
    const result = await markAsRead(notificationId);
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
  };

  const handleMarkAllAsRead = async () => {
    const result = await markAllAsRead();
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
  };

  const getFilteredNotifications = (): NotificationViewModel[] => {
    switch (activeTab) {
      case 'unread':
        return notifications.filter((n) => n.isUnread);
      case 'read':
        return notifications.filter((n) => n.isRead);
      default:
        return notifications;
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return (
      <Card style={{ margin: 24 }}>
        <Alert message="載入失敗" description={error} type="error" showIcon />
      </Card>
    );
  }

  const filteredNotifications = getFilteredNotifications();

  const tabItems = [
    {
      key: 'all',
      label: `全部 (${summary?.totalCount || 0})`,
    },
    {
      key: 'unread',
      label: (
        <Badge count={unreadCount} size="small" offset={[8, 0]}>
          未讀
        </Badge>
      ),
    },
    {
      key: 'read',
      label: '已讀',
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>
              <BellOutlined /> 我的通知
              {unreadCount > 0 && (
                <Badge count={unreadCount} style={{ marginLeft: 8 }} />
              )}
            </Title>
            <Text type="secondary">查看和管理您的通知訊息</Text>
          </div>
          <Space>
            {unreadCount > 0 && (
              <Button icon={<CheckOutlined />} onClick={handleMarkAllAsRead}>
                全部標為已讀
              </Button>
            )}
            <Button icon={<ReloadOutlined />} onClick={refresh}>
              重新整理
            </Button>
          </Space>
        </Space>
      </div>

      {summary && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="全部通知"
                value={summary.totalCount}
                prefix={<BellOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="未讀通知"
                value={summary.unreadCount}
                prefix={<MailOutlined />}
                valueStyle={{ color: summary.unreadCount > 0 ? '#ff4d4f' : undefined }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="審核請求"
                value={summary.approvalRequestCount}
                prefix={<AuditOutlined />}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="今日通知"
                value={summary.todayCount}
                prefix={<ExclamationCircleOutlined />}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />

        {filteredNotifications.length === 0 ? (
          <Empty
            description={activeTab === 'unread' ? '沒有未讀通知' : '沒有通知'}
            style={{ padding: '50px 0' }}
          />
        ) : (
          <List
            itemLayout="horizontal"
            dataSource={filteredNotifications}
            renderItem={(notification) => (
              <NotificationItem
                key={notification.notificationId}
                notification={notification}
                onMarkAsRead={handleMarkAsRead}
              />
            )}
            pagination={{
              pageSize: 10,
              showSizeChanger: false,
            }}
          />
        )}
      </Card>
    </div>
  );
};
