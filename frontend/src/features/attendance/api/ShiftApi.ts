import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import type {
    CreateShiftRequest,
    ShiftDto,
    UpdateShiftRequest,
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * 後端 shiftType → 前端 shiftType 映射（讀取用）
 */
const SHIFT_TYPE_MAP: Record<string, ShiftDto['shiftType']> = {
  REGULAR: 'STANDARD',
  FLEXIBLE: 'FLEXIBLE',
  SHIFT: 'ROTATING',
};

/**
 * 前端 shiftType → 後端 shiftType 映射（寫入用）
 */
const REVERSE_SHIFT_TYPE_MAP: Record<string, string> = {
  'STANDARD': 'REGULAR',
  'FLEXIBLE': 'FLEXIBLE',
  'ROTATING': 'SHIFT',
};

function adaptShiftDto(raw: any): ShiftDto {
  return {
    ...raw,
    shiftType: guardEnum('shift.shiftType', SHIFT_TYPE_MAP[raw.shiftType] ?? raw.shiftType, ['STANDARD', 'FLEXIBLE', 'ROTATING'] as const, 'STANDARD'),
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
   * 送出前將前端 shiftType 反向映射為後端 enum 值
   */
  static async createShift(request: CreateShiftRequest): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.createShift(request);
    const payload = {
      ...request,
      shiftType: REVERSE_SHIFT_TYPE_MAP[request.shiftType] ?? request.shiftType,
    };
    return apiClient.post(this.BASE_PATH, payload);
  }

  /**
   * 更新班別
   * 送出前將前端 shiftType 反向映射為後端 enum 值
   */
  static async updateShift(shiftId: string, request: UpdateShiftRequest): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.updateShift(shiftId, request);
    const payload = {
      ...request,
      shiftType: request.shiftType
        ? (REVERSE_SHIFT_TYPE_MAP[request.shiftType] ?? request.shiftType)
        : undefined,
    };
    return apiClient.put(`${this.BASE_PATH}/${shiftId}`, payload);
  }

  /**
   * 停用班別
   */
  static async deactivateShift(shiftId: string): Promise<any> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.deleteShift(shiftId);
    return apiClient.put(`${this.BASE_PATH}/${shiftId}/deactivate`, {});
  }
}
