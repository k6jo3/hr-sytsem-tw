import { useState } from 'react';
import { NotificationApi } from '../api';

/**
 * Notification Hook (通知服務 Hook)
 * 處理通知服務相關的業務邏輯
 */
export const useNotification = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
