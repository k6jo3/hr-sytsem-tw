import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import {
    Button,
    Card,
    Col,
    DatePicker,
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
import { useInsuranceEnrollments } from '../features/insurance/hooks';
import type { EnrollmentViewModel } from '../features/insurance/model/InsuranceViewModel';

const { Title } = Typography;
const { Option } = Select;

/**
 * HR05InsuranceEnrollmentPage - 員工加退保管理
 * 頁面代碼：HR05-P02
 */
export const HR05InsuranceEnrollmentPage: React.FC = () => {
  const { enrollments, total, loading, fetchEnrollments, enrollEmployee, withdrawEmployee } = useInsuranceEnrollments();
  const [modalVisible, setModalVisible] = useState(false);
  const [withdrawModalVisible, setWithdrawModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<EnrollmentViewModel | null>(null);
  const [form] = Form.useForm();
  const [withdrawForm] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    fetchEnrollments();
  }, [fetchEnrollments]);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    fetchEnrollments({
      employee_id: values.employeeId,
      status: values.status,
      insurance_type: values.insuranceType
    });
  };

  const handleEnroll = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        enroll_date: values.enroll_date.format('YYYY-MM-DD'),
      };
      const success = await enrollEmployee(payload);
      if (success) {
        message.success('已送出加保申請');
        setModalVisible(false);
      }
    } catch (error) {}
  };

  const handleWithdraw = async () => {
    if (!selectedRecord) return;
    try {
      const values = await withdrawForm.validateFields();
      const success = await withdrawEmployee(
        selectedRecord.id,
        values.withdraw_date.format('YYYY-MM-DD'),
        values.reason
      );
      if (success) {
        message.success('已完成退保');
        setWithdrawModalVisible(false);
      }
    } catch (error) {}
  };

  const columns = [
    { title: '員工姓名', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '保險類型', dataIndex: 'insuranceTypeLabel', key: 'insuranceType' },
    { title: '投保單位', dataIndex: 'insuranceUnitName', key: 'unit' },
    { title: '投保薪資', dataIndex: 'monthlySalaryDisplay', key: 'salary' },
    { title: '生效日期', dataIndex: 'enrollDate', key: 'enrollDate' },
    { 
      title: '狀態', 
      dataIndex: 'statusLabel', 
      key: 'status',
      render: (label: string, record: EnrollmentViewModel) => (
        <Tag color={record.statusColor}>{label}</Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: EnrollmentViewModel) => (
        record.status === 'ACTIVE' && (
          <Button size="small" danger onClick={() => {
            setSelectedRecord(record);
            setWithdrawModalVisible(true);
          }}>
            人員退保
          </Button>
        )
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col><Title level={3}>員工加退保管理</Title></Col>
          <Col>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
              手動加保
            </Button>
          </Col>
        </Row>

        <Card>
          <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
            <Form.Item name="employeeId">
              <Input placeholder="員工 ID" style={{ width: 150 }} />
            </Form.Item>
            <Form.Item name="insuranceType">
              <Select placeholder="保險類型" style={{ width: 120 }} allowClear>
                <Option value="LABOR">勞保</Option>
                <Option value="HEALTH">健保</Option>
                <Option value="PENSION">勞退</Option>
              </Select>
            </Form.Item>
            <Form.Item name="status">
              <Select placeholder="狀態" style={{ width: 120 }} allowClear>
                <Option value="ACTIVE">投保中</Option>
                <Option value="WITHDRAWN">已退保</Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查詢</Button>
            </Form.Item>
          </Form>

          <Table 
            columns={columns} 
            dataSource={enrollments} 
            rowKey="id" 
            loading={loading}
            pagination={{ total }}
          />
        </Card>
      </Space>

      {/* 加保 Modal */}
      <Modal
        title="手動加保申請"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleEnroll}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="employee_id" label="員工 ID" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="insurance_unit_id" label="投保單位 ID" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="insurance_types" label="投保項目" rules={[{ required: true }]}>
            <Select mode="multiple">
              <Option value="LABOR">勞工保險</Option>
              <Option value="HEALTH">全民健保</Option>
              <Option value="PENSION">勞退提撥</Option>
            </Select>
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="monthly_salary" label="投保薪資" rules={[{ required: true }]}>
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="enroll_date" label="生效日期" rules={[{ required: true }]}>
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="reason" label="加保原因">
            <Input.TextArea placeholder="例如：新進人員加保" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 退保 Modal */}
      <Modal
        title="人員退保"
        open={withdrawModalVisible}
        onCancel={() => setWithdrawModalVisible(false)}
        onOk={handleWithdraw}
        okText="確認退保"
        okButtonProps={{ danger: true }}
      >
        <Form form={withdrawForm} layout="vertical">
          <div style={{ marginBottom: 16 }}>
             是否確認將 <b>{selectedRecord?.employeeName}</b> 的 <b>{selectedRecord?.insuranceTypeLabel}</b> 辦理退保？
          </div>
          <Form.Item name="withdraw_date" label="退保日期" rules={[{ required: true }]} initialValue={dayjs()}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="reason" label="退保原因" rules={[{ required: true }]}>
            <Input.TextArea placeholder="例如：離職退保" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default HR05InsuranceEnrollmentPage;
