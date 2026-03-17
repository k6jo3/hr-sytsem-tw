/**
 * Employee Form Modal
 * Domain Code: HR02
 * 新增員工表單 Modal
 */

import { DatePicker, Form, Input, Modal, Select, Switch } from 'antd';
import React, { useEffect, useState } from 'react';
import { OrganizationApi } from '../api/OrganizationApi';
import type { DepartmentDto } from '../api/OrganizationTypes';
import { RoleApi } from '../../auth/api/RoleApi';
import type { RoleDto } from '../../auth/api/AuthTypes';

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
  const [createAccount, setCreateAccount] = useState(false);
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [rolesLoading, setRolesLoading] = useState(false);
  /** 追蹤使用者是否手動修改過帳號名稱 */
  const [usernameManuallyEdited, setUsernameManuallyEdited] = useState(false);

  /** 載入部門列表與角色列表供選擇 */
  useEffect(() => {
    if (open) {
      form.resetFields();
      setCreateAccount(false);
      setUsernameManuallyEdited(false);

      // 載入部門列表
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

      // 載入角色列表（供帳號建立使用）
      setRolesLoading(true);
      RoleApi.getAllRoles()
        .then(setRoles)
        .catch(() => setRoles([]))
        .finally(() => setRolesLoading(false));
    }
  }, [open, form]);

  /** 員工編號變更時，自動同步登入帳號（若使用者未手動修改） */
  const handleValuesChange = (changedValues: any) => {
    if (changedValues.employeeNumber !== undefined && createAccount && !usernameManuallyEdited) {
      form.setFieldValue('accountUsername', changedValues.employeeNumber);
    }
    // 偵測使用者是否手動修改帳號名稱
    if (changedValues.accountUsername !== undefined) {
      setUsernameManuallyEdited(true);
    }
  };

  const handleFinish = async () => {
    try {
      const values = await form.validateFields();
      // 取得所選部門對應的組織ID
      const selectedDept = departments.find(d => d.departmentId === values.departmentId);
      const payload: any = {
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

      // 附加帳號建立資訊
      if (createAccount) {
        payload.createAccount = true;
        payload.accountInfo = {
          username: values.accountUsername || values.employeeNumber,
          password: 'Change@123',
          roleIds: values.accountRoles || [],
          mustChangePassword: true,
        };
      }

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
      <Form form={form} layout="vertical" onValuesChange={handleValuesChange}>
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

        {/* 同時建立系統帳號區塊 */}
        <Form.Item label="同時建立系統帳號" valuePropName="checked">
          <Switch checked={createAccount} onChange={(checked) => {
            setCreateAccount(checked);
            if (checked && !usernameManuallyEdited) {
              // 開啟時自動填入目前的員工編號
              const currentEmpNo = form.getFieldValue('employeeNumber');
              if (currentEmpNo) {
                form.setFieldValue('accountUsername', currentEmpNo);
              }
            }
          }} />
        </Form.Item>

        {createAccount && (
          <>
            <div style={{ display: 'flex', gap: 16 }}>
              <Form.Item
                name="accountUsername"
                label="登入帳號"
                rules={createAccount ? [{ required: true, message: '請輸入登入帳號' }] : []}
                style={{ flex: 1 }}
              >
                <Input placeholder="預設為員工編號" />
              </Form.Item>
              <Form.Item
                name="accountRoles"
                label="系統角色"
                rules={createAccount ? [{ required: true, message: '請選擇角色' }] : []}
                style={{ flex: 1 }}
              >
                <Select mode="multiple" placeholder="選擇角色" loading={rolesLoading}>
                  {roles.map((role) => (
                    <Option key={role.id} value={role.id}>
                      {role.role_name}（{role.role_code}）
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </div>
            <div style={{
              padding: '8px 12px',
              background: '#f6ffed',
              border: '1px solid #b7eb8f',
              borderRadius: 6,
              marginBottom: 16,
              fontSize: 13,
              color: '#52c41a',
            }}>
              預設密碼為 <strong>Change@123</strong>，使用者首次登入時須強制變更密碼
            </div>
          </>
        )}
      </Form>
    </Modal>
  );
};
