import React from 'react';
import { Card, Tag, Typography, Space, Empty, Skeleton, Divider } from 'antd';
import { ClockCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import type { TodayAttendanceSummary } from '../model/AttendanceRecordViewModel';

const { Title, Text } = Typography;

/**
 * 格式化工時顯示
 * - 不足 1 小時：顯示「X 分鐘」
 * - 1 小時以上：顯示「X 小時 Y 分鐘」
 * - 整數小時且分鐘為 0：顯示「X 小時」
 */
const formatWorkHours = (hours: number): string => {
  if (hours < 0) return '0 分鐘';
  const totalMinutes = Math.round(hours * 60);
  const h = Math.floor(totalMinutes / 60);
  const m = totalMinutes % 60;
  if (h === 0) {
    return `${m} 分鐘`;
  }
  if (m === 0) {
    return `${h} 小時`;
  }
  return `${h} 小時 ${m} 分鐘`;
};

/**
 * TodayAttendanceCard Props
 */
export interface TodayAttendanceCardProps {
  summary: TodayAttendanceSummary | null;
  loading?: boolean;
}

/**
 * TodayAttendanceCard Component
 * 今日考勤卡片元件
 */
export const TodayAttendanceCard: React.FC<TodayAttendanceCardProps> = ({
  summary,
  loading = false,
}) => {
  if (loading) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    );
  }

  if (!summary) {
    return (
      <Card>
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暫無考勤記錄" />
      </Card>
    );
  }

  const renderStatusMessage = () => {
    if (summary.hasCheckedIn && summary.hasCheckedOut) {
      return (
        <Text type="success">
          <CheckCircleOutlined /> 今日考勤已完成
        </Text>
      );
    } else if (summary.hasCheckedIn && !summary.hasCheckedOut) {
      return (
        <Text type="warning">
          <ClockCircleOutlined /> 尚未打下班卡
        </Text>
      );
    } else {
      return (
        <Text type="secondary">
          <ClockCircleOutlined /> 尚未打上班卡
        </Text>
      );
    }
  };

  return (
    <Card>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div>
          <Title level={4} style={{ marginBottom: 8 }}>
            今日考勤
          </Title>
          {renderStatusMessage()}
        </div>

        {summary.totalWorkHours !== undefined && (
          <div>
            <Text type="secondary">總工作時數</Text>
            <Title level={2} style={{ margin: '8px 0 0 0', color: '#1890ff' }}>
              {formatWorkHours(summary.totalWorkHours)}
            </Title>
          </div>
        )}

        <Divider style={{ margin: '8px 0' }} />

        <div>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>
            打卡記錄
          </Text>
          {summary.records.length === 0 ? (
            <Empty description="暫無打卡記錄" image={Empty.PRESENTED_IMAGE_SIMPLE} />
          ) : (
            <Space direction="vertical" size="small" style={{ width: '100%' }}>
              {summary.records.map((record) => (
                <div
                  key={record.id}
                  style={{
                    padding: '12px',
                    background: '#f5f5f5',
                    borderRadius: '4px',
                  }}
                >
                  <Space split={<Divider type="vertical" />}>
                    <Tag color={record.checkTypeColor}>{record.checkTypeLabel}</Tag>
                    <Text strong>{record.checkTimeDisplay}</Text>
                    <Tag color={record.statusColor}>{record.statusLabel}</Tag>
                  </Space>
                  {record.address && (
                    <div style={{ marginTop: 4 }}>
                      <Text type="secondary" style={{ fontSize: '12px' }}>
                        {record.address}
                      </Text>
                    </div>
                  )}
                </div>
              ))}
            </Space>
          )}
        </div>
      </Space>
    </Card>
  );
};
