import { RootState } from '@/store';
import { DatePicker, Form, Input, Modal, Select, TimePicker, message } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { AttendanceApi } from '../api/AttendanceApi';
import type { CreateCorrectionRequest } from '../api/AttendanceTypes';

interface ApplyCorrectionModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  initialValues?: {
    attendanceRecordId?: string;
    correctionDate?: string;
  };
}

export const ApplyCorrectionModal: React.FC<ApplyCorrectionModalProps> = ({
  open,
  onCancel,
  onSuccess,
  initialValues,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    if (open && initialValues) {
      form.setFieldsValue({
        correctionDate: initialValues.correctionDate ? dayjs(initialValues.correctionDate) : dayjs(),
      });
    }
  }, [open, initialValues, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (!user?.employeeId) {
        message.error('員工資訊不完整');
        return;
      }

      setLoading(true);
      const request: CreateCorrectionRequest = {
        employeeId: user.employeeId,
        attendanceRecordId: initialValues?.attendanceRecordId,
        correctionDate: values.correctionDate.format('YYYY-MM-DD'),
        correctionType: values.correctionType,
        correctedCheckInTime: values.checkInTime?.format('HH:mm'),
        correctedCheckOutTime: values.checkOutTime?.format('HH:mm'),
        reason: values.reason,
      };

      await AttendanceApi.createCorrection(request);
      message.success('補卡申請已提交');
      onSuccess();
    } catch (err) {
      if (err instanceof Error) {
        message.error(err.message || '申請失敗');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="補卡申請"
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="correctionDate"
          label="補卡日期"
          rules={[{ required: true, message: '請選擇日期' }]}
        >
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="correctionType"
          label="補卡類型"
          rules={[{ required: true, message: '請選擇類型' }]}
        >
          <Select placeholder="請選擇類型">
            <Select.Option value="FORGET_CHECK_IN">忘記打卡（上班）</Select.Option>
            <Select.Option value="FORGET_CHECK_OUT">忘記打卡（下班）</Select.Option>
            <Select.Option value="DEVICE_FAILURE">設備故障</Select.Option>
            <Select.Option value="OUT_FOR_BUSINESS">公出</Select.Option>
            <Select.Option value="OTHER">其他</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="checkInTime"
          label="修正上班時間"
        >
          <TimePicker format="HH:mm" style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="checkOutTime"
          label="修正下班時間"
        >
          <TimePicker format="HH:mm" style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="reason"
          label="原因"
          rules={[{ required: true, message: '請輸入原因' }]}
        >
          <Input.TextArea rows={4} placeholder="請輸入補卡原因" />
        </Form.Item>
      </Form>
    </Modal>
  );
};
