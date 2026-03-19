import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MyApplicationsPanel } from './MyApplicationsPanel';
import type { WorkflowInstanceViewModel } from '../model/WorkflowViewModel';

// Mock hooks
const mockRefresh = vi.fn();

vi.mock('../hooks', () => ({
  useMyApplications: vi.fn(),
}));

// Mock ProcessTimeline 避免子元件副作用
vi.mock('./ProcessTimeline', () => ({
  ProcessTimeline: ({ instanceId, onClose }: { instanceId: string | null; onClose: () => void }) =>
    instanceId ? <div data-testid="process-timeline">Timeline: {instanceId}<button onClick={onClose}>關閉</button></div> : null,
}));

import { useMyApplications } from '../hooks';

const mockApplications: WorkflowInstanceViewModel[] = [
  {
    instanceId: 'inst-001',
    definitionId: 'def-001',
    flowName: '請假審核流程',
    businessType: 'LEAVE' as any,
    businessTypeLabel: '請假申請',
    businessId: 'leave-001',
    applicantId: 'emp-003',
    applicantName: '陳志強',
    currentNode: 'node-1',
    currentNodeName: '主管審核',
    status: 'RUNNING' as any,
    statusLabel: '審核中',
    statusColor: 'processing',
    startedAt: '2026-03-05T09:00:00Z',
    startedAtDisplay: '2026-03-05 09:00',
    isRunning: true,
    isCompleted: false,
    isRejected: false,
  },
  {
    instanceId: 'inst-002',
    definitionId: 'def-002',
    flowName: '加班審核流程',
    businessType: 'OVERTIME' as any,
    businessTypeLabel: '加班申請',
    businessId: 'ot-001',
    applicantId: 'emp-003',
    applicantName: '陳志強',
    currentNode: '',
    currentNodeName: '',
    status: 'COMPLETED' as any,
    statusLabel: '已核准',
    statusColor: 'success',
    startedAt: '2026-03-01T10:00:00Z',
    startedAtDisplay: '2026-03-01 10:00',
    completedAt: '2026-03-02T14:00:00Z',
    completedAtDisplay: '2026-03-02 14:00',
    isRunning: false,
    isCompleted: true,
    isRejected: false,
    duration: '1 天 4 小時',
  },
];

describe('MyApplicationsPanel', () => {
  describe('正常渲染', () => {
    it('應顯示申請列表與相關資料', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: mockApplications,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<MyApplicationsPanel />);

      // 表格欄位標題（scroll 模式下可能有重複表頭，用 getAllByText）
      expect(screen.getAllByText('申請類型').length).toBeGreaterThanOrEqual(1);
      expect(screen.getAllByText('流程名稱').length).toBeGreaterThanOrEqual(1);
      expect(screen.getAllByText('目前節點').length).toBeGreaterThanOrEqual(1);

      // 資料列
      expect(screen.getByText('請假申請')).toBeInTheDocument();
      expect(screen.getByText('請假審核流程')).toBeInTheDocument();
      expect(screen.getByText('主管審核')).toBeInTheDocument();
      expect(screen.getByText('審核中')).toBeInTheDocument();
    });

    it('應顯示已完成申請的耗時與完成時間', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: mockApplications,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<MyApplicationsPanel />);

      expect(screen.getByText('加班申請')).toBeInTheDocument();
      expect(screen.getByText('已核准')).toBeInTheDocument();
      expect(screen.getByText('1 天 4 小時')).toBeInTheDocument();
    });

    it('應顯示進度按鈕', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: mockApplications,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<MyApplicationsPanel />);

      const progressButtons = screen.getAllByText('進度');
      expect(progressButtons.length).toBe(2);
    });

    it('應顯示狀態篩選下拉選單', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: mockApplications,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      const { container } = render(<MyApplicationsPanel />);

      // Select 元件存在
      expect(container.querySelector('.ant-select')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: [],
        loading: true,
        error: null,
        refresh: mockRefresh,
      });

      const { container } = render(<MyApplicationsPanel />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無申請記錄時應顯示空狀態', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: [],
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<MyApplicationsPanel />);

      expect(screen.getByText('暫無申請記錄')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息與重試按鈕', () => {
      vi.mocked(useMyApplications).mockReturnValue({
        applications: [],
        loading: false,
        error: '網路錯誤',
        refresh: mockRefresh,
      });

      render(<MyApplicationsPanel />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
      expect(screen.getByRole('button')).toBeInTheDocument();
    });
  });
});
