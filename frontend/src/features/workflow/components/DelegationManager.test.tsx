import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { DelegationManager } from './DelegationManager';
import type { DelegationViewModel } from '../model/WorkflowViewModel';

const mockRefresh = vi.fn();
const mockCreateDelegation = vi.fn();
const mockDeleteDelegation = vi.fn();

vi.mock('../hooks', () => ({
  useDelegations: vi.fn(),
}));

import { useDelegations } from '../hooks';

const mockDelegations: DelegationViewModel[] = [
  {
    delegationId: 'del-001',
    delegatorId: 'emp-001',
    delegatorName: '王大明',
    delegateeId: 'emp-002',
    delegateeName: '李小美',
    startDate: '2026-03-10',
    startDateDisplay: '2026-03-10',
    endDate: '2026-03-15',
    endDateDisplay: '2026-03-15',
    isActive: true,
    statusLabel: '生效中',
    statusColor: 'success',
    createdAt: '2026-03-05T10:00:00Z',
    dateRangeDisplay: '2026-03-10 ~ 2026-03-15',
    canDelete: true,
  },
];

describe('DelegationManager', () => {
  describe('正常渲染', () => {
    it('應顯示代理人列表', () => {
      vi.mocked(useDelegations).mockReturnValue({
        delegations: mockDelegations,
        loading: false,
        error: null,
        submitting: false,
        refresh: mockRefresh,
        createDelegation: mockCreateDelegation,
        deleteDelegation: mockDeleteDelegation,
      });

      render(<DelegationManager />);

      expect(screen.getByText('代理人設定')).toBeInTheDocument();
      expect(screen.getByText('李小美')).toBeInTheDocument();
      expect(screen.getByText('生效中')).toBeInTheDocument();
    });

    it('應顯示新增與重新整理按鈕', () => {
      vi.mocked(useDelegations).mockReturnValue({
        delegations: mockDelegations,
        loading: false,
        error: null,
        submitting: false,
        refresh: mockRefresh,
        createDelegation: mockCreateDelegation,
        deleteDelegation: mockDeleteDelegation,
      });

      render(<DelegationManager />);

      expect(screen.getByText('新增代理人')).toBeInTheDocument();
      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useDelegations).mockReturnValue({
        delegations: [],
        loading: true,
        error: null,
        submitting: false,
        refresh: mockRefresh,
        createDelegation: mockCreateDelegation,
        deleteDelegation: mockDeleteDelegation,
      });

      const { container } = render(<DelegationManager />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無代理人時應顯示空狀態', () => {
      vi.mocked(useDelegations).mockReturnValue({
        delegations: [],
        loading: false,
        error: null,
        submitting: false,
        refresh: mockRefresh,
        createDelegation: mockCreateDelegation,
        deleteDelegation: mockDeleteDelegation,
      });

      render(<DelegationManager />);

      expect(screen.getByText('暫無代理人設定')).toBeInTheDocument();
    });
  });

  describe('新增代理人 Modal', () => {
    it('點擊新增應開啟 Modal', () => {
      vi.mocked(useDelegations).mockReturnValue({
        delegations: [],
        loading: false,
        error: null,
        submitting: false,
        refresh: mockRefresh,
        createDelegation: mockCreateDelegation,
        deleteDelegation: mockDeleteDelegation,
      });

      render(<DelegationManager />);

      fireEvent.click(screen.getByText('新增代理人'));

      // Modal title 為 "新增代理人"（與按鈕文字相同，但 Modal 內有獨立標題）
      const modalTitle = document.querySelector('.ant-modal-title');
      expect(modalTitle).toBeInTheDocument();
    });
  });
});
