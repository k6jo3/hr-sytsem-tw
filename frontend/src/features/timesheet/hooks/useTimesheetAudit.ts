import dayjs from 'dayjs';
import { useCallback, useState } from 'react';
import { AttendanceApi } from '../../attendance/api/AttendanceApi';
import type { AttendanceRecordDto, LeaveApplicationDto } from '../../attendance/api/AttendanceTypes';
import { LeaveApi } from '../../attendance/api/LeaveApi';
import type { AuditStatus, DailyAuditResult, WeeklyAuditSummary } from '../model/TimesheetAuditModel';
import type { WeeklyTimesheetSummary } from '../model/TimesheetViewModel';

/**
 * Timesheet Audit Hook (工時與考勤勾稽 Hook)
 */
export const useTimesheetAudit = () => {
  const [auditSummary, setAuditSummary] = useState<WeeklyAuditSummary | null>(null);
  const [loading, setLoading] = useState(false);

  const calculateAudit = useCallback(async (
    timesheet: WeeklyTimesheetSummary,
    employeeId: string
  ) => {
    setLoading(true);
    try {
      const startDate = timesheet.weekStartDate;
      const endDate = timesheet.weekEndDate;

      // 1. 取得考勤記錄
      const attendanceResponse = await AttendanceApi.getAttendanceHistory({
        employeeId,
        startDate,
        endDate,
        pageSize: 100
      });

      // 2. 取得請假記錄
      const leaveResponse = await LeaveApi.getLeaveApplications({
        employeeId,
        startDate,
        endDate,
        status: 'APPROVED'
      });

      // 3. 執行勾稽計算
      const summary = performAudit(timesheet, attendanceResponse.records, leaveResponse.items);
      setAuditSummary(summary);
    } catch (err) {
      console.error('Audit failed:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  return { auditSummary, loading, calculateAudit };
};

/**
 * 執行勾稽核心邏輯
 */
function performAudit(
  timesheet: WeeklyTimesheetSummary,
  attendanceRecords: AttendanceRecordDto[],
  leaveApplications: LeaveApplicationDto[]
): WeeklyAuditSummary {
  const dailyResults: DailyAuditResult[] = [];
  let totalTimesheet = 0;
  let totalAttendance = 0;
  let totalLeave = 0;

  // 遍歷該週 7 天
  for (let i = 0; i < 7; i++) {
    const currentDate = dayjs(timesheet.weekStartDate).add(i, 'day').format('YYYY-MM-DD');
    
    // 計算該日工時 (從 Timesheet)
    const dayTimesheetHours = timesheet.entries
      .filter(e => e.workDate === currentDate)
      .reduce((sum, e) => sum + e.hours, 0);

    // 計算該日考勤時數
    const dayAttendanceHours = calculateDailyAttendanceHours(currentDate, attendanceRecords);

    // 計算該日請假時數
    const dayLeaveHours = calculateDailyLeaveHours(currentDate, leaveApplications);

    // 判定狀態
    let status: AuditStatus = 'OK';
    let message = '';

    if (dayTimesheetHours > dayAttendanceHours + dayLeaveHours + 0.1) {
      // 容差 0.1 小時
      status = 'ERROR';
      message = `報工時數 (${dayTimesheetHours}h) 超過在場加請假時數 (${(dayAttendanceHours + dayLeaveHours).toFixed(1)}h)`;
    } else if (dayLeaveHours > 0 && dayTimesheetHours > 0) {
      status = 'WARN';
      message = '請假期間仍有報工記錄';
    }

    dailyResults.push({
      date: currentDate,
      timesheetHours: dayTimesheetHours,
      attendanceHours: dayAttendanceHours,
      leaveHours: dayLeaveHours,
      status,
      message
    });

    totalTimesheet += dayTimesheetHours;
    totalAttendance += dayAttendanceHours;
    totalLeave += dayLeaveHours;
  }

  return {
    weekStartDate: timesheet.weekStartDate,
    totalTimesheetHours: totalTimesheet,
    totalAttendanceHours: totalAttendance,
    totalLeaveHours: totalLeave,
    dailyResults,
    hasMismatch: dailyResults.some(r => r.status !== 'OK')
  };
}

/**
 * 計算單日考勤總時數 (Pairing logic)
 */
function calculateDailyAttendanceHours(date: string, records: AttendanceRecordDto[]): number {
  const dayRecords = records
    .filter(r => dayjs(r.checkTime).format('YYYY-MM-DD') === date)
    .sort((a, b) => dayjs(a.checkTime).diff(dayjs(b.checkTime)));

  if (dayRecords.length < 2) return 0;

  let totalMs = 0;
  let lastIn: number | null = null;

  for (const record of dayRecords) {
    if (record.checkType === 'CHECK_IN' || record.checkType === 'BREAK_IN') {
      lastIn = dayjs(record.checkTime).valueOf();
    } else if ((record.checkType === 'CHECK_OUT' || record.checkType === 'BREAK_OUT') && lastIn !== null) {
      totalMs += (dayjs(record.checkTime).valueOf() - lastIn);
      lastIn = null;
    }
  }

  return Number((totalMs / (1000 * 60 * 60)).toFixed(2));
}

/**
 * 計算單日請假總時數 (簡化為 8 小時制)
 */
function calculateDailyLeaveHours(date: string, leaves: LeaveApplicationDto[]): number {
  const targetDate = dayjs(date);
  const activeLeaves = leaves.filter(l => {
    const start = dayjs(l.startDate);
    const end = dayjs(l.endDate);
    return (targetDate.isSame(start, 'day') || targetDate.isAfter(start, 'day')) &&
           (targetDate.isSame(end, 'day') || targetDate.isBefore(end, 'day'));
  });

  // 這裡簡化處理：如果有請假，當日視為 8 小時請假時數 (實際應對接班表)
  return activeLeaves.length > 0 ? 8 : 0;
}
