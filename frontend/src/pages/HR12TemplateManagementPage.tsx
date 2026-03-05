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
  Switch,
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
  MailOutlined,
} from '@ant-design/icons';
import { useNotificationTemplates } from '../features/notification/hooks/useNotificationTemplates';
import type { NotificationTemplateViewModel } from '../features/notification/model/NotificationViewModel';
import type {
  CreateNotificationTemplateRequest,
  NotificationChannel,
} from '../features/notification/api/NotificationTypes';

const { Title, Text } = Typography;
const { TextArea } = Input;

const CHANNEL_OPTIONS: { label: string; value: NotificationChannel }[] = [
  { label: '系統內', value: 'IN_APP' },
  { label: '電郵', value: 'EMAIL' },
  { label: '推播', value: 'PUSH' },
  { label: 'Teams', value: 'TEAMS' },
  { label: 'LINE', value: 'LINE' },
];

/**
 * HR12TemplateManagementPage - 通知範本管理頁面
 * Feature: notification
 * Page Code: HR12-P01
 */
export const HR12TemplateManagementPage: React.FC = () => {
  const {
    templates,
    loading,
    error,
    saving,
    refresh,
    createTemplate,
    updateTemplate,
    deleteTemplate,
  } = useNotificationTemplates();

  const [modalVisible, setModalVisible] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState<NotificationTemplateViewModel | null>(null);
  const [form] = Form.useForm();

  const handleCreate = () => {
    setEditingTemplate(null);
    form.resetFields();
    form.setFieldsValue({ default_channels: ['IN_APP', 'EMAIL'] });
    setModalVisible(true);
  };

  const handleEdit = (template: NotificationTemplateViewModel) => {
    setEditingTemplate(template);
    form.setFieldsValue({
      template_code: template.templateCode,
      template_name: template.templateName,
      subject: template.subject,
      body: template.body,
      default_channels: template.defaultChannels,
      is_active: template.isActive,
    });
    setModalVisible(true);
  };

  const handleDelete = async (templateId: string) => {
    const result = await deleteTemplate(templateId);
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingTemplate) {
        const result = await updateTemplate(editingTemplate.templateId, {
          template_name: values.template_name,
          subject: values.subject,
          body: values.body,
          default_channels: values.default_channels,
          is_active: values.is_active,
        });
        if (result.success) {
          message.success(result.message);
          setModalVisible(false);
        } else {
          message.error(result.message);
        }
      } else {
        const request: CreateNotificationTemplateRequest = {
          template_code: values.template_code,
          template_name: values.template_name,
          subject: values.subject,
          body: values.body,
          default_channels: values.default_channels,
        };
        const result = await createTemplate(request);
        if (result.success) {
          message.success(result.message);
          setModalVisible(false);
        } else {
          message.error(result.message);
        }
      }
    } catch {
      // 表單驗證失敗
    }
  };

  const columns = [
    {
      title: '範本代碼',
      dataIndex: 'templateCode',
      key: 'templateCode',
      width: 160,
      render: (code: string) => <Text code>{code}</Text>,
    },
    {
      title: '範本名稱',
      dataIndex: 'templateName',
      key: 'templateName',
      width: 200,
    },
    {
      title: '主旨',
      dataIndex: 'subject',
      key: 'subject',
      ellipsis: true,
    },
    {
      title: '預設渠道',
      dataIndex: 'defaultChannelsDisplay',
      key: 'defaultChannelsDisplay',
      width: 180,
      render: (_: string, record: NotificationTemplateViewModel) => (
        <Space size={4} wrap>
          {record.defaultChannels.map((ch) => (
            <Tag key={ch} color="blue">{ch}</Tag>
          ))}
        </Space>
      ),
    },
    {
      title: '狀態',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (_: boolean, record: NotificationTemplateViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '建立日期',
      dataIndex: 'createdAtDisplay',
      key: 'createdAtDisplay',
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      render: (_: unknown, record: NotificationTemplateViewModel) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            編輯
          </Button>
          <Popconfirm
            title="確定要刪除此範本嗎？"
            onConfirm={() => handleDelete(record.templateId)}
            okText="確定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              刪除
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
    return (
      <Card style={{ margin: 24 }}>
        <Alert message="載入失敗" description={error} type="error" showIcon />
      </Card>
    );
  }

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>
              <MailOutlined /> 通知範本管理
            </Title>
            <Text type="secondary">管理系統通知的訊息範本</Text>
          </div>
          <Space>
            <Button icon={<ReloadOutlined />} onClick={refresh}>
              重新整理
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增範本
            </Button>
          </Space>
        </Space>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={templates}
          rowKey="templateId"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingTemplate ? '編輯通知範本' : '新增通知範本'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        confirmLoading={saving}
        width={640}
        okText={editingTemplate ? '更新' : '建立'}
        cancelText="取消"
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item
            name="template_code"
            label="範本代碼"
            rules={[
              { required: true, message: '請輸入範本代碼' },
              { pattern: /^[A-Z][A-Z0-9_]*$/, message: '範本代碼僅允許大寫英文、數字與底線' },
            ]}
          >
            <Input
              placeholder="例如：LEAVE_APPROVED"
              disabled={!!editingTemplate}
            />
          </Form.Item>

          <Form.Item
            name="template_name"
            label="範本名稱"
            rules={[{ required: true, message: '請輸入範本名稱' }]}
          >
            <Input placeholder="例如：請假核准通知" />
          </Form.Item>

          <Form.Item name="subject" label="主旨">
            <Input placeholder="通知郵件主旨（支援變數 {{variable}}）" />
          </Form.Item>

          <Form.Item
            name="body"
            label="內容"
            rules={[{ required: true, message: '請輸入範本內容' }]}
          >
            <TextArea
              rows={6}
              placeholder="通知內容（支援變數 {{employee_name}}、{{date}} 等）"
            />
          </Form.Item>

          <Form.Item
            name="default_channels"
            label="預設渠道"
            rules={[{ required: true, message: '請選擇至少一個渠道' }]}
          >
            <Select
              mode="multiple"
              options={CHANNEL_OPTIONS}
              placeholder="選擇通知渠道"
            />
          </Form.Item>

          {editingTemplate && (
            <Form.Item name="is_active" label="啟用狀態" valuePropName="checked">
              <Switch checkedChildren="啟用" unCheckedChildren="停用" />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};
