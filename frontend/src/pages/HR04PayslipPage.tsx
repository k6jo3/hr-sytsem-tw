import { DownloadOutlined, EyeOutlined, FilePdfOutlined, ProjectOutlined } from '@ant-design/icons';
import { Button, Card, Col, Descriptions, Divider, List, Modal, Progress, Row, Space, Statistic, Table, Tag, Typography, message } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { PayrollItemDto, PayslipDto, PayslipSummaryDto } from '../features/payroll/api/PayrollTypes';

const { Title, Text } = Typography;

/**
 * HR04 電子薪資單發布與查詢 (ESS)
 * 頁面代碼：HR04-P02
 */
export const HR04PayslipPage: React.FC = () => {
  const [payslips, setPayslips] = useState<PayslipSummaryDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedPayslip, setSelectedPayslip] = useState<PayslipDto | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const fetchPayslips = async () => {
    setLoading(true);
    try {
      const res = await PayrollApi.getMyPayslips({ year: dayjs().year() });
      setPayslips(res.payslips || []);
    } catch (error) {
      message.error('載入薪資單失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayslips();
  }, []);

  const handleViewDetail = async (id: string) => {
    setDetailLoading(true);
    setDetailVisible(true);
    try {
      const res = await PayrollApi.getPayslipDetail(id);
      setSelectedPayslip(res.payslip);
    } catch (error) {
      message.error('獲取薪資詳情失敗');
      setDetailVisible(false);
    } finally {
      setDetailLoading(false);
    }
  };

  const handleDownloadPdf = async (id: string) => {
    try {
      const blob = await PayrollApi.downloadPayslipPdf(id);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Payslip_${id}.pdf`;
      link.click();
      message.info('PDF 密碼為身分證字號末 4 碼');
    } catch (error) {
      message.error('下載失敗');
    }
  };

  const columns = [
    { title: '計薪月份', dataIndex: 'pay_period', key: 'pay_period' },
    { title: '發薪日', dataIndex: 'payment_date', key: 'payment_date' },
    { 
      title: '應發金額', 
      dataIndex: 'gross_pay', 
      key: 'gross_pay', 
      render: (val: number) => `$${val.toLocaleString()}` 
    },
    { 
      title: '實發金額', 
      dataIndex: 'net_pay', 
      key: 'net_pay', 
      render: (val: number) => <Text strong type="success">${val.toLocaleString()}</Text> 
    },
    { 
      title: '狀態', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'PAID' ? 'green' : 'blue'}>
          {status === 'PAID' ? '已發放' : '已核准'}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayslipSummaryDto) => (
        <Space>
          <Button icon={<EyeOutlined />} onClick={() => handleViewDetail(record.id)}>查看</Button>
          <Button icon={<DownloadOutlined />} onClick={() => handleDownloadPdf(record.id)}>下載</Button>
        </Space>
      )
    }
  ];

  const renderItems = (items: PayrollItemDto[], type: 'INCOME' | 'DEDUCTION') => (
    <div style={{ marginBottom: 16 }}>
      <Text strong>{type === 'INCOME' ? '【收入項目】' : '【扣除項目】'}</Text>
      <div style={{ marginTop: 8 }}>
        {items.filter(i => i.item_type === type).map(item => (
          <Row key={item.item_code} style={{ marginBottom: 4 }}>
            <Col span={16}>{item.item_name}</Col>
            <Col span={8} style={{ textAlign: 'right' }}>
              <Text type={type === 'DEDUCTION' ? 'danger' : undefined}>
                ${item.amount.toLocaleString()}
              </Text>
            </Col>
          </Row>
        ))}
      </div>
    </div>
  );

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>電子薪資單發布與查詢</Title>
      <Card>
        <Table 
          columns={columns} 
          dataSource={payslips} 
          rowKey="id" 
          loading={loading}
        />
      </Card>

      <Modal
        title={`薪資明細 - ${selectedPayslip?.pay_period_start.substring(0, 7)}`}
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>關閉</Button>,
          <Button key="pdf" type="primary" icon={<FilePdfOutlined />} onClick={() => selectedPayslip && handleDownloadPdf(selectedPayslip.id)}>
            下載 PDF
          </Button>
        ]}
        width={700}
        loading={detailLoading}
      >
        {selectedPayslip && (
          <div>
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="員工姓名">{selectedPayslip.employee_name}</Descriptions.Item>
              <Descriptions.Item label="發薪日">{selectedPayslip.payment_date}</Descriptions.Item>
              <Descriptions.Item label="計薪期間" span={2}>
                {selectedPayslip.pay_period_start} ~ {selectedPayslip.pay_period_end}
              </Descriptions.Item>
            </Descriptions>

            <Row gutter={32} style={{ marginTop: 24 }}>
              <Col span={12}>
                {renderItems(selectedPayslip.items, 'INCOME')}
                <Divider style={{ margin: '8px 0' }} />
                <Row>
                  <Col span={16}><Text strong>應發合計</Text></Col>
                  <Col span={8} style={{ textAlign: 'right' }}>
                    <Text strong>${selectedPayslip.gross_pay.toLocaleString()}</Text>
                  </Col>
                </Row>
              </Col>
              <Col span={12}>
                {renderItems(selectedPayslip.items, 'DEDUCTION')}
                <Divider style={{ margin: '8px 0' }} />
                <Row>
                  <Col span={16}><Text strong>扣除合計</Text></Col>
                  <Col span={8} style={{ textAlign: 'right' }}>
                    <Text strong type="danger">${selectedPayslip.total_deductions.toLocaleString()}</Text>
                  </Col>
                </Row>
              </Col>
            </Row>

            {selectedPayslip.project_costs && selectedPayslip.project_costs.length > 0 && (
              <div style={{ marginTop: 24 }}>
                <Divider orientation="left"><Space><ProjectOutlined /><span>專案工時分佈 (HR07 同步)</span></Space></Divider>
                <List
                  size="small"
                  dataSource={selectedPayslip.project_costs}
                  renderItem={(item) => (
                    <List.Item>
                      <div style={{ width: '100%' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                          <Text>{item.project_name}</Text>
                          <Text type="secondary">{item.hours}h (${item.amount.toLocaleString()})</Text>
                        </div>
                        <Progress 
                          percent={Math.round((item.amount / selectedPayslip.gross_pay) * 100)} 
                          size="small" 
                          strokeColor="#1890ff"
                        />
                      </div>
                    </List.Item>
                  )}
                />
              </div>
            )}

            <Card style={{ marginTop: 24, background: '#f6ffed', border: '1px solid #b7eb8f' }}>
              <Statistic 
                title={<Text strong style={{ fontSize: 16 }}>實發薪資 (Net Pay)</Text>} 
                value={selectedPayslip.net_pay} 
                prefix="$" 
                valueStyle={{ color: '#52c41a', fontWeight: 'bold' }}
              />
            </Card>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default HR04PayslipPage;
