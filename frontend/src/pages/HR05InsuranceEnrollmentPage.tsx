import {
    EditOutlined,
    ExportOutlined,
    HistoryOutlined,
    PlusOutlined,
    ReloadOutlined,
    SearchOutlined
} from '@ant-design/icons';
import { Button, Card, Col, DatePicker, Input, message, Row, Select, Space, Table, Tag, Typography } from 'antd';
import React, { useEffect } from 'react';
import type { EnrollmentStatus, InsuranceType } from '../features/insurance/api/InsuranceTypes';
import { useInsurance } from '../features/insurance/hooks/useInsurance';
import type { EnrollmentViewModel } from '../features/insurance/model/InsuranceViewModel';

const { RangePicker } = DatePicker;
const { Option } = Select;
const { Title, Text } = Typography;

/**
 * HR05-P02: 員工加退保記錄頁面
 */
export const HR05InsuranceEnrollmentPage: React.FC = () => {
  const {
    enrollments,
    loading,
    error,
    total,
    page,
    pageSize,
    filters,
    handlePageChange,
    handleFilterChange,
    refresh
  } = useInsurance();

  // 顯示錯誤訊息
  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const columns = [
    {
      title: '員工姓名',
      dataIndex: 'employeeName',
      key: 'employeeName',
      width: 120,
      render: (text: string, record: EnrollmentViewModel) => (
        <Space direction="vertical" size={0}>
          <Text strong>{text}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>{record.enrollmentId.substring(0, 8)}</Text>
        </Space>
      )
    },
    {
      title: '保險類型',
      dataIndex: 'insuranceTypeLabel',
      key: 'insuranceType',
      width: 100,
      render: (text: string, record: EnrollmentViewModel) => (
        <Tag color={record.insuranceTypeColor}>{text}</Tag>
      )
    },
    {
      title: '加保日期',
      dataIndex: 'enrollDateDisplay',
      key: 'enrollDate',
      width: 110
    },
    {
      title: '退保日期',
      dataIndex: 'withdrawDateDisplay',
      key: 'withdrawDate',
      width: 110,
      render: (text: string) => text || '-'
    },
    {
      title: '投保薪資',
      dataIndex: 'monthlySalaryDisplay',
      key: 'monthlySalary',
      width: 110,
      align: 'right' as const
    },
    {
      title: '級距',
      dataIndex: 'levelDisplay',
      key: 'level',
      width: 90
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      width: 100,
      render: (text: string, record: EnrollmentViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_: any, record: EnrollmentViewModel) => (
        <Space size="middle">
          <Button 
            type="link" 
            size="small" 
            icon={<EditOutlined />}
            disabled={record.isWithdrawn}
          >
            調整
          </Button>
          <Button 
            type="link" 
            size="small" 
            danger 
            disabled={record.isWithdrawn}
          >
            退保
          </Button>
          <Button 
            type="link" 
            size="small" 
            icon={<HistoryOutlined />}
          >
            歷程
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Title level={2}>加退保記錄管理</Title>
            <Text type="secondary">管理全體員工的勞保、健保、勞退投保資料與級距異動。</Text>
          </Col>
          <Col>
            <Space>
              <Button icon={<PlusOutlined />} type="primary">手動加保</Button>
              <Button icon={<ExportOutlined />}>匯出申報檔</Button>
              <Button icon={<ReloadOutlined />} onClick={() => refresh()}>重新整理</Button>
            </Space>
          </Col>
        </Row>

        <Card size="small">
          <Row gutter={16}>
            <Col span={6}>
              <Input 
                placeholder="員工姓名/編號" 
                prefix={<SearchOutlined />} 
                onPressEnter={(e) => handleFilterChange({ ...filters, employee_id: e.currentTarget.value })}
              />
            </Col>
            <Col span={4}>
              <Select 
                placeholder="保險類型" 
                style={{ width: '100%' }} 
                allowClear
                onChange={(val) => handleFilterChange({ ...filters, insurance_type: val as InsuranceType })}
              >
                <Option value="LABOR">勞保</Option>
                <Option value="HEALTH">健保</Option>
                <Option value="PENSION">勞退</Option>
              </Select>
            </Col>
            <Col span={4}>
              <Select 
                placeholder="投保狀態" 
                style={{ width: '100%' }} 
                allowClear
                onChange={(val) => handleFilterChange({ ...filters, status: val as EnrollmentStatus })}
              >
                <Option value="ACTIVE">已加保</Option>
                <Option value="PENDING">待處理</Option>
                <Option value="WITHDRAWN">已退保</Option>
              </Select>
            </Col>
            <Col span={6}>
              <RangePicker 
                placeholder={['開始日期', '結束日期']} 
                style={{ width: '100%' }}
                onChange={(_, dateStrings) => {
                  handleFilterChange({
                    ...filters,
                    start_date: dateStrings[0],
                    end_date: dateStrings[1]
                  });
                }}
              />
            </Col>
            <Col span={4}>
              <Button type="primary" onClick={() => refresh()}>搜尋</Button>
            </Col>
          </Row>
        </Card>

        <Card>
          <Table 
            columns={columns} 
            dataSource={enrollments} 
            rowKey="enrollmentId" 
            loading={loading}
            pagination={{
              current: page,
              pageSize: pageSize,
              total: total,
              onChange: handlePageChange,
              showTotal: (total) => `共 ${total} 筆記錄`
            }}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR05InsuranceEnrollmentPage;
