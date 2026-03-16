/**
 * Employee Form Modal
 * Domain Code: HR02
 * 新增員工表單 Modal
 */

import { DatePicker, Form, Input, Modal, Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { OrganizationApi } from '../api/OrganizationApi';
import type { DepartmentDto } from '../api/OrganizationTypes';

const { Option } = Select;

interface EmployeeFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => Promise<void>;
  loading?: boolean;
}

/**
 * 新增員工表單 Modal
 * 包含基本的員工建立欄位
 */
export const EmployeeFormModal: React.FC<EmployeeFormModalProps> = ({
  open,
  onCancel,
  onSubmit,
  loading = false,
}) => {
  const [form] = Form.useForm();
  const [departments, setDepartments] = useState<DepartmentDto[]>([]);
  const [deptLoading, setDeptLoading] = useState(false);

  /** 載入部門列表供選擇 */
  useEffect(() => {
    if (open) {
      form.resetFields();
      setDeptLoading(true);
      OrganizationApi.getDepartments()
        .then((res) => {
          setDepartments(res.items || []);
        })
        .catch(() => {
          setDepartments([]);
        })
        .finally(() => {
          setDeptLoading(false);
        });
    }
  }, [open, form]);

  const handleFinish = async () => {
    try {
      const values = await form.validateFields();
      // 取得所選部門對應的組織ID
      const selectedDept = departments.find(d => d.departmentId === values.departmentId);
      const payload = {
        employeeNumber: values.employeeNumber,
        lastName: values.lastName,
        firstName: values.firstName,
        nationalId: values.nationalId,
        dateOfBirth: values.birthDate?.format('YYYY-MM-DD'),
        gender: values.gender,
        companyEmail: values.companyEmail,
        mobilePhone: values.mobilePhone?.replace(/-/g, ''),
        organizationId: selectedDept?.organizationId ?? '',
        departmentId: values.departmentId,
        jobTitle: values.jobTitle,
        employmentType: values.employmentType,
        hireDate: values.hireDate?.format('YYYY-MM-DD'),
      };
      await onSubmit(payload);
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Modal
      title="新增員工"
      open={open}
      onCancel={onCancel}
      onOk={handleFinish}
      confirmLoading={loading}
      okText="建立"
      cancelText="取消"
      width={640}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="employeeNumber"
          label="員工編號"
          rules={[{ required: true, message: '請輸入員工編號' }]}
        >
          <Input placeholder="例如：E001" />
        </Form.Item>

        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="lastName"
            label="姓"
            rules={[{ required: true, message: '請輸入姓' }]}
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：王" />
          </Form.Item>
          <Form.Item
            name="firstName"
            label="名"
            rules={[{ required: true, message: '請輸入名' }]}
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：大明" />
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="nationalId"
            label="身分證字號"
            rules={[{ required: true, message: '請輸入身分證字號' }]}
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：A123456789" />
          </Form.Item>
          <Form.Item
            name="gender"
            label="性別"
            rules={[{ required: true, message: '請選擇性別' }]}
            style={{ flex: 1 }}
          >
            <Select placeholder="選擇性別">
              <Option value="MALE">男</Option>
              <Option value="FEMALE">女</Option>
            </Select>
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="birthDate"
            label="生日"
            rules={[{ required: true, message: '請選擇生日' }]}
            style={{ flex: 1 }}
          >
            <DatePicker style={{ width: '100%' }} placeholder="選擇日期" />
          </Form.Item>
          <Form.Item
            name="hireDate"
            label="到職日"
            rules={[{ required: true, message: '請選擇到職日' }]}
            style={{ flex: 1 }}
          >
            <DatePicker style={{ width: '100%' }} placeholder="選擇日期" />
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="companyEmail"
            label="公司 Email"
            rules={[
              { required: true, message: '請輸入公司 Email' },
              { type: 'email', message: '請輸入有效的 Email' },
            ]}
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：daming.wang@company.com" />
          </Form.Item>
          <Form.Item
            name="mobilePhone"
            label="手機"
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：0912-345-678" />
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="departmentId"
            label="部門"
            rules={[{ required: true, message: '請選擇部門' }]}
            style={{ flex: 1 }}
          >
            <Select placeholder="選擇部門" loading={deptLoading}>
              {departments.map((dept) => (
                <Option key={dept.departmentId} value={dept.departmentId}>
                  {dept.name}（{dept.code}）
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="jobTitle"
            label="職位"
            rules={[{ required: true, message: '請輸入職位' }]}
            style={{ flex: 1 }}
          >
            <Input placeholder="例如：軟體工程師" />
          </Form.Item>
        </div>

        <Form.Item
          name="employmentType"
          label="僱用類型"
          rules={[{ required: true, message: '請選擇僱用類型' }]}
        >
          <Select placeholder="選擇僱用類型">
            <Option value="FULL_TIME">全職</Option>
            <Option value="PART_TIME">兼職</Option>
            <Option value="CONTRACT">約聘</Option>
            <Option value="INTERN">實習</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};
