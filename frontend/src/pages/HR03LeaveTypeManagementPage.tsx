import { EditOutlined, PlusOutlined, StopOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, InputNumber, message, Modal, Space, Switch, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import type { LeaveTypeDto } from '../features/attendance/api/AttendanceTypes';
import { LeaveApi } from '../features/attendance/api/LeaveApi';

const { Title } = Typography;

export const HR03LeaveTypeManagementPage: React.FC = () => {
  const [leaveTypes, setLeaveTypes] = useState<LeaveTypeDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingType, setEditingType] = useState<LeaveTypeDto | null>(null);
  const [form] = Form.useForm();

  const fetchLeaveTypes = async () => {
    setLoading(true);
    try {
      const data = await LeaveApi.getLeaveTypes();
      setLeaveTypes(data);
    } catch (error) {
      message.error('載入假別列表失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaveTypes();
  }, []);

  const handleCreateOrUpdate = async () => {
    try {
      const values = await form.validateFields();
      if (editingType) {
        await LeaveApi.updateLeaveType(editingType.leaveTypeId, values);
        message.success('更新假別成功');
      } else {
        await LeaveApi.createLeaveType(values);
        message.success('建立假別成功');
      }
      setModalVisible(false);
      fetchLeaveTypes();
    } catch (error) {
      message.error('操作失敗');
    }
  };

  const handleDeactivate = async (id: string) => {
    try {
      await LeaveApi.deactivateLeaveType(id);
      message.success('假別已停用');
      fetchLeaveTypes();
    } catch (error) {
      message.error('操作失敗');
    }
  };

  const showModal = (type?: LeaveTypeDto) => {
    setEditingType(type || null);
    if (type) {
      form.setFieldsValue(type);
    } else {
      form.resetFields();
    }
    setModalVisible(true);
  };

  const columns = [
    { title: '假別名稱', dataIndex: 'leaveTypeName', key: 'leaveTypeName' },
    { title: '代碼', dataIndex: 'leaveTypeCode', key: 'leaveTypeCode' },
    { title: '有薪/無薪', dataIndex: 'isPaid', key: 'isPaid', render: (paid: boolean) => paid ? <Tag color="gold">有薪</Tag> : <Tag>無薪</Tag> },
    { title: '年度額度', dataIndex: 'annualQuotaDays', key: 'annualQuotaDays', render: (days: number) => `${days} 天` },
    { title: '允許留用', dataIndex: 'allowCarryOver', key: 'allowCarryOver', render: (carry: boolean) => carry ? <Tag color="blue">是</Tag> : <Tag>否</Tag> },
    { title: '狀態', dataIndex: 'isActive', key: 'isActive', render: (active: boolean) => active ? <Tag color="green">啟用中</Tag> : <Tag color="red">已停用</Tag> },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: LeaveTypeDto) => (
        <Space size="middle">
          <Button icon={<EditOutlined />} onClick={() => showModal(record)}>編輯</Button>
          {record.isActive && (
            <Button danger icon={<StopOutlined />} onClick={() => handleDeactivate(record.leaveTypeId)}>停用</Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card 
        title={<Title level={3}>假別管理</Title>}
        extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>新增假別</Button>}
      >
        <Table 
          columns={columns} 
          dataSource={leaveTypes} 
          rowKey="leaveTypeId" 
          loading={loading} 
        />
      </Card>

      <Modal
        title={editingType ? '編輯假別' : '新增假別'}
        visible={modalVisible}
        onOk={handleCreateOrUpdate}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="leaveTypeName" label="假別名稱" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="leaveTypeCode" label="假別代碼" rules={[{ required: true }]}>
            <Input disabled={!!editingType} />
          </Form.Item>
          <Form.Item name="annualQuotaDays" label="年度額度 (天)" rules={[{ required: true }]}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="isPaid" label="是否有薪" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="allowCarryOver" label="允許留用至明年" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="isActive" label="啟用狀態" valuePropName="checked" initialValue={true}>
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default HR03LeaveTypeManagementPage;
