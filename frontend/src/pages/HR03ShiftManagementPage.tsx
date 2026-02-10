import { EditOutlined, PlusOutlined, StopOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, InputNumber, message, Modal, Select, Space, Table, Tag, TimePicker, Typography } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import type { CreateShiftRequest, ShiftDto } from '../features/attendance/api/AttendanceTypes';
import { ShiftApi } from '../features/attendance/api/ShiftApi';

const { Title } = Typography;
const { Option } = Select;

export const HR03ShiftManagementPage: React.FC = () => {
  const [shifts, setShifts] = useState<ShiftDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingShift, setEditingShift] = useState<ShiftDto | null>(null);
  const [form] = Form.useForm();

  const fetchShifts = async () => {
    setLoading(true);
    try {
      const data = await ShiftApi.getShiftList();
      setShifts(data);
    } catch (error) {
      message.error('載入班別失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShifts();
  }, []);

  const handleCreateOrUpdate = async () => {
    try {
      const values = await form.validateFields();
      const request: CreateShiftRequest = {
        ...values,
        workStartTime: values.workStartTime.format('HH:mm'),
        workEndTime: values.workEndTime.format('HH:mm'),
      };

      if (editingShift) {
        await ShiftApi.updateShift(editingShift.shiftId, request);
        message.success('更新班別成功');
      } else {
        await ShiftApi.createShift(request);
        message.success('建立班別成功');
      }
      setModalVisible(false);
      fetchShifts();
    } catch (error) {
      message.error('操作失敗');
    }
  };

  const handleDeactivate = async (id: string) => {
    try {
      await ShiftApi.deactivateShift(id);
      message.success('班別已停用');
      fetchShifts();
    } catch (error) {
      message.error('操作失敗');
    }
  };

  const showModal = (shift?: ShiftDto) => {
    setEditingShift(shift || null);
    if (shift) {
      form.setFieldsValue({
        ...shift,
        workStartTime: dayjs(shift.workStartTime, 'HH:mm'),
        workEndTime: dayjs(shift.workEndTime, 'HH:mm'),
      });
    } else {
      form.resetFields();
    }
    setModalVisible(true);
  };

  const columns = [
    { title: '班別名稱', dataIndex: 'shiftName', key: 'shiftName' },
    { title: '代碼', dataIndex: 'shiftCode', key: 'shiftCode' },
    { title: '類型', dataIndex: 'shiftType', key: 'shiftType', render: (type: string) => <Tag color="blue">{type}</Tag> },
    { title: '上班時間', dataIndex: 'workStartTime', key: 'workStartTime' },
    { title: '下班時間', dataIndex: 'workEndTime', key: 'workEndTime' },
    { title: '狀態', dataIndex: 'isActive', key: 'isActive', render: (active: boolean) => active ? <Tag color="green">啟用中</Tag> : <Tag color="red">已停用</Tag> },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: ShiftDto) => (
        <Space size="middle">
          <Button icon={<EditOutlined />} onClick={() => showModal(record)}>編輯</Button>
          {record.isActive && (
            <Button danger icon={<StopOutlined />} onClick={() => handleDeactivate(record.shiftId)}>停用</Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card 
        title={<Title level={3}>班別管理</Title>}
        extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>新增班別</Button>}
      >
        <Table 
          columns={columns} 
          dataSource={shifts} 
          rowKey="shiftId" 
          loading={loading} 
        />
      </Card>

      <Modal
        title={editingShift ? '編輯班別' : '新增班別'}
        visible={modalVisible}
        onOk={handleCreateOrUpdate}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="shiftName" label="班別名稱" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="shiftCode" label="班別代碼" rules={[{ required: true }]}>
            <Input disabled={!!editingShift} />
          </Form.Item>
          <Form.Item name="shiftType" label="班別類型" rules={[{ required: true }]}>
            <Select>
              <Option value="STANDARD">固定班 (Standard)</Option>
              <Option value="FLEXIBLE">彈性班 (Flexible)</Option>
              <Option value="ROTATING">輪班 (Rotating)</Option>
            </Select>
          </Form.Item>
          <div style={{ display: 'flex', gap: '16px' }}>
            <Form.Item name="workStartTime" label="上班時間" rules={[{ required: true }]} style={{ flex: 1 }}>
              <TimePicker format="HH:mm" style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="workEndTime" label="下班時間" rules={[{ required: true }]} style={{ flex: 1 }}>
              <TimePicker format="HH:mm" style={{ width: '100%' }} />
            </Form.Item>
          </div>
          <div style={{ display: 'flex', gap: '16px' }}>
            <Form.Item name="lateToleranceMinutes" label="遲到緩衝 (分鐘)" initialValue={0} style={{ flex: 1 }}>
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="earlyLeaveToleranceMinutes" label="早退緩衝 (分鐘)" initialValue={0} style={{ flex: 1 }}>
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </div>
        </Form>
      </Modal>
    </div>
  );
};

export default HR03ShiftManagementPage;
