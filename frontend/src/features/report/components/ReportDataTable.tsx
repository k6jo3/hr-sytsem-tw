/**
 * 報表歷史記錄表格元件
 * Domain Code: HR14
 * 自包含元件：內部呼叫 useReports hook
 */

import React, { useState } from 'react';
import {
  Table,
  Tag,
  Button,
  Space,
  Tooltip,
  Modal,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  FileTextOutlined,
  DownloadOutlined,
  DeleteOutlined,
  ReloadOutlined,
  SyncOutlined,
} from '@ant-design/icons';
import {
  useReports,
  useDownloadReport,
  useDeleteReport,
} from '../hooks';
import type { ReportViewModel } from '../model/ReportViewModel';

const { confirm } = Modal;

export const ReportDataTable: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1);

  const { data, isLoading, refetch } = useReports({
    page: currentPage,
    pageSize: 10,
  });

  const { mutate: downloadReport } = useDownloadReport();
  const { mutate: deleteReport, isPending: isDeleting } = useDeleteReport();

  const handleDelete = (report: ReportViewModel) => {
    confirm({
      title: '確認刪除',
      content: `確定要刪除報表「${report.reportName}」嗎？`,
      okText: '確認',
      cancelText: '取消',
      okType: 'danger',
      onOk: () => {
        deleteReport(report.reportId);
      },
    });
  };

  const columns: ColumnsType<ReportViewModel> = [
    {
      title: '報表名稱',
      dataIndex: 'reportName',
      key: 'reportName',
      render: (text) => (
        <Space>
          <FileTextOutlined />
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: '類型',
      dataIndex: 'reportTypeLabel',
      key: 'reportTypeLabel',
      width: 150,
    },
    {
      title: '格式',
      dataIndex: 'formatLabel',
      key: 'formatLabel',
      width: 80,
      render: (text) => <Tag>{text}</Tag>,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'statusLabel',
      width: 100,
      render: (text, record) => (
        <Tag
          icon={record.isProcessing ? <SyncOutlined spin /> : undefined}
          color={record.statusColor}
        >
          {text}
        </Tag>
      ),
    },
    {
      title: '產生時間',
      dataIndex: 'generatedAtDisplay',
      key: 'generatedAtDisplay',
      width: 150,
      render: (text) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          {record.canDownload && (
            <Tooltip title="下載">
              <Button
                type="primary"
                size="small"
                icon={<DownloadOutlined />}
                onClick={() => downloadReport(record.reportId)}
              >
                下載
              </Button>
            </Tooltip>
          )}
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
        dataSource={data?.reports}
        rowKey="reportId"
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
