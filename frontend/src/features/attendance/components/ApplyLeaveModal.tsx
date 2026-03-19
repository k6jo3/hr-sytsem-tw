import { RootState } from '@/store';
import { DatePicker, Form, Input, message, Modal, Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import type { ApplyLeaveRequest, LeaveTypeDto } from '../api/AttendanceTypes';
import { LeaveApi } from '../api/LeaveApi';

interface ApplyLeaveModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
}

export const ApplyLeaveModal: React.FC<ApplyLeaveModalProps> = ({
  open,
  onCancel,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [leaveTypes, setLeaveTypes] = useState<LeaveTypeDto[]>([]);
  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    if (open) {
      const fetchLeaveTypes = async () => {
        try {
          const types = await LeaveApi.getLeaveTypes();
          setLeaveTypes(types);
        } catch (err) {
          message.error('無法載入假別列表');
        }
      };
      fetchLeaveTypes();
    } else {
      form.resetFields();
    }
  }, [open, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (!user?.employeeId) {
        message.error('員工資訊不完整');
        return;
      }

      setLoading(true);
      const request: ApplyLeaveRequest = {
        employeeId: user.employeeId,
        leaveTypeCode: values.leaveTypeCode, // 保留以供前端使用
        startDate: values.range[0].format('YYYY-MM-DD'),
        endDate: values.range[1].format('YYYY-MM-DD'),
        reason: values.reason,
      };

      await LeaveApi.applyLeave(request);
      message.success('請假申請已提交');
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
      title="申請請假"
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="leaveTypeCode"
          label="假別"
          rules={[{ required: true, message: '請選擇假別' }]}
        >
          <Select placeholder="請選擇假別">
            {leaveTypes.map((type) => (
              <Select.Option key={type.leaveTypeId} value={type.leaveTypeId}>
                {type.leaveTypeName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="range"
          label="請假期間"
          rules={[{ required: true, message: '請選擇日期範圍' }]}
        >
          <DatePicker.RangePicker style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="reason"
          label="原因"
          rules={[{ required: true, message: '請輸入請假原因' }]}
        >
          <Input.TextArea rows={4} placeholder="請輸入請假原因" />
        </Form.Item>
      </Form>
    </Modal>
  );
};
