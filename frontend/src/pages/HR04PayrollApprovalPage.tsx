import {
    CheckCircleOutlined,
    CloseCircleOutlined,
    ExclamationCircleOutlined,
    EyeOutlined,
    ReloadOutlined
} from '@ant-design/icons';
import { Button, Card, Col, Form, Input, message, Modal, Row, Space, Statistic, Table, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { PayrollRunDto } from '../features/payroll/api/PayrollTypes';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { confirm } = Modal;

/**
 * HR04PayrollApprovalPage - 薪資核准頁面
 * 頁面代碼：HR04-P05
 * 目的：供主管/財務人員審核已計算完成的薪資批次
 */
export const HR04PayrollApprovalPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PayrollRunDto[]>([]);
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [selectedRunId, setSelectedRunId] = useState<string | null>(null);
  const [form] = Form.useForm();

  const fetchData = async () => {
    setLoading(true);
    try {
      // 僅查詢 status 為 COMPLETED (待審核) 的批次
      const res = await PayrollApi.getPayrollRuns({ status: 'COMPLETED' });
      setData(res.items || res.content || []);
    } catch (error: any) {
      const status = error?.response?.status;
      if (status !== 404) {
        message.error('載入待審核批次失敗');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleApprove = (runId: string) => {
    confirm({
      title: '確認核准此計薪批次？',
      icon: <ExclamationCircleOutlined />,
      content: '核准後，計薪數據將鎖定，並待後續發送薪資單與產生薪轉檔。',
      okText: '確認核准',
      cancelText: '取消',
      onOk: async () => {
        try {
          await PayrollApi.approvePayrollRun(runId);
          message.success('批次已核准');
          fetchData();
        } catch (error) {
          message.error('核准失敗');
        }
      },
    });
  };

  const showRejectModal = (runId: string) => {
    setSelectedRunId(runId);
    setRejectModalVisible(true);
    form.resetFields();
  };

  const handleReject = async () => {
    if (!selectedRunId) return;
    try {
      const values = await form.validateFields();
      await PayrollApi.rejectPayrollRun(selectedRunId, values.reason);
      message.warning('批次已退回');
      setRejectModalVisible(false);
      fetchData();
    } catch (error) {
      message.error('退回失敗');
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
          <Text type="secondary" style={{ fontSize: '12px' }}>期間: {record.start} ~ {record.end}</Text>
        </Space>
      )
    },
    { 
      title: '實發合計', 
      dataIndex: 'totalNetPay', 
      key: 'totalNetPay',
      render: (val: number) => <Text strong type="success">${(val || 0).toLocaleString()}</Text>
    },
    { 
      title: '應發合計', 
      dataIndex: 'totalGrossPay', 
      key: 'totalGrossPay',
      render: (val: number) => `$${(val || 0).toLocaleString()}`
    },
    { 
      title: '扣額合計', 
      dataIndex: 'totalDeductions', 
      key: 'totalDeductions',
      render: (val: number) => <Text type="danger">${(val || 0).toLocaleString()}</Text>
    },
    { 
      title: '員工數', 
      dataIndex: 'totalEmployees', 
      key: 'totalEmployees',
      render: (val: number) => `${val} 人`
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayrollRunDto) => (
        <Space>
          <Button 
            icon={<EyeOutlined />} 
            size="small" 
            onClick={() => navigate(`/admin/payroll/runs/${record.runId}`)}
          >
            查看明細
          </Button>
          <Button 
            type="primary" 
            icon={<CheckCircleOutlined />} 
            size="small"
            onClick={() => handleApprove(record.runId)}
          >
            核准
          </Button>
          <Button 
            danger 
            icon={<CloseCircleOutlined />} 
            size="small"
            onClick={() => showRejectModal(record.runId)}
          >
            退回
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
            <Title level={2}>薪資核准待辦</Title>
            <Text type="secondary">請審核已計算完成的薪資批次。核准後將鎖定數據且無法再修改。</Text>
          </Col>
          <Col>
            <Button icon={<ReloadOutlined />} onClick={fetchData}>重新整理</Button>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Card size="small">
              <Statistic title="待核准批次" value={data.length} suffix="個" />
            </Card>
          </Col>
          <Col span={8}>
            <Card size="small">
              <Statistic 
                title="待支付總額" 
                value={data.reduce((sum, item) => sum + (item.totalNetPay || 0), 0)} 
                prefix="$" 
                precision={0}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card size="small">
              <Statistic 
                title="待核准總人數" 
                value={data.reduce((sum, item) => sum + (item.totalEmployees || 0), 0)} 
                suffix="人" 
              />
            </Card>
          </Col>
        </Row>

        <Card>
          <Table 
            columns={columns} 
            dataSource={data} 
            rowKey="runId" 
            loading={loading}
            pagination={false}
            locale={{ emptyText: '目前無待核准的批次' }}
          />
        </Card>
      </Space>

      <Modal
        title="退回計薪批次"
        visible={rejectModalVisible}
        onCancel={() => setRejectModalVisible(false)}
        onOk={handleReject}
        okText="確認退回"
        cancelText="取消"
        okButtonProps={{ danger: true }}
      >
        <Form form={form} layout="vertical">
          <Form.Item 
            name="reason" 
            label="退回原因" 
            rules={[{ required: true, message: '請輸入退回原因' }]}
          >
            <TextArea rows={4} placeholder="例如：某部門加班費異常，需重新確認數據。" />
          </Form.Item>
          <Text type="secondary">註：批次被退回後，狀態將變更為「草稿」，HR 可重新調整數據或計算。</Text>
        </Form>
      </Modal>
    </div>
  );
};

export default HR04PayrollApprovalPage;
