import { EditOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Col, DatePicker, Divider, Form, Input, InputNumber, message, Modal, Row, Select, Space, Table, Tag, Typography } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { SalaryStructureDto } from '../features/payroll/api/PayrollTypes';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR04SalaryStructurePage - 薪資結構設定頁面
 * 頁面代碼：HR04-P01
 */
export const HR04SalaryStructurePage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<SalaryStructureDto[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SalaryStructureDto | null>(null);
  const [form] = Form.useForm();

  const fetchData = async (filters: any = {}) => {
    setLoading(true);
    try {
      const res = await PayrollApi.getSalaryStructures(filters);
      setData(res.items || res.content || []);
    } catch (error) {
      message.error('載入薪資結構失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    const values = form.getFieldsValue(['searchEmployeeId', 'searchStatus', 'searchSystem']);
    fetchData({
      employeeId: values.searchEmployeeId,
      isActive: values.searchStatus,
      payrollSystem: values.searchSystem,
    });
  };

  const handleEdit = (record: SalaryStructureDto) => {
    // ... existing code ...
    setEditingRecord(record);
    form.setFieldsValue({
      ...record,
      effectiveDate: record.effectiveDate ? dayjs(record.effectiveDate) : null,
      endDate: record.endDate ? dayjs(record.endDate) : null,
    });
    setModalVisible(true);
  };

  const handleSave = async () => {
    // ... existing code ...
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        effectiveDate: values.effectiveDate?.format('YYYY-MM-DD'),
        endDate: values.endDate?.format('YYYY-MM-DD'),
      };

      if (editingRecord) {
        await PayrollApi.updateSalaryStructure(editingRecord.id, payload);
        message.success('更新成功');
      } else {
        await PayrollApi.createSalaryStructure(payload);
        message.success('建立成功');
      }
      setModalVisible(false);
      fetchData();
    } catch (error) {
      message.error('儲存失敗');
    }
  };

  const columns = [
    // ... existing code ...
    { 
      title: '員工ID', 
      dataIndex: 'employeeId', 
      key: 'employeeId' 
    },
    { 
      title: '薪資制度', 
      dataIndex: 'payrollSystem', 
      key: 'payrollSystem',
      render: (val: string) => val === 'MONTHLY' ? <Tag color="blue">月薪制</Tag> : <Tag color="orange">時薪制</Tag>
    },
    { 
      title: '薪資/時薪', 
      key: 'rate',
      render: (_: any, record: SalaryStructureDto) => (
        record.payrollSystem === 'MONTHLY' 
          ? `$${record.monthlySalary?.toLocaleString()}` 
          : `$${record.hourlyRate?.toLocaleString()}/時`
      )
    },
    { 
      title: '生效日期', 
      dataIndex: 'effectiveDate', 
      key: 'effectiveDate' 
    },
    { 
      title: '狀態', 
      dataIndex: 'active', 
      key: 'active',
      render: (active: boolean) => active ? <Tag color="success">生效中</Tag> : <Tag color="default">已失效</Tag>
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: SalaryStructureDto) => (
        <Button icon={<EditOutlined />} onClick={() => handleEdit(record)}>編輯</Button>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col><Title level={2}>薪資結構管理</Title></Col>
          <Col>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => {
              setEditingRecord(null);
              form.resetFields();
              setModalVisible(true);
            }}>
              新增薪資結構
            </Button>
          </Col>
        </Row>

        <Card>
          <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
            <Form.Item name="searchEmployeeId">
              <Input placeholder="搜尋員工 ID" prefix={<SearchOutlined />} style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="searchSystem">
              <Select placeholder="薪資制度" style={{ width: 120 }} allowClear>
                <Option value="MONTHLY">月薪制</Option>
                <Option value="HOURLY">時薪制</Option>
              </Select>
            </Form.Item>
            <Form.Item name="searchStatus">
              <Select placeholder="狀態" style={{ width: 120 }} allowClear>
                <Option value="true">生效中</Option>
                <Option value="false">已失效</Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Button type="primary" onClick={handleSearch}>查詢</Button>
            </Form.Item>
          </Form>

          <Table 
            columns={columns} 
            dataSource={data} 
            rowKey="id" 
            loading={loading}
          />
        </Card>
      </Space>

      <Modal
        title={editingRecord ? '編輯薪資結構' : '新增薪資結構'}
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSave}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="employeeId" label="員工ID" rules={[{ required: true }]}>
                <Input disabled={!!editingRecord} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="payrollSystem" label="薪資制度" rules={[{ required: true }]}>
                <Select>
                  <Option value="MONTHLY">月薪制</Option>
                  <Option value="HOURLY">時薪制</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item noStyle shouldUpdate={(prev, curr) => prev.payrollSystem !== curr.payrollSystem}>
            {({ getFieldValue }) => (
              getFieldValue('payrollSystem') === 'MONTHLY' ? (
                <Form.Item name="monthlySalary" label="月薪" rules={[{ required: true }]}>
                  <InputNumber style={{ width: '100%' }} prefix="$" formatter={val => `${val}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} />
                </Form.Item>
              ) : (
                <Form.Item name="hourlyRate" label="時薪" rules={[{ required: true }]}>
                  <InputNumber style={{ width: '100%' }} prefix="$" />
                </Form.Item>
              )
            )}
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="effectiveDate" label="生效日期" rules={[{ required: true }]}>
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="active" label="狀態" valuePropName="checked">
                <Select>
                  <Option value={true}>生效中</Option>
                  <Option value={false}>已失效</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Divider orientation="left">固定津貼項目</Divider>
          <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
            這裡可以設定員工每月的固定加給或扣額，如伙食津貼、職務加給等。
          </Text>
          {/* TODO: Implement dynamic item list if needed, or simple fields */}
        </Form>
      </Modal>
    </div>
  );
};

export default HR04SalaryStructurePage;
