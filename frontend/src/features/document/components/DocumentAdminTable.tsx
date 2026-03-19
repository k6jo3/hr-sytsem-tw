/**
 * 文件管理表格元件（管理端）
 * Domain Code: HR13
 * 管理端文件表格 + 篩選
 */

import React, { useState } from 'react';
import {
  Card,
  Table,
  Tag,
  Space,
  Button,
  Input,
  Select,
  Tooltip,
  Modal,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  FileTextOutlined,
  DownloadOutlined,
  DeleteOutlined,
  SearchOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  ReloadOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons';
import {
  useDocuments,
  useDeleteDocument,
  useDownloadDocument,
} from '../hooks';
import type { DocumentViewModel } from '../model/DocumentViewModel';
import type { DocumentType, DocumentVisibility } from '../api/DocumentTypes';

const { Search } = Input;
const { confirm } = Modal;

export const DocumentAdminTable: React.FC = () => {
  const [keyword, setKeyword] = useState('');
  const [documentType, setDocumentType] = useState<DocumentType | undefined>();
  const [visibility, setVisibility] = useState<DocumentVisibility | undefined>();
  const [currentPage, setCurrentPage] = useState(1);

  const { data, isLoading, refetch } = useDocuments({
    keyword: keyword || undefined,
    documentType,
    page: currentPage,
    pageSize: 15,
  });

  const { mutate: downloadDocument } = useDownloadDocument();
  const { mutate: deleteDocument, isPending: isDeleting } = useDeleteDocument();

  const handleDelete = (document: DocumentViewModel) => {
    if (document.documentType === 'PAYSLIP') {
      message.warning('薪資單不允許刪除');
      return;
    }
    confirm({
      title: '確認刪除',
      content: `確定要刪除「${document.originalFileName}」（所有者：${document.ownerName}）？`,
      okText: '確認',
      cancelText: '取消',
      okType: 'danger',
      onOk: () => deleteDocument(document.documentId),
    });
  };

  const getFileIcon = (doc: DocumentViewModel) => {
    if (doc.isPdf) return <FilePdfOutlined style={{ color: '#ff4d4f' }} />;
    if (doc.isImage) return <FileImageOutlined style={{ color: '#1890ff' }} />;
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
      width: 110,
      render: (text) => <Tag>{text}</Tag>,
    },
    {
      title: '所有者',
      dataIndex: 'ownerName',
      key: 'ownerName',
      width: 120,
    },
    {
      title: '可見性',
      dataIndex: 'visibilityLabel',
      key: 'visibilityLabel',
      width: 90,
      render: (text, record) => <Tag color={record.visibilityColor}>{text}</Tag>,
    },
    {
      title: '大小',
      dataIndex: 'fileSizeDisplay',
      key: 'fileSizeDisplay',
      width: 100,
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 70,
      render: (v: number) => `v${v}`,
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
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="下載">
            <Button
              type="text"
              size="small"
              icon={<DownloadOutlined />}
              onClick={() => downloadDocument(record.documentId)}
            />
          </Tooltip>
          <Tooltip title="刪除">
            <Button
              type="text"
              size="small"
              danger
              icon={<DeleteOutlined />}
              loading={isDeleting}
              disabled={record.documentType === 'PAYSLIP'}
              onClick={() => handleDelete(record)}
            />
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ReloadOutlined />} onClick={() => refetch()}>重新整理</Button>
      </Space>

      <Card>
        <div style={{ marginBottom: 16, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <Search
            placeholder="搜尋文件名稱..."
            allowClear
            style={{ width: 250 }}
            prefix={<SearchOutlined />}
            onSearch={(value) => { setKeyword(value); setCurrentPage(1); }}
          />
          <Select
            placeholder="文件類型"
            allowClear
            style={{ width: 140 }}
            value={documentType}
            onChange={(v) => { setDocumentType(v); setCurrentPage(1); }}
            options={[
              { label: '合約', value: 'CONTRACT' },
              { label: '證明文件', value: 'CERTIFICATE' },
              { label: '薪資單', value: 'PAYSLIP' },
              { label: '稅務表單', value: 'TAX_FORM' },
              { label: '上傳文件', value: 'UPLOADED' },
              { label: '系統產生', value: 'GENERATED' },
            ]}
          />
          <Select
            placeholder="可見性"
            allowClear
            style={{ width: 120 }}
            value={visibility}
            onChange={(v) => { setVisibility(v); setCurrentPage(1); }}
            options={[
              { label: '私人', value: 'PRIVATE' },
              { label: '部門', value: 'DEPARTMENT' },
              { label: '公司', value: 'COMPANY' },
              { label: '公開', value: 'PUBLIC' },
            ]}
          />
        </div>

        <Table
          columns={columns}
          dataSource={data?.documents}
          rowKey="documentId"
          loading={isLoading}
          scroll={{ x: 'max-content' }}
          pagination={{
            current: currentPage,
            pageSize: 15,
            total: data?.pagination.total,
            showSizeChanger: false,
            showTotal: (total) => `共 ${total} 筆`,
            onChange: (page) => setCurrentPage(page),
          }}
        />
      </Card>
    </>
  );
};
