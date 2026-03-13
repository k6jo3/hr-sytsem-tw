import { describe, it, expect } from 'vitest';
import { ReportViewModelFactory } from './ReportViewModelFactory';
import type {
  DashboardKpiDto,
  HeadcountTrendDataDto,
  DepartmentDistributionDto,
  AttendanceStatsDto,
  SalaryDistributionDto,
  GetDashboardResponse,
  ReportDefinitionDto,
  ReportDto,
  ScheduledReportDto,
} from '../api/ReportTypes';

describe('ReportViewModelFactory', () => {
  describe('createKpiFromDTO', () => {
    it('應正確轉換 KPI 資料', () => {
      const dto: DashboardKpiDto = {
        total_employees: 150,
        active_employees: 140,
        new_hires_this_month: 5,
        turnover_rate: 8.5,
        average_attendance_rate: 95.2,
        pending_leave_requests: 12,
        overtime_hours_this_month: 320,
        training_completion_rate: 78.3,
      };

      const vm = ReportViewModelFactory.createKpiFromDTO(dto);

      expect(vm.totalEmployees).toBe(150);
      expect(vm.activeEmployees).toBe(140);
      expect(vm.newHiresThisMonth).toBe(5);
      expect(vm.turnoverRate).toBe(8.5);
      expect(vm.turnoverRateDisplay).toBe('8.5%');
      expect(vm.attendanceRateDisplay).toBe('95.2%');
      expect(vm.overtimeHoursThisMonth).toBe(320);
      expect(vm.trainingCompletionRate).toBe(78.3);
      expect(vm.trainingCompletionDisplay).toBe('78.3%');
    });
  });

  describe('createHeadcountTrendFromDTO', () => {
    it('應正確轉換人員趨勢資料', () => {
      const dto: HeadcountTrendDataDto = {
        month: '2026-03',
        headcount: 150,
        new_hires: 8,
        terminations: 3,
      };

      const vm = ReportViewModelFactory.createHeadcountTrendFromDTO(dto);

      expect(vm.month).toBe('2026-03');
      expect(vm.monthLabel).toBe('2026年03月');
      expect(vm.headcount).toBe(150);
      expect(vm.newHires).toBe(8);
      expect(vm.terminations).toBe(3);
      expect(vm.netChange).toBe(5);
    });

    it('應正確處理批次轉換', () => {
      const dtos: HeadcountTrendDataDto[] = [
        { month: '2026-01', headcount: 140, new_hires: 5, terminations: 2 },
        { month: '2026-02', headcount: 143, new_hires: 6, terminations: 3 },
      ];

      const vms = ReportViewModelFactory.createHeadcountTrendListFromDTOs(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0]!.netChange).toBe(3);
      expect(vms[1]!.netChange).toBe(3);
    });
  });

  describe('createDepartmentDistributionFromDTO', () => {
    it('應正確轉換部門分佈資料', () => {
      const dto: DepartmentDistributionDto = {
        department_id: 'dept-001',
        department_name: '研發部',
        employee_count: 45,
        percentage: 30.0,
      };

      const vm = ReportViewModelFactory.createDepartmentDistributionFromDTO(dto);

      expect(vm.departmentId).toBe('dept-001');
      expect(vm.departmentName).toBe('研發部');
      expect(vm.employeeCount).toBe(45);
      expect(vm.percentage).toBe(30.0);
      expect(vm.percentageDisplay).toBe('30.0%');
    });
  });

  describe('createAttendanceStatsFromDTO', () => {
    it('應正確轉換出勤統計資料', () => {
      const dto: AttendanceStatsDto = {
        date: '2026-03-05',
        present_count: 135,
        absent_count: 5,
        late_count: 3,
        leave_count: 7,
        attendance_rate: 95.0,
      };

      const vm = ReportViewModelFactory.createAttendanceStatsFromDTO(dto);

      expect(vm.date).toBe('2026-03-05');
      expect(vm.dateLabel).toBe('2026-03-05');
      expect(vm.presentCount).toBe(135);
      expect(vm.absentCount).toBe(5);
      expect(vm.lateCount).toBe(3);
      expect(vm.leaveCount).toBe(7);
      expect(vm.attendanceRateDisplay).toBe('95.0%');
    });
  });

  describe('createSalaryDistributionFromDTO', () => {
    it('應正確轉換薪資分佈資料', () => {
      const dto: SalaryDistributionDto = {
        range: '30K-40K',
        count: 25,
        percentage: 16.7,
      };

      const vm = ReportViewModelFactory.createSalaryDistributionFromDTO(dto);

      expect(vm.range).toBe('30K-40K');
      expect(vm.count).toBe(25);
      expect(vm.percentage).toBe(16.7);
      expect(vm.percentageDisplay).toBe('16.7%');
    });
  });

  describe('createDashboardFromDTO', () => {
    it('應正確轉換完整儀表板資料', () => {
      const dto: GetDashboardResponse = {
        kpis: {
          total_employees: 150,
          active_employees: 140,
          new_hires_this_month: 5,
          turnover_rate: 8.5,
          average_attendance_rate: 95.2,
          pending_leave_requests: 12,
          overtime_hours_this_month: 320,
          training_completion_rate: 78.3,
        },
        headcount_trend: [
          { month: '2026-01', headcount: 140, new_hires: 5, terminations: 2 },
        ],
        department_distribution: [
          { department_id: 'dept-001', department_name: '研發部', employee_count: 45, percentage: 30.0 },
        ],
        attendance_stats: [
          { date: '2026-03-05', present_count: 135, absent_count: 5, late_count: 3, leave_count: 7, attendance_rate: 95.0 },
        ],
        salary_distribution: [
          { range: '30K-40K', count: 25, percentage: 16.7 },
        ],
      };

      const vm = ReportViewModelFactory.createDashboardFromDTO(dto);

      expect(vm.kpis.totalEmployees).toBe(150);
      expect(vm.headcountTrend).toHaveLength(1);
      expect(vm.departmentDistribution).toHaveLength(1);
      expect(vm.attendanceStats).toHaveLength(1);
      expect(vm.salaryDistribution).toHaveLength(1);
    });
  });

  describe('createDefinitionFromDTO', () => {
    it('應正確轉換報表定義', () => {
      const dto: ReportDefinitionDto = {
        id: 'def-001',
        report_code: 'RPT_EMP_SUMMARY',
        report_name: '員工統計報表',
        report_type: 'EMPLOYEE_SUMMARY',
        description: '員工統計總覽',
        parameters: [
          { name: 'department_id', label: '部門', type: 'SELECT', required: false },
        ],
        available_formats: ['PDF', 'EXCEL'],
        is_scheduled: true,
        created_at: '2026-01-01T00:00:00Z',
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = ReportViewModelFactory.createDefinitionFromDTO(dto);

      expect(vm.definitionId).toBe('def-001');
      expect(vm.reportCode).toBe('RPT_EMP_SUMMARY');
      expect(vm.reportTypeLabel).toBe('員工統計報表');
      expect(vm.reportTypeIcon).toBe('TeamOutlined');
      expect(vm.availableFormatsDisplay).toBe('PDF, Excel');
      expect(vm.parameters).toHaveLength(1);
      expect(vm.isScheduled).toBe(true);
    });
  });

  describe('createReportFromDTO', () => {
    it('應正確轉換已完成的報表', () => {
      const dto: ReportDto = {
        id: 'rpt-001',
        report_definition_id: 'def-001',
        report_name: '員工統計報表',
        report_type: 'EMPLOYEE_SUMMARY',
        format: 'PDF',
        parameters: { department_id: 'dept-001' },
        status: 'COMPLETED',
        file_path: '/reports/rpt-001.pdf',
        download_url: '/api/v1/reports/rpt-001/download',
        generated_by: 'user-001',
        generated_by_name: '王大明',
        generated_at: '2026-03-05T10:00:00Z',
        expires_at: '2026-04-05T10:00:00Z',
        created_at: '2026-03-05T09:59:00Z',
        updated_at: '2026-03-05T10:00:00Z',
      };

      const vm = ReportViewModelFactory.createReportFromDTO(dto);

      expect(vm.reportId).toBe('rpt-001');
      expect(vm.reportTypeLabel).toBe('員工統計報表');
      expect(vm.formatLabel).toBe('PDF');
      expect(vm.statusLabel).toBe('已完成');
      expect(vm.statusColor).toBe('success');
      expect(vm.canDownload).toBe(true);
      expect(vm.isProcessing).toBe(false);
    });

    it('應正確轉換處理中的報表', () => {
      const dto: ReportDto = {
        id: 'rpt-002',
        report_definition_id: 'def-001',
        report_name: '員工統計報表',
        report_type: 'EMPLOYEE_SUMMARY',
        format: 'EXCEL',
        parameters: {},
        status: 'GENERATING',
        generated_by: 'user-001',
        generated_by_name: '王大明',
        created_at: '2026-03-05T10:00:00Z',
        updated_at: '2026-03-05T10:00:00Z',
      };

      const vm = ReportViewModelFactory.createReportFromDTO(dto);

      expect(vm.statusLabel).toBe('產生中');
      expect(vm.statusColor).toBe('processing');
      expect(vm.canDownload).toBe(false);
      expect(vm.isProcessing).toBe(true);
    });

    it('應正確轉換失敗的報表', () => {
      const dto: ReportDto = {
        id: 'rpt-003',
        report_definition_id: 'def-001',
        report_name: '員工統計報表',
        report_type: 'EMPLOYEE_SUMMARY',
        format: 'CSV',
        parameters: {},
        status: 'FAILED',
        error_message: '資料量過大',
        generated_by: 'user-001',
        generated_by_name: '王大明',
        created_at: '2026-03-05T10:00:00Z',
        updated_at: '2026-03-05T10:00:00Z',
      };

      const vm = ReportViewModelFactory.createReportFromDTO(dto);

      expect(vm.statusLabel).toBe('失敗');
      expect(vm.statusColor).toBe('error');
      expect(vm.canDownload).toBe(false);
      expect(vm.isProcessing).toBe(false);
      expect(vm.errorMessage).toBe('資料量過大');
    });
  });

  describe('createScheduledReportFromDTO', () => {
    it('應正確轉換每週排程報表', () => {
      const dto: ScheduledReportDto = {
        id: 'sch-001',
        report_definition_id: 'def-001',
        report_name: '每週出勤統計',
        schedule_type: 'WEEKLY',
        schedule_time: '08:00',
        schedule_day: 1,
        format: 'EXCEL',
        parameters: {},
        recipients: ['user-001', 'user-002'],
        is_active: true,
        last_run_at: '2026-03-03T08:00:00Z',
        next_run_at: '2026-03-10T08:00:00Z',
        created_at: '2026-01-01T00:00:00Z',
        updated_at: '2026-03-03T08:00:00Z',
      };

      const vm = ReportViewModelFactory.createScheduledReportFromDTO(dto);

      expect(vm.scheduleId).toBe('sch-001');
      expect(vm.scheduleTypeLabel).toBe('每週');
      expect(vm.scheduleDayLabel).toBe('週一');
      expect(vm.formatLabel).toBe('Excel');
      expect(vm.recipientsDisplay).toBe('2 位收件人');
      expect(vm.isActive).toBe(true);
      expect(vm.statusLabel).toBe('啟用');
      expect(vm.statusColor).toBe('success');
    });

    it('應正確轉換每月排程報表', () => {
      const dto: ScheduledReportDto = {
        id: 'sch-002',
        report_definition_id: 'def-002',
        report_name: '月薪資報表',
        schedule_type: 'MONTHLY',
        schedule_time: '09:00',
        schedule_day: 5,
        format: 'PDF',
        parameters: {},
        recipients: [],
        is_active: false,
        next_run_at: '2026-04-05T09:00:00Z',
        created_at: '2026-01-01T00:00:00Z',
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = ReportViewModelFactory.createScheduledReportFromDTO(dto);

      expect(vm.scheduleTypeLabel).toBe('每月');
      expect(vm.scheduleDayLabel).toBe('5日');
      expect(vm.recipientsDisplay).toBe('無');
      expect(vm.isActive).toBe(false);
      expect(vm.statusLabel).toBe('停用');
      expect(vm.statusColor).toBe('default');
    });
  });
});
