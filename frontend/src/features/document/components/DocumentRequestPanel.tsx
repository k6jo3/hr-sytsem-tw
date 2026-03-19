/**
 * 文件申請面板元件
 * Domain Code: HR13
 * 包含文件申請類型卡片 + 歷史記錄
 */

import React, { useState } from 'react';
import {
  Typography,
  Card,
  Table,
  Tag,
  Button,
  Space,
  List,
  Spin,
  Empty,
  Modal,
  Tabs,
  Tooltip,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  FileTextOutlined,
  DownloadOutlined,
  ReloadOutlined,
  SafetyCertificateOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import {
  useMyDocumentRequests,
  useAvailableDocumentTypes,
  useDownloadDocument,
  useGenerateDocument,
} from '../hooks';
import type {
  DocumentRequestViewModel,
  AvailableDocumentTypeViewModel,
} from '../model/DocumentViewModel';

const { Title, Text } = Typography;
const { confirm } = Modal;

/**
 * 申請文件類型卡片
 */
const RequestDocumentSection: React.FC = () => {
  const { data: availableTypes, isLoading: isLoadingTypes } = useAvailableDocumentTypes();
  const { mutate: generateDocument } = useGenerateDocument();

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
        <Empty description="暫無可申請的文件類型" />
      )}
    </div>
  );
};

/**
 * 申請記錄表格
 */
const RequestHistorySection: React.FC = () => {
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
      render: (text, record) => <Tag color={record.statusColor}>{text}</Tag>,
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

export const DocumentRequestPanel: React.FC = () => {
  return (
    <Tabs
      items={[
        { key: 'request', label: '申請文件', children: <RequestDocumentSection /> },
        { key: 'history', label: '申請記錄', children: <RequestHistorySection /> },
      ]}
    />
  );
};
