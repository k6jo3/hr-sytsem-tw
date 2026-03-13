import { describe, it, expect } from 'vitest';
import { TimesheetViewModelFactory } from './TimesheetViewModelFactory';
import type { TimesheetEntryDto, WeeklyTimesheetDto } from '../api/TimesheetTypes';

describe('TimesheetViewModelFactory', () => {
  const mockEntryDto: TimesheetEntryDto = {
    id: '1',
    employee_id: 'emp-1',
    employee_name: '王小明',
    project_id: 'proj-1',
    project_name: 'HR系統開發專案',
    wbs_code: 'WBS-001',
    wbs_name: '前端開發',
    work_date: '2024-12-08',
    hours: 8,
    description: '完成登入功能',
    status: 'SUBMITTED',
    created_at: '2024-12-08T18:00:00Z',
    updated_at: '2024-12-08T18:00:00Z',
  };

  describe('createFromDTO', () => {
    it('應該正確轉換工時記錄DTO為ViewModel', () => {
      const viewModel = TimesheetViewModelFactory.createFromDTO(mockEntryDto);

      expect(viewModel.id).toBe('1');
      expect(viewModel.employeeName).toBe('王小明');
      expect(viewModel.projectName).toBe('HR系統開發專案');
      expect(viewModel.workDate).toBe('2024-12-08');
      expect(viewModel.hours).toBe(8);
      expect(viewModel.description).toBe('完成登入功能');
    });

    it('應該正確組合專案顯示名稱', () => {
      const viewModel = TimesheetViewModelFactory.createFromDTO(mockEntryDto);

      expect(viewModel.projectDisplay).toBe('HR系統開發專案');
    });

    it('應該正確組合WBS顯示名稱', () => {
      const viewModel = TimesheetViewModelFactory.createFromDTO(mockEntryDto);

      expect(viewModel.wbsDisplay).toBe('WBS-001 - 前端開發');
    });

    it('應該處理沒有WBS的情況', () => {
      const dtoWithoutWbs: TimesheetEntryDto = {
        ...mockEntryDto,
        wbs_code: undefined,
        wbs_name: undefined,
      };

      const viewModel = TimesheetViewModelFactory.createFromDTO(dtoWithoutWbs);

      expect(viewModel.wbsDisplay).toBeUndefined();
    });

    it('應該正確格式化日期顯示', () => {
      const viewModel = TimesheetViewModelFactory.createFromDTO(mockEntryDto);

      // 應該顯示為 MM/DD 格式
      expect(viewModel.workDateDisplay).toMatch(/^\d{2}\/\d{2}$/);
    });

    it('應該正確處理草稿狀態', () => {
      const draftDto: TimesheetEntryDto = {
        ...mockEntryDto,
        status: 'DRAFT',
      };

      const viewModel = TimesheetViewModelFactory.createFromDTO(draftDto);

      expect(viewModel.statusLabel).toBe('草稿');
      expect(viewModel.statusColor).toBe('default');
      expect(viewModel.canEdit).toBe(true);
      expect(viewModel.canDelete).toBe(true);
    });

    it('應該正確處理已提交狀態', () => {
      const viewModel = TimesheetViewModelFactory.createFromDTO(mockEntryDto);

      expect(viewModel.statusLabel).toBe('已提交');
      expect(viewModel.statusColor).toBe('processing');
      expect(viewModel.canEdit).toBe(false);
      expect(viewModel.canDelete).toBe(false);
    });

    it('應該正確處理已核准狀態', () => {
      const approvedDto: TimesheetEntryDto = {
        ...mockEntryDto,
        status: 'APPROVED',
      };

      const viewModel = TimesheetViewModelFactory.createFromDTO(approvedDto);

      expect(viewModel.statusLabel).toBe('已核准');
      expect(viewModel.statusColor).toBe('success');
      expect(viewModel.canEdit).toBe(false);
      expect(viewModel.canDelete).toBe(false);
    });

    it('應該正確處理已駁回狀態', () => {
      const rejectedDto: TimesheetEntryDto = {
        ...mockEntryDto,
        status: 'REJECTED',
      };

      const viewModel = TimesheetViewModelFactory.createFromDTO(rejectedDto);

      expect(viewModel.statusLabel).toBe('已駁回');
      expect(viewModel.statusColor).toBe('error');
      expect(viewModel.canEdit).toBe(true);
      expect(viewModel.canDelete).toBe(true);
    });
  });

  describe('createListFromDTOs', () => {
    it('應該批量轉換DTO列表', () => {
      const dtoList: TimesheetEntryDto[] = [
        mockEntryDto,
        {
          ...mockEntryDto,
          id: '2',
          hours: 4,
          status: 'DRAFT',
        },
      ];

      const viewModels = TimesheetViewModelFactory.createListFromDTOs(dtoList);

      expect(viewModels).toHaveLength(2);
      expect(viewModels[0]!.hours).toBe(8);
      expect(viewModels[0]!.statusLabel).toBe('已提交');
      expect(viewModels[1]!.hours).toBe(4);
      expect(viewModels[1]!.statusLabel).toBe('草稿');
    });

    it('應該正確處理空列表', () => {
      const viewModels = TimesheetViewModelFactory.createListFromDTOs([]);

      expect(viewModels).toEqual([]);
    });
  });

  describe('createWeeklySummary', () => {
    const mockWeeklyDto: WeeklyTimesheetDto = {
      id: 'weekly-1',
      employee_id: 'emp-1',
      employee_name: '王小明',
      week_start_date: '2024-12-02',
      week_end_date: '2024-12-08',
      entries: [mockEntryDto],
      total_hours: 40,
      status: 'SUBMITTED',
    };

    it('應該正確建立週工時摘要', () => {
      const summary = TimesheetViewModelFactory.createWeeklySummary(mockWeeklyDto);

      expect(summary.weekStartDate).toBe('2024-12-02');
      expect(summary.weekEndDate).toBe('2024-12-08');
      expect(summary.totalHours).toBe(40);
      expect(summary.statusLabel).toBe('已提交');
      expect(summary.entries).toHaveLength(1);
    });

    it('應該正確格式化週區間顯示', () => {
      const summary = TimesheetViewModelFactory.createWeeklySummary(mockWeeklyDto);

      // 應該顯示為 "MM/DD - MM/DD" 格式
      expect(summary.weekDisplay).toMatch(/^\d{2}\/\d{2} - \d{2}\/\d{2}$/);
    });

    it('草稿狀態可以提交', () => {
      const draftWeekly: WeeklyTimesheetDto = {
        ...mockWeeklyDto,
        status: 'DRAFT',
      };

      const summary = TimesheetViewModelFactory.createWeeklySummary(draftWeekly);

      expect(summary.canSubmit).toBe(true);
      expect(summary.canEdit).toBe(true);
    });

    it('已提交狀態不可提交和編輯', () => {
      const summary = TimesheetViewModelFactory.createWeeklySummary(mockWeeklyDto);

      expect(summary.canSubmit).toBe(false);
      expect(summary.canEdit).toBe(false);
    });

    it('已核准狀態不可提交和編輯', () => {
      const approvedWeekly: WeeklyTimesheetDto = {
        ...mockWeeklyDto,
        status: 'APPROVED',
      };

      const summary = TimesheetViewModelFactory.createWeeklySummary(approvedWeekly);

      expect(summary.canSubmit).toBe(false);
      expect(summary.canEdit).toBe(false);
    });

    it('已駁回狀態可以重新提交和編輯', () => {
      const rejectedWeekly: WeeklyTimesheetDto = {
        ...mockWeeklyDto,
        status: 'REJECTED',
      };

      const summary = TimesheetViewModelFactory.createWeeklySummary(rejectedWeekly);

      expect(summary.canSubmit).toBe(true);
      expect(summary.canEdit).toBe(true);
    });
  });

  describe('groupByDate', () => {
    it('應該按日期分組工時記錄', () => {
      const entries: TimesheetEntryDto[] = [
        { ...mockEntryDto, work_date: '2024-12-08', hours: 8 },
        { ...mockEntryDto, id: '2', work_date: '2024-12-08', hours: 2 },
        { ...mockEntryDto, id: '3', work_date: '2024-12-09', hours: 8 },
      ];

      const grouped = TimesheetViewModelFactory.groupByDate(entries);

      expect(grouped).toHaveLength(2);
      expect(grouped[0]!.date).toBe('2024-12-08');
      expect(grouped[0]!.totalHours).toBe(10);
      expect(grouped[0]!.entries).toHaveLength(2);
      expect(grouped[1]!.date).toBe('2024-12-09');
      expect(grouped[1]!.totalHours).toBe(8);
      expect(grouped[1]!.entries).toHaveLength(1);
    });

    it('應該正確處理空列表', () => {
      const grouped = TimesheetViewModelFactory.groupByDate([]);

      expect(grouped).toEqual([]);
    });
  });
});
