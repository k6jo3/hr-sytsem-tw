/**
 * 公告管理元件
 * Domain Code: HR12
 * 自包含元件：內部呼叫 useAnnouncements hook
 */

import React, { useState } from 'react';
import {
  Card,
  Typography,
  Table,
  Tag,
  Space,
  Button,
  Modal,
  Form,
  Input,
  Select,
  DatePicker,
  Spin,
  Alert,
  message,
  Popconfirm,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { useAnnouncements } from '../hooks/useAnnouncements';
import type { AnnouncementViewModel } from '../model/NotificationViewModel';
import type { CreateAnnouncementRequest } from '../api/NotificationTypes';

const { Paragraph } = Typography;
const { TextArea } = Input;

const PRIORITY_OPTIONS = [
  { label: '低', value: 'LOW' },
  { label: '一般', value: 'NORMAL' },
  { label: '高', value: 'HIGH' },
  { label: '緊急', value: 'URGENT' },
];

const ROLE_OPTIONS = [
  { label: '全體', value: 'ALL' },
  { label: '管理員', value: 'ADMIN' },
  { label: '人資', value: 'HR' },
  { label: '財務', value: 'FINANCE' },
  { label: '專案經理', value: 'PM' },
  { label: '主管', value: 'MANAGER' },
  { label: '員工', value: 'EMPLOYEE' },
];

export const AnnouncementManager: React.FC = () => {
  const {
    announcements,
    loading,
    error,
    saving,
    refresh,
    createAnnouncement,
    updateAnnouncement,
    deleteAnnouncement,
  } = useAnnouncements();

  const [modalVisible, setModalVisible] = useState(false);
  const [editingAnnouncement, setEditingAnnouncement] = useState<AnnouncementViewModel | null>(null);
  const [form] = Form.useForm();

  const handleCreate = () => {
    setEditingAnnouncement(null);
    form.resetFields();
    form.setFieldsValue({ priority: 'NORMAL' });
    setModalVisible(true);
  };

  const handleEdit = (announcement: AnnouncementViewModel) => {
    setEditingAnnouncement(announcement);
    form.setFieldsValue({
      title: announcement.title,
      content: announcement.content,
      priority: announcement.priority,
      target_roles: announcement.targetRoles.length > 0 ? announcement.targetRoles : ['ALL'],
    });
    setModalVisible(true);
  };

  const handleDelete = async (announcementId: string) => {
    const result = await deleteAnnouncement(announcementId);
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const targetRoles = values.target_roles?.includes('ALL') ? [] : values.target_roles;

      if (editingAnnouncement) {
        const result = await updateAnnouncement(editingAnnouncement.announcementId, {
          title: values.title,
          content: values.content,
          priority: values.priority,
          target_roles: targetRoles,
          expires_at: values.expires_at?.toISOString(),
        });
        if (result.success) {
          message.success(result.message);
          setModalVisible(false);
        } else {
          message.error(result.message);
        }
      } else {
        const request: CreateAnnouncementRequest = {
          title: values.title,
          content: values.content,
          priority: values.priority,
          target_roles: targetRoles,
          expires_at: values.expires_at?.toISOString(),
        };
        const result = await createAnnouncement(request);
        if (result.success) {
          message.success(result.message);
          setModalVisible(false);
        } else {
          message.error(result.message);
        }
      }
    } catch {
      // 表單驗證失敗，由 Ant Design 表單元件自行顯示錯誤
    }
  };

  const columns = [
    {
      title: '標題',
      dataIndex: 'title',
      key: 'title',
      width: 250,
      render: (title: string) => (
        <Paragraph ellipsis={{ rows: 1 }} style={{ marginBottom: 0 }}>
          {title}
        </Paragraph>
      ),
    },
    {
      title: '優先級',
      dataIndex: 'priorityLabel',
      key: 'priorityLabel',
      width: 80,
      render: (_: string, record: AnnouncementViewModel) => (
        <Tag color={record.priorityColor}>{record.priorityLabel}</Tag>
      ),
    },
    {
      title: '對象',
      dataIndex: 'targetRolesDisplay',
      key: 'targetRolesDisplay',
      width: 150,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (_: string, record: AnnouncementViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '發布日期',
      dataIndex: 'publishedAtDisplay',
      key: 'publishedAtDisplay',
      width: 150,
      render: (text: string | undefined) => text ?? '-',
    },
    {
      title: '建立日期',
      dataIndex: 'createdAtDisplay',
      key: 'createdAtDisplay',
      width: 150,
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      render: (_: unknown, record: AnnouncementViewModel) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            disabled={record.status === 'REVOKED'}
          >
            編輯
          </Button>
          <Popconfirm
            title="確定要撤銷此公告嗎？"
            onConfirm={() => handleDelete(record.announcementId)}
            okText="確定"
            cancelText="取消"
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={record.status === 'REVOKED'}
            >
              撤銷
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <Alert message="載入失敗" description={error} type="error" showIcon action={<Button onClick={refresh}>重試</Button>} />;
  }

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ReloadOutlined />} onClick={refresh}>重新整理</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>發布公告</Button>
      </Space>

      <Card>
        <Table
          columns={columns}
          dataSource={announcements}
          rowKey="announcementId"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingAnnouncement ? '編輯公告' : '發布新公告'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        confirmLoading={saving}
        width={640}
        okText={editingAnnouncement ? '更新' : '發布'}
        cancelText="取消"
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item
            name="title"
            label="公告標題"
            rules={[{ required: true, message: '請輸入公告標題' }]}
          >
            <Input placeholder="輸入公告標題" />
          </Form.Item>

          <Form.Item
            name="content"
            label="公告內容"
            rules={[{ required: true, message: '請輸入公告內容' }]}
          >
            <TextArea rows={6} placeholder="輸入公告內容" />
          </Form.Item>

          <Form.Item name="priority" label="優先級">
            <Select options={PRIORITY_OPTIONS} />
          </Form.Item>

          <Form.Item name="target_roles" label="通知對象">
            <Select
              mode="multiple"
              options={ROLE_OPTIONS}
              placeholder="選擇通知對象（不選則通知全體）"
            />
          </Form.Item>

          <Form.Item name="expires_at" label="過期日期">
            <DatePicker
              showTime
              placeholder="選擇過期日期（可選）"
              style={{ width: '100%' }}
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};
