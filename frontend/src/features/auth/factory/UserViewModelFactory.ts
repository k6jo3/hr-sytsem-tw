import type { UserDto } from '../api/AuthTypes';
import type { UserViewModel } from '../model/UserProfile';

/**
 * 使用者 ViewModel Factory
 * 將 API 回傳的 DTO 轉換為前端顯示用的 ViewModel
 *
 * @description
 * 根據規範，禁止在 Component 內直接使用 API 回傳的原始資料，
 * 必須透過 Factory 轉換為 ViewModel。
 */
export class UserViewModelFactory {
  /**
   * 將 API DTO 轉換為 ViewModel
   * @param dto - API 回傳的使用者資料
   * @returns 前端顯示用的 ViewModel
   */
  static createFromDTO(dto: UserDto): UserViewModel {
    return {
      id: dto.id,
      username: dto.username,
      fullName: `${dto.first_name} ${dto.last_name}`.trim(),
      email: dto.email,
      isAdmin: dto.role_list.includes('ADMIN'),
      roles: dto.role_list,
      displayStatus: this.mapStatusToDisplay(dto.status),
      avatarUrl: dto.avatar_url,
    };
  }

  /**
   * 批量轉換 DTO 陣列為 ViewModel 陣列
   * @param dtos - DTO 陣列
   * @returns ViewModel 陣列
   */
  static createListFromDTO(dtos: UserDto[]): UserViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * 將狀態碼對應為顯示文字
   * @param status - 狀態碼
   * @returns 顯示文字
   */
  private static mapStatusToDisplay(
    status: 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'DELETED'
  ): string {
    const statusMap: Record<string, string> = {
      ACTIVE: '在職',
      INACTIVE: '停用',
      LOCKED: '鎖定',
      DELETED: '已刪除',
    };
    return statusMap[status] ?? '未知';
  }
}

export default UserViewModelFactory;
