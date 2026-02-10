import { RootState } from '@/store';
import { DatePicker, Form, Input, Modal, Select, TimePicker, message } from 'antd';
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import type { ApplyOvertimeRequest } from '../api/AttendanceTypes';
import { OvertimeApi } from '../api/OvertimeApi';

interface ApplyOvertimeModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
}

export const ApplyOvertimeModal: React.FC<ApplyOvertimeModalProps> = ({
  visible,
  onCancel,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const user = useSelector((state: RootState) => state.auth.user);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (!user?.employeeId) {
        message.error('員工資訊不完整');
        return;
      }

      setLoading(true);
      const request: ApplyOvertimeRequest = {
        employeeId: user.employeeId,
        overtimeDate: values.date.format('YYYY-MM-DD'),
        startTime: values.time[0].format('HH:mm'),
        endTime: values.time[1].format('HH:mm'),
        overtimeType: values.overtimeType,
        reason: values.reason,
      };

      await OvertimeApi.applyOvertime(request);
      message.success('加班申請已提交');
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
      title="申請加班"
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="date"
          label="加班日期"
          rules={[{ required: true, message: '請選擇日期' }]}
        >
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="time"
          label="加班時間"
          rules={[{ required: true, message: '請選擇時間' }]}
        >
          <TimePicker.RangePicker format="HH:mm" style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="overtimeType"
          label="加班類型"
          rules={[{ required: true, message: '請選擇類型' }]}
        >
          <Select placeholder="請選擇類型">
            <Select.Option value="NORMAL">通常加班</Select.Option>
            <Select.Option value="HOLIDAY">例假日加班</Select.Option>
            <Select.Option value="NATIONAL_HOLIDAY">國定假日加班</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="reason"
          label="原因"
          rules={[{ required: true, message: '請輸入加班原因' }]}
        >
          <Input.TextArea rows={4} placeholder="請輸入加班原因" />
        </Form.Item>
      </Form>
    </Modal>
  );
};
