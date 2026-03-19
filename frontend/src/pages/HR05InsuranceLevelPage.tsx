import React, { useState, useEffect, useCallback } from 'react';
import {
  Card, Table, Button, Modal, Form, InputNumber, DatePicker,
  Select, message, Tag, Space, Typography, Divider, Alert, Checkbox
} from 'antd';
import { SettingOutlined, ReloadOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { InsuranceApi } from '../features/insurance/api/InsuranceApi';
import type { InsuranceLevelDto, InsuranceType } from '../features/insurance/api/InsuranceTypes';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR05 投保級距管理頁面
 * Feature: insurance
 * 提供查詢級距表與批量調整級距功能
 */
const HR05InsuranceLevelPage: React.FC = () => {
  const [levels, setLevels] = useState<InsuranceLevelDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterType, setFilterType] = useState<InsuranceType>('LABOR');
  const [adjustModalOpen, setAdjustModalOpen] = useState(false);
  const [adjusting, setAdjusting] = useState(false);
  const [form] = Form.useForm();

  const fetchLevels = useCallback(async () => {
    setLoading(true);
    try {
      const res = await InsuranceApi.getLevels({ insurance_type: filterType });
      // 只顯示沒有 end_date 的有效級距
      const activeLevels = res.levels.filter(l => !l.end_date);
      setLevels(activeLevels.sort((a, b) => a.level_number - b.level_number));
    } catch (e: any) {
      message.error('查詢級距失敗: ' + (e.message || ''));
    } finally {
      setLoading(false);
    }
  }, [filterType]);

  useEffect(() => { fetchLevels(); }, [fetchLevels]);

  const handleAdjust = async (values: any) => {
    setAdjusting(true);
    try {
      const types: InsuranceType[] = values.insuranceTypes;
      const res = await InsuranceApi.batchAdjustLevels({
        insurance_types: types,
        adjustment_amount: values.adjustmentAmount,
        effective_date: values.effectiveDate.format('YYYY-MM-DD'),
        new_highest_level_salary: values.addNewHighest ? values.newHighestSalary : undefined,
      });
      message.success(`${res.message} (停用 ${res.old_levels_deactivated} 筆，新增 ${res.new_levels_created} 筆)`);
      setAdjustModalOpen(false);
      form.resetFields();
      fetchLevels();
    } catch (e: any) {
      message.error('調整失敗: ' + (e.message || ''));
    } finally {
      setAdjusting(false);
    }
  };

  const columns = [
    { title: '級距', dataIndex: 'level_number', key: 'level_number', width: 80 },
    {
      title: '投保薪資', dataIndex: 'monthly_salary', key: 'monthly_salary',
      render: (v: number) => `$${v.toLocaleString()}`,
    },
    {
      title: '類型', dataIndex: 'insurance_type', key: 'insurance_type',
      render: (v: string) => {
        const colorMap: Record<string, string> = { LABOR: 'blue', HEALTH: 'green', PENSION: 'orange' };
        const nameMap: Record<string, string> = { LABOR: '勞保', HEALTH: '健保', PENSION: '勞退' };
        return <Tag color={colorMap[v]}>{nameMap[v] || v}</Tag>;
      }
    },
    {
      title: '員工費率', key: 'employee_rate',
      render: (_: any, r: InsuranceLevelDto) => {
        if (r.insurance_type === 'LABOR') return r.labor_employee_rate ? (r.labor_employee_rate * 100).toFixed(2) + '%' : '-';
        if (r.insurance_type === 'HEALTH') return r.health_employee_rate ? (r.health_employee_rate * 100).toFixed(3) + '%' : '-';
        return '-';
      }
    },
    {
      title: '雇主費率', key: 'employer_rate',
      render: (_: any, r: InsuranceLevelDto) => {
        if (r.insurance_type === 'LABOR') return r.labor_employer_rate ? (r.labor_employer_rate * 100).toFixed(2) + '%' : '-';
        if (r.insurance_type === 'HEALTH') return r.health_employer_rate ? (r.health_employer_rate * 100).toFixed(3) + '%' : '-';
        if (r.insurance_type === 'PENSION') return r.pension_employer_rate ? (r.pension_employer_rate * 100).toFixed(1) + '%' : '-';
        return '-';
      }
    },
    {
      title: '生效日', dataIndex: 'effective_date', key: 'effective_date',
      render: (v: string) => v ? dayjs(v).format('YYYY-MM-DD') : '-',
    },
  ];

  const [addNewHighest, setAddNewHighest] = useState(false);

  return (
    <div style={{ padding: 24 }}>
      <Title level={2}><SettingOutlined /> 投保級距管理</Title>
      <Text type="secondary">查詢與調整勞保、健保投保級距表</Text>

      <Card style={{ marginTop: 16 }}>
        <Space style={{ marginBottom: 16 }}>
          <Select value={filterType} onChange={setFilterType} style={{ width: 120 }}>
            <Option value="LABOR">勞保</Option>
            <Option value="HEALTH">健保</Option>
            <Option value="PENSION">勞退</Option>
          </Select>
          <Button icon={<ReloadOutlined />} onClick={fetchLevels}>重新整理</Button>
          <Button type="primary" icon={<SettingOutlined />} onClick={() => setAdjustModalOpen(true)}>
            批量調整級距
          </Button>
        </Space>

        <Alert
          message={`目前顯示 ${levels.length} 筆有效 ${filterType === 'LABOR' ? '勞保' : filterType === 'HEALTH' ? '健保' : '勞退'} 級距`}
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
        />

        <Table
          dataSource={levels}
          columns={columns}
          rowKey="level_id"
          loading={loading}
          pagination={false}
          size="small"
          scroll={{ x: 'max-content', y: 500 }}
        />
      </Card>

      <Modal
        title="批量調整投保級距"
        open={adjustModalOpen}
        onCancel={() => { setAdjustModalOpen(false); form.resetFields(); }}
        footer={null}
        width={520}
      >
        <Alert
          message="此操作將停用選定保險類型的所有現有級距，並產生調整後的新級距"
          type="warning"
          showIcon
          style={{ marginBottom: 16 }}
        />

        <Form form={form} layout="vertical" onFinish={handleAdjust}
          initialValues={{
            insuranceTypes: ['LABOR', 'HEALTH'],
            adjustmentAmount: -10000,
            effectiveDate: dayjs('2026-05-01'),
            newHighestSalary: 160000,
          }}
        >
          <Form.Item name="insuranceTypes" label="保險類型" rules={[{ required: true, message: '請選擇' }]}>
            <Select mode="multiple" placeholder="選擇要調整的保險類型">
              <Option value="LABOR">勞保</Option>
              <Option value="HEALTH">健保</Option>
              <Option value="PENSION">勞退</Option>
            </Select>
          </Form.Item>

          <Form.Item name="adjustmentAmount" label="每級調整金額" rules={[{ required: true }]}
            extra="負數表示下修（如 -10000 = 每級下修 1 萬元）"
          >
            <InputNumber style={{ width: '100%' }} step={1000}
              formatter={v => `$ ${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
            />
          </Form.Item>

          <Form.Item name="effectiveDate" label="新級距生效日" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Divider />

          <Form.Item>
            <Checkbox checked={addNewHighest} onChange={e => setAddNewHighest(e.target.checked)}>
              新增更高投保級距
            </Checkbox>
          </Form.Item>

          {addNewHighest && (
            <Form.Item name="newHighestSalary" label="新最高級距月薪" rules={[{ required: addNewHighest }]}>
              <InputNumber style={{ width: '100%' }} min={1}
                formatter={v => `$ ${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
              />
            </Form.Item>
          )}

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={adjusting}>
                確認調整
              </Button>
              <Button onClick={() => { setAdjustModalOpen(false); form.resetFields(); }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default HR05InsuranceLevelPage;
