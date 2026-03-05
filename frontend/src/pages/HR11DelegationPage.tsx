import React from 'react';
import { DelegationManager } from '../features/workflow/components';

/**
 * HR11-P05: 代理人設定頁面
 * Feature: workflow
 */
export const HR11DelegationPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <DelegationManager />
    </div>
  );
};
