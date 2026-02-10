import { ExclamationCircleOutlined, LockOutlined } from '@ant-design/icons';
import { Alert, Button, Card, DatePicker, message, Modal, Space, Steps, Typography } from 'antd';
import dayjs from 'dayjs';
import React, { useState } from 'react';
import { MonthCloseApi } from '../features/attendance/api/MonthCloseApi';

const { Title, Paragraph, Text } = Typography;
const { confirm } = Modal;

export const HR03MonthClosePage: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<dayjs.Dayjs | null>(dayjs().subtract(1, 'month'));
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);

  const handleExecute = () => {
    if (!selectedDate) {
      message.warning('請選擇月份');
      return;
    }

    const year = selectedDate.year();
    const month = selectedDate.month() + 1;

    confirm({
      title: `確認執行 ${year}/${month} 考勤月結？`,
      icon: <ExclamationCircleOutlined />,
      content: '執行後將鎖定該月份的打卡、請假、加班等所有資料，無法再進行修改。此動作不可復原！',
      okText: '確認執行',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        setLoading(true);
        setCurrentStep(1);
        try {
          const res = await MonthCloseApi.executeMonthClose({ year, month });
          if (res.success) {
            message.success('月結成功！資料已鎖定。');
            setCurrentStep(2);
          } else {
            message.error(res.message || '月結失敗');
            setCurrentStep(0);
          }
        } catch (error) {
          message.error('系統錯誤，請聯繫管理員');
          setCurrentStep(0);
        } finally {
          setLoading(false);
        }
      },
    });
  };

  return (
    <div style={{ padding: '24px', maxWidth: 800, margin: '0 auto' }}>
      <Title level={2}>考勤月結管理</Title>
      
      <Alert
        message="重大操作警示"
        description="執行月結將永久鎖定選定月份的所有差勤記錄。請確保所有核准流程已完成，且與薪資結算時程相符。"
        type="warning"
        showIcon
        style={{ marginBottom: 24 }}
      />

      <Card>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Text strong>選擇結算月份：</Text>
            <DatePicker 
              picker="month" 
              value={selectedDate} 
              onChange={setSelectedDate}
              disabled={loading}
              style={{ marginLeft: 16 }}
            />
          </div>

          <Steps
            current={currentStep}
            items={[
              { title: '選擇月份', description: '確認結算的月份' },
              { title: '執行中', description: '正在鎖定資料' },
              { title: '完成', description: '資料已移轉至薪資模組' },
            ]}
          />

          <div style={{ textAlign: 'center', padding: '24px 0' }}>
            <Button 
              type="primary" 
              danger 
              size="large" 
              icon={<LockOutlined />} 
              onClick={handleExecute}
              loading={loading}
              disabled={currentStep === 2}
            >
              執行考勤月結 (Lock Data)
            </Button>
          </div>

          {currentStep === 2 && (
            <Alert
              message="該月份已成功結算"
              type="success"
              showIcon
              action={
                <Button size="small" type="primary" ghost onClick={() => setCurrentStep(0)}>
                  結算其他月份
                </Button>
              }
            />
          )}
        </Space>
      </Card>

      <Card title="月結注意事項" style={{ marginTop: 24 }}>
        <Paragraph>
          <ol>
            <li><Text type="secondary">確保資料完整性：</Text> 結算前請先檢查是否有尚未審核的請假或加班申請。</li>
            <li><Text type="secondary">薪資連動：</Text> 結算後的考勤統計數值將自動匯入至 **HR04 薪資服務**。</li>
            <li><Text type="secondary">資料鎖定：</Text> 結算完成後，員工將無法再針對該月份提交補卡或任何申請。</li>
            <li><Text type="secondary">異常處理：</Text> 若結算過程中斷，請檢視系統日誌並聯繫 IT 技術支援。</li>
          </ol>
        </Paragraph>
      </Card>
    </div>
  );
};

export default HR03MonthClosePage;
