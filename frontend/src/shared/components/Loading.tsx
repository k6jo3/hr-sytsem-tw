import React from 'react';
import { Spin } from 'antd';

interface LoadingProps {
  size?: 'small' | 'default' | 'large';
}

/**
 * Loading Component
 * 統一的載入中顯示元件
 */
export const Loading: React.FC<LoadingProps> = ({ size = 'large' }) => {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '200px' }}>
      <Spin size={size} />
    </div>
  );
};
