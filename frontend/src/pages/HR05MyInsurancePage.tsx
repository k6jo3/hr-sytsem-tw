import { ReloadOutlined } from '@ant-design/icons';
import { Button, Card, Space, message } from 'antd';
import React, { useEffect } from 'react';
import { InsuranceInfo } from '../features/insurance/components/InsuranceInfo';
import { useMyInsurance } from '../features/insurance/hooks';

/**
 * HR05-P07: 我的保險資訊頁面 (ESS)
 */
export const HR05MyInsurancePage: React.FC = () => {
  const { insuranceInfo, loading, error, refresh } = useMyInsurance();

  // 初始載入
  useEffect(() => {
    refresh();
  }, [refresh]);

  // 顯示錯誤訊息
  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={<span style={{ fontSize: 20, fontWeight: 600 }}>我的保險資訊</span>}
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={refresh}>
              重新整理
            </Button>
          </Space>
        }
      >
        <InsuranceInfo insuranceInfo={insuranceInfo} loading={loading} />
      </Card>
    </div>
  );
};
export default HR05MyInsurancePage;
