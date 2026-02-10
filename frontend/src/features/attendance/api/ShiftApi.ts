import { apiClient } from '@shared/api';
import type {
    CreateShiftRequest,
    ShiftDto,
    UpdateShiftRequest,
} from './AttendanceTypes';

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
    return apiClient.get(this.BASE_PATH, { params });
  }

  /**
   * 建立班別
   */
  static async createShift(request: CreateShiftRequest): Promise<any> {
    return apiClient.post(this.BASE_PATH, request);
  }

  /**
   * 更新班別
   */
  static async updateShift(shiftId: string, request: UpdateShiftRequest): Promise<any> {
    return apiClient.put(`${this.BASE_PATH}/${shiftId}`, request);
  }

  /**
   * 停用班別
   */
  static async deactivateShift(shiftId: string): Promise<any> {
    return apiClient.put(`${this.BASE_PATH}/${shiftId}/deactivate`, {});
  }
}
