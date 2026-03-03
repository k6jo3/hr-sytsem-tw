import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    CreateShiftRequest,
    ShiftDto,
    UpdateShiftRequest,
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * 後端 shiftType → 前端 shiftType 映射
 */
const SHIFT_TYPE_MAP: Record<string, ShiftDto['shiftType']> = {
  REGULAR: 'STANDARD',
  FLEXIBLE: 'FLEXIBLE',
  SHIFT: 'ROTATING',
};

function adaptShiftDto(raw: any): ShiftDto {
  return {
    ...raw,
    shiftType: SHIFT_TYPE_MAP[raw.shiftType] ?? raw.shiftType,
  };
}

/**
 * Shift Management API (班別管理 API)
 * Domain Code: HR03
 */
export class ShiftApi {
  private static readonly BASE_PATH = '/shifts';

  /**
   * 查詢班別列表
   */
  static async getShiftList(params?: any): Promise<ShiftDto[]> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getShifts();
    const data: any[] = await apiClient.get(this.BASE_PATH, { params });
    return data.map(adaptShiftDto);
  }

  /**
   * 建立班別
   */
  static async createShift(request: CreateShiftRequest): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.createShift(request);
    return apiClient.post(this.BASE_PATH, request);
  }

  /**
   * 更新班別
   */
  static async updateShift(shiftId: string, request: UpdateShiftRequest): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.updateShift(shiftId, request);
    return apiClient.put(`${this.BASE_PATH}/${shiftId}`, request);
  }

  /**
   * 停用班別
   */
  static async deactivateShift(shiftId: string): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.deleteShift(shiftId);
    return apiClient.put(`${this.BASE_PATH}/${shiftId}/deactivate`, {});
  }
}
