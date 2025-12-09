import React from 'react';
import { Button } from 'antd';
import { ClockCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import type { CheckType } from '../api/AttendanceTypes';

/**
 * CheckInButton Props
 */
export interface CheckInButtonProps {
  type: CheckType;
  disabled: boolean;
  loading: boolean;
  onClick: () => void;
}

/**
 * CheckInButton Component
 * 打卡按鈕元件
 */
export const CheckInButton: React.FC<CheckInButtonProps> = ({
  type,
  disabled,
  loading,
  onClick,
}) => {
  const getButtonText = () => {
    switch (type) {
      case 'CHECK_IN':
        return '上班打卡';
      case 'CHECK_OUT':
        return '下班打卡';
      case 'BREAK_OUT':
        return '外出';
      case 'BREAK_IN':
        return '返回';
      default:
        return '打卡';
    }
  };

  const getButtonType = () => {
    return type === 'CHECK_IN' ? 'primary' : 'default';
  };

  const getIcon = () => {
    return type === 'CHECK_IN' ? <ClockCircleOutlined /> : <CheckCircleOutlined />;
  };

  return (
    <Button
      type={getButtonType()}
      size="large"
      icon={getIcon()}
      disabled={disabled}
      loading={loading}
      onClick={onClick}
      aria-label={getButtonText()}
      style={{ width: '100%' }}
    >
      {getButtonText()}
    </Button>
  );
};
