/**
 * 首頁儀表板頁面 (HR01-P00)
 * 登入後的首頁，顯示系統總覽、快捷操作與待辦事項
 */

import {
  BellOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FileTextOutlined,
  HomeOutlined,
  InboxOutlined,
  TeamOutlined,
  ProjectOutlined,
} from '@ant-design/icons';
import { apiClient } from '@shared/api';
import { MockConfig } from '../config/MockConfig';
import { useAppSelector } from '@store/hooks';
import { Avatar, Card, Col, Empty, List, Modal, Row, Spin, Statistic, Tag, Typography } from 'antd';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

/** 快捷操作項目 */
interface QuickAction {
  title: string;
  icon: React.ReactNode;
  path: string;
  color: string;
  roles?: string[];
}

const QUICK_ACTIONS: QuickAction[] = [
  { title: '每日打卡', icon: <ClockCircleOutlined />, path: '/attendance/check-in', color: '#667eea' },
  { title: '請假申請', icon: <CalendarOutlined />, path: '/attendance/leave/apply', color: '#52c41a' },
  { title: '工時填報', icon: <FileTextOutlined />, path: '/profile/timesheets', color: '#faad14' },
  { title: '我的通知', icon: <BellOutlined />, path: '/profile/notifications', color: '#f5222d' },
  { title: '員工管理', icon: <TeamOutlined />, path: '/admin/employees', color: '#13c2c2', roles: ['ADMIN', 'HR'] },
  { title: '專案總覽', icon: <ProjectOutlined />, path: '/admin/projects', color: '#722ed1', roles: ['ADMIN', 'PM'] },
];

/** 公告資料 */
interface Announcement {
  id: string;
  title: string;
  content: string;
  date: string;
  tag: string;
}

/** 儀表板統計數據 */
interface DashboardStats {
  unreadNotifications: number;
  attendanceDays: number;
  leaveBalance: number;
  pendingTodos: number;
}

/** 待辦事項 */
interface TodoItem {
  id: string;
  title: string;
  type: string;
  status: 'pending' | 'warning';
  path: string;
}

