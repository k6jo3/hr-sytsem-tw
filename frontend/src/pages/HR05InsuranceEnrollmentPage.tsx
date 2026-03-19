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
import React, { useCallback, useEffect, useState } from 'react';
import { InsuranceApi } from '../features/insurance/api/InsuranceApi';
import { useInsuranceEnrollments } from '../features/insurance/hooks';
import type { EnrollmentViewModel } from '../features/insurance/model/InsuranceViewModel';
import { OrganizationApi } from '../features/organization/api/OrganizationApi';
import type { EmployeeDto } from '../features/organization/api/OrganizationTypes';

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

  /** 員工選項列表（供下拉選單使用） */
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeDto[]>([]);
  const [employeeLoading, setEmployeeLoading] = useState(false);

  /** 投保單位選項列表（從現有加保記錄中提取） */
  const [unitOptions, setUnitOptions] = useState<{ unit_id: string; unit_name: string }[]>([]);

  /** 載入員工列表供選單使用 */
  const loadEmployeeOptions = useCallback(async () => {
    setEmployeeLoading(true);
    try {
      const response = await OrganizationApi.getEmployeeList({ page: 1, page_size: 500 });
      setEmployeeOptions(response.employees);
    } catch (err) {
      console.warn('[HR05] 無法載入員工列表，將允許手動輸入員工 ID', err);
    } finally {
      setEmployeeLoading(false);
    }
  }, []);

  /** 從現有加保記錄的原始 DTO 中提取不重複的投保單位作為選項 */
  const loadUnitOptions = useCallback(async () => {
    try {
      const response = await InsuranceApi.getEnrollments({});
      const unitMap = new Map<string, string>();
      response.enrollments.forEach((e) => {
        if (e.insurance_unit_id && e.insurance_unit_name) {
          unitMap.set(e.insurance_unit_id, e.insurance_unit_name);
        }
      });
      const options = Array.from(unitMap.entries()).map(([id, name]) => ({
        unit_id: id,
        unit_name: name,
      }));
      if (options.length > 0) {
        setUnitOptions(options);
      }
    } catch (err) {
      console.warn('[HR05] 無法載入投保單位列表', err);
    }
  }, []);

  useEffect(() => {
    fetchEnrollments();
    loadEmployeeOptions();
    loadUnitOptions();
  }, [fetchEnrollments, loadEmployeeOptions, loadUnitOptions]);

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
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message || '加保失敗，請稍後再試');
      }
    }
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
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message || '退保失敗，請稍後再試');
      }
    }
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
              <Select
                placeholder="員工編號或姓名"
                style={{ width: 220 }}
                allowClear
                showSearch
                loading={employeeLoading}
                filterOption={(input, option) =>
                  (option?.label as string ?? '').toLowerCase().includes(input.toLowerCase())
                }
                options={employeeOptions.map(emp => ({
                  value: emp.id,
                  label: `${emp.employee_number} - ${emp.full_name}`,
                }))}
                notFoundContent={employeeLoading ? '載入中...' : '無符合的員工'}
              />
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
            scroll={{ x: 'max-content' }}
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
          <Form.Item name="employee_id" label="員工" rules={[{ required: true, message: '請選擇員工' }]}>
            <Select
              placeholder="搜尋員工編號或姓名"
              showSearch
              loading={employeeLoading}
              filterOption={(input, option) =>
                (option?.label as string ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={employeeOptions.map(emp => ({
                value: emp.id,
                label: `${emp.employee_number} - ${emp.full_name}`,
              }))}
              notFoundContent={employeeLoading ? '載入中...' : '無符合的員工'}
            />
          </Form.Item>
          <Form.Item name="insurance_unit_id" label="投保單位" rules={[{ required: true, message: '請選擇投保單位' }]}>
            <Select
              placeholder="搜尋投保單位"
              showSearch
              filterOption={(input, option) =>
                (option?.label as string ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={unitOptions.map(unit => ({
                value: unit.unit_id,
                label: unit.unit_name,
              }))}
              notFoundContent="無可用的投保單位"
            />
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
