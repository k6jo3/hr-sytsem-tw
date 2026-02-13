import { CheckInButton } from '@features/attendance/components/CheckInButton';
import { TodayAttendanceCard } from '@features/attendance/components/TodayAttendanceCard';
import { useAttendance } from '@features/attendance/hooks/useAttendance';
import { Card, Col, Row, Space, Typography, message } from 'antd';
import React from 'react';

const { Title } = Typography;

/**
 * HR03 考勤打卡頁面
 * 頁面代碼：HR03-P01
 */
const HR03AttendanceCheckInPage: React.FC = () => {
  const { summary, loading, error, checkingIn, handleCheckIn } = useAttendance();

  // 顯示錯誤訊息
  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  // 處理上班打卡
  const handleCheckInClick = async () => {
    try {
      await handleCheckIn('CHECK_IN');
      message.success('上班打卡成功！');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '打卡失敗，請稍後再試');
    }
  };

  // 處理下班打卡
  const handleCheckOutClick = async () => {
    try {
      await handleCheckIn('CHECK_OUT');
      message.success('下班打卡成功！');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '打卡失敗，請稍後再試');
    }
  };

  return (
    <>
      <div style={{ maxWidth: 800, margin: '0 auto', padding: '24px 0' }}>
        <Title level={3} style={{ marginBottom: 24 }}>
          員工自助打卡
        </Title>

        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 打卡按鈕卡片 */}
          <Card>
            <Row gutter={16}>
              <Col span={12}>
                <CheckInButton
                  type="CHECK_IN"
                  disabled={!summary?.canCheckIn || checkingIn}
                  loading={checkingIn}
                  onClick={handleCheckInClick}
                />
              </Col>
              <Col span={12}>
                <CheckInButton
                  type="CHECK_OUT"
                  disabled={!summary?.canCheckOut || checkingIn}
                  loading={checkingIn}
                  onClick={handleCheckOutClick}
                />
              </Col>
            </Row>
          </Card>

          {/* 今日考勤卡片 */}
          <TodayAttendanceCard summary={summary} loading={loading} />
        </Space>
      </div>
    </>
  );
};

export default HR03AttendanceCheckInPage;
