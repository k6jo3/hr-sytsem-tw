import React from 'react';
import { Spin } from 'antd';

interface LoadingProps {
  tip?: string;
  size?: 'small' | 'default' | 'large';
}

/**
 * Loading Component
 * 統一的載入中顯示元件
 */
export const Loading: React.FC<LoadingProps> = ({ tip = '載入中...', size = 'large' }) => {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '200px' }}>
      <Spin size={size} tip={tip} />
    </div>
  );
};
