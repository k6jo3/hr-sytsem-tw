/**
 * 代理人設定管理元件
 * Domain Code: HR11
 */

import { DeleteOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import {
  Alert,
  Button,
  Card,
  DatePicker,
  Empty,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Space,
  Spin,
  Table,
  Tag,
  Typography,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import { useDelegations } from '../hooks';
import type { DelegationViewModel } from '../model/WorkflowViewModel';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

export const DelegationManager: React.FC = () => {
  const { delegations, loading, error, submitting, refresh, createDelegation, deleteDelegation } = useDelegations();
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      const [startDate, endDate] = values.dateRange;
      const result = await createDelegation(
        values.delegateeId,
        startDate.format('YYYY-MM-DD'),
        endDate.format('YYYY-MM-DD')
      );
      if (result.success) {
        message.success(result.message);
        setModalVisible(false);
        form.resetFields();
      } else {
        message.error(result.message);
      }
    } catch {
      // 表單驗證失敗
    }
  };

  const handleDelete = async (delegationId: string) => {
    const result = await deleteDelegation(delegationId);
    if (result.success) {
      message.success(result.message);
    } else {
      message.error(result.message);
    }
  };

  const columns: ColumnsType<DelegationViewModel> = [
    {
      title: '代理人',
      dataIndex: 'delegateeName',
      key: 'delegateeName',
      width: 120,
    },
    {
      title: '代理期間',
      dataIndex: 'dateRangeDisplay',
      key: 'dateRangeDisplay',
      width: 220,
    },
    {
      title: '狀態',
      key: 'status',
      width: 100,
      render: (_: unknown, record: DelegationViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: DelegationViewModel) =>
        record.canDelete ? (
          <Popconfirm
            title="確定刪除此代理人設定？"
            onConfirm={() => handleDelete(record.delegationId)}
            okText="確認"
            cancelText="取消"
          >
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              刪除
            </Button>
          </Popconfirm>
        ) : null,
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <Alert message="載入失敗" description={error} type="error" showIcon action={<Button onClick={refresh}>重試</Button>} />;
  }

  return (
    <Card>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Title level={4} style={{ margin: 0 }}>代理人設定</Title>
          <Text type="secondary">設定請假或出差期間的審核代理人</Text>
        </div>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={refresh}>重新整理</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>新增代理人</Button>
        </Space>
      </div>

      {delegations.length === 0 ? (
        <Empty description="目前沒有代理人設定" />
      ) : (
        <Table
          columns={columns}
          dataSource={delegations}
          rowKey="delegationId"
          pagination={false}
        />
      )}

      <Modal
        title="新增代理人"
        open={modalVisible}
        onOk={handleCreate}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        confirmLoading={submitting}
        okText="確認"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="delegateeId"
            label="代理人員工編號"
            rules={[{ required: true, message: '請輸入代理人員工編號' }]}
          >
            <Input placeholder="請輸入代理人員工編號" />
          </Form.Item>
          <Form.Item
            name="dateRange"
            label="代理期間"
            rules={[{ required: true, message: '請選擇代理期間' }]}
          >
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};
