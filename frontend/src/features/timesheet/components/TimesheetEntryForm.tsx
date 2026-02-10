import { DatePicker, Form, Input, InputNumber, message, Modal, Select } from 'antd';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';
import { ProjectApi } from '../../project/api/ProjectApi';
import type { ProjectDto, WbsItemDto } from '../api/TimesheetTypes';
import type { TimesheetEntryViewModel } from '../model/TimesheetViewModel';

interface TimesheetEntryFormProps {
  visible: boolean;
  initialValues?: Partial<TimesheetEntryViewModel>;
  onCancel: () => void;
  onSave: (values: any) => Promise<void>;
}

/**
 * HR07-M01: 工時填報對話框
 */
export const TimesheetEntryForm: React.FC<TimesheetEntryFormProps> = ({
  visible,
  initialValues,
  onCancel,
  onSave,
}) => {
  const [form] = Form.useForm();
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [wbsItems, setWbsItems] = useState<WbsItemDto[]>([]);
  const [loadingProjects, setLoadingProjects] = useState(false);
  const [loadingWbs, setLoadingWbs] = useState(false);

  useEffect(() => {
    if (visible) {
      fetchProjects();
      if (initialValues) {
        form.setFieldsValue({
          ...initialValues,
          workDate: dayjs(initialValues.workDate),
        });
        if (initialValues.projectName) {
          // 如果是編輯，嘗試載入該專案的 WBS
          // 這裡簡化處理，實際可能需要專案 ID
        }
      } else {
        form.resetFields();
      }
    }
  }, [visible, initialValues, form]);

  const fetchProjects = async () => {
    setLoadingProjects(true);
    try {
      const response = await ProjectApi.getProjectList({ status: 'IN_PROGRESS' });
      setProjects(response.projects.map(p => ({
        id: p.id,
        code: p.project_code,
        name: p.project_name
      })));
    } catch (err) {
      message.error('無法取得專案列表');
    } finally {
      setLoadingProjects(false);
    }
  };

  const handleProjectChange = async (projectId: string) => {
    setLoadingWbs(true);
    form.setFieldValue('wbsCode', undefined);
    try {
      const tasks = await ProjectApi.getProjectTasks(projectId);
      // 轉換任務為 WBS Item
      const items: WbsItemDto[] = [];
      const flattenTasks = (list: any[]) => {
        list.forEach(t => {
          items.push({ id: t.id, code: t.task_code, name: t.task_name, level: t.level });
          if (t.children) flattenTasks(t.children);
        });
      };
      flattenTasks(tasks);
      setWbsItems(items);
    } catch (err) {
      message.error('無法取得 WBS 工項');
    } finally {
      setLoadingWbs(false);
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      await onSave({
        ...values,
        workDate: values.workDate.format('YYYY-MM-DD'),
      });
      onCancel();
    } catch (err) {
      // 驗證失敗
    }
  };

  return (
    <Modal
      title={initialValues?.id ? '編輯工時' : '新增工時'}
      open={visible}
      onOk={handleOk}
      onCancel={onCancel}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="workDate"
          label="工作日期"
          rules={[{ required: true, message: '請選擇工作日期' }]}
        >
          <DatePicker style={{ width: '100%' }} disabledDate={d => d.isAfter(dayjs())} />
        </Form.Item>

        <Form.Item
          name="projectId"
          label="參與專案"
          rules={[{ required: true, message: '請選擇專案' }]}
        >
          <Select
            placeholder="請選擇專案"
            loading={loadingProjects}
            onChange={handleProjectChange}
            options={projects.map(p => ({ label: `${p.code} ${p.name}`, value: p.id }))}
          />
        </Form.Item>

        <Form.Item
          name="wbsCode"
          label="WBS 工項"
        >
          <Select
            placeholder="請選擇工項"
            loading={loadingWbs}
            allowClear
            options={wbsItems.map(w => ({ 
              label: `${w.code} ${w.name}`, 
              value: w.code,
              style: { paddingLeft: (w.level - 1) * 16 } 
            }))}
          />
        </Form.Item>

        <Form.Item
          name="hours"
          label="投入工時 (小時)"
          rules={[
            { required: true, message: '請輸入工時' },
            { type: 'number', min: 0.5, max: 24, message: '工時必須在 0.5 到 24 之間' }
          ]}
        >
          <InputNumber style={{ width: '100%' }} step={0.5} precision={1} />
        </Form.Item>

        <Form.Item
          name="description"
          label="工作說明"
          rules={[{ required: true, message: '請輸入工作內容說明' }]}
        >
          <Input.TextArea rows={3} placeholder="請簡述您的工作內容..." />
        </Form.Item>
      </Form>
    </Modal>
  );
};
