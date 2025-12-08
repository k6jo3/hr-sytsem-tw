import type { AttendanceDto } from '../api/AttendanceTypes';
import type { AttendanceViewModel } from '../model/AttendanceViewModel';

/**
 * Attendance ViewModel Factory (考勤視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class AttendanceViewModelFactory {
  /**
   * 將 AttendanceDto 轉換為 AttendanceViewModel
   */
  static createFromDTO(dto: AttendanceDto): AttendanceViewModel {
    return {
      id: dto.id,
      employeeId: dto.employee_id,
      date: dto.date,
      checkInTime: dto.check_in_time,
      checkOutTime: dto.check_out_time,
      workHours: dto.work_hours,
      statusLabel: this.getStatusLabel(dto.status),
      statusColor: this.getStatusColor(dto.status),
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: AttendanceDto[]): AttendanceViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  private static getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      NORMAL: '正常',
      LATE: '遲到',
      EARLY_LEAVE: '早退',
      ABSENT: '缺勤',
    };
    return labels[status] ?? '未知';
  }

  private static getStatusColor(status: string): string {
    const colors: Record<string, string> = {
      NORMAL: 'success',
      LATE: 'warning',
      EARLY_LEAVE: 'warning',
      ABSENT: 'error',
    };
    return colors[status] ?? 'default';
  }
}
