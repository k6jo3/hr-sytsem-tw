import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { WeeklyTimesheetView } from './WeeklyTimesheetView';
import type { WeeklyTimesheetSummary } from '../model/TimesheetViewModel';

describe('WeeklyTimesheetView', () => {
  const mockSummary: WeeklyTimesheetSummary = {
    weekStartDate: '2024-12-02',
    weekEndDate: '2024-12-08',
    weekDisplay: '12/02 - 12/08',
    entries: [
      {
        id: '1',
        employeeName: '王小明',
        projectName: 'HR系統',
        projectDisplay: 'HR系統',
        workDate: '2024-12-08',
        workDateDisplay: '12/08',
        hours: 8,
        statusLabel: '草稿',
        statusColor: 'default',
        canEdit: true,
        canDelete: true,
      },
    ],
    totalHours: 40,
    statusLabel: '草稿',
    statusColor: 'default',
    canSubmit: true,
    canEdit: true,
  };

  describe('顯示週工時摘要', () => {
    it('應該顯示週區間', () => {
      render(
        <WeeklyTimesheetView
          summary={mockSummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      expect(screen.getByText(/12\/02 - 12\/08/)).toBeInTheDocument();
    });

    it('應該顯示總工時', () => {
      render(
        <WeeklyTimesheetView
          summary={mockSummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      expect(screen.getByText(/40/)).toBeInTheDocument();
      expect(screen.getByText(/小時/)).toBeInTheDocument();
    });

    it('應該顯示工時記錄', () => {
      render(
        <WeeklyTimesheetView
          summary={mockSummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      expect(screen.getByText('HR系統')).toBeInTheDocument();
      expect(screen.getByText('12/08')).toBeInTheDocument();
      expect(screen.getByText('8')).toBeInTheDocument();
    });
  });

  describe('提交按鈕', () => {
    it('可以提交時應該顯示提交按鈕', () => {
      render(
        <WeeklyTimesheetView
          summary={mockSummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      const submitButton = screen.getByText('提交工時');
      expect(submitButton).toBeInTheDocument();
      expect(submitButton.closest('button')).not.toBeDisabled();
    });

    it('不可提交時按鈕應該禁用', () => {
      const submittedSummary: WeeklyTimesheetSummary = {
        ...mockSummary,
        canSubmit: false,
        statusLabel: '已提交',
      };

      render(
        <WeeklyTimesheetView
          summary={submittedSummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      const submitButton = screen.getByText('提交工時');
      expect(submitButton.closest('button')).toBeDisabled();
    });

    it('點擊提交按鈕應該呼叫onSubmit', () => {
      const handleSubmit = vi.fn();

      render(
        <WeeklyTimesheetView
          summary={mockSummary}
          onSubmit={handleSubmit}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      fireEvent.click(screen.getByText('提交工時'));

      expect(handleSubmit).toHaveBeenCalledTimes(1);
    });
  });

  describe('載入狀態', () => {
    it('載入中時應該顯示骨架屏', () => {
      render(
        <WeeklyTimesheetView
          summary={null}
          loading={true}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      const skeleton = document.querySelector('.ant-skeleton');
      expect(skeleton).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('沒有資料時應該顯示空狀態', () => {
      const emptySummary: WeeklyTimesheetSummary = {
        ...mockSummary,
        entries: [],
        totalHours: 0,
      };

      render(
        <WeeklyTimesheetView
          summary={emptySummary}
          onSubmit={() => {}}
          onEdit={() => {}}
          onDelete={() => {}}
        />
      );

      expect(screen.getByText(/尚無工時記錄/)).toBeInTheDocument();
    });
  });
});
