import { DeleteOutlined, EditOutlined, FileTextOutlined, PlayCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { PageHeader } from '@shared/components/PageHeader';
import { Button, Card, DatePicker, Form, Input, Modal, Select, Space, Table, Tag, Tooltip } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { CreateCycleRequest, CycleType, UpdateCycleRequest } from '../features/performance/api/PerformanceTypes';
import { useCycles } from '../features/performance/hooks';
import type { PerformanceCycleViewModel } from '../features/performance/model/PerformanceViewModel';

const { Option } = Select;
const { RangePicker } = DatePicker;

/**
 * HR08-P01 考核週期管理頁面 (Cycle Management)
 */
export const HR08CycleManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { cycles, loading, fetchCycles, createCycle, updateCycle, deleteCycle, startCycle } = useCycles();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  useEffect(() => {
    fetchCycles();
  }, [fetchCycles]);

  const handleEdit = (record: PerformanceCycleViewModel) => {
    setEditingId(record.cycleId);
    // Parse dates for form
    const [startDate, endDate] = record.periodDisplay.split(' ~ ');
    form.setFieldsValue({
      cycle_name: record.cycleName,
      cycle_type: Object.keys(CYCLE_TYPE_MAP).find(key => CYCLE_TYPE_MAP[key as CycleType] === record.cycleTypeLabel),
      period: [dayjs(startDate), dayjs(endDate)],
      self_eval_deadline: record.selfEvalDeadlineDisplay ? dayjs(record.selfEvalDeadlineDisplay) : undefined,
      manager_eval_deadline: record.managerEvalDeadlineDisplay ? dayjs(record.managerEvalDeadlineDisplay) : undefined,
    });
    setModalVisible(true);
  };

  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '確認刪除',
      content: '確定要刪除此考核週期嗎？此操作無法復原。',
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await deleteCycle(id);
      },
    });
  };

  const handleStart = (id: string) => {
    Modal.confirm({
      title: '啟動考核週期',
      content: '啟動後將通知所有員工開始自評，確定要啟動嗎？',
      onOk: async () => {
        await startCycle(id);
      },
    });
  };

  const handleTemplateDesign = (id: string, name: string) => {
    navigate(`/admin/performance/cycles/${id}/template`, { state: { cycleName: name } });
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const requestData = {
        cycle_name: values.cycle_name,
        cycle_type: values.cycle_type,
        start_date: values.period[0].format('YYYY-MM-DD'),
        end_date: values.period[1].format('YYYY-MM-DD'),
        self_eval_deadline: values.self_eval_deadline?.format('YYYY-MM-DD'),
        manager_eval_deadline: values.manager_eval_deadline?.format('YYYY-MM-DD'),
      };

      if (editingId) {
        await updateCycle(editingId, requestData as UpdateCycleRequest);
      } else {
        await createCycle(requestData as CreateCycleRequest);
      }
      setModalVisible(false);
      form.resetFields();
      setEditingId(null);
    } catch (error) {
      // Form validation failed
    }
  };

  // Helper map for cycle types (reversed from factory)
  const CYCLE_TYPE_MAP: Record<CycleType, string> = {
    PROBATION: '試用期考核',
    QUARTERLY: '季度考核',
    ANNUAL: '年度考核',
  };

  const columns = [
    {
      title: '週期名稱',
      dataIndex: 'cycleName',
      key: 'cycleName',
    },
    {
      title: '考核類型',
      dataIndex: 'cycleTypeLabel',
      key: 'cycleType',
      render: (text: string, record: PerformanceCycleViewModel) => (
        <Tag color={record.cycleTypeColor}>{text}</Tag>
      ),
    },
    {
      title: '考核期間',
      dataIndex: 'periodDisplay',
      key: 'period',
    },
    {
      title: '自評截止',
      dataIndex: 'selfEvalDeadlineDisplay',
      key: 'selfEvalDeadline',
    },
    {
      title: '主管評截止',
      dataIndex: 'managerEvalDeadlineDisplay',
      key: 'managerEvalDeadline',
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      render: (text: string, record: PerformanceCycleViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PerformanceCycleViewModel) => (
        <Space size="small">
          {record.isDraft && (
            <>
              <Tooltip title="編輯">
                <Button 
                  icon={<EditOutlined />} 
                  onClick={() => handleEdit(record)} 
                  size="small"
                />
              </Tooltip>
              <Tooltip title="啟動">
                <Button 
                  icon={<PlayCircleOutlined />} 
                  onClick={() => handleStart(record.cycleId)} 
                  size="small"
                  type="primary"
                  ghost
                />
              </Tooltip>
              <Tooltip title="刪除">
                <Button 
                  icon={<DeleteOutlined />} 
                  onClick={() => handleDelete(record.cycleId)} 
                  size="small" 
                  danger 
                />
              </Tooltip>
            </>
          )}
          <Tooltip title="表單設計">
            <Button 
              icon={<FileTextOutlined />} 
              onClick={() => handleTemplateDesign(record.cycleId, record.cycleName)} 
              size="small"
            >
              表單
            </Button>
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader
        title="績效週期"
        subtitle="管理考核週期，包含建立、編輯、啟動與表單設計"
        breadcrumbs={[
          { title: '績效管理' },
          { title: '績效週期' },
        ]}
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => {
              setEditingId(null);
              form.resetFields();
              setModalVisible(true);
            }}
          >
            新增考核週期
          </Button>
        }
      />
      <Card>
        <Table
          columns={columns}
          dataSource={cycles}
          rowKey="cycleId"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingId ? '編輯考核週期' : '新增考核週期'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="cycle_name"
            label="週期名稱"
            rules={[{ required: true, message: '請輸入週期名稱' }]}
          >
            <Input placeholder="例如: 2025年度考核" />
          </Form.Item>
          
          <Form.Item
            name="cycle_type"
            label="考核類型"
            rules={[{ required: true, message: '請選擇考核類型' }]}
          >
            <Select placeholder="請選擇">
              <Option value="ANNUAL">年度考核</Option>
              <Option value="QUARTERLY">季度考核</Option>
              <Option value="PROBATION">試用期考核</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="period"
            label="考核期間"
            rules={[{ required: true, message: '請選擇考核期間' }]}
          >
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>
          
          <Form.Item
            name="self_eval_deadline"
            label="自評截止日期"
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          
          <Form.Item
            name="manager_eval_deadline"
            label="主管評截止日期"
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
