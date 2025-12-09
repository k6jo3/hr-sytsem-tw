import React from 'react';
import { Card, Descriptions, Table, Tag, Button, Divider, Spin } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { PayslipDetailViewModel, PayrollItemViewModel } from '../model/PayrollViewModel';

export interface PayslipDetailProps {
  payslip: PayslipDetailViewModel | null;
  loading?: boolean;
  onDownload?: (id: string) => void;
}

/**
 * 薪資單詳情組件
 */
export const PayslipDetail: React.FC<PayslipDetailProps> = ({ payslip, loading, onDownload }) => {
  if (loading) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (!payslip) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
          請選擇薪資單查看詳情
        </div>
      </Card>
    );
  }

  const itemColumns: ColumnsType<PayrollItemViewModel> = [
    {
      title: '項目名稱',
      dataIndex: 'itemName',
      key: 'itemName',
    },
    {
      title: '金額',
      dataIndex: 'amountDisplay',
      key: 'amount',
      align: 'right',
      width: 150,
    },
    {
      title: '說明',
      dataIndex: 'description',
      key: 'description',
    },
  ];

  return (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>{payslip.payslipCode} - {payslip.payPeriod}</span>
          <Tag color={payslip.statusColor}>{payslip.statusLabel}</Tag>
        </div>
      }
      extra={
        payslip.canDownload && onDownload && (
          <Button icon={<DownloadOutlined />} onClick={() => onDownload(payslip.id)}>
            下載PDF薪資單
          </Button>
        )
      }
    >
      <Descriptions column={2} bordered>
        <Descriptions.Item label="員工姓名">{payslip.employeeName}</Descriptions.Item>
        <Descriptions.Item label="員工編號">{payslip.employeeCode}</Descriptions.Item>
        {payslip.departmentName && (
          <Descriptions.Item label="部門">{payslip.departmentName}</Descriptions.Item>
        )}
        <Descriptions.Item label="計薪期間">{payslip.payPeriodDisplay}</Descriptions.Item>
        <Descriptions.Item label="發薪日">{payslip.paymentDateDisplay}</Descriptions.Item>
        {payslip.workDays !== undefined && (
          <Descriptions.Item label="工作天數">{payslip.workDays} 天</Descriptions.Item>
        )}
        {payslip.overtimeHours !== undefined && (
          <Descriptions.Item label="加班時數">{payslip.overtimeHours} 小時</Descriptions.Item>
        )}
        {payslip.leaveDays !== undefined && (
          <Descriptions.Item label="請假天數">{payslip.leaveDays} 天</Descriptions.Item>
        )}
      </Descriptions>

      <Divider orientation="left">收入項目</Divider>
      <Table
        dataSource={payslip.incomeItems}
        columns={itemColumns}
        rowKey="itemCode"
        pagination={false}
        size="small"
        summary={() => (
          <Table.Summary fixed>
            <Table.Summary.Row>
              <Table.Summary.Cell index={0}>
                <strong>應發合計</strong>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={1} align="right">
                <strong style={{ color: '#52c41a', fontSize: 16 }}>
                  {payslip.grossPayDisplay}
                </strong>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={2} />
            </Table.Summary.Row>
          </Table.Summary>
        )}
      />

      <Divider orientation="left" style={{ marginTop: 24 }}>
        扣除項目
      </Divider>
      <Table
        dataSource={payslip.deductionItems}
        columns={itemColumns}
        rowKey="itemCode"
        pagination={false}
        size="small"
        summary={() => (
          <Table.Summary fixed>
            <Table.Summary.Row>
              <Table.Summary.Cell index={0}>
                <strong>扣除合計</strong>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={1} align="right">
                <strong style={{ color: '#ff4d4f', fontSize: 16 }}>
                  {payslip.totalDeductionsDisplay}
                </strong>
              </Table.Summary.Cell>
              <Table.Summary.Cell index={2} />
            </Table.Summary.Row>
          </Table.Summary>
        )}
      />

      <Divider />
      <div style={{ textAlign: 'right', fontSize: 20, fontWeight: 'bold' }}>
        實發薪資：
        <span style={{ color: '#1890ff', marginLeft: 16 }}>{payslip.netPayDisplay}</span>
      </div>
    </Card>
  );
};
