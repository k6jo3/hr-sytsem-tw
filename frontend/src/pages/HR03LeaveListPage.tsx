import { RootState } from '@/store';
import { Button, Card, message, Space, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import React, { useCallback, useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import type { LeaveApplicationDto, LeaveStatus } from '../features/attendance/api/AttendanceTypes';
import { LeaveApi } from '../features/attendance/api/LeaveApi';

const { Title } = Typography;

import { ApplyLeaveModal } from '../features/attendance/components/ApplyLeaveModal';

/**
 * HR03 請假申請列表頁面
 * 頁面代碼：HR03-P02
 */
export const HR03LeaveListPage: React.FC = () => {
  const [data, setData] = useState<LeaveApplicationDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [modalVisible, setModalVisible] = useState(false);
  const pageSize = 10;
  
  const user = useSelector((state: RootState) => state.auth.user);

  const fetchLeaves = useCallback(async (currentPage: number) => {
    if (!user?.employeeId) return;
    
    setLoading(true);
    try {
      const response = await LeaveApi.getLeaveApplications({
        employeeId: user.employeeId,
        page: currentPage,
        pageSize: pageSize,
      });
      setData(response.items);
      setTotal(response.totalElements);
    } catch (err) {
      message.error('無法取得請假記錄');
    } finally {
      setLoading(false);
    }
  }, [user?.employeeId]);

  useEffect(() => {
    fetchLeaves(page);
  }, [fetchLeaves, page]);

  const handleCancel = async (id: string) => {
    try {
      await LeaveApi.cancelLeave(id);
      message.success('已取消申請');
      fetchLeaves(page);
    } catch (err) {
      message.error('取消失敗');
    }
  };

  const handleApplySuccess = () => {
    setModalVisible(false);
    fetchLeaves(1);
    setPage(1);
  };

  const getStatusTag = (status: LeaveStatus) => {
    const statusConfig: Record<LeaveStatus, { color: string; label: string }> = {
      PENDING: { color: 'blue', label: '待審核' },
      APPROVED: { color: 'green', label: '已通過' },
      REJECTED: { color: 'red', label: '已拒絕' },
      CANCELLED: { color: 'default', label: '已取消' },
    };
    const config = statusConfig[status] || { color: 'default', label: status };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<LeaveApplicationDto> = [
    {
      title: '假別',
      dataIndex: 'leaveTypeName',
      key: 'leaveTypeName',
    },
    {
      title: '開始時間',
      dataIndex: 'startDate',
      key: 'startDate',
      render: (date) => dayjs(date).format('YYYY-MM-DD'),
    },
    {
      title: '結束時間',
      dataIndex: 'endDate',
      key: 'endDate',
      render: (date) => dayjs(date).format('YYYY-MM-DD'),
    },
    {
      title: '天數',
      dataIndex: 'leaveDays',
      key: 'leaveDays',
    },
    {
      title: '狀態',
      dataIndex: 'status',
      key: 'status',
      render: (status: LeaveStatus) => getStatusTag(status),
    },
    {
      title: '申請時間',
      dataIndex: 'appliedAt',
      key: 'appliedAt',
      render: (date) => dayjs(date).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'PENDING' && (
            <Button type="link" danger onClick={() => handleCancel(record.applicationId)}>
              取消
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ padding: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <Title level={3}>請假與加班申請列表</Title>
          <Button type="primary" onClick={() => setModalVisible(true)}>申請請假</Button>
        </div>

        <Card>
          <Table
            columns={columns}
            dataSource={data}
            rowKey="applicationId"
            loading={loading}
            pagination={{
              current: page,
              pageSize: pageSize,
              total: total,
              onChange: (p) => setPage(p),
            }}
          />
        </Card>

        <ApplyLeaveModal
          visible={modalVisible}
          onCancel={() => setModalVisible(false)}
          onSuccess={handleApplySuccess}
        />
      </div>
    </>
  );
};

export default HR03LeaveListPage;
