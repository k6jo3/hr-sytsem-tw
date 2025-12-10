/**
 * HR13DocumentListPage - 文件管理頁面
 * Domain Code: HR13
 * Feature: document
 */

import React, { useState } from 'react';
import {
  Card,
  Typography,
  Tabs,
  Table,
  Tag,
  Button,
  Space,
  Input,
  Select,
  Upload,
  Modal,
  List,
  Spin,
  Empty,
  Tooltip,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { UploadProps } from 'antd';
import {
  FileTextOutlined,
  UploadOutlined,
  DownloadOutlined,
  DeleteOutlined,
  EyeOutlined,
  PlusOutlined,
  SearchOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  ReloadOutlined,
  SafetyCertificateOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import {
  useMyDocuments,
  useMyDocumentRequests,
  useAvailableDocumentTypes,
  useUploadDocument,
  useDeleteDocument,
  useDownloadDocument,
  useGenerateDocument,
} from '@features/document/hooks';
import type {
  DocumentViewModel,
  DocumentRequestViewModel,
  AvailableDocumentTypeViewModel,
} from '@features/document/model/DocumentViewModel';
import type { DocumentType, TemplateType } from '@features/document/api/DocumentTypes';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { Search } = Input;
const { Option } = Select;
const { confirm } = Modal;

// ========== Document List Tab ==========

const DocumentListTab: React.FC = () => {
  const [keyword, setKeyword] = useState('');
  const [documentType, setDocumentType] = useState<DocumentType | undefined>();
  const [currentPage, setCurrentPage] = useState(1);

  const { data, isLoading, refetch } = useMyDocuments({
    keyword: keyword || undefined,
    documentType,
    page: currentPage,
    pageSize: 10,
  });

  const { mutate: downloadDocument } = useDownloadDocument();
  const { mutate: deleteDocument, isPending: isDeleting } = useDeleteDocument();
  const { mutate: uploadDocument, isPending: isUploading } = useUploadDocument();

  const handleDelete = (document: DocumentViewModel) => {
    confirm({
      title: '確認刪除',
      content: `確定要刪除文件「${document.originalFileName}」嗎？`,
      okText: '確認',
      cancelText: '取消',
      okType: 'danger',
      onOk: () => {
        deleteDocument(document.documentId);
      },
    });
  };

  const uploadProps: UploadProps = {
    name: 'file',
    showUploadList: false,
    beforeUpload: (file) => {
      uploadDocument({
        file,
        documentType: 'UPLOADED',
      });
      return false;
    },
  };

  const getFileIcon = (document: DocumentViewModel) => {
    if (document.isPdf) return <FilePdfOutlined style={{ color: '#ff4d4f' }} />;
    if (document.isImage) return <FileImageOutlined style={{ color: '#1890ff' }} />;
    return <FileTextOutlined />;
  };

  const columns: ColumnsType<DocumentViewModel> = [
    {
      title: '文件名稱',
      dataIndex: 'originalFileName',
      key: 'originalFileName',
      render: (text, record) => (
        <Space>
          {getFileIcon(record)}
          <span>{text}</span>
          {record.isEncrypted && (
            <Tooltip title="已加密">
              <SafetyCertificateOutlined style={{ color: '#52c41a' }} />
            </Tooltip>
          )}
        </Space>
      ),
    },
    {
      title: '類型',
      dataIndex: 'documentTypeLabel',
      key: 'documentTypeLabel',
      width: 120,
      render: (text) => <Tag>{text}</Tag>,
    },
    {
      title: '大小',
      dataIndex: 'fileSizeDisplay',
      key: 'fileSizeDisplay',
      width: 100,
    },
    {
      title: '可見性',
      dataIndex: 'visibilityLabel',
      key: 'visibilityLabel',
      width: 100,
      render: (text, record) => <Tag color={record.visibilityColor}>{text}</Tag>,
    },
    {
      title: '上傳時間',
      dataIndex: 'uploadedAtDisplay',
      key: 'uploadedAtDisplay',
      width: 150,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          {record.canDownload && (
            <Tooltip title="下載">
              <Button
                type="text"
                size="small"
                icon={<DownloadOutlined />}
                onClick={() => downloadDocument(record.documentId)}
              />
            </Tooltip>
          )}
          {record.isImage && (
            <Tooltip title="預覽">
              <Button type="text" size="small" icon={<EyeOutlined />} />
            </Tooltip>
          )}
          {record.canDelete && (
            <Tooltip title="刪除">
              <Button
                type="text"
                size="small"
                danger
                icon={<DeleteOutlined />}
                loading={isDeleting}
                onClick={() => handleDelete(record)}
              />
            </Tooltip>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, flexWrap: 'wrap' }}>
        <Search
          placeholder="搜尋文件名稱..."
          allowClear
          style={{ width: 250 }}
          prefix={<SearchOutlined />}
          onSearch={(value) => setKeyword(value)}
        />
        <Select
          placeholder="文件類型"
          allowClear
          style={{ width: 150 }}
          value={documentType}
          onChange={setDocumentType}
        >
          <Option value="CONTRACT">合約</Option>
          <Option value="CERTIFICATE">證明文件</Option>
          <Option value="PAYSLIP">薪資單</Option>
          <Option value="TAX_FORM">稅務表單</Option>
          <Option value="UPLOADED">上傳文件</Option>
        </Select>
        <div style={{ flex: 1 }} />
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => refetch()}>
            重新整理
          </Button>
          <Upload {...uploadProps}>
            <Button type="primary" icon={<UploadOutlined />} loading={isUploading}>
              上傳文件
            </Button>
          </Upload>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data?.documents}
        rowKey="documentId"
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
    </div>
  );
};

// ========== Request Document Tab ==========

const RequestDocumentTab: React.FC = () => {
  const [selectedType, setSelectedType] = useState<TemplateType | null>(null);
  const [showModal, setShowModal] = useState(false);

  const { data: availableTypes, isLoading: isLoadingTypes } = useAvailableDocumentTypes();
  const { mutate: generateDocument, isPending: isGenerating } = useGenerateDocument();

  const handleRequest = (type: AvailableDocumentTypeViewModel) => {
    if (type.requiresApproval) {
      confirm({
        title: '申請確認',
        content: `「${type.templateTypeLabel}」需要經過主管審核，確定要申請嗎？`,
        okText: '確認申請',
        cancelText: '取消',
        onOk: () => {
          generateDocument({
            template_id: type.templateType,
            template_type: type.templateType,
          });
        },
      });
    } else {
      generateDocument({
        template_id: type.templateType,
        template_type: type.templateType,
      });
    }
  };

  if (isLoadingTypes) {
    return (
      <div style={{ textAlign: 'center', padding: 48 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <Text type="secondary" style={{ display: 'block', marginBottom: 24 }}>
        選擇您需要申請的文件類型，系統將自動產生對應文件
      </Text>

      {availableTypes && availableTypes.length > 0 ? (
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4 }}
          dataSource={availableTypes}
          renderItem={(item) => (
            <List.Item>
              <Card
                hoverable
                style={{ textAlign: 'center' }}
                onClick={() => handleRequest(item)}
              >
                <div style={{ fontSize: 32, marginBottom: 12 }}>
                  {item.icon === 'IdcardOutlined' && <SafetyCertificateOutlined />}
                  {item.icon === 'DollarOutlined' && <FileTextOutlined />}
                  {item.icon === 'ClockCircleOutlined' && <ClockCircleOutlined />}
                  {!['IdcardOutlined', 'DollarOutlined', 'ClockCircleOutlined'].includes(
                    item.icon
                  ) && <FileTextOutlined />}
                </div>
                <Title level={5} style={{ marginBottom: 4 }}>
                  {item.templateTypeLabel}
                </Title>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {item.description}
                </Text>
                {item.requiresApproval && (
                  <div style={{ marginTop: 8 }}>
                    <Tag color="orange">需審核</Tag>
                  </div>
                )}
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="目前沒有可申請的文件類型" />
      )}
    </div>
  );
};

// ========== Request History Tab ==========

const RequestHistoryTab: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1);

  const { data, isLoading, refetch } = useMyDocumentRequests({
    page: currentPage,
    pageSize: 10,
  });

  const { mutate: downloadDocument } = useDownloadDocument();

  const columns: ColumnsType<DocumentRequestViewModel> = [
    {
      title: '文件類型',
      dataIndex: 'templateTypeLabel',
      key: 'templateTypeLabel',
      render: (text) => (
        <Space>
          <FileTextOutlined />
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: '申請時間',
      dataIndex: 'requestDateDisplay',
      key: 'requestDateDisplay',
      width: 150,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text, record) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '完成時間',
      dataIndex: 'generatedAtDisplay',
      key: 'generatedAtDisplay',
      width: 150,
      render: (text) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Space size="small">
          {record.canDownload && record.documentId && (
            <Tooltip title="下載">
              <Button
                type="primary"
                size="small"
                icon={<DownloadOutlined />}
                onClick={() => downloadDocument(record.documentId!)}
              >
                下載
              </Button>
            </Tooltip>
          )}
          {record.isProcessing && (
            <Tag icon={<ClockCircleOutlined spin />} color="processing">
              處理中
            </Tag>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button icon={<ReloadOutlined />} onClick={() => refetch()}>
          重新整理
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data?.requests}
        rowKey="requestId"
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
    </div>
  );
};

// ========== Main Page Component ==========

export const HR13DocumentListPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('documents');

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Title level={2}>
          <FileTextOutlined style={{ marginRight: 12 }} />
          文件管理
        </Title>

        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="我的文件" key="documents">
            <DocumentListTab />
          </TabPane>
          <TabPane tab="申請文件" key="request">
            <RequestDocumentTab />
          </TabPane>
          <TabPane tab="申請記錄" key="history">
            <RequestHistoryTab />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
