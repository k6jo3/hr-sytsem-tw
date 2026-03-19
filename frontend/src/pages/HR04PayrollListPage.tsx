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
import { Button, Card, DatePicker, Form, Input, message, Modal, Popconfirm, Progress, Select, Space, Table, Tag, Tooltip, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePayrollRuns } from '../features/payroll/hooks/usePayrollRuns';
import type { PayrollRunViewModel } from '../features/payroll/model/PayrollViewModel';
import { PageHeader } from '@shared/components/PageHeader';

const { Text } = Typography;
const { Option } = Select;

/**
 * HR04PayrollListPage - 薪資計算批次頁面
 * 頁面代碼：HR04-P03
 */
export const HR04PayrollListPage: React.FC = () => {
  const navigate = useNavigate();
  const { runs, loading, error, fetchRuns, startRun, calculateRun, approveRun } = usePayrollRuns();
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    fetchRuns();
  }, [fetchRuns]);

  useEffect(() => {
    if (error) {
      message.error(typeof error === 'string' ? error : '載入薪資批次失敗');
    }
  }, [error]);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    fetchRuns({
      status: values.status,
      startDate: values.range?.[0]?.format('YYYY-MM-DD'),
      endDate: values.range?.[1]?.format('YYYY-MM-DD'),
    });
  };

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        name: values.name,
        payrollSystem: values.payrollSystem,
        start: values.period[0].format('YYYY-MM-DD'),
        end: values.period[1].format('YYYY-MM-DD'),
        payDate: values.payDate.format('YYYY-MM-DD'),
      };
      const success = await startRun(payload);
      if (success) {
        message.success('批次建立成功');
        setModalVisible(false);
      }
    } catch (error) {
      // 錯誤訊息由 Hook 處理或是 validation error
    }
  };

  const onExecute = async (runId: string) => {
    message.loading({ content: '計算中...', key: 'calc' });
    const success = await calculateRun(runId);
    if (success) {
      message.success({ content: '計算完成', key: 'calc' });
    } else {
      message.error({ content: '計算失敗', key: 'calc' });
    }
  };

  const onApprove = async (runId: string) => {
    const success = await approveRun(runId);
    if (success) {
      message.success('已核准批次');
    }
  };

  const columns = [
    { title: '批次名稱', dataIndex: 'name', key: 'name' },
    { 
      title: '計薪期間', 
      dataIndex: 'periodDisplay',
      key: 'periodDisplay',
    },
    { title: '發薪日', dataIndex: 'payDate', key: 'payDate' },
    { 
      title: '進度', 
      key: 'progress',
      render: (_: any, record: PayrollRunViewModel) => (
        <Space direction="vertical" size={2} style={{ width: '100%' }}>
          <Text type="secondary">{record.processedEmployees}/{record.totalEmployees}</Text>
          <Progress percent={record.progress} size="small" />
        </Space>
      )
    },
    { 
      title: '實發總額', 
      dataIndex: 'totalNetPayDisplay', 
      key: 'totalNetPayDisplay',
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
          <Tooltip title="查看明細">
            <Button 
              icon={<EyeOutlined />} 
              size="small" 
              onClick={() => navigate(`/admin/payroll/runs/${record.runId}`)} 
            />
          </Tooltip>
          
          {record.status === 'DRAFT' && (
            <Popconfirm title="確認執行計算？" onConfirm={() => onExecute(record.runId)}>
              <Button type="primary" icon={<PlayCircleOutlined />} size="small">執行計算</Button>
            </Popconfirm>
          )}

          {record.status === 'COMPLETED' && (
            <Popconfirm title="確認核准此批次？" onConfirm={() => onApprove(record.runId)}>
              <Button type="primary" ghost icon={<CheckCircleOutlined />} size="small">核准發放</Button>
            </Popconfirm>
          )}

          {record.status === 'APPROVED' && (
            <Space>
              <Button icon={<SendOutlined />} size="small">發送薪資單</Button>
              <Button icon={<BankOutlined />} size="small">產生薪轉檔</Button>
            </Space>
          )}

          <Button icon={<ReloadOutlined />} size="small" onClick={() => fetchRuns()} />
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <PageHeader
          title="薪資計算批次管理"
          subtitle="管理薪資計算批次，進行計薪與發薪作業"
          breadcrumbs={[
            { title: '薪資核算', path: '/admin/payroll/runs' },
            { title: '計薪作業中心' },
          ]}
          extra={
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
              建立新批次
            </Button>
          }
        />

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
            dataSource={runs} 
            rowKey="runId" 
            loading={loading}
          />
        </Card>
      </Space>

      <Modal
        title="建立薪資計算批次"
        open={modalVisible}
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
