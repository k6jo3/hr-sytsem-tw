import {
    BankOutlined,
    DownloadOutlined,
    FileTextOutlined,
    ReloadOutlined
} from '@ant-design/icons';
import { Button, Card, Col, message, Popconfirm, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { PayrollRunDto } from '../features/payroll/api/PayrollTypes';

const { Title, Text } = Typography;

/**
 * HR04BankTransferPage - 薪轉檔案產生頁面
 * 頁面代碼：HR04-P08
 */
export const HR04BankTransferPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PayrollRunDto[]>([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      // 獲取已核准或已發薪的批次，這些才需要產生薪轉檔
      const res = await PayrollApi.getPayrollRuns({ 
        // 假設後端支持多個狀態篩選，若不支持則可能需要前端過濾或多次請求
        // 這裡暫定查詢已核准以上的狀態
      });
      // 前端過濾以確保狀態正確
      const filtered = (res.items || res.content || []).filter((run: PayrollRunDto) => 
        ['APPROVED', 'PAID'].includes(run.status)
      );
      setData(filtered);
    } catch (error) {
      message.error('載入計薪批次失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleGenerate = async (runId: string) => {
    try {
      await PayrollApi.generateBankTransferFile(runId);
      message.success('薪轉檔案產生成功');
      fetchData();
    } catch (error) {
      message.error('產生失敗');
    }
  };

  const handleDownload = async (runId: string) => {
    try {
      const url = await PayrollApi.getBankTransferDownloadUrl(runId);
      if (url) {
        window.open(url, '_blank');
      } else {
        message.warning('尚未產生檔案或下載位址失效');
      }
    } catch (error) {
      message.error('獲取下載連結失敗');
    }
  };

  const columns = [
    { 
      title: '批次名稱', 
      dataIndex: 'name', 
      key: 'name',
      render: (text: string, record: PayrollRunDto) => (
        <Space direction="vertical" size={0}>
          <Text strong>{text}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>{record.start} ~ {record.end}</Text>
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
      dataIndex: 'totalNetPay', 
      key: 'totalNetPay',
      render: (val: number) => <Text strong type="success">${(val || 0).toLocaleString()}</Text>
    },
    { 
      title: '人數', 
      dataIndex: 'totalEmployees', 
      key: 'totalEmployees',
      render: (val: number) => `${val} 人`
    },
    { 
      title: '狀態', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: string) => {
        const statusMap: any = {
          'APPROVED': { color: 'green', text: '已核准 (待薪轉)' },
          'PAID': { color: 'cyan', text: '已發薪' }
        };
        const config = statusMap[status] || { color: 'default', text: status };
        return <Tag color={config.color}>{config.text}</Tag>;
      }
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayrollRunDto) => (
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

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Title level={2}>銀行薪轉檔案管理</Title>
            <Text type="secondary">在此產生並下載符合銀行格式的薪資發放媒體檔。</Text>
          </Col>
          <Col>
            <Button icon={<ReloadOutlined />} onClick={fetchData}>重新整理</Button>
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
                  <Statistic title="待處理批次" value={data.filter(r => r.status === 'APPROVED').length} suffix="個" />
                </Col>
                <Col span={12}>
                  <Statistic 
                    title="待撥付金額" 
                    value={data.filter(r => r.status === 'APPROVED').reduce((sum, r) => sum + (r.totalNetPay || 0), 0)} 
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
            dataSource={data} 
            rowKey="runId" 
            loading={loading}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR04BankTransferPage;
