import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { WorkflowDefinitionList } from './WorkflowDefinitionList';
import type { WorkflowDefinitionViewModel } from '../model/WorkflowViewModel';

// Mock hooks
const mockRefresh = vi.fn();

vi.mock('../hooks', () => ({
  useWorkflowDefinitions: vi.fn(),
}));

import { useWorkflowDefinitions } from '../hooks';

const mockDefinitions: WorkflowDefinitionViewModel[] = [
  {
    definitionId: 'def-001',
    flowName: '請假審核流程',
    flowType: 'LEAVE',
    flowTypeLabel: '請假流程',
    nodes: [],
    edges: [],
    nodeCount: 5,
    isActive: true,
    statusLabel: '啟用中',
    statusColor: 'success',
    version: 2,
    createdAt: '2026-01-15T10:00:00Z',
    createdAtDisplay: '2026-01-15 10:00',
  },
  {
    definitionId: 'def-002',
    flowName: '加班審核流程',
    flowType: 'OVERTIME',
    flowTypeLabel: '加班流程',
    nodes: [],
    edges: [],
    nodeCount: 3,
    isActive: false,
    statusLabel: '已停用',
    statusColor: 'default',
    version: 1,
    createdAt: '2026-02-20T14:00:00Z',
    createdAtDisplay: '2026-02-20 14:00',
  },
];

describe('WorkflowDefinitionList', () => {
  describe('正常渲染', () => {
    it('應顯示流程定義列表', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: mockDefinitions,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<WorkflowDefinitionList />);

      // 標題
      expect(screen.getByText('流程定義管理')).toBeInTheDocument();
      expect(screen.getByText('管理所有簽核流程的定義與設計')).toBeInTheDocument();

      // 資料列
      expect(screen.getByText('請假審核流程')).toBeInTheDocument();
      expect(screen.getByText('請假流程')).toBeInTheDocument();
      expect(screen.getByText('啟用中')).toBeInTheDocument();
      expect(screen.getByText('v2')).toBeInTheDocument();

      expect(screen.getByText('加班審核流程')).toBeInTheDocument();
      expect(screen.getByText('已停用')).toBeInTheDocument();
    });

    it('應顯示重新整理與新增按鈕', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: mockDefinitions,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<WorkflowDefinitionList />);

      expect(screen.getByText('重新整理')).toBeInTheDocument();
      expect(screen.getByText('新增流程')).toBeInTheDocument();
    });

    it('點擊編輯設計應觸發 onDesign 回調', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: mockDefinitions,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      const onDesign = vi.fn();
      render(<WorkflowDefinitionList onDesign={onDesign} />);

      const designButtons = screen.getAllByText('編輯設計');
      fireEvent.click(designButtons[0]!);

      expect(onDesign).toHaveBeenCalledWith('def-001');
    });

    it('點擊新增流程應觸發 onCreate 回調', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: mockDefinitions,
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      const onCreate = vi.fn();
      render(<WorkflowDefinitionList onCreate={onCreate} />);

      fireEvent.click(screen.getByText('新增流程'));

      expect(onCreate).toHaveBeenCalled();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: [],
        loading: true,
        error: null,
        refresh: mockRefresh,
      });

      const { container } = render(<WorkflowDefinitionList />);

      expect(container.querySelector('.ant-spin')).toBeTruthy();
    });
  });

  describe('空狀態', () => {
    it('無定義時應顯示空狀態', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: [],
        loading: false,
        error: null,
        refresh: mockRefresh,
      });

      render(<WorkflowDefinitionList />);

      expect(screen.getByText('暫無流程定義')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息與重試按鈕', () => {
      vi.mocked(useWorkflowDefinitions).mockReturnValue({
        definitions: [],
        loading: false,
        error: '伺服器錯誤',
        refresh: mockRefresh,
      });

      render(<WorkflowDefinitionList />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
      expect(screen.getByRole('button')).toBeInTheDocument();
    });
  });
});
