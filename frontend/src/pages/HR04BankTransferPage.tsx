import {
    BankOutlined,
    DownloadOutlined,
    FileTextOutlined,
    ReloadOutlined
} from '@ant-design/icons';
import { Button, Card, Col, message, Popconfirm, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import React, { useEffect } from 'react';
import { usePayrollRuns } from '../features/payroll/hooks/usePayrollRuns';
import type { PayrollRunViewModel } from '../features/payroll/model/PayrollViewModel';

const { Title, Text } = Typography;

/**
 * HR04BankTransferPage - 薪轉檔案產生頁面
 * 頁面代碼：HR04-P08
 */
export const HR04BankTransferPage: React.FC = () => {
  const { runs, loading, error, fetchRuns, generateBankFile, getBankFileUrl } = usePayrollRuns();

  useEffect(() => {
    fetchRuns();
  }, [fetchRuns]);

  useEffect(() => {
    if (error) {
      message.error(typeof error === 'string' ? error : '載入薪轉資料失敗');
    }
  }, [error]);

  const handleGenerate = async (runId: string) => {
    const success = await generateBankFile(runId);
    if (success) {
      message.success('薪轉檔案產生成功');
    }
  };

  const handleDownload = async (runId: string) => {
    const url = await getBankFileUrl(runId);
    if (url) {
      window.open(url, '_blank');
    } else {
      message.warning('尚未產生檔案或下載位址失效');
    }
  };

  const columns = [
    { 
      title: '批次名稱', 
      dataIndex: 'name', 
      key: 'name',
      render: (text: string, record: PayrollRunViewModel) => (
        <Space direction="vertical" size={0}>
          <Text strong>{text}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>{record.periodDisplay}</Text>
        </Space>
      )
    },
    { 
      title: '發薪日', 
      dataIndex: 'payDate', 
      key: 'payDate' 
    },
    { 
      title: '實發總額', 
      dataIndex: 'totalNetPayDisplay', 
      key: 'totalNetPayDisplay',
      render: (val: string) => <Text strong type="success">{val}</Text>
    },
    { 
      title: '人數', 
      dataIndex: 'totalEmployees', 
      key: 'totalEmployees',
      render: (val: number) => `${val} 人`
    },
    { 
      title: '狀態', 
      key: 'status',
      render: (_: any, record: PayrollRunViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayrollRunViewModel) => (
        <Space>
          <Popconfirm title="確認產生此批次的銀行薪轉檔？" onConfirm={() => handleGenerate(record.runId)}>
            <Button icon={<BankOutlined />} type="primary" size="small">產生薪轉檔</Button>
          </Popconfirm>
          <Button 
            icon={<DownloadOutlined />} 
            size="small"
            onClick={() => handleDownload(record.runId)}
          >
            下載檔案
          </Button>
        </Space>
      )
    }
  ];

  const displayData = runs.filter(record => ['APPROVED', 'PAID'].includes(record.status));

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Title level={2}>銀行薪轉檔案管理</Title>
            <Text type="secondary">在此產生並下載符合銀行格式的薪資發放媒體檔。</Text>
          </Col>
          <Col>
            <Button icon={<ReloadOutlined />} onClick={() => fetchRuns()}>重新整理</Button>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Card title="薪轉流程說明" size="small" extra={<FileTextOutlined />}>
              <ol style={{ paddingLeft: '20px', marginBottom: 0 }}>
                <li>確認計薪批次已由主管「核准」。</li>
                <li>點擊下方「產生薪轉檔」按鈕，系統將彙整員工銀行帳戶資訊。</li>
                <li>「下載檔案」並上傳至企業網銀系統進行發放。</li>
                <li>發放完成後，批次狀態將可變更為「已發薪」。</li>
              </ol>
            </Card>
          </Col>
          <Col span={12}>
            <Card title="本月待產製概況" size="small">
              <Row>
                <Col span={12}>
                  <Statistic title="待處理批次" value={displayData.filter(r => r.status === 'APPROVED').length} suffix="個" />
                </Col>
                <Col span={12}>
                  <Statistic 
                    title="待撥付金額" 
                    value={displayData.filter(r => r.status === 'APPROVED').reduce((sum, r) => sum + (r.totalNetPay || 0), 0)} 
                    prefix="$" 
                  />
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>

        <Card title="薪轉批次清單">
          <Table
            columns={columns}
            dataSource={displayData}
            rowKey="runId"
            loading={loading}
            scroll={{ x: 'max-content' }}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR04BankTransferPage;
