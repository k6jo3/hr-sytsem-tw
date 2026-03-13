import { EditOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons';
import {
    Button,
    Card,
    Col,
    DatePicker,
    Divider,
    Form,
    Input,
    InputNumber,
    message,
    Modal,
    Row,
    Select,
    Space,
    Table,
    Tag,
    Typography
} from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import type { SalaryStructureViewModel } from '../features/payroll/factory/SalaryStructureViewModelFactory';
import { useSalaryStructure } from '../features/payroll/hooks/useSalaryStructure';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR04SalaryStructurePage - 薪資結構設定頁面
 * 頁面代碼：HR04-P01
 */
export const HR04SalaryStructurePage: React.FC = () => {
  const { structures, loading, fetchStructures, createStructure, updateStructure } = useSalaryStructure();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SalaryStructureViewModel | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    fetchStructures();
  }, [fetchStructures]);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    fetchStructures({
      employeeId: values.searchEmployeeId,
      isActive: values.searchStatus,
      payrollSystem: values.searchSystem,
    });
  };

  const handleEdit = (record: SalaryStructureViewModel) => {
    setEditingRecord(record);
    // Find raw DTO data if needed, but here we can use what we have
    // Actually we might need the raw DTO for complex fields like sub-items
    form.setFieldsValue({
      ...record,
      effectiveDate: record.effectiveDate ? dayjs(record.effectiveDate) : null,
    });
    setModalVisible(true);
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        effectiveDate: values.effectiveDate?.format('YYYY-MM-DD'),
      };

      let success = false;
      if (editingRecord) {
        success = await updateStructure(editingRecord.id, payload);
      } else {
        success = await createStructure(payload);
      }

      if (success) {
        message.success(editingRecord ? '更新成功' : '建立成功');
        setModalVisible(false);
      }
    } catch (error) {
      // Form validation failed or API error handled by hook
    }
  };

  const columns = [
    { 
      title: '員工ID', 
      dataIndex: 'employeeId', 
      key: 'employeeId' 
    },
    { 
      title: '薪資制度', 
      dataIndex: 'payrollSystemLabel', 
      key: 'payrollSystem',
      render: (label: string, record: SalaryStructureViewModel) => (
        <Tag color={record.payrollSystem === 'MONTHLY' ? 'blue' : 'orange'}>{label}</Tag>
      )
    },
    { 
      title: '薪資/時薪', 
      dataIndex: 'amountDisplay',
      key: 'rate'
    },
    { 
      title: '生效日期', 
      dataIndex: 'effectiveDate', 
      key: 'effectiveDate' 
    },
    { 
      title: '狀態', 
      key: 'active',
      render: (_: any, record: SalaryStructureViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: SalaryStructureViewModel) => (
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
          <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
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
            dataSource={structures} 
            rowKey="id" 
            loading={loading}
          />
        </Card>
      </Space>

      <Modal
        title={editingRecord ? '編輯薪資結構' : '新增薪資結構'}
        open={modalVisible}
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

          <Divider orientation="left">固定薪資項目</Divider>
          <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
            這裡可以設定員工每月的固定加給或扣額，如伙食津貼、職務加給等。
          </Text>
          {/* 固定薪資項目列表由薪資項目定義管理（HR04PayrollItemPage）維護 */}
        </Form>
      </Modal>
    </div>
  );
};

export default HR04SalaryStructurePage;
