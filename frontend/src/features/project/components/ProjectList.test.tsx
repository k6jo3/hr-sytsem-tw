import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { ProjectList } from './ProjectList';
import type { ProjectViewModel } from '../model/ProjectViewModel';

const mockProjects: ProjectViewModel[] = [
  {
    id: 'proj-001',
    projectCode: 'P-2026-001',
    projectName: 'ERP系統導入',
    customerName: '台灣科技',
    projectManagerName: '林雅婷',
    projectTypeLabel: '固定總價',
    projectTypeColor: 'blue',
    budgetTypeLabel: '固定預算',
    budgetAmount: 5000000,
    budgetAmountDisplay: '$5,000,000',
    actualCost: 3000000,
    actualCostDisplay: '$3,000,000',
    costUtilization: 60,
    costUtilizationDisplay: '60.0%',
    progress: 65,
    progressDisplay: '65%',
    statusLabel: '進行中',
    statusColor: 'blue',
    plannedSchedule: '2026/01/01 - 2026/12/31',
    isOverBudget: false,
    isDelayed: false,
  },
  {
    id: 'proj-002',
    projectCode: 'P-2026-002',
    projectName: '行動APP開發',
    customerName: '創新數位',
    projectManagerName: '王大明',
    projectTypeLabel: '實報實銷',
    projectTypeColor: 'green',
    budgetTypeLabel: '彈性預算',
    budgetAmount: 2000000,
    budgetAmountDisplay: '$2,000,000',
    actualCost: 2500000,
    actualCostDisplay: '$2,500,000',
    costUtilization: 125,
    costUtilizationDisplay: '125.0%',
    progress: 80,
    progressDisplay: '80%',
    statusLabel: '進行中',
    statusColor: 'blue',
    plannedSchedule: '2026/03/01 - 2026/09/30',
    isOverBudget: true,
    isDelayed: false,
  },
];

describe('ProjectList', () => {
  const defaultProps = {
    projects: mockProjects,
    total: 2,
    page: 1,
    pageSize: 10,
    onPageChange: vi.fn(),
  };

  describe('渲染', () => {
    it('應正確顯示專案列表', () => {
      render(<ProjectList {...defaultProps} />);

      expect(screen.getByText('P-2026-001')).toBeInTheDocument();
      expect(screen.getByText('ERP系統導入')).toBeInTheDocument();
      expect(screen.getByText('台灣科技')).toBeInTheDocument();
      expect(screen.getByText('P-2026-002')).toBeInTheDocument();
      expect(screen.getByText('行動APP開發')).toBeInTheDocument();
    });

    it('應顯示分頁資訊', () => {
      render(<ProjectList {...defaultProps} />);

      expect(screen.getByText('共 2 個專案')).toBeInTheDocument();
    });
  });

  describe('按鈕操作', () => {
    it('有 onAdd 時應顯示新增按鈕', () => {
      const onAdd = vi.fn();
      render(<ProjectList {...defaultProps} onAdd={onAdd} />);

      const addBtn = screen.getByText('新增專案');
      expect(addBtn).toBeInTheDocument();

      fireEvent.click(addBtn);
      expect(onAdd).toHaveBeenCalledTimes(1);
    });

    it('有 onRefresh 時應顯示重新整理按鈕', () => {
      const onRefresh = vi.fn();
      render(<ProjectList {...defaultProps} onRefresh={onRefresh} />);

      const refreshBtn = screen.getByText('重新整理');
      fireEvent.click(refreshBtn);
      expect(onRefresh).toHaveBeenCalledTimes(1);
    });

    it('無 onAdd 時不應顯示新增按鈕', () => {
      render(<ProjectList {...defaultProps} />);

      expect(screen.queryByText('新增專案')).not.toBeInTheDocument();
    });
  });

  describe('空列表', () => {
    it('無資料時應顯示空狀態', () => {
      render(<ProjectList {...defaultProps} projects={[]} total={0} />);

      expect(screen.queryByText('P-2026-001')).not.toBeInTheDocument();
    });
  });
});
