import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons';
import {
    Button,
    Card,
    Col,
    DatePicker,
    Form,
    Input,
    InputNumber,
    message,
    Row,
    Select,
    Space,
    Typography
} from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useEmployees } from '../features/organization/hooks/useEmployees';
import { EmployeeViewModel } from '../features/organization/model/EmployeeViewModel';
import { ProjectApi } from '../features/project/api/ProjectApi';
import { useCustomers } from '../features/project/hooks/useCustomers';
import { CustomerViewModel } from '../features/project/model/CustomerViewModel';

const { Title } = Typography;
const { Option } = Select;
const { TextArea } = Input;

/**
 * HR06-P04: 專案建立/編輯頁面
 */
export const HR06ProjectEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const { customers, fetchCustomers } = useCustomers();
  const { employees } = useEmployees({ page_size: 100 });

  useEffect(() => {
    fetchCustomers({ size: 100 });
    
    if (isEdit) {
      loadProjectData(id);
    }
  }, [id, isEdit, fetchCustomers]);

  const loadProjectData = async (projectId: string) => {
    setLoading(true);
    try {
      const { project } = await ProjectApi.getProjectDetail(projectId);
      form.setFieldsValue({
        ...project,
        planned_start_date: dayjs(project.planned_start_date),
        planned_end_date: dayjs(project.planned_end_date),
      });
    } catch (error: any) {
      message.error(error.message || '載入專案資料失敗');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values: any) => {
    setLoading(true);
    const payload = {
      ...values,
      planned_start_date: values.planned_start_date.format('YYYY-MM-DD'),
      planned_end_date: values.planned_end_date.format('YYYY-MM-DD'),
    };

    try {
      if (isEdit) {
        await ProjectApi.updateProject(id, payload);
        message.success('更新專案成功');
      } else {
        await ProjectApi.createProject(payload);
        message.success('建立專案成功');
      }
      navigate('/admin/projects');
    } catch (error: any) {
      message.error(error.message || '儲存失敗');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)} />
              <Title level={3} style={{ margin: 0 }}>
                {isEdit ? '編輯專案' : '新增專案'}
              </Title>
            </Space>
          </Col>
        </Row>

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            project_type: 'DEVELOPMENT',
            budget_type: 'FIXED_PRICE',
            status: 'PLANNING'
          }}
        >
          <Card loading={loading}>
            <Row gutter={24}>
              <Col span={12}>
                <Form.Item
                  name="project_code"
                  label="專案代碼"
                  rules={[{ required: true, message: '請輸入專案代碼' }]}
                >
                  <Input placeholder="例如: PRJ-2025-001" disabled={isEdit} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="project_name"
                  label="專案名稱"
                  rules={[{ required: true, message: '請輸入專案名稱' }]}
                >
                  <Input placeholder="例如: XX銀行系統開發案" />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={24}>
              <Col span={12}>
                <Form.Item
                  name="customer_id"
                  label="客戶"
                  rules={[{ required: true, message: '請選擇客戶' }]}
                >
                  <Select placeholder="請選擇客戶">
                    {customers.map((c: CustomerViewModel) => (
                      <Option key={c.id} value={c.id}>
                        {c.customerCode} - {c.customerName}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="project_manager_id"
                  label="專案經理"
                  rules={[{ required: true, message: '請選擇專案經理' }]}
                >
                  <Select placeholder="請選擇專案經理">
                    {employees.map((e: EmployeeViewModel) => (
                      <Option key={e.id} value={e.id}>{e.fullName}</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={24}>
              <Col span={8}>
                <Form.Item name="project_type" label="專案類型" rules={[{ required: true }]}>
                  <Select>
                    <Option value="DEVELOPMENT">新開發</Option>
                    <Option value="MAINTENANCE">維護</Option>
                    <Option value="CONSULTING">顧問</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="budget_type" label="預算模式" rules={[{ required: true }]}>
                  <Select>
                    <Option value="FIXED_PRICE">固定價格</Option>
                    <Option value="TIME_AND_MATERIAL">實報實銷</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="status" label="專案狀態">
                  <Select>
                    <Option value="PLANNING">規劃中</Option>
                    <Option value="IN_PROGRESS">進行中</Option>
                    <Option value="COMPLETED">已結案</Option>
                    <Option value="ON_HOLD">暫停</Option>
                    <Option value="CANCELLED">已取消</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={24}>
              <Col span={12}>
                <Form.Item name="budget_amount" label="預算金額" rules={[{ required: true }]}>
                  <InputNumber
                    style={{ width: '100%' }}
                    formatter={val => `$ ${val}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                    parser={val => val!.replace(/\$\s?|(,*)/g, '')}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="budget_hours" label="預估工時 (h)" rules={[{ required: true }]}>
                  <InputNumber style={{ width: '100%' }} />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={24}>
              <Col span={12}>
                <Form.Item name="planned_start_date" label="計畫開始日期" rules={[{ required: true }]}>
                  <DatePicker style={{ width: '100%' }} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="planned_end_date" label="計畫結束日期" rules={[{ required: true }]}>
                  <DatePicker style={{ width: '100%' }} />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="description" label="備註內容">
              <TextArea rows={4} placeholder="請輸入專案相關備註資訊..." />
            </Form.Item>

            <div style={{ textAlign: 'right', marginTop: 24 }}>
              <Space>
                <Button onClick={() => navigate(-1)}>取消</Button>
                <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={loading}>
                  確認儲存
                </Button>
              </Space>
            </div>
          </Card>
        </Form>
      </Space>
    </div>
  );
};

export default HR06ProjectEditPage;
