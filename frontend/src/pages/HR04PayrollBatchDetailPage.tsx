import {
    ArrowLeftOutlined,
    BankOutlined,
    CheckCircleOutlined,
    ReloadOutlined,
    SendOutlined
} from '@ant-design/icons';
import { Breadcrumb, Button, Card, Col, message, Popconfirm, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { PayrollRunDto, PayslipDto } from '../features/payroll/api/PayrollTypes';

const { Title, Text } = Typography;

/**
 * HR04PayrollBatchDetailPage - 薪資批次明細頁面
 * 頁面代碼：HR04-P04
 */
export const HR04PayrollBatchDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [run, setRun] = useState<PayrollRunDto | null>(null);
  const [payslips, setPayslips] = useState<PayslipDto[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [runRes, payslipsRes] = await Promise.all([
        PayrollApi.getPayrollRunById(id),
        PayrollApi.getPayslips({ runId: id })
      ]);
      setRun(runRes);
      setPayslips(payslipsRes.items || []);
    } catch (error) {
      message.error('載入資料失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const handleApprove = async () => {
    if (!id) return;
    try {
      await PayrollApi.approvePayrollRun(id);
      message.success('已核准批次');
      fetchData();
    } catch (error) {
      message.error('核准失敗');
    }
  };

  const columns = [
    { title: '員工編號', dataIndex: 'employee_code', key: 'employee_code' },
    { title: '姓名', dataIndex: 'employee_name', key: 'employee_name' },
    { 
      title: '底薪', 
      key: 'base',
      render: (_: any, record: PayslipDto) => {
        const base = record.items.find(i => i.item_code === 'BASE_SALARY')?.amount || 0;
        return `$${base.toLocaleString()}`;
      }
    },
    { 
      title: '應發總額', 
      dataIndex: 'gross_pay', 
      key: 'gross_pay',
      render: (val: number) => `$${val.toLocaleString()}`
    },
    { 
      title: '扣除總額', 
      dataIndex: 'total_deductions', 
      key: 'total_deductions',
      render: (val: number) => <Text type="danger">${val.toLocaleString()}</Text>
    },
    { 
      title: '實發薪資', 
      dataIndex: 'net_pay', 
      key: 'net_pay',
      render: (val: number) => <Text strong type="success">${val.toLocaleString()}</Text>
    }
  ];

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      'DRAFT': { color: 'default', text: '草稿' },
      'CALCULATING': { color: 'processing', text: '計算中' },
      'COMPLETED': { color: 'warning', text: '待審核' },
      'APPROVED': { color: 'success', text: '已核准' },
      'PAID': { color: 'cyan', text: '已發薪' },
    };
    const config = statusMap[status] || { color: 'default', text: status };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  return (
    <div style={{ padding: '24px' }}>
      <Breadcrumb style={{ marginBottom: 16 }}>
        <Breadcrumb.Item onClick={() => navigate('/admin/payroll/runs')}>
          <span style={{ cursor: 'pointer' }}>薪資計算批次</span>
        </Breadcrumb.Item>
        <Breadcrumb.Item>批次明細</Breadcrumb.Item>
      </Breadcrumb>

      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/admin/payroll/runs')} />
              <Title level={2} style={{ margin: 0 }}>{run?.name || '載入中...'}</Title>
              {run && getStatusTag(run.status)}
            </Space>
          </Col>
          <Col>
            <Space>
              {run?.status === 'COMPLETED' && (
                <Popconfirm title="核准後資料將鎖定，確認核准？" onConfirm={handleApprove}>
                  <Button type="primary" icon={<CheckCircleOutlined />}>核准發放</Button>
                </Popconfirm>
              )}
              {run?.status === 'APPROVED' && (
                <>
                  <Button icon={<SendOutlined />}>發送薪資單</Button>
                  <Button icon={<BankOutlined />}>產生薪轉檔</Button>
                </>
              )}
              <Button icon={<ReloadOutlined />} onClick={fetchData}>重新整理</Button>
            </Space>
          </Col>
        </Row>

        {run && (
          <Row gutter={16}>
            <Col span={6}>
              <Card size="small">
                <Statistic title="總人數" value={run.totalEmployees} suffix="人" />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="應發總額" value={run.totalGrossPay} prefix="$" />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="扣額合計" value={run.totalDeductions} prefix="$" valueStyle={{ color: '#cf1322' }} />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="實發合計" value={run.totalNetPay} prefix="$" valueStyle={{ color: '#3f8600' }} />
              </Card>
            </Col>
          </Row>
        )}

        <Card title="員工計薪結果清單">
          <Table 
            columns={columns} 
            dataSource={payslips} 
            rowKey="id" 
            loading={loading}
            pagination={{ pageSize: 50 }}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR04PayrollBatchDetailPage;
