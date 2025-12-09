import React from 'react';
import { Alert } from 'antd';

interface ErrorMessageProps {
  message: string;
  description?: string;
  onClose?: () => void;
}

/**
 * Error Message Component
 * 統一的錯誤訊息顯示元件
 */
export const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, description, onClose }) => {
  return (
    <Alert
      message={message}
      description={description}
      type="error"
      showIcon
      closable={!!onClose}
      onClose={onClose}
      style={{ marginBottom: '16px' }}
    />
  );
};
