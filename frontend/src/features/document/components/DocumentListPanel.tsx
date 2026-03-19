/**
 * 文件列表面板元件
 * Domain Code: HR13
 * 包含我的文件列表 + 搜尋/篩選 + 上傳/下載/刪除
 */

import React, { useState } from 'react';
import {
  Table,
  Tag,
  Button,
  Space,
  Input,
  Select,
  Upload,
  Modal,
  Tooltip,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { UploadProps } from 'antd';
import {
  FileTextOutlined,
  UploadOutlined,
  DownloadOutlined,
  DeleteOutlined,
  EyeOutlined,
  SearchOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  ReloadOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons';
import {
  useMyDocuments,
  useUploadDocument,
  useDeleteDocument,
  useDownloadDocument,
} from '../hooks';
import type { DocumentViewModel } from '../model/DocumentViewModel';
import type { DocumentType } from '../api/DocumentTypes';

const { Search } = Input;
const { Option } = Select;
const { confirm } = Modal;

export const DocumentListPanel: React.FC = () => {
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
        scroll={{ x: 'max-content' }}
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
