import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { WorkflowDesigner } from './WorkflowDesigner';

// Mock WorkflowApi
vi.mock('../api', () => ({
  WorkflowApi: {
    getDefinitions: vi.fn(),
    createDefinition: vi.fn(),
    getInstance: vi.fn(),
  },
}));

// Mock antd message
vi.mock('antd', async () => {
  const actual = await vi.importActual('antd');
  return {
    ...actual,
    message: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
    },
  };
});

import { WorkflowApi } from '../api';
import { message } from 'antd';

describe('WorkflowDesigner', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('新增模式', () => {
    it('應顯示新增流程定義標題', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('新增流程定義')).toBeInTheDocument();
      expect(screen.getByText('設計審核流程的節點與連線')).toBeInTheDocument();
    });

    it('應顯示預設的開始與結束節點', () => {
      render(<WorkflowDesigner />);

      // 開始/結束節點在表格和預覽區都有，確認至少存在
      expect(screen.getAllByText('開始').length).toBeGreaterThanOrEqual(1);
      expect(screen.getAllByText('結束').length).toBeGreaterThanOrEqual(1);
    });

    it('應顯示流程名稱與類型輸入欄', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('流程名稱：')).toBeInTheDocument();
      expect(screen.getByText('流程類型：')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('例：請假審核流程')).toBeInTheDocument();
    });

    it('應顯示新增節點按鈕', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('新增節點')).toBeInTheDocument();
    });

    it('應顯示儲存按鈕', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('儲存')).toBeInTheDocument();
    });

    it('應顯示流程預覽區', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('流程預覽：')).toBeInTheDocument();
    });
  });

  describe('編輯模式', () => {
    it('應顯示編輯流程定義標題', () => {
      vi.mocked(WorkflowApi.getDefinitions).mockResolvedValue({
        data: [],
        total: 0,
      } as any);

      render(<WorkflowDesigner definitionId="def-001" />);

      expect(screen.getByText('編輯流程定義')).toBeInTheDocument();
    });
  });

  describe('新增節點 Modal', () => {
    it('點擊新增節點應開啟 Modal', async () => {
      render(<WorkflowDesigner />);

      fireEvent.click(screen.getByText('新增節點'));

      await waitFor(() => {
        expect(screen.getByText('新增流程節點')).toBeInTheDocument();
      });

      // Modal 內含表單欄位
      expect(screen.getByPlaceholderText('例：主管審核')).toBeInTheDocument();
      expect(screen.getByText('條件表達式')).toBeInTheDocument();
    });
  });

  describe('返回按鈕', () => {
    it('有 onBack 時應顯示返回列表按鈕', () => {
      const onBack = vi.fn();
      render(<WorkflowDesigner onBack={onBack} />);

      const backButton = screen.getByText('返回列表');
      expect(backButton).toBeInTheDocument();

      fireEvent.click(backButton);
      expect(onBack).toHaveBeenCalled();
    });

    it('無 onBack 時不應顯示返回列表按鈕', () => {
      render(<WorkflowDesigner />);

      expect(screen.queryByText('返回列表')).not.toBeInTheDocument();
    });
  });

  describe('儲存流程', () => {
    it('未填流程名稱時應警告', async () => {
      render(<WorkflowDesigner />);

      fireEvent.click(screen.getByText('儲存'));

      await waitFor(() => {
        expect(message.warning).toHaveBeenCalledWith('請輸入流程名稱');
      });
    });
  });

  describe('節點表格', () => {
    it('應顯示表格欄位標題', () => {
      render(<WorkflowDesigner />);

      expect(screen.getByText('順序')).toBeInTheDocument();
      // 表格標題 - 使用 columnheader role 避免與 Modal 表單重複
      expect(screen.getByRole('columnheader', { name: '節點名稱' })).toBeInTheDocument();
      expect(screen.getByRole('columnheader', { name: '節點類型' })).toBeInTheDocument();
    });

    it('開始與結束節點不應顯示刪除按鈕', () => {
      render(<WorkflowDesigner />);

      expect(screen.queryByText('刪除')).not.toBeInTheDocument();
    });
  });
});
