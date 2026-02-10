import {
    BankOutlined,
    CheckCircleOutlined,
    EyeOutlined,
    PlayCircleOutlined,
    PlusOutlined,
    ReloadOutlined,
    SearchOutlined,
    SendOutlined
} from '@ant-design/icons';
import { Button, Card, Col, DatePicker, Form, Input, message, Modal, Popconfirm, Row, Select, Space, Table, Tag, Tooltip, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { PayrollRunDto } from '../features/payroll/api/PayrollTypes';

const { Title } = Typography;
const { Option } = Select;

/**
 * HR04PayrollListPage - 薪資計算批次頁面
 * 頁面代碼：HR04-P03
 */
export const HR04PayrollListPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PayrollRunDto[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  const fetchData = async (filters: any = {}) => {
    setLoading(true);
    try {
      const res = await PayrollApi.getPayrollRuns(filters);
      setData(res.items || res.content || []);
    } catch (error) {
      message.error('載入薪資批次失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    fetchData({
      status: values.status,
      startDate: values.range?.[0]?.format('YYYY-MM-DD'),
      endDate: values.range?.[1]?.format('YYYY-MM-DD'),
    });
  };

  const handleCreate = async () => {
    // ... existing code ...
    try {
      const values = await form.validateFields();
      const payload = {
        name: values.name,
        payrollSystem: values.payrollSystem,
        start: values.period[0].format('YYYY-MM-DD'),
        end: values.period[1].format('YYYY-MM-DD'),
        payDate: values.payDate.format('YYYY-MM-DD'),
      };
      await PayrollApi.startPayrollRun(payload);
      message.success('批次建立成功');
      setModalVisible(false);
      fetchData();
    } catch (error) {
      message.error('建立失敗');
    }
  };

  const handleExecute = async (runId: string) => {
    // ... existing code ...
    try {
      message.loading({ content: '計算中...', key: 'calc' });
      await PayrollApi.executePayrollRun(runId);
      message.success({ content: '計算完成', key: 'calc' });
      fetchData();
    } catch (error) {
      message.error({ content: '計算失敗', key: 'calc' });
    }
  };

  const handleApprove = async (runId: string) => {
    // ... existing code ...
    try {
      await PayrollApi.approvePayrollRun(runId);
      message.success('已核准批次');
      fetchData();
    } catch (error) {
      message.error('核准失敗');
    }
  };

  const getStatusTag = (status: string) => {
    // ... existing code ...
    const statusMap: Record<string, { color: string; text: string }> = {
      'DRAFT': { color: 'default', text: '草稿' },
      'CALCULATING': { color: 'processing', text: '計算中' },
      'COMPLETED': { color: 'warning', text: '待審核' },
      'APPROVED': { color: 'success', text: '已核准' },
      'REJECTED': { color: 'error', text: '已退回' },
      'PAID': { color: 'cyan', text: '已發薪' },
    };
    const config = statusMap[status] || { color: 'default', text: status };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  const columns = [
    // ... existing code ...
    { title: '批次名稱', dataIndex: 'name', key: 'name' },
    { 
      title: '計薪期間', 
      key: 'period',
      render: (_: any, record: PayrollRunDto) => `${record.start} ~ ${record.end}`
    },
    { title: '發薪日', dataIndex: 'payDate', key: 'payDate' },
    { 
      title: '人數', 
      key: 'employees',
      render: (_: any, record: PayrollRunDto) => `${record.processedEmployees}/${record.totalEmployees}`
    },
    { 
      title: '實發總額', 
      dataIndex: 'totalNetPay', 
      key: 'totalNetPay',
      render: (val: number) => `$${(val || 0).toLocaleString()}`
    },
    { 
      title: '狀態', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayrollRunDto) => (
        <Space>
          <Tooltip title="查看明細">
            <Button 
              icon={<EyeOutlined />} 
              size="small" 
              onClick={() => navigate(`/admin/payroll/runs/${record.runId}`)} 
            />
          </Tooltip>
          
          {record.status === 'DRAFT' && (
            <Popconfirm title="確認執行計算？" onConfirm={() => handleExecute(record.runId)}>
              <Button type="primary" icon={<PlayCircleOutlined />} size="small">執行計算</Button>
            </Popconfirm>
          )}

          {record.status === 'COMPLETED' && (
            <Popconfirm title="確認核准此批次？" onConfirm={() => handleApprove(record.runId)}>
              <Button type="primary" ghost icon={<CheckCircleOutlined />} size="small">核准發放</Button>
            </Popconfirm>
          )}

          {record.status === 'APPROVED' && (
            <Space>
              <Button icon={<SendOutlined />} size="small">發送薪資單</Button>
              <Button icon={<BankOutlined />} size="small">產生薪轉檔</Button>
            </Space>
          )}

          <Button icon={<ReloadOutlined />} size="small" onClick={() => fetchData()} />
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col><Title level={2}>薪資計算批次管理</Title></Col>
          <Col>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
              建立新批次
            </Button>
          </Col>
        </Row>

        <Card title="篩選條件">
          <Form form={searchForm} layout="inline">
            <Form.Item name="status" label="狀態">
              <Select placeholder="選擇狀態" style={{ width: 120 }} allowClear>
                <Option value="DRAFT">草稿</Option>
                <Option value="COMPLETED">待審核</Option>
                <Option value="APPROVED">已核准</Option>
                <Option value="PAID">已發薪</Option>
              </Select>
            </Form.Item>
            <Form.Item name="range" label="期間">
              <DatePicker.RangePicker />
            </Form.Item>
            <Form.Item>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查詢</Button>
            </Form.Item>
          </Form>
        </Card>

        <Card>
          <Table 
            columns={columns} 
            dataSource={data} 
            rowKey="runId" 
            loading={loading}
          />
        </Card>
      </Space>

      <Modal
        title="建立薪資計算批次"
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleCreate}
        width={500}
      >
        <Form form={form} layout="vertical" initialValues={{ payrollSystem: 'ALL' }}>
          <Form.Item name="name" label="批次名稱" rules={[{ required: true, message: '請輸入名稱' }]}>
            <Input placeholder="例：2025年12月正式薪資" />
          </Form.Item>
          
          <Form.Item name="period" label="計薪期間" rules={[{ required: true, message: '請選擇期間' }]}>
            <DatePicker.RangePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="payDate" label="預計發薪日" rules={[{ required: true, message: '請選擇發薪日' }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="payrollSystem" label="薪資制度範疇">
            <Select>
              <Option value="ALL">全部制度</Option>
              <Option value="MONTHLY">僅月薪制</Option>
              <Option value="HOURLY">僅時薪制</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default HR04PayrollListPage;
