import { RootState } from '@/store';
import { Card, Col, message, Progress, Row, Statistic, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import type { LeaveBalanceDto } from '../features/attendance/api/AttendanceTypes';
import { LeaveApi } from '../features/attendance/api/LeaveApi';

const { Title, Text } = Typography;

/**
 * HR03 我的假期餘額頁面
 * 頁面代碼：HR03-P04
 */
export const HR03LeaveBalancePage: React.FC = () => {
  const [balances, setBalances] = useState<LeaveBalanceDto[]>([]);
  const [loading, setLoading] = useState(false);
  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    const fetchBalances = async () => {
      if (!user?.employeeId) return;
      setLoading(true);
      try {
        const res = await LeaveApi.getLeaveBalances(user.employeeId);
        setBalances(res.balances || []);
      } catch (error) {
        message.error('載入假期餘額失敗');
      } finally {
        setLoading(false);
      }
    };
    fetchBalances();
  }, [user?.employeeId]);

  const columns: ColumnsType<LeaveBalanceDto> = [
    {
      title: '假別名稱',
      dataIndex: 'leaveTypeName',
      key: 'leaveTypeName',
      render: (text) => <Text strong>{text}</Text>,
    },
    {
      title: '年度總額',
      dataIndex: 'totalDays',
      key: 'totalDays',
      render: (days) => `${days} 天`,
    },
    {
      title: '已使用',
      dataIndex: 'usedDays',
      key: 'usedDays',
      render: (days) => <Text type="danger">{days} 天</Text>,
    },
    {
      title: '剩餘額度',
      dataIndex: 'remainingDays',
      key: 'remainingDays',
      render: (days) => (
        <Tag color={days > 0 ? 'green' : 'red'} style={{ fontSize: '14px', padding: '4px 8px' }}>
          {days} 天
        </Tag>
      ),
    },
    {
      title: '使用進度',
      key: 'progress',
      render: (_, record) => {
        const percent = record.totalDays > 0 
          ? Math.round((record.usedDays / record.totalDays) * 100) 
          : 0;
        return <Progress percent={percent} size="small" status={percent > 90 ? 'exception' : 'active'} />;
      },
    },
  ];

  return (
    <>
      <div style={{ padding: '24px' }}>
        <Title level={2} style={{ marginBottom: 24 }}>我的假期餘額 ({new Date().getFullYear()}年度)</Title>
        
        <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
          {balances.slice(0, 3).map((item) => (
            <Col xs={24} sm={8} key={item.leaveTypeId}>
              <Card hoverable>
                <Statistic
                  title={item.leaveTypeName}
                  value={item.remainingDays}
                  suffix={`/ ${item.totalDays} 天`}
                  valueStyle={{ color: item.remainingDays > 0 ? '#3f8600' : '#cf1322' }}
                />
                <div style={{ marginTop: 12 }}>
                  <Text type="secondary">已使用: {item.usedDays} 天</Text>
                </div>
              </Card>
            </Col>
          ))}
        </Row>

        <Card title="假期詳細資訊">
          <Table
            columns={columns}
            dataSource={balances}
            rowKey="leaveTypeId"
            loading={loading}
            pagination={false}
          />
        </Card>
      </div>
    </>
  );
};

export default HR03LeaveBalancePage;
