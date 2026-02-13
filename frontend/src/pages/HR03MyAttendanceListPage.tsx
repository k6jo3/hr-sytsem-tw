import { RootState } from '@/store';
import { Button, Card, DatePicker, message, Space, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import React, { useCallback, useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { AttendanceApi } from '../features/attendance/api/AttendanceApi';
import type { AttendanceRecordDto, AttendanceStatus } from '../features/attendance/api/AttendanceTypes';

const { Title } = Typography;
const { RangePicker } = DatePicker;

import { ApplyCorrectionModal } from '../features/attendance/components/ApplyCorrectionModal';

/**
 * HR03 我的出勤記錄頁面
 * 頁面代碼：HR03-P03
 */
export const HR03MyAttendanceListPage: React.FC = () => {
  const [data, setData] = useState<AttendanceRecordDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>([
    dayjs().startOf('month'),
    dayjs().endOf('month'),
  ]);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<AttendanceRecordDto | null>(null);
  
  const pageSize = 10;
  
  const user = useSelector((state: RootState) => state.auth.user);

  const fetchHistory = useCallback(async (currentPage: number, range: any) => {
    if (!user?.employeeId) return;
    
    setLoading(true);
    try {
      const response = await AttendanceApi.getAttendanceHistory({
        employeeId: user.employeeId,
        startDate: range?.[0]?.format('YYYY-MM-DD'),
        endDate: range?.[1]?.format('YYYY-MM-DD'),
        page: currentPage,
        pageSize: pageSize,
      });
      setData(response.records);
      setTotal(response.total);
    } catch (err) {
      message.error('無法取得出勤記錄');
    } finally {
      setLoading(false);
    }
  }, [user?.employeeId]);

  useEffect(() => {
    fetchHistory(page, dateRange);
  }, [fetchHistory, page, dateRange]);

  const handleApplyCorrection = (record: AttendanceRecordDto) => {
    setSelectedRecord(record);
    setModalVisible(true);
  };

  const getStatusTag = (status: AttendanceStatus) => {
    const statusConfig: Record<AttendanceStatus, { color: string; label: string }> = {
      NORMAL: { color: 'green', label: '正常' },
      LATE: { color: 'orange', label: '遲到' },
      EARLY_LEAVE: { color: 'volcano', label: '早退' },
      ABSENT: { color: 'red', label: '缺勤' },
    };
    const config = statusConfig[status] || { color: 'default', label: status };
    return <Tag color={config.color}>{config.label}</Tag>;
  };

  const columns: ColumnsType<AttendanceRecordDto> = [
    {
      title: '日期',
      dataIndex: 'checkTime',
      key: 'date',
      render: (text) => dayjs(text).format('YYYY-MM-DD'),
    },
    {
      title: '類型',
      dataIndex: 'checkType',
      key: 'checkType',
      render: (type) => type === 'CHECK_IN' ? '上班' : '下班',
    },
    {
      title: '打卡時間',
      dataIndex: 'checkTime',
      key: 'time',
      render: (text) => dayjs(text).format('HH:mm:ss'),
    },
    {
      title: '狀態',
      dataIndex: 'status',
      key: 'status',
      render: (status: AttendanceStatus) => getStatusTag(status),
    },
    {
      title: '地點',
      dataIndex: 'address',
      key: 'address',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          {record.status !== 'NORMAL' && (
            <Button type="link" onClick={() => handleApplyCorrection(record)}>補卡申請</Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ padding: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <Title level={3}>我的考勤記錄查詢</Title>
          <RangePicker 
            value={dateRange} 
            onChange={(val) => setDateRange(val as any)}
          />
        </div>

        <Card>
          <Table
            columns={columns}
            dataSource={data}
            rowKey="id"
            loading={loading}
            pagination={{
              current: page,
              pageSize: pageSize,
              total: total,
              onChange: (p) => setPage(p),
            }}
          />
        </Card>

        <ApplyCorrectionModal
          visible={modalVisible}
          onCancel={() => setModalVisible(false)}
          onSuccess={() => {
            setModalVisible(false);
            fetchHistory(page, dateRange);
          }}
          initialValues={{
            attendanceRecordId: selectedRecord?.id,
            correctionDate: selectedRecord?.checkTime,
          }}
        />
      </div>
    </>
  );
};

export default HR03MyAttendanceListPage;
