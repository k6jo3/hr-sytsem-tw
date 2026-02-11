import {
    EyeOutlined,
    FilePdfOutlined,
    ReloadOutlined,
    SearchOutlined
} from '@ant-design/icons';
import { Button, Card, Col, DatePicker, Form, Input, message, Row, Select, Space, Table, Tag, Tooltip, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PayrollApi } from '../features/payroll/api/PayrollApi';
import type { GetPayslipListRequest } from '../features/payroll/api/PayrollTypes';
import { PayslipViewModelFactory } from '../features/payroll/factory/PayslipViewModelFactory';
import type { PayslipDetailViewModel } from '../features/payroll/model/PayrollViewModel';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR04PayrollHistoryPage - 員工薪資查詢頁面
 * 頁面代碼：HR04-P07
 * 目的：供管理者跨批次查詢所有員工的薪資記錄
 */
export const HR04PayrollHistoryPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PayslipDetailViewModel[]>([]);
  const [total, setTotal] = useState(0);
  const [form] = Form.useForm();
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const fetchData = async (page = 1) => {
    setLoading(true);
    try {
      const values = form.getFieldsValue();
      const params: GetPayslipListRequest = {
        employeeId: values.employeeId,
        yearMonth: values.month ? values.month.format('YYYY-MM') : undefined,
        status: values.status,
        page,
        page_size: pageSize
      };
      
      const res = await PayrollApi.getPayslips(params);
      const viewModels = (res.items || []).map(dto => PayslipViewModelFactory.createDetailFromDTO(dto));
      setData(viewModels);
      setTotal(res.total || 0);
      setCurrentPage(page);
    } catch (error) {
      message.error('查詢薪資記錄失敗');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = () => {
    fetchData(1);
  };

  const handleReset = () => {
    form.resetFields();
    fetchData(1);
  };

  const handleDownloadPdf = async (payslipId: string) => {
    try {
      const blob = await PayrollApi.downloadPayslipPdf(payslipId);
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `payslip_${payslipId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      message.success('開始下載薪資單 PDF');
    } catch (error) {
      message.error('下載失敗');
    }
  };

  const columns = [
    { 
      title: '員工資訊', 
      key: 'employee',
      render: (_: any, record: PayslipDetailViewModel) => (
        <Space direction="vertical" size={0}>
          <Text strong>{record.employeeName}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>{record.employeeCode}</Text>
        </Space>
      )
    },
    { title: '部門', dataIndex: 'departmentName', key: 'departmentName' },
    { 
      title: '計薪期間', 
      dataIndex: 'payPeriodDisplay',
      key: 'payPeriodDisplay',
    },
    { title: '發薪日', dataIndex: 'paymentDateDisplay', key: 'paymentDateDisplay' },
    { 
      title: '應發薪資', 
      dataIndex: 'grossPayDisplay', 
      key: 'grossPayDisplay',
    },
    { 
      title: '實發薪資', 
      dataIndex: 'netPayDisplay', 
      key: 'netPayDisplay',
      render: (val: string) => <Text strong type="success">{val}</Text>
    },
    { 
      title: '狀態', 
      key: 'status',
      render: (_: any, record: PayslipDetailViewModel) => (
        <Tag color={record.statusColor}>{record.statusLabel}</Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: PayslipDetailViewModel) => (
        <Space>
          <Tooltip title="查看詳情">
            <Button 
              icon={<EyeOutlined />} 
              size="small" 
              onClick={() => navigate(`/admin/payroll/runs/${record.id}`)} 
              disabled 
            />
          </Tooltip>
          <Tooltip title="下載 PDF">
            <Button 
              icon={<FilePdfOutlined />} 
              size="small"
              onClick={() => handleDownloadPdf(record.id)}
            />
          </Tooltip>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col><Title level={2}>員工薪資歷史查詢</Title></Col>
          <Col>
            <Button icon={<ReloadOutlined />} onClick={() => fetchData(currentPage)}>重新整理</Button>
          </Col>
        </Row>

        <Card>
          <Form form={form} layout="inline">
            <Form.Item name="employeeId" label="員工編號/姓名">
              <Input placeholder="請輸入關鍵字" allowClear onPressEnter={handleSearch} />
            </Form.Item>
            <Form.Item name="month" label="計薪月份">
              <DatePicker picker="month" placeholder="選擇月份" />
            </Form.Item>
            <Form.Item name="status" label="狀態">
              <Select placeholder="項目狀態" style={{ width: 120 }} allowClear>
                <Option value="PAID">已發放</Option>
                <Option value="APPROVED">已核准</Option>
                <Option value="CALCULATED">已計算</Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜尋</Button>
                <Button onClick={handleReset}>重置</Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>

        <Card>
          <Table 
            columns={columns} 
            dataSource={data} 
            rowKey="id" 
            loading={loading}
            pagination={{
              current: currentPage,
              pageSize,
              total,
              onChange: (page) => fetchData(page),
              showTotal: (total) => `共 ${total} 筆記錄`
            }}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR04PayrollHistoryPage;