export const HR01DashboardPage: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  const [selectedAnnouncement, setSelectedAnnouncement] = useState<Announcement | null>(null);
  const [stats, setStats] = useState<DashboardStats>({ unreadNotifications: 0, attendanceDays: 0, leaveBalance: 0, pendingTodos: 0 });
  const [todos, setTodos] = useState<TodoItem[]>([]);
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState(true);

  /** 從各 API 取得儀表板資料（mock 模式回傳假資料） */
  const fetchDashboardData = useCallback(async () => {
    setLoading(true);
    let unreadNotifications = 0;
    let attendanceDays = 0;
    let leaveBalance = 0;
    const todoList: TodoItem[] = [];
    const announcementList: Announcement[] = [];

    // Mock 模式：直接回傳模擬資料，不呼叫 API
    if (MockConfig.isEnabled('DASHBOARD') || MockConfig.isEnabled('AUTH')) {
      setStats({ unreadNotifications: 3, attendanceDays: 18, leaveBalance: 7, pendingTodos: 2 });
      setTodos([
        { id: 'wf', title: '2 筆簽核待處理', type: '簽核', status: 'pending', path: '/admin/workflow' },
        { id: 'notif', title: '3 則未讀通知', type: '通知', status: 'warning', path: '/profile/notifications' },
      ]);
      setAnnouncements([
        { id: '1', title: '2026 年第一季考核開始', content: '請各部門主管於 3/31 前完成部屬考核評分。', date: '2026-03-15', tag: '人事公告' },
        { id: '2', title: '系統維護通知', content: '系統將於 3/22（六）02:00-06:00 進行例行維護。', date: '2026-03-10', tag: '系統通知' },
        { id: '3', title: '新版請假流程上線', content: '自 3/1 起，請假申請改為線上簽核，紙本表單停用。', date: '2026-03-01', tag: '制度變更' },
      ]);
      setLoading(false);
      return;
    }

    // 平行呼叫各 API，任一失敗不影響其他（靜默模式：不彈錯誤 toast）
    const results = await Promise.allSettled([
      // 1. 未讀通知數
      apiClient.get<any>('/notifications/unread-count', { silent: true }),
      // 2. 本月考勤紀錄
      apiClient.get<any>('/attendance/records', {
        params: { page: 1, size: 1 },
        silent: true,
      }),
      // 3. 假別餘額
      apiClient.get<any>('/leave/balances', { silent: true }).catch(() => null),
      // 4. 簽核待處理
      apiClient.get<any>('/workflows/instances', {
        params: { status: 'PENDING', page: 1, size: 1 },
        silent: true,
      }).catch(() => null),
      // 5. 公告列表
      apiClient.get<any>('/notifications/announcements', {
        params: { page: 1, size: 5 },
        silent: true,
      }),
    ]);

    // 解析未讀通知數
    if (results[0]?.status === 'fulfilled' && results[0].value) {
      unreadNotifications = results[0].value.unreadCount ?? results[0].value.unread_count ?? 0;
    }

    // 解析考勤紀錄數
    if (results[1]?.status === 'fulfilled' && results[1].value) {
      const data = results[1].value;
      attendanceDays = data.totalElements ?? data.total ?? data.items?.length ?? 0;
    }

    // 解析假別餘額
    if (results[2]?.status === 'fulfilled' && results[2].value) {
      const data = results[2].value;
      // 嘗試從回傳中找到特休餘額
      if (Array.isArray(data)) {
        const annual = data.find((b: any) => b.leaveType === 'ANNUAL' || b.leave_type === 'ANNUAL');
        leaveBalance = annual?.balance ?? annual?.remaining ?? 0;
      } else if (data.items) {
        const annual = data.items.find((b: any) => b.leaveType === 'ANNUAL' || b.leave_type === 'ANNUAL');
        leaveBalance = annual?.balance ?? annual?.remaining ?? 0;
      }
    }

    // 解析簽核待處理
    if (results[3]?.status === 'fulfilled' && results[3].value) {
      const data = results[3].value;
      const pendingCount = data.totalElements ?? data.total ?? 0;
      if (pendingCount > 0) {
        todoList.push({
          id: 'wf',
          title: `${pendingCount} 筆簽核待處理`,
          type: '簽核',
          status: 'pending',
          path: '/admin/workflow',
        });
      }
    }

    // 解析公告
    if (results[4]?.status === 'fulfilled' && results[4].value) {
      const data = results[4].value;
      const items = data.items ?? data.content ?? [];
      for (const item of items) {
        announcementList.push({
          id: item.id ?? item.announcementId ?? item.announcement_id,
          title: item.title,
          content: item.content ?? item.body ?? '',
          date: (item.publishedAt ?? item.published_at ?? item.createdAt ?? item.created_at ?? '').substring(0, 10),
          tag: item.category ?? item.tag ?? '公告',
        });
      }
    }

    // 如果有未讀通知，加到待辦
    if (unreadNotifications > 0) {
      todoList.push({
        id: 'notif',
        title: `${unreadNotifications} 則未讀通知`,
        type: '通知',
        status: 'warning',
        path: '/profile/notifications',
      });
    }

    setStats({
      unreadNotifications,
      attendanceDays,
      leaveBalance,
      pendingTodos: todoList.length,
    });
    setTodos(todoList);
    setAnnouncements(announcementList);
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  if (!user) return null;

  const userRoles = user.roles || [];

  /** 根據角色篩選可用的快捷操作 */
  const availableActions = QUICK_ACTIONS.filter(
    (action) => !action.roles || action.roles.some((r) => userRoles.includes(r))
  );

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto' }}>
      {/* 歡迎區塊 */}
      <Card style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Avatar
            size={56}
            icon={<HomeOutlined />}
            style={{ backgroundColor: '#667eea', marginRight: 16 }}
          />
          <div>
            <Title level={3} style={{ margin: 0 }}>
              {getGreeting()}，{`${user.lastName ?? ''}${user.firstName ?? ''}`.trim() || user.displayName}
            </Title>
            <Text type="secondary">歡迎使用人力資源暨專案管理系統</Text>
          </div>
        </div>
      </Card>

      {/* 統計摘要 */}
      <Spin spinning={loading}>
        <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
          <Col xs={12} sm={6}>
            <Card hoverable onClick={() => navigate('/admin/workflow')} style={{ cursor: 'pointer' }} styles={{ body: { padding: '20px 24px' } }}>
              <Statistic title="待辦事項" value={stats.pendingTodos} prefix={<FileTextOutlined />} valueStyle={{ color: '#667eea' }} />
            </Card>
          </Col>
          <Col xs={12} sm={6}>
            <Card hoverable onClick={() => navigate('/attendance/my-records')} style={{ cursor: 'pointer' }} styles={{ body: { padding: '20px 24px' } }}>
              <Statistic title="本月出勤" value={stats.attendanceDays} suffix="天" prefix={<CheckCircleOutlined />} valueStyle={{ color: '#52c41a' }} />
            </Card>
          </Col>
          <Col xs={12} sm={6}>
            <Card hoverable onClick={() => navigate('/profile/notifications')} style={{ cursor: 'pointer' }} styles={{ body: { padding: '20px 24px' } }}>
              <Statistic title="未讀通知" value={stats.unreadNotifications} prefix={<BellOutlined />} valueStyle={{ color: '#faad14' }} />
            </Card>
          </Col>
          <Col xs={12} sm={6}>
            <Card hoverable onClick={() => navigate('/attendance/leave/balance')} style={{ cursor: 'pointer' }} styles={{ body: { padding: '20px 24px' } }}>
              <Statistic title="特休餘額" value={stats.leaveBalance} suffix="天" prefix={<CalendarOutlined />} valueStyle={{ color: '#13c2c2' }} />
            </Card>
          </Col>
        </Row>
      </Spin>

      <Row gutter={[16, 16]}>
        {/* 快捷操作 */}
        <Col xs={24} lg={14}>
          <Card title="快捷操作" style={{ marginBottom: 16 }}>
            <Row gutter={[12, 12]}>
              {availableActions.map((action) => (
                <Col xs={12} sm={8} key={action.path}>
                  <Card
                    hoverable
                    size="small"
                    onClick={() => navigate(action.path)}
                    style={{ textAlign: 'center', cursor: 'pointer' }}
                  >
                    <div style={{ fontSize: 28, color: action.color, marginBottom: 8 }}>
                      {action.icon}
                    </div>
                    <Text>{action.title}</Text>
                  </Card>
                </Col>
              ))}
            </Row>
          </Card>

          {/* 系統公告 */}
          <Card title="系統公告">
            {announcements.length === 0 ? (
              <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="目前沒有公告" />
            ) : (
              <List
                dataSource={announcements}
                renderItem={(item) => (
                  <List.Item
                    style={{ cursor: 'pointer' }}
                    onClick={() => setSelectedAnnouncement(item)}
                  >
                    <List.Item.Meta
                      title={
                        <span>
                          <Tag color="blue">{item.tag}</Tag>
                          {item.title}
                        </span>
                      }
                      description={item.date}
                    />
                  </List.Item>
                )}
              />
            )}
          </Card>
        </Col>

        {/* 待辦事項 */}
        <Col xs={24} lg={10}>
          <Card title="待辦事項">
            {todos.length === 0 ? (
              <Empty
                image={<InboxOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />}
                description="目前沒有待辦事項"
              />
            ) : (
              <List
                dataSource={todos}
                renderItem={(item) => (
                  <List.Item
                    style={{ cursor: 'pointer' }}
                    onClick={() => navigate(item.path)}
                  >
                    <List.Item.Meta
                      avatar={
                        <Tag color={item.status === 'warning' ? 'orange' : 'blue'}>
                          {item.type}
                        </Tag>
                      }
                      title={<span style={{ color: '#1677ff' }}>{item.title}</span>}
                    />
                  </List.Item>
                )}
              />
            )}
          </Card>
        </Col>
      </Row>

      {/* 公告內文 Modal */}
      <Modal
        title={
          selectedAnnouncement && (
            <span>
              <Tag color="blue">{selectedAnnouncement.tag}</Tag>
              {selectedAnnouncement.title}
            </span>
          )
        }
        open={!!selectedAnnouncement}
        onCancel={() => setSelectedAnnouncement(null)}
        footer={null}
        width={600}
      >
        {selectedAnnouncement && (
          <div>
            <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
              發布日期：{selectedAnnouncement.date}
            </Text>
            <Text style={{ fontSize: 14, lineHeight: 1.8 }}>
              {selectedAnnouncement.content}
            </Text>
          </div>
        )}
      </Modal>
    </div>
  );
};

/** 根據時間回傳問候語 */
function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 12) return '早安';
  if (hour < 18) return '午安';
  return '晚安';
}

export default HR01DashboardPage;
