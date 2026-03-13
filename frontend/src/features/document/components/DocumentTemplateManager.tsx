/**
 * 文件範本管理元件
 * Domain Code: HR13
 * 範本 Table + CRUD Modal
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
  message,
  Popconfirm,
  Descriptions,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { useTemplates } from '../hooks';
import { DocumentApi } from '../api';
import type { DocumentTemplateViewModel } from '../model/DocumentViewModel';
import type { TemplateType } from '../api/DocumentTypes';

const { Text } = Typography;
const { TextArea } = Input;

const TEMPLATE_TYPE_OPTIONS: { label: string; value: TemplateType }[] = [
  { label: '在職證明', value: 'EMPLOYMENT_CERTIFICATE' },
  { label: '薪資證明', value: 'SALARY_CERTIFICATE' },
  { label: '離職證明', value: 'RESIGNATION_CERTIFICATE' },
  { label: '出勤紀錄', value: 'ATTENDANCE_RECORD' },
  { label: '薪資單', value: 'PAYSLIP' },
  { label: '扣繳憑單', value: 'TAX_WITHHOLDING' },
  { label: '自訂範本', value: 'CUSTOM' },
];

export const DocumentTemplateManager: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const { data, isLoading, refetch } = useTemplates({ page: currentPage, pageSize: 10 });
  const [modalVisible, setModalVisible] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState<DocumentTemplateViewModel | null>(null);
  const [saving, setSaving] = useState(false);
  const [detailTemplate, setDetailTemplate] = useState<DocumentTemplateViewModel | null>(null);
  const [form] = Form.useForm();

  const handleCreate = () => {
    setEditingTemplate(null);
    form.resetFields();
    form.setFieldsValue({ template_type: 'CUSTOM', variables: '' });
    setModalVisible(true);
  };

  const handleEdit = (template: DocumentTemplateViewModel) => {
    setEditingTemplate(template);
    form.setFieldsValue({
      template_code: template.templateCode,
      template_name: template.templateName,
      template_type: template.templateType,
      template_content: template.templateContent,
      variables: template.variables.join(', '),
    });
    setModalVisible(true);
  };

  const handleDelete = async (templateId: string) => {
    try {
      await DocumentApi.deleteTemplate(templateId);
      message.success('範本已刪除');
      refetch();
    } catch {
      message.error('刪除範本失敗');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const variablesList = values.variables
        ? values.variables.split(',').map((v: string) => v.trim()).filter(Boolean)
        : [];

      setSaving(true);

      if (editingTemplate) {
        await DocumentApi.updateTemplate(editingTemplate.templateId, {
          template_name: values.template_name,
          template_type: values.template_type,
          template_content: values.template_content,
          variables: variablesList,
        });
        message.success('範本已更新');
      } else {
        await DocumentApi.createTemplate({
          template_code: values.template_code,
          template_name: values.template_name,
          template_type: values.template_type,
          template_content: values.template_content,
          variables: variablesList,
        });
        message.success('範本已建立');
      }

      setModalVisible(false);
      refetch();
    } catch {
      // 表單驗證失敗，由 Ant Design 表單元件自行顯示錯誤
    } finally {
      setSaving(false);
    }
  };

  const handleToggleActive = async (template: DocumentTemplateViewModel) => {
    try {
      await DocumentApi.updateTemplate(template.templateId, {
        is_active: !template.isActive,
      });
      message.success(template.isActive ? '範本已停用' : '範本已啟用');
      refetch();
    } catch {
      message.error('操作失敗');
    }
  };

  const columns = [
    {
      title: '範本代碼',
      dataIndex: 'templateCode',
      key: 'templateCode',
      width: 180,
      render: (code: string) => <Text code>{code}</Text>,
    },
    {
      title: '範本名稱',
      dataIndex: 'templateName',
      key: 'templateName',
      width: 200,
      render: (text: string, record: DocumentTemplateViewModel) => (
        <Button type="link" onClick={() => setDetailTemplate(record)}>
          {text}
        </Button>
      ),
    },
    {
      title: '類型',
      dataIndex: 'templateTypeLabel',
      key: 'templateTypeLabel',
      width: 120,
      render: (text: string) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '變數',
      dataIndex: 'variables',
      key: 'variables',
      width: 120,
      render: (vars: string[]) => <Tag>{vars.length} 個變數</Tag>,
    },
    {
      title: '狀態',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (_: boolean, record: DocumentTemplateViewModel) => (
        <Tag
          color={record.statusColor}
          style={{ cursor: 'pointer' }}
          onClick={() => handleToggleActive(record)}
        >
          {record.statusLabel}
        </Tag>
      ),
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
      render: (_: unknown, record: DocumentTemplateViewModel) => (
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

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ReloadOutlined />} onClick={() => refetch()}>重新整理</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增範本</Button>
      </Space>

      <Card>
        <Table
          columns={columns}
          dataSource={data?.templates}
          rowKey="templateId"
          loading={isLoading}
          pagination={{
            current: currentPage,
            pageSize: 10,
            total: data?.pagination.total,
            showSizeChanger: false,
            showTotal: (total) => `共 ${total} 筆`,
            onChange: (page) => setCurrentPage(page),
          }}
        />
      </Card>

      <Modal
        title={editingTemplate ? '編輯文件範本' : '新增文件範本'}
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
              { pattern: /^[A-Z][A-Z0-9_]*$/, message: '僅允許大寫英文、數字與底線' },
            ]}
          >
            <Input placeholder="例如：EMPLOYMENT_CERT" disabled={!!editingTemplate} />
          </Form.Item>

          <Form.Item
            name="template_name"
            label="範本名稱"
            rules={[{ required: true, message: '請輸入範本名稱' }]}
          >
            <Input placeholder="例如：在職證明" />
          </Form.Item>

          <Form.Item
            name="template_type"
            label="範本類型"
            rules={[{ required: true, message: '請選擇範本類型' }]}
          >
            <Select options={TEMPLATE_TYPE_OPTIONS} />
          </Form.Item>

          <Form.Item name="template_content" label="範本內容">
            <TextArea
              rows={8}
              placeholder="範本內容（支援 {{employeeName}}、{{department}} 等變數）"
            />
          </Form.Item>

          <Form.Item
            name="variables"
            label="變數清單"
            help="以逗號分隔，例如：employeeName, department, position"
          >
            <Input placeholder="employeeName, department, position" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="範本詳情"
        open={!!detailTemplate}
        onCancel={() => setDetailTemplate(null)}
        footer={null}
        width={600}
      >
        {detailTemplate && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="範本代碼">
              <Text code>{detailTemplate.templateCode}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="範本名稱">{detailTemplate.templateName}</Descriptions.Item>
            <Descriptions.Item label="類型">
              <Tag color="blue">{detailTemplate.templateTypeLabel}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="狀態">
              <Tag color={detailTemplate.statusColor}>{detailTemplate.statusLabel}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="變數">
              <Space wrap>
                {detailTemplate.variables.length > 0
                  ? detailTemplate.variables.map((v) => (
                      <Tag key={v} color="geekblue">{`{{${v}}}`}</Tag>
                    ))
                  : <Text type="secondary">無變數</Text>}
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="範本內容">
              <pre style={{ whiteSpace: 'pre-wrap', margin: 0, fontSize: 13 }}>
                {detailTemplate.templateContent || '（無內容）'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="建立日期">{detailTemplate.createdAtDisplay}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </>
  );
};
