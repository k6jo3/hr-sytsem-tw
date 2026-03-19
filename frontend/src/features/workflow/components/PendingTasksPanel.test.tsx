import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { PendingTasksPanel } from './PendingTasksPanel';
import type { ApprovalTaskViewModel, TaskSummaryViewModel } from '../model/WorkflowViewModel';

// Mock hook
const mockRefresh = vi.fn();
const mockApproveTask = vi.fn();
const mockRejectTask = vi.fn();

vi.mock('../hooks', () => ({
  usePendingTasks: vi.fn(),
}));

import { usePendingTasks } from '../hooks';

const mockTasks: ApprovalTaskViewModel[] = [
  {
    taskId: 'task-001',
    instanceId: 'inst-001',
    flowName: '請假簽核',
    businessType: 'LEAVE',
    businessTypeLabel: '請假申請',
    businessId: 'leave-001',
    businessSummary: '特休假 3 天',
    nodeId: 'node-1',
    nodeName: '主管審核',
    applicantId: 'emp-003',
    applicantName: '陳志強',
    assigneeId: 'emp-001',
    assigneeName: '王大明',
    status: 'PENDING',
    statusLabel: '待審核',
    statusColor: 'processing',
    isOverdue: false,
    overdueLabel: '',
    createdAt: '2026-03-05T09:00:00Z',
    createdAtDisplay: '2026-03-05 09:00',
    isPending: true,
    canApprove: true,
    canReject: true,
    urgencyColor: 'blue',
  },
];

const mockSummary: TaskSummaryViewModel = {
  totalPending: 3,
  overdueCount: 1,
  dueTodayCount: 0,
  normalCount: 2,
};

describe('PendingTasksPanel', () => {
  describe('正常渲染', () => {
    it('應顯示待辦任務列表與統計', () => {
      vi.mocked(usePendingTasks).mockReturnValue({
        tasks: mockTasks,
        summary: mockSummary,
        loading: false,
        error: null,
        refresh: mockRefresh,
        approveTask: mockApproveTask,
        rejectTask: mockRejectTask,
      });

      render(<PendingTasksPanel />);

      // 統計卡片
      expect(screen.getByText('待處理')).toBeInTheDocument();
      expect(screen.getByText('已逾期')).toBeInTheDocument();
      expect(screen.getByText('今日到期')).toBeInTheDocument();
      expect(screen.getByText('正常')).toBeInTheDocument();

      // 任務資料
      expect(screen.getByText('陳志強')).toBeInTheDocument();
      expect(screen.getByText('特休假 3 天')).toBeInTheDocument();
      expect(screen.getByText('主管審核')).toBeInTheDocument();
    });

    it('應顯示核准與駁回按鈕', () => {
      vi.mocked(usePendingTasks).mockReturnValue({
        tasks: mockTasks,
        summary: mockSummary,
        loading: false,
        error: null,
        refresh: mockRefresh,
        approveTask: mockApproveTask,
        rejectTask: mockRejectTask,
      });

      render(<PendingTasksPanel />);

      expect(screen.getByText('核准')).toBeInTheDocument();
      expect(screen.getByText('駁回')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(usePendingTasks).mockReturnValue({
        tasks: [],
        summary: null,
        loading: true,
        error: null,
        refresh: mockRefresh,
        approveTask: mockApproveTask,
        rejectTask: mockRejectTask,
      });

      const { container } = render(<PendingTasksPanel />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無待辦時應顯示空狀態', () => {
      vi.mocked(usePendingTasks).mockReturnValue({
        tasks: [],
        summary: mockSummary,
        loading: false,
        error: null,
        refresh: mockRefresh,
        approveTask: mockApproveTask,
        rejectTask: mockRejectTask,
      });

      render(<PendingTasksPanel />);

      expect(screen.getByText('暫無待辦任務')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息與重試按鈕', () => {
      vi.mocked(usePendingTasks).mockReturnValue({
        tasks: [],
        summary: null,
        loading: false,
        error: '網路錯誤',
        refresh: mockRefresh,
        approveTask: mockApproveTask,
        rejectTask: mockRejectTask,
      });

      render(<PendingTasksPanel />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
      // Alert action 按鈕渲染在不同 DOM 結構
      expect(screen.getByRole('button')).toBeInTheDocument();
    });
  });
});
