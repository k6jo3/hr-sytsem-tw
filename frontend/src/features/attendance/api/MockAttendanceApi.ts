import type {
    ApplyLeaveRequest,
    ApplyLeaveResponse,
    ApplyOvertimeRequest,
    ApplyOvertimeResponse,
    AttendanceRecordDto,
    CheckInRequest,
    CheckInResponse,
    CheckOutRequest,
    CheckOutResponse,
    CreateShiftRequest,
    ExecuteMonthCloseRequest,
    ExecuteMonthCloseResponse,
    GetAttendanceHistoryRequest,
    GetAttendanceHistoryResponse,
    GetDailyReportResponse,
    GetLeaveApplicationsRequest,
    GetLeaveApplicationsResponse,
    GetMonthlyReportResponse,
    GetOvertimeApplicationsRequest,
    GetOvertimeApplicationsResponse,
    GetTodayAttendanceRequest,
    GetTodayAttendanceResponse,
    LeaveApplicationDto,
    LeaveBalanceListResponse,
    LeaveTypeDto,
    ShiftDto,
    UpdateShiftRequest
} from './AttendanceTypes';

const uuidv4 = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockAttendanceApi {
  
  private static attendanceRecords: AttendanceRecordDto[] = [
    {
      id: 'att001',
      employeeId: 'emp001',
      employeeName: 'John Doe',
      checkType: 'CHECK_IN',
      checkTime: '2024-01-15T09:00:00Z',
      status: 'NORMAL',
      address: '台北市信義區',
      createdAt: '2024-01-15T09:00:00Z'
    }
  ];

  private static leaveApplications: LeaveApplicationDto[] = [
    {
      applicationId: 'leave001',
      employeeId: 'emp001',
      employeeName: 'John Doe',
      employeeNumber: 'E001',
      leaveTypeCode: 'ANNUAL',
      leaveTypeName: '特休假',
      startDate: '2024-02-01',
      endDate: '2024-02-03',
      leaveDays: 3,
      status: 'APPROVED',
      appliedAt: '2024-01-20T10:00:00Z',
      reason: '家庭旅遊'
    }
  ];

  private static leaveTypes: LeaveTypeDto[] = [
    {
      leaveTypeId: 'lt001',
      leaveTypeCode: 'ANNUAL',
      leaveTypeName: '特休假',
      isPaid: true,
      annualQuotaDays: 14,
      allowCarryOver: true,
      isActive: true
    },
    {
      leaveTypeId: 'lt002',
      leaveTypeCode: 'SICK',
      leaveTypeName: '病假',
      isPaid: true,
      annualQuotaDays: 30,
      allowCarryOver: false,
      isActive: true
    }
  ];

  private static shifts: ShiftDto[] = [
    {
      shiftId: 'shift001',
      shiftCode: 'DAY',
      shiftName: '日班',
      shiftType: 'STANDARD',
      workStartTime: '09:00',
      workEndTime: '18:00',
      workingHours: 8,
      isActive: true,
      employeeCount: 50
    }
  ];

  // --- Attendance APIs ---

  static async checkIn(request: CheckInRequest): Promise<CheckInResponse> {
    await delay(500);
    const recordId = uuidv4();
    const checkInTime = request.checkTime || new Date().toISOString();
    
    return {
      success: true,
      recordId,
      checkInTime,
      isLate: false,
      lateMinutes: 0,
      shiftName: '日班',
      message: '打卡成功'
    };
  }

  static async checkOut(request: CheckOutRequest): Promise<CheckOutResponse> {
    await delay(500);
    const recordId = uuidv4();
    const checkOutTime = request.checkOutTime || new Date().toISOString();
    
    return {
      success: true,
      recordId,
      checkOutTime,
      isEarlyLeave: false,
      earlyLeaveMinutes: 0,
      shiftName: '日班',
      message: '打卡成功'
    };
  }

  static async getTodayAttendance(params?: GetTodayAttendanceRequest): Promise<GetTodayAttendanceResponse> {
    await delay(400);
    return {
      records: this.attendanceRecords.slice(0, 2),
      hasCheckedIn: true,
      hasCheckedOut: false,
      totalWorkHours: 0
    };
  }

  static async getAttendanceHistory(params?: GetAttendanceHistoryRequest): Promise<GetAttendanceHistoryResponse> {
    await delay(400);
    const page = params?.page || 1;
    const pageSize = params?.pageSize || 10;
    
    return {
      records: this.attendanceRecords,
      total: this.attendanceRecords.length,
      page,
      pageSize
    };
  }

  // --- Leave APIs ---

  static async applyLeave(_request: ApplyLeaveRequest): Promise<ApplyLeaveResponse> {
    await delay(600);
    return {
      success: true,
      applicationId: uuidv4(),
      message: '請假申請已提交'
    };
  }

  static async getLeaveApplications(params?: GetLeaveApplicationsRequest): Promise<GetLeaveApplicationsResponse> {
    await delay(400);
    let filtered = [...this.leaveApplications];
    
    if (params?.status) {
      filtered = filtered.filter(app => app.status === params.status);
    }
    
    const page = params?.page || 1;
    const size = params?.pageSize || 10;
    
    return {
      items: filtered,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      page,
      size,
      hasNext: false,
      hasPrevious: false
    };
  }

  static async getLeaveBalance(employeeId: string): Promise<LeaveBalanceListResponse> {
    await delay(400);
    return {
      employeeId,
      balances: [
        {
          leaveTypeId: 'lt001',
          leaveTypeName: '特休假',
          totalDays: 14,
          usedDays: 3,
          remainingDays: 11,
          year: 2024
        },
        {
          leaveTypeId: 'lt002',
          leaveTypeName: '病假',
          totalDays: 30,
          usedDays: 0,
          remainingDays: 30,
          year: 2024
        }
      ]
    };
  }

  static async getLeaveTypes(): Promise<LeaveTypeDto[]> {
    await delay(300);
    return this.leaveTypes;
  }

  // --- Overtime APIs ---

  static async applyOvertime(_request: ApplyOvertimeRequest): Promise<ApplyOvertimeResponse> {
    await delay(600);
    return {
      success: true,
      applicationId: uuidv4(),
      message: '加班申請已提交'
    };
  }

  static async getOvertimeApplications(params?: GetOvertimeApplicationsRequest): Promise<GetOvertimeApplicationsResponse> {
    await delay(400);
    const page = params?.page || 1;
    const size = params?.pageSize || 10;
    
    return {
      items: [],
      totalElements: 0,
      totalPages: 0,
      page,
      size
    };
  }

  // --- Shift APIs ---

  static async getShifts(): Promise<ShiftDto[]> {
    await delay(400);
    return this.shifts;
  }

  static async createShift(request: CreateShiftRequest): Promise<ShiftDto> {
    await delay(600);
    const newShift: ShiftDto = {
      shiftId: uuidv4(),
      shiftCode: request.shiftCode,
      shiftName: request.shiftName,
      shiftType: request.shiftType as any,
      workStartTime: request.workStartTime,
      workEndTime: request.workEndTime,
      workingHours: 8,
      isActive: true,
      employeeCount: 0
    };
    this.shifts = [newShift, ...this.shifts];
    return newShift;
  }

  static async updateShift(id: string, _request: UpdateShiftRequest): Promise<ShiftDto> {
    await delay(500);
    const shift = this.shifts.find(s => s.shiftId === id);
    if (!shift) throw new Error('Shift not found');
    return shift;
  }

  static async deleteShift(id: string): Promise<void> {
    await delay(400);
    this.shifts = this.shifts.filter(s => s.shiftId !== id);
  }

  // --- Report APIs ---

  static async getMonthlyReport(year: number, month: number): Promise<GetMonthlyReportResponse> {
    await delay(600);
    return {
      year,
      month,
      items: [
        {
          employeeId: 'emp001',
          employeeName: 'John Doe',
          employeeNumber: 'E001',
          departmentName: '資訊部',
          scheduledDays: 22,
          actualDays: 21,
          absentDays: 1,
          lateCount: 2,
          earlyLeaveCount: 0,
          leaveDays: 1,
          overtimeHours: 10,
          totalWorkHours: 168
        }
      ],
      summary: {
        totalEmployees: 1,
        averageAttendanceRate: 95.5,
        totalLateCount: 2,
        totalEarlyLeaveCount: 0,
        totalOvertimeHours: 10
      }
    };
  }

  static async getDailyReport(date: string): Promise<GetDailyReportResponse> {
    await delay(500);
    return {
      date,
      items: [
        {
          employeeId: 'emp001',
          employeeName: 'John Doe',
          employeeNumber: 'E001',
          departmentName: '資訊部',
          status: 'PRESENT',
          checkInTime: '09:00:00',
          checkOutTime: '18:00:00',
          isLate: false,
          isEarlyLeave: false
        }
      ],
      totalPresent: 1,
      totalAbsent: 0
    };
  }

  static async executeMonthClose(_request: ExecuteMonthCloseRequest): Promise<ExecuteMonthCloseResponse> {
    await delay(1000);
    return {
      success: true,
      message: '月結作業已完成',
      batchId: uuidv4()
    };
  }
}
