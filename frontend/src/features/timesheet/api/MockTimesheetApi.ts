import type {
    GetPendingApprovalsRequest,
    GetPendingApprovalsResponse,
    GetWeeklyTimesheetRequest,
    GetWeeklyTimesheetResponse,
    SaveTimesheetEntryRequest,
    SubmitTimesheetResponse,
    TimesheetApprovalRequest,
    TimesheetEntryDto,
    TimesheetReportSummaryDto,
    TimesheetStatus,
    WeeklyTimesheetDto,
} from './TimesheetTypes';

const uuidv4 = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockTimesheetApi {
  
  private static timesheets: WeeklyTimesheetDto[] = [
    {
      id: 'ts001',
      employee_id: 'emp001',
      employee_name: 'John Doe',
      week_start_date: '2024-06-03',
      week_end_date: '2024-06-09',
      entries: [
        {
          id: 'entry001',
          timesheet_id: 'ts001',
          employee_id: 'emp001',
          employee_name: 'John Doe',
          project_id: 'proj001',
          project_name: 'ERP系統開發專案',
          wbs_code: 'WBS-001',
          wbs_name: '需求分析',
          work_date: '2024-06-03',
          hours: 8,
          description: '需求訪談與文件撰寫',
          status: 'APPROVED' as TimesheetStatus,
          created_at: '2024-06-03T18:00:00Z',
          updated_at: '2024-06-04T09:00:00Z'
        },
        {
          id: 'entry002',
          timesheet_id: 'ts001',
          employee_id: 'emp001',
          employee_name: 'John Doe',
          project_id: 'proj001',
          project_name: 'ERP系統開發專案',
          wbs_code: 'WBS-002',
          wbs_name: '系統設計',
          work_date: '2024-06-04',
          hours: 8,
          description: '架構設計',
          status: 'APPROVED' as TimesheetStatus,
          created_at: '2024-06-04T18:00:00Z',
          updated_at: '2024-06-05T09:00:00Z'
        }
      ],
      total_hours: 40,
      status: 'APPROVED' as TimesheetStatus,
      submitted_at: '2024-06-09T18:00:00Z'
    },
    {
      id: 'ts002',
      employee_id: 'emp002',
      employee_name: 'Jane Smith',
      week_start_date: '2024-06-03',
      week_end_date: '2024-06-09',
      entries: [
        {
          id: 'entry003',
          timesheet_id: 'ts002',
          employee_id: 'emp002',
          employee_name: 'Jane Smith',
          project_id: 'proj002',
          project_name: '系統維護專案',
          work_date: '2024-06-03',
          hours: 8,
          description: '系統維護',
          status: 'SUBMITTED' as TimesheetStatus,
          created_at: '2024-06-03T18:00:00Z',
          updated_at: '2024-06-03T18:00:00Z'
        }
      ],
      total_hours: 40,
      status: 'SUBMITTED' as TimesheetStatus,
      submitted_at: '2024-06-09T18:00:00Z'
    }
  ];

  // --- Timesheet APIs ---

  static async getWeeklyTimesheet(params: GetWeeklyTimesheetRequest): Promise<GetWeeklyTimesheetResponse> {
    await delay(400);
    
    let timesheet: WeeklyTimesheetDto | undefined;
    
    if (params.id) {
      timesheet = this.timesheets.find(t => t.id === params.id);
    } else if (params.employee_id && params.week_start_date) {
      timesheet = this.timesheets.find(t => 
        t.employee_id === params.employee_id && 
        t.week_start_date === params.week_start_date
      );
    }
    
    if (!timesheet) {
      // Create a new empty timesheet
      timesheet = {
        id: uuidv4(),
        employee_id: params.employee_id || 'emp001',
        employee_name: 'Mock Employee',
        week_start_date: params.week_start_date || new Date().toISOString().split('T')[0],
        week_end_date: params.week_start_date || new Date().toISOString().split('T')[0],
        entries: [],
        total_hours: 0,
        status: 'DRAFT'
      };
    }
    
    return { timesheet };
  }

  static async saveTimesheetEntry(request: SaveTimesheetEntryRequest): Promise<TimesheetEntryDto> {
    await delay(500);
    
    const newEntry: TimesheetEntryDto = {
      id: uuidv4(),
      timesheet_id: request.timesheet_id,
      employee_id: 'emp001',
      employee_name: 'Mock Employee',
      project_id: request.project_id,
      project_name: 'Mock Project',
      wbs_code: request.wbs_code,
      wbs_name: 'Mock WBS',
      work_date: request.work_date,
      hours: request.hours,
      description: request.description,
      status: 'DRAFT',
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    
    return newEntry;
  }

  static async submitTimesheet(timesheetId: string): Promise<SubmitTimesheetResponse> {
    await delay(600);
    
    const index = this.timesheets.findIndex(t => t.id === timesheetId);
    if (index !== -1) {
      this.timesheets[index] = {
        ...this.timesheets[index]!,
        status: 'SUBMITTED',
        submitted_at: new Date().toISOString()
      };
    }
    
    return {
      timesheet_id: timesheetId,
      message: '工時表已送出審核'
    };
  }

  static async getPendingApprovals(params?: GetPendingApprovalsRequest): Promise<GetPendingApprovalsResponse> {
    await delay(400);
    
    let filtered = this.timesheets.filter(t => t.status === 'SUBMITTED');
    
    if (params?.project_id) {
      filtered = filtered.filter(t => 
        t.entries.some(e => e.project_id === params.project_id)
      );
    }
    
    if (params?.employee_id) {
      filtered = filtered.filter(t => t.employee_id === params.employee_id);
    }
    
    const page = params?.page || 1;
    const page_size = params?.page_size || 10;
    const start = (page - 1) * page_size;
    const end = start + page_size;
    
    return {
      timesheets: filtered.slice(start, end),
      total: filtered.length
    };
  }

  static async approveTimesheet(timesheetId: string): Promise<void> {
    await delay(500);
    
    const index = this.timesheets.findIndex(t => t.id === timesheetId);
    if (index !== -1) {
      this.timesheets[index] = {
        ...this.timesheets[index]!,
        status: 'APPROVED'
      };
      
      // Update all entries status
      this.timesheets[index]!.entries = this.timesheets[index]!.entries.map(e => ({
        ...e,
        status: 'APPROVED'
      }));
    }
  }

  static async rejectTimesheet(timesheetId: string, request: TimesheetApprovalRequest): Promise<void> {
    await delay(500);
    
    const index = this.timesheets.findIndex(t => t.id === timesheetId);
    if (index !== -1) {
      this.timesheets[index] = {
        ...this.timesheets[index]!,
        status: 'REJECTED',
        rejection_reason: request.rejection_reason
      };
    }
  }

  static async getTimesheetReport(params: { start_date: string; end_date: string }): Promise<TimesheetReportSummaryDto> {
    await delay(600);
    
    return {
      total_hours: 320,
      project_hours: [
        { project_name: 'ERP系統開發專案', hours: 160 },
        { project_name: '系統維護專案', hours: 160 }
      ],
      department_hours: [
        { department_name: '資訊部', hours: 240 },
        { department_name: '研發部', hours: 80 }
      ],
      unreported_employees: []
    };
  }

  static async deleteTimesheetEntry(entryId: string): Promise<void> {
    await delay(400);
    
    for (const timesheet of this.timesheets) {
      const index = timesheet.entries.findIndex(e => e.id === entryId);
      if (index !== -1) {
        timesheet.entries.splice(index, 1);
        timesheet.total_hours = timesheet.entries.reduce((sum, e) => sum + e.hours, 0);
        break;
      }
    }
  }
}
