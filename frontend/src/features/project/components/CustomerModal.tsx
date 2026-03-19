import { Form, Input, Modal, Select } from 'antd';
import React, { useEffect } from 'react';
import type { CustomerViewModel } from '../model/CustomerViewModel';

interface CustomerModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  initialData?: CustomerViewModel | null;
  loading?: boolean;
  onSubmit: (values: any) => Promise<boolean>;
}

/**
 * HR06-M01: 客戶編輯對話框
 */
export const CustomerModal: React.FC<CustomerModalProps> = ({
  open,
  onCancel,
  onSuccess,
  initialData,
  loading,
  onSubmit,
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open) {
      if (initialData) {
        form.setFieldsValue({
          customer_code: initialData.customerCode,
          customer_name: initialData.customerName,
          tax_id: initialData.taxId,
          industry: initialData.industry,
          email: initialData.email,
          phone_number: initialData.phoneNumber,
          status: initialData.status,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 'ACTIVE' });
      }
    }
  }, [open, initialData, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const success = await onSubmit(values);
      if (success) {
        onSuccess();
      }
    } catch (error) {
      // 驗證失敗
    }
  };

  return (
    <Modal
      title={initialData ? '編輯客戶' : '新增客戶'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      confirmLoading={loading}
      destroyOnClose
      width={600}
    >
      <Form form={form} layout="vertical" preserve={false}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Form.Item
            name="customer_code"
            label="客戶代碼"
            rules={[{ required: true, message: '請輸入客戶代碼' }]}
          >
            <Input placeholder="例如: CUST-001" disabled={!!initialData} />
          </Form.Item>
          <Form.Item
            name="customer_name"
            label="客戶名稱"
            rules={[{ required: true, message: '請輸入客戶名稱' }]}
          >
            <Input placeholder="請輸入完整公司名稱" />
          </Form.Item>
          <Form.Item name="tax_id" label="統一編號">
            <Input placeholder="例如: 12345678" />
          </Form.Item>
          <Form.Item name="industry" label="所屬行業">
            <Input placeholder="例如: 科技業" />
          </Form.Item>
          <Form.Item
            name="email"
            label="聯絡 Email"
            rules={[{ type: 'email', message: '格式不正確' }]}
          >
            <Input placeholder="example@company.com" />
          </Form.Item>
          <Form.Item name="phone_number" label="聯絡電話">
            <Input placeholder="例如: 02-12345678" />
          </Form.Item>
          <Form.Item name="status" label="狀態" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="ACTIVE">啟用</Select.Option>
              <Select.Option value="INACTIVE">停用</Select.Option>
            </Select>
          </Form.Item>
        </div>
      </Form>
    </Modal>
  );
};
