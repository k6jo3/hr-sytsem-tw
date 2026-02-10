import { RootState } from '@/store';
import { PageLayout } from '@shared/components';
import { Button, Card, message, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import React, { useCallback, useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import type { OvertimeApplicationDto, OvertimeStatus } from '../features/attendance/api/AttendanceTypes';
import { OvertimeApi } from '../features/attendance/api/OvertimeApi';

const { Title } = Typography;

import { ApplyOvertimeModal } from '../features/attendance/components/ApplyOvertimeModal';

/**
 * HR03 加班申請列表頁面
 * 頁面代碼：HR03-P05 (暫定)
 */
export const HR03OvertimeListPage: React.FC = () => {
  const [data, setData] = useState<OvertimeApplicationDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [modalVisible, setModalVisible] = useState(false);
  const pageSize = 10;
  
  const user = useSelector((state: RootState) => state.auth.user);

  const fetchOvertimes = useCallback(async (currentPage: number) => {
    if (!user?.employeeId) return;
    
    setLoading(true);
    try {
      const response = await OvertimeApi.getOvertimeApplications({
        employeeId: user.employeeId,
        page: currentPage,
        pageSize: pageSize,
      });
      setData(response.items);
      setTotal(response.totalElements);
    } catch (err) {
      message.error('無法取得加班記錄');
    } finally {
      setLoading(false);
    }
  }, [user?.employeeId]);

  useEffect(() => {
    fetchOvertimes(page);
  }, [fetchOvertimes, page]);

  const handleApplySuccess = () => {
    setModalVisible(false);
    fetchOvertimes(1);
    setPage(1);
  };

  const getStatusTag = (status: OvertimeStatus) => {
    const statusConfig: Record<OvertimeStatus, { color: string; label: string }> = {
      PENDING: { color: 'blue', label: '待審核' },
      APPROVED: { color: 'green', label: '已通過' },
      REJECTED: { color: 'red', label: '已拒絕' },
      CANCELLED: { color: 'default', label: '已取消' },
    };
    const config = statusConfig[status] || { color: 'default', label: status };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<OvertimeApplicationDto> = [
    {
      title: '加班日期',
      dataIndex: 'overtimeDate',
      key: 'overtimeDate',
      render: (date) => dayjs(date).format('YYYY-MM-DD'),
    },
    {
      title: '時數',
      dataIndex: 'overtimeHours',
      key: 'overtimeHours',
      render: (hours) => `${hours} 小時`,
    },
    {
      title: '類型',
      dataIndex: 'overtimeType',
      key: 'overtimeType',
    },
    {
      title: '狀態',
      dataIndex: 'status',
      key: 'status',
      render: (status: OvertimeStatus) => getStatusTag(status),
    },
    {
      title: '申請時間',
      dataIndex: 'appliedAt',
      key: 'appliedAt',
      render: (date) => dayjs(date).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '原因',
      dataIndex: 'reason',
      key: 'reason',
      ellipsis: true,
    },
  ];

  return (
    <PageLayout>
      <div style={{ padding: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <Title level={3}>我的加班紀錄</Title>
          <Button type="primary" onClick={() => setModalVisible(true)}>申請加班</Button>
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

        <ApplyOvertimeModal
          visible={modalVisible}
          onCancel={() => setModalVisible(false)}
          onSuccess={handleApplySuccess}
        />
      </div>
    </PageLayout>
  );
};

export default HR03OvertimeListPage;
