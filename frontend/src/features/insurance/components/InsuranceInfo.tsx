import React from 'react';
import { Card, Descriptions, Table, Alert, Spin } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type {
  MyInsuranceInfoViewModel,
  EnrollmentViewModel,
  EnrollmentHistoryViewModel,
} from '../model/InsuranceViewModel';

export interface InsuranceInfoProps {
  insuranceInfo: MyInsuranceInfoViewModel | null;
  loading?: boolean;
}

/**
 * 保險資訊顯示組件
 */
export const InsuranceInfo: React.FC<InsuranceInfoProps> = ({ insuranceInfo, loading }) => {
  if (loading) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (!insuranceInfo) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
          查無保險資訊
        </div>
      </Card>
    );
  }

  // Enrollment columns
  const enrollmentColumns: ColumnsType<EnrollmentViewModel> = [
    {
      title: '保險類型',
      dataIndex: 'insuranceTypeLabel',
      key: 'insuranceType',
      width: 100,
    },
    {
      title: '加保日期',
      dataIndex: 'enrollDateDisplay',
      key: 'enrollDate',
      width: 120,
    },
    {
      title: '投保薪資',
      dataIndex: 'monthlySalaryDisplay',
      key: 'monthlySalary',
      width: 120,
      align: 'right',
    },
    {
      title: '投保級距',
      dataIndex: 'levelDisplay',
      key: 'level',
      width: 100,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      width: 100,
    },
  ];

  // History columns
  const historyColumns: ColumnsType<EnrollmentHistoryViewModel> = [
    {
      title: '異動日期',
      dataIndex: 'changeDateDisplay',
      key: 'changeDate',
      width: 120,
    },
    {
      title: '異動類型',
      dataIndex: 'changeTypeLabel',
      key: 'changeType',
      width: 100,
    },
    {
      title: '保險類型',
      dataIndex: 'insuranceTypeLabel',
      key: 'insuranceType',
      width: 100,
    },
    {
      title: '投保薪資',
      dataIndex: 'monthlySalaryDisplay',
      key: 'monthlySalary',
      width: 120,
      align: 'right',
    },
    {
      title: '投保級距',
      dataIndex: 'levelDisplay',
      key: 'level',
      width: 100,
    },
    {
      title: '調整原因',
      dataIndex: 'reason',
      key: 'reason',
      ellipsis: true,
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      {/* 目前投保狀態 */}
      <Card title="目前投保狀態" size="small">
        <Alert
          message={insuranceInfo.statusMessage}
          type={insuranceInfo.statusType}
          showIcon
          style={{ marginBottom: 16 }}
        />
        <Descriptions column={2} bordered size="small">
          <Descriptions.Item label="投保單位">{insuranceInfo.unitName}</Descriptions.Item>
          <Descriptions.Item label="員工編號">{insuranceInfo.employeeCode}</Descriptions.Item>
          <Descriptions.Item label="員工姓名">{insuranceInfo.employeeName}</Descriptions.Item>
          {insuranceInfo.currentEnrollDate && (
            <Descriptions.Item label="加保日期">{insuranceInfo.currentEnrollDate}</Descriptions.Item>
          )}
          {insuranceInfo.currentSalaryDisplay && (
            <Descriptions.Item label="投保薪資">{insuranceInfo.currentSalaryDisplay}</Descriptions.Item>
          )}
          {insuranceInfo.currentLevelDisplay && (
            <Descriptions.Item label="投保級距">{insuranceInfo.currentLevelDisplay}</Descriptions.Item>
          )}
        </Descriptions>

        {/* 目前投保明細 */}
        {insuranceInfo.enrollments.length > 0 && (
          <div style={{ marginTop: 16 }}>
            <h4 style={{ marginBottom: 8 }}>投保明細</h4>
            <Table
              dataSource={insuranceInfo.enrollments}
              columns={enrollmentColumns}
              rowKey="enrollmentId"
              pagination={false}
              size="small"
            />
          </div>
        )}
      </Card>

      {/* 每月保費明細 */}
      <Card title="每月保費明細" size="small">
        <Table
          dataSource={[
            {
              key: 'labor',
              item: '勞保費',
              employee: insuranceInfo.fees.laborEmployeeDisplay,
              employer: insuranceInfo.fees.laborEmployerDisplay,
            },
            {
              key: 'health',
              item: '健保費',
              employee: insuranceInfo.fees.healthEmployeeDisplay,
              employer: insuranceInfo.fees.healthEmployerDisplay,
            },
            {
              key: 'pension',
              item: '勞退',
              employee: '-',
              employer: insuranceInfo.fees.pensionEmployerDisplay,
            },
          ]}
          columns={[
            {
              title: '項目',
              dataIndex: 'item',
              key: 'item',
            },
            {
              title: '個人負擔',
              dataIndex: 'employee',
              key: 'employee',
              align: 'right',
            },
            {
              title: '公司負擔',
              dataIndex: 'employer',
              key: 'employer',
              align: 'right',
            },
          ]}
          pagination={false}
          size="small"
          summary={() => (
            <Table.Summary fixed>
              <Table.Summary.Row>
                <Table.Summary.Cell index={0}>
                  <strong>合計</strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={1} align="right">
                  <strong style={{ color: '#ff4d4f' }}>
                    {insuranceInfo.fees.totalEmployeeDisplay}
                  </strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={2} align="right">
                  <strong style={{ color: '#52c41a' }}>
                    {insuranceInfo.fees.totalEmployerDisplay}
                  </strong>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            </Table.Summary>
          )}
        />
      </Card>

      {/* 投保歷程 */}
      <Card title="投保歷程" size="small">
        <Table
          dataSource={insuranceInfo.history}
          columns={historyColumns}
          rowKey="historyId"
          pagination={insuranceInfo.history.length > 10 ? { pageSize: 10 } : false}
          size="small"
          locale={{ emptyText: '查無投保歷程' }}
        />
      </Card>
    </div>
  );
};
