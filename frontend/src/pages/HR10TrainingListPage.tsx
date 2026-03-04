import React, { useEffect, useState } from 'react';
import { Card, Table, Tabs, Tag, Typography, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { useTraining } from '@features/training/hooks/useTraining';
import type { CertificateViewModel, CourseViewModel, EnrollmentViewModel } from '@features/training/model/TrainingViewModel';

const { Title } = Typography;

const courseColumns: ColumnsType<CourseViewModel> = [
  { title: '課程代碼', dataIndex: 'courseCode', key: 'courseCode', width: 120 },
  { title: '課程名稱', dataIndex: 'courseName', key: 'courseName', ellipsis: true },
  { title: '類別', dataIndex: 'categoryLabel', key: 'category', width: 100 },
  { title: '類型', dataIndex: 'typeLabel', key: 'type', width: 80 },
  { title: '授課方式', dataIndex: 'modeLabel', key: 'mode', width: 100 },
  { title: '講師', dataIndex: 'instructor', key: 'instructor', width: 120 },
  { title: '時數', dataIndex: 'durationHours', key: 'hours', width: 80, render: (v: number) => `${v}h` },
  { title: '日期', key: 'date', width: 180, render: (_: unknown, r: CourseViewModel) => `${r.startDate} ~ ${r.endDate}` },
  { title: '名額', key: 'spots', width: 100, render: (_: unknown, r: CourseViewModel) => r.maxParticipants ? `${r.currentEnrollments}/${r.maxParticipants}` : `${r.currentEnrollments}` },
  {
    title: '狀態', dataIndex: 'statusLabel', key: 'status', width: 100,
    render: (label: string, r: CourseViewModel) => <Tag color={r.statusColor}>{label}</Tag>,
  },
];

const enrollmentColumns: ColumnsType<EnrollmentViewModel> = [
  { title: '課程', dataIndex: 'courseName', key: 'courseName', ellipsis: true },
  {
    title: '狀態', dataIndex: 'statusLabel', key: 'status', width: 100,
    render: (label: string, r: EnrollmentViewModel) => <Tag color={r.statusColor}>{label}</Tag>,
  },
  { title: '報名日期', dataIndex: 'createdAt', key: 'createdAt', width: 120, render: (v: string) => v?.substring(0, 10) },
  { title: '完成日期', dataIndex: 'completedAt', key: 'completedAt', width: 120, render: (v: string) => v?.substring(0, 10) || '-' },
  { title: '出席時數', dataIndex: 'attendedHours', key: 'attendedHours', width: 100, render: (v: number) => v ? `${v}h` : '-' },
  { title: '成績', dataIndex: 'score', key: 'score', width: 80, render: (v: number | null) => v != null ? v : '-' },
  { title: '通過', dataIndex: 'passed', key: 'passed', width: 80, render: (v: boolean | null) => v == null ? '-' : v ? '是' : '否' },
];

const certificateColumns: ColumnsType<CertificateViewModel> = [
  { title: '證照名稱', dataIndex: 'certificateName', key: 'name', ellipsis: true },
  { title: '發證機構', dataIndex: 'issuingOrganization', key: 'org', width: 180 },
  { title: '類別', dataIndex: 'categoryLabel', key: 'category', width: 100 },
  { title: '發證日期', dataIndex: 'issueDate', key: 'issueDate', width: 120 },
  { title: '到期日期', dataIndex: 'expiryDate', key: 'expiryDate', width: 120, render: (v: string) => v || '永久' },
  {
    title: '剩餘天數', dataIndex: 'daysUntilExpiry', key: 'days', width: 100,
    render: (v: number | null) => v == null ? '-' : v > 0 ? `${v} 天` : '已過期',
  },
  {
    title: '狀態', dataIndex: 'statusLabel', key: 'status', width: 100,
    render: (label: string, r: CertificateViewModel) => <Tag color={r.statusColor}>{label}</Tag>,
  },
];

export const HR10TrainingListPage: React.FC = () => {
  const { courses, myTrainings, certificates, loading, error, fetchCourses, fetchMyTrainings, fetchCertificates } = useTraining();
  const [activeTab, setActiveTab] = useState('courses');

  useEffect(() => {
    fetchCourses();
  }, [fetchCourses]);

  const handleTabChange = (key: string) => {
    setActiveTab(key);
    if (key === 'courses' && courses.length === 0) fetchCourses();
    if (key === 'myTrainings' && myTrainings.length === 0) fetchMyTrainings();
    if (key === 'certificates' && certificates.length === 0) fetchCertificates();
  };

  useEffect(() => {
    if (error) {
      message.error(error.message);
    }
  }, [error]);

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>訓練管理</Title>
      <Card>
        <Tabs activeKey={activeTab} onChange={handleTabChange} items={[
          {
            key: 'courses',
            label: '課程列表',
            children: (
              <Table<CourseViewModel>
                columns={courseColumns}
                dataSource={courses}
                rowKey="id"
                loading={loading && activeTab === 'courses'}
                pagination={{ pageSize: 10 }}
                scroll={{ x: 1200 }}
              />
            ),
          },
          {
            key: 'myTrainings',
            label: '我的訓練',
            children: (
              <Table<EnrollmentViewModel>
                columns={enrollmentColumns}
                dataSource={myTrainings}
                rowKey="id"
                loading={loading && activeTab === 'myTrainings'}
                pagination={{ pageSize: 10 }}
              />
            ),
          },
          {
            key: 'certificates',
            label: '證照管理',
            children: (
              <Table<CertificateViewModel>
                columns={certificateColumns}
                dataSource={certificates}
                rowKey="id"
                loading={loading && activeTab === 'certificates'}
                pagination={{ pageSize: 10 }}
                scroll={{ x: 1000 }}
              />
            ),
          },
        ]} />
      </Card>
    </div>
  );
};
