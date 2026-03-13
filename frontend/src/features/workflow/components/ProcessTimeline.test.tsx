import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ProcessTimeline } from './ProcessTimeline';

// Mock WorkflowApi
vi.mock('../api', () => ({
  WorkflowApi: {
    getInstance: vi.fn(),
  },
}));

// Mock Factory
vi.mock('../factory/WorkflowViewModelFactory', () => ({
  WorkflowViewModelFactory: {
    createInstanceViewModel: vi.fn((data: any) => data),
    createTaskList: vi.fn((data: any) => data || []),
  },
}));

import { WorkflowApi } from '../api';
import { WorkflowViewModelFactory } from '../factory/WorkflowViewModelFactory';

const mockInstance = {
  instanceId: 'inst-001',
  definitionId: 'def-001',
  flowName: '請假審核流程',
  businessType: 'LEAVE',
  businessTypeLabel: '請假申請',
  businessId: 'leave-001',
  applicantId: 'emp-003',
  applicantName: '陳志強',
  currentNode: 'node-2',
  currentNodeName: '人資審核',
  status: 'RUNNING',
  statusLabel: '審核中',
  statusColor: 'processing',
  startedAt: '2026-03-05T09:00:00Z',
  startedAtDisplay: '2026-03-05 09:00',
  isRunning: true,
  isCompleted: false,
  isRejected: false,
};

const mockTasks = [
  {
    taskId: 'task-001',
    instanceId: 'inst-001',
    flowName: '請假審核流程',
    businessType: 'LEAVE',
    businessTypeLabel: '請假申請',
    businessId: 'leave-001',
    businessSummary: '特休假',
    nodeId: 'node-1',
    nodeName: '主管審核',
    applicantId: 'emp-003',
    applicantName: '陳志強',
    assigneeId: 'emp-001',
    assigneeName: '王大明',
    status: 'APPROVED',
    statusLabel: '已核准',
    statusColor: 'success',
    isOverdue: false,
    overdueLabel: '',
    createdAt: '2026-03-05T09:00:00Z',
    createdAtDisplay: '2026-03-05 09:00',
    completedAt: '2026-03-05T14:00:00Z',
    completedAtDisplay: '2026-03-05 14:00',
    isPending: false,
    canApprove: false,
    canReject: false,
    urgencyColor: 'blue',
    comments: '同意',
  },
  {
    taskId: 'task-002',
    instanceId: 'inst-001',
    flowName: '請假審核流程',
    businessType: 'LEAVE',
    businessTypeLabel: '請假申請',
    businessId: 'leave-001',
    businessSummary: '特休假',
    nodeId: 'node-2',
    nodeName: '人資審核',
    applicantId: 'emp-003',
    applicantName: '陳志強',
    assigneeId: 'emp-002',
    assigneeName: '李小美',
    status: 'PENDING',
    statusLabel: '待審核',
    statusColor: 'processing',
    isOverdue: false,
    overdueLabel: '',
    createdAt: '2026-03-05T14:00:00Z',
    createdAtDisplay: '2026-03-05 14:00',
    isPending: true,
    canApprove: true,
    canReject: true,
    urgencyColor: 'blue',
  },
];

describe('ProcessTimeline', () => {
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Drawer 開關', () => {
    it('instanceId 為 null 時不應顯示 Drawer 內容', () => {
      render(<ProcessTimeline instanceId={null} onClose={mockOnClose} />);

      expect(screen.queryByText('流程進度追蹤')).not.toBeInTheDocument();
    });

    it('instanceId 有值時應開啟 Drawer', async () => {
      vi.mocked(WorkflowApi.getInstance).mockResolvedValue({
        instance: mockInstance as any,
        tasks: mockTasks as any,
      });
      vi.mocked(WorkflowViewModelFactory.createInstanceViewModel).mockReturnValue(mockInstance as any);
      vi.mocked(WorkflowViewModelFactory.createTaskList).mockReturnValue(mockTasks as any);

      render(<ProcessTimeline instanceId="inst-001" onClose={mockOnClose} />);

      expect(screen.getByText('流程進度追蹤')).toBeInTheDocument();
    });
  });

  describe('流程詳情渲染', () => {
    it('應顯示流程資訊', async () => {
      vi.mocked(WorkflowApi.getInstance).mockResolvedValue({
        instance: mockInstance as any,
        tasks: mockTasks as any,
      });
      vi.mocked(WorkflowViewModelFactory.createInstanceViewModel).mockReturnValue(mockInstance as any);
      vi.mocked(WorkflowViewModelFactory.createTaskList).mockReturnValue(mockTasks as any);

      render(<ProcessTimeline instanceId="inst-001" onClose={mockOnClose} />);

      await waitFor(() => {
        expect(screen.getByText('請假審核流程')).toBeInTheDocument();
      });

      expect(screen.getByText('陳志強')).toBeInTheDocument();
      expect(screen.getByText('請假申請')).toBeInTheDocument();
      expect(screen.getByText('審核中')).toBeInTheDocument();
    });

    it('應顯示審核步驟', async () => {
      vi.mocked(WorkflowApi.getInstance).mockResolvedValue({
        instance: mockInstance as any,
        tasks: mockTasks as any,
      });
      vi.mocked(WorkflowViewModelFactory.createInstanceViewModel).mockReturnValue(mockInstance as any);
      vi.mocked(WorkflowViewModelFactory.createTaskList).mockReturnValue(mockTasks as any);

      render(<ProcessTimeline instanceId="inst-001" onClose={mockOnClose} />);

      await waitFor(() => {
        expect(screen.getByText('主管審核')).toBeInTheDocument();
      });

      expect(screen.getByText('人資審核')).toBeInTheDocument();
      expect(screen.getByText(/王大明/)).toBeInTheDocument();
      expect(screen.getByText(/李小美/)).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(WorkflowApi.getInstance).mockReturnValue(new Promise(() => {}));

      render(<ProcessTimeline instanceId="inst-001" onClose={mockOnClose} />);

      // Drawer 使用 portal 渲染，需用 document.body 查找
      expect(document.body.querySelector('.ant-spin')).toBeTruthy();
    });
  });

  describe('空狀態', () => {
    it('API 失敗時應顯示空狀態', async () => {
      vi.mocked(WorkflowApi.getInstance).mockRejectedValue(new Error('失敗'));

      render(<ProcessTimeline instanceId="inst-001" onClose={mockOnClose} />);

      await waitFor(() => {
        expect(screen.getByText('無法載入流程資料')).toBeInTheDocument();
      });
    });
  });
});
