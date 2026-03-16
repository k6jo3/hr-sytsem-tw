import type { AttendanceRecordDto, AttendanceStatus, CheckType } from '../api/AttendanceTypes';
import type { AttendanceRecordViewModel, TodayAttendanceSummary } from '../model/AttendanceRecordViewModel';

/**
 * Attendance ViewModel Factory
 * 將 API 回傳的考勤 DTO 轉換為前端 ViewModel
 */
export class AttendanceViewModelFactory {
  /**
   * 將單一考勤記錄 DTO 轉換為 ViewModel
   */
  static createFromDTO(dto: AttendanceRecordDto): AttendanceRecordViewModel {
    return {
      id: dto.id,
      employeeName: dto.employeeName,
      checkTypeLabel: this.mapCheckTypeLabel(dto.checkType),
      checkTypeColor: this.mapCheckTypeColor(dto.checkType),
      checkTime: dto.checkTime,
      checkTimeDisplay: this.formatTimeDisplay(dto.checkTime),
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      address: dto.address,
      isNormal: dto.status === 'NORMAL',
    };
  }

  /**
   * 批量轉換考勤記錄 DTO 列表
   */
  static createListFromDTOs(dtos: AttendanceRecordDto[]): AttendanceRecordViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * 建立今日考勤摘要
   */
  static createTodaySummary(
    records: AttendanceRecordDto[],
    hasCheckedIn: boolean,
    hasCheckedOut: boolean,
    totalWorkHours?: number
  ): TodayAttendanceSummary {
    return {
      hasCheckedIn,
      hasCheckedOut,
      totalWorkHours,
      records: this.createListFromDTOs(records),
      canCheckIn: !hasCheckedIn,
      canCheckOut: hasCheckedIn && !hasCheckedOut,
    };
  }

  /**
   * 將打卡類型對應為中文標籤
   */
  private static mapCheckTypeLabel(checkType: CheckType): string {
    const labelMap: Record<CheckType, string> = {
      CHECK_IN: '上班打卡',
      CHECK_OUT: '下班打卡',
      BREAK_OUT: '外出',
      BREAK_IN: '返回',
    };
    return labelMap[checkType];
  }

  /**
   * 將打卡類型對應為顏色
   */
  private static mapCheckTypeColor(checkType: CheckType): string {
    const colorMap: Record<CheckType, string> = {
      CHECK_IN: 'blue',
      CHECK_OUT: 'green',
      BREAK_OUT: 'orange',
      BREAK_IN: 'cyan',
    };
    return colorMap[checkType];
  }

  /**
   * 將考勤狀態對應為中文標籤
   */
  private static mapStatusLabel(status: AttendanceStatus): string {
    const labelMap: Record<AttendanceStatus, string> = {
      NORMAL: '正常',
      LATE: '遲到',
      EARLY_LEAVE: '早退',
      ABSENT: '曠職',
    };
    return labelMap[status];
  }

  /**
   * 將考勤狀態對應為顏色
   */
  private static mapStatusColor(status: AttendanceStatus): string {
    const colorMap: Record<AttendanceStatus, string> = {
      NORMAL: 'success',
      LATE: 'warning',
      EARLY_LEAVE: 'error',
      ABSENT: 'error',
    };
    return colorMap[status];
  }

  /**
   * 格式化時間顯示為 HH:mm
   */
  private static formatTimeDisplay(isoTime: string): string {
    if (!isoTime) return '--:--';
    const date = new Date(isoTime);
    if (isNaN(date.getTime())) return '--:--';
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
}
