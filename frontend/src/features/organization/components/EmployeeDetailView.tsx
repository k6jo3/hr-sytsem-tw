/**
 * Employee Detail View Component
 * Domain Code: HR02
 * Page Code: HR02-P04 (Partial)
 */

import { BankOutlined, IdcardOutlined, MailOutlined, PhoneOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Badge, Card, Descriptions, Tabs, Tag } from 'antd';
import dayjs from 'dayjs';
import React from 'react';
import { EmployeeDto } from '../api/OrganizationTypes';

interface EmployeeDetailViewProps {
  employee: EmployeeDto;
  loading?: boolean;
}

const STATUS_MAP: Record<string, { color: string; label: string }> = {
  ACTIVE: { color: 'green', label: '在職' },
  PROBATION: { color: 'blue', label: '試用期' },
  INACTIVE: { color: 'default', label: '非使用中' },
  ON_LEAVE: { color: 'orange', label: '留職停薪' },
  TERMINATED: { color: 'red', label: '已離職' },
};

export const EmployeeDetailView: React.FC<EmployeeDetailViewProps> = ({ employee, loading = false }) => {
  const statusInfo = STATUS_MAP[employee.status] || { color: 'default', label: employee.status };

  return (
    <Card loading={loading} bordered={false}>
      <div style={{ display: 'flex', marginBottom: 24, alignItems: 'center' }}>
        <Avatar size={80} icon={<UserOutlined />} style={{ backgroundColor: '#87d068', marginRight: 24 }} />
        <div>
          <h2 style={{ marginBottom: 4 }}>
            {employee.full_name}
            <Tag color={statusInfo.color} style={{ marginLeft: 12, verticalAlign: 'middle' }}>
              {statusInfo.label}
            </Tag>
          </h2>
          <div style={{ color: '#666' }}>
            <BankOutlined style={{ marginRight: 6 }} />
            {employee.department_name} | {employee.position}
          </div>
          <div style={{ marginTop: 8 }}>
            <Tag icon={<IdcardOutlined />} color="blue">{employee.employee_number}</Tag>
          </div>
        </div>
      </div>

      <Tabs
        defaultActiveKey="basic"
        items={[
          {
            key: 'basic',
            label: '基本資料',
            children: (
              <Descriptions bordered column={2}>
                <Descriptions.Item label="員工編號">{employee.employee_number}</Descriptions.Item>
                <Descriptions.Item label="入職日期">
                  {dayjs(employee.hire_date).format('YYYY-MM-DD')}
                </Descriptions.Item>
                <Descriptions.Item label="Email">
                  <a href={`mailto:${employee.email}`}>
                    <MailOutlined style={{ marginRight: 6 }} />
                    {employee.email}
                  </a>
                </Descriptions.Item>
                <Descriptions.Item label="聯絡電話">
                  {employee.phone ? (
                    <>
                       <PhoneOutlined style={{ marginRight: 6 }} />
                       {employee.phone}
                    </>
                  ) : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="所屬部門">{employee.department_name}</Descriptions.Item>
                <Descriptions.Item label="職稱">{employee.position}</Descriptions.Item>
                <Descriptions.Item label="狀態">
                  <Badge status={employee.status === 'ACTIVE' ? 'success' : 'default'} text={statusInfo.label} />
                </Descriptions.Item>
                {employee.termination_date && (
                  <Descriptions.Item label="離職日期">
                    {dayjs(employee.termination_date).format('YYYY-MM-DD')}
                  </Descriptions.Item>
                )}
                <Descriptions.Item label="建立時間">
                  {employee.created_at ? dayjs(employee.created_at).format('YYYY-MM-DD HH:mm') : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="更新時間">
                  {employee.updated_at ? dayjs(employee.updated_at).format('YYYY-MM-DD HH:mm') : '-'}
                </Descriptions.Item>
              </Descriptions>
            )
          },
          {
            key: 'history',
            label: '人事歷程 (TBD)',
            children: <div style={{ padding: 20, textAlign: 'center', color: '#999' }}>異動紀錄功能開發中...</div>
          }
        ]}
      />
    </Card>
  );
};
