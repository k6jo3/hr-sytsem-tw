import { EditOutlined, InfoCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Card, Col, Form, Input, InputNumber, Modal, Popconfirm, Row, Select, Space, Switch, Table, Tag, Tooltip, Typography, message } from 'antd';
import React, { useEffect, useState } from 'react';
import type { PayrollItemDefinitionDto } from '../features/payroll/api/PayrollTypes';
import { usePayrollItems } from '../features/payroll/hooks/usePayrollItems';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR04PayrollItemPage - 薪資項目設定頁面
 * 頁面代碼：HR04-P02
 */
export const HR04PayrollItemPage: React.FC = () => {
  const { items, loading, error, fetchItems, createItem, updateItem, deleteItem } = usePayrollItems();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingItem, setEditingItem] = useState<PayrollItemDefinitionDto | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchItems();
  }, [fetchItems]);

  useEffect(() => {
    if (error) {
      message.error(typeof error === 'string' ? error : '載入薪資項目失敗');
    }
  }, [error]);

  const handleEdit = (item: PayrollItemDefinitionDto) => {
    setEditingItem(item);
    form.setFieldsValue(item);
    setModalVisible(true);
  };

  const handleCreate = () => {
    setEditingItem(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      let success = false;
      if (editingItem) {
        success = await updateItem(editingItem.id, values);
      } else {
        success = await createItem(values);
      }
      
      if (success) {
        message.success(editingItem ? '更新成功' : '建立成功');
        setModalVisible(false);
      }
    } catch (error) {
      // Validation error or handled by hook
    }
  };

  const columns = [
    { 
      title: '項目代碼', 
      dataIndex: 'itemCode', 
      key: 'itemCode',
      render: (code: string) => <Text code>{code}</Text>
    },
    { title: '項目名稱', dataIndex: 'itemName', key: 'itemName' },
    { 
      title: '類型', 
      dataIndex: 'itemType', 
      key: 'itemType',
      render: (type: string) => type === 'EARNING' ? <Tag color="blue">收入</Tag> : <Tag color="red">扣除</Tag>
    },
    { 
      title: '應課稅', 
      dataIndex: 'taxable', 
      key: 'taxable',
      render: (taxable: boolean) => taxable ? <Tag color="success">是</Tag> : <Tag color="default">否</Tag>
    },
    { 
      title: '計入保額', 
      dataIndex: 'insurable', 
      key: 'insurable',
      render: (insurable: boolean) => insurable ? <Tag color="success">是</Tag> : <Tag color="default">否</Tag>
    },
    { 
      title: '排序', 
      dataIndex: 'displayOrder', 
      key: 'displayOrder' 
    },
    { 
      title: '狀態', 
      dataIndex: 'active', 
      key: 'active',
      render: (active: boolean) => active ? <Tag color="success">啟用</Tag> : <Tag color="default">停用</Tag>
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayrollItemDefinitionDto) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)}>編輯</Button>
          <Popconfirm title="確定刪除此項目？" onConfirm={() => deleteItem(record.id)}>
            <Button danger size="small">刪除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col><Title level={2}>薪資項目設定</Title></Col>
          <Col>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增薪資項目
            </Button>
          </Col>
        </Row>

        <Card>
          <Table 
            columns={columns} 
            dataSource={items} 
            rowKey="id" 
            loading={loading}
          />
        </Card>
      </Space>

      <Modal
        title={editingItem ? '編輯薪資項目' : '新增薪資項目'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSave}
        width={600}
      >
        <Form form={form} layout="vertical" initialValues={{ itemType: 'EARNING', taxable: true, insurable: true, active: true, displayOrder: 0 }}>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="itemCode" label="項目代碼" rules={[{ required: true, message: '請輸入代碼' }]}>
                <Input placeholder="例: MEAL_ALLOWANCE" disabled={!!editingItem} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="itemName" label="項目名稱" rules={[{ required: true, message: '請輸入名稱' }]}>
                <Input placeholder="例: 伙食津貼" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="itemType" label="項目類型" rules={[{ required: true }]}>
                <Select>
                  <Option value="EARNING">收入項 (Earning)</Option>
                  <Option value="DEDUCTION">扣除項 (Deduction)</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="displayOrder" label="顯示順序">
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="taxable" label="應課所得稅" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="insurable" label="計入投保薪資" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="active" label="狀態" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="description" label="項目描述">
            <Input.TextArea rows={3} placeholder="說明此薪資項目的發放或扣除規則" />
          </Form.Item>

          <Form.Item 
            name="calculationFormula" 
            label={
              <span>
                計算公式 (預留)&nbsp;
                <Tooltip title="未來可設定公式，目前預設為固定金額輸入">
                  <InfoCircleOutlined />
                </Tooltip>
              </span>
            }
          >
            <Input placeholder="例: BASIC_SALARY * 0.1" disabled />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default HR04PayrollItemPage;
