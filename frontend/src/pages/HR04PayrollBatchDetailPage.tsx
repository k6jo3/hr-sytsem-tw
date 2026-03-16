import {
    ArrowLeftOutlined,
    BankOutlined,
    CheckCircleOutlined,
    ReloadOutlined,
    SendOutlined
} from '@ant-design/icons';
import { Breadcrumb, Button, Card, Col, List, message, Popconfirm, Progress, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import { PayslipViewModelFactory } from '../features/payroll/factory/PayslipViewModelFactory';
import { usePayrollRuns } from '../features/payroll/hooks/usePayrollRuns';
import type { PayslipDetailViewModel } from '../features/payroll/model/PayrollViewModel';

const { Title, Text } = Typography;

/**
 * HR04PayrollBatchDetailPage - 薪資批次明細頁面
 * 頁面代碼：HR04-P04
 */
export const HR04PayrollBatchDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { currentRun, fetchRunDetail, approveRun, loading: runLoading } = usePayrollRuns();
  const [payslips, setPayslips] = useState<PayslipDetailViewModel[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      await fetchRunDetail(id);
      const payslipsRes = await PayrollApi.getPayslips({ runId: id });
      const viewModels = (payslipsRes.items || []).map((dto: any) => PayslipViewModelFactory.createDetailFromDTO(dto));
      setPayslips(viewModels);
    } catch (error) {
      message.error('載入資料失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const onApprove = async () => {
    if (!id) return;
    const success = await approveRun(id);
    if (success) {
      message.success('已核准批次');
      fetchData();
    }
  };

  const columns = [
    { title: '員工編號', dataIndex: 'employeeCode', key: 'employeeCode' },
    { title: '姓名', dataIndex: 'employeeName', key: 'employeeName' },
    {
      title: '底薪',
      dataIndex: 'baseSalaryDisplay',
      key: 'base',
    },
    { 
      title: '應發總額', 
      dataIndex: 'grossPayDisplay', 
      key: 'grossPayDisplay',
    },
    { 
      title: '扣除總額', 
      dataIndex: 'totalDeductionsDisplay', 
      key: 'totalDeductionsDisplay',
      render: (val: string) => <Text type="danger">{val}</Text>
    },
    { 
      title: '實發薪資', 
      dataIndex: 'netPayDisplay', 
      key: 'netPayDisplay',
      render: (val: string) => <Text strong type="success">{val}</Text>
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
              <Title level={2} style={{ margin: 0 }}>{currentRun?.name || '載入中...'}</Title>
              {currentRun && getStatusTag(currentRun.status)}
            </Space>
          </Col>
          <Col>
            <Space>
              {currentRun?.status === 'COMPLETED' && (
                <Popconfirm title="核准後資料將鎖定，確認核准？" onConfirm={onApprove}>
                  <Button type="primary" icon={<CheckCircleOutlined />}>核准發放</Button>
                </Popconfirm>
              )}
              {currentRun?.status === 'APPROVED' && (
                <>
                  <Button icon={<SendOutlined />}>發送薪資單</Button>
                  <Button icon={<BankOutlined />}>產生薪轉檔</Button>
                </>
              )}
              <Button icon={<ReloadOutlined />} onClick={fetchData}>重新整理</Button>
            </Space>
          </Col>
        </Row>

        {currentRun && (
          <Row gutter={16}>
            <Col span={6}>
              <Card size="small">
                <Statistic title="總人數" value={currentRun.totalEmployees} suffix="人" />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="應發總額" value={currentRun.totalGrossPayDisplay} prefix="$" />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="扣額合計" value={currentRun.totalDeductionsDisplay} prefix="$" valueStyle={{ color: '#cf1322' }} />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic title="實發合計" value={currentRun.totalNetPayDisplay} prefix="$" valueStyle={{ color: '#3f8600' }} />
              </Card>
            </Col>
          </Row>
        )}

        {currentRun && currentRun.projectStats && currentRun.projectStats.length > 0 && (
          <Card title={<Space><BankOutlined /><span>專案成本分佈 (HR07 數據同步)</span></Space>}>
            <List
              grid={{ gutter: 16, xs: 1, sm: 2, lg: 3 }}
              dataSource={currentRun.projectStats}
              renderItem={(item) => (
                <List.Item>
                  <Card size="small" bordered={false} style={{ background: '#fafafa' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                      <Text strong>{item.projectName}</Text>
                      <Text type="secondary">{item.totalHours}h</Text>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Title level={4} style={{ margin: 0 }}>{item.totalAmountDisplay}</Title>
                      <Progress 
                        percent={Math.round((item.totalAmount / (currentRun.totalGrossPay || 1)) * 100)} 
                        size="small" 
                        status="active"
                      />
                    </div>
                  </Card>
                </List.Item>
              )}
            />
          </Card>
        )}

        <Card title="員工計薪結果清單">
          <Table 
            columns={columns} 
            dataSource={payslips} 
            rowKey="id" 
            loading={loading || runLoading}
            pagination={{ pageSize: 50 }}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR04PayrollBatchDetailPage;
