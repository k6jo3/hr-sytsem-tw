import type { UserDto } from '../api/AuthTypes';
import type { UserProfile } from '../model/UserProfile';

/**
 * User ViewModel Factory
 * 將 API DTO 轉換為前端 ViewModel
 */
export class UserViewModelFactory {
  /**
   * Convert UserDto to UserProfile ViewModel
   */
  static createFromDTO(dto: UserDto): UserProfile {
    return {
      id: dto.id,
      username: dto.username,
      email: dto.email,
      fullName: `${dto.first_name} ${dto.last_name}`,
      roles: dto.role_list,
      displayRoles: this.mapRoles(dto.role_list),
      isAdmin: dto.role_list.includes('ADMIN'),
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      displayStatus: this.mapStatusLabel(dto.status),
      avatarUrl: dto.avatar_url,
    };
  }

  /**
   * Batch convert DTOs to ViewModels
   */
  static createListFromDTO(dtos: UserDto[]): UserProfile[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * Batch convert DTOs to ViewModels (alternative method name)
   */
  static createListFromDTOs(dtos: UserDto[]): UserProfile[] {
    return this.createListFromDTO(dtos);
  }

  /**
   * Map roles to display text
   */
  private static mapRoles(roles: string[]): string {
    if (roles.length === 0) return '無';

    const roleMap: Record<string, string> = {
      ADMIN: '管理員',
      EMPLOYEE: '員工',
      HR_MANAGER: 'HR主管',
      MANAGER: '主管',
    };

    return roles.map(role => roleMap[role] ?? role).join(', ');
  }

  /**
   * Map status code to display label
   */
  private static mapStatusLabel(status: UserDto['status']): string {
    const statusMap: Record<UserDto['status'], string> = {
      ACTIVE: '啟用',
      INACTIVE: '停用',
      LOCKED: '鎖定',
      DELETED: '已刪除',
    };
    return statusMap[status] ?? '未知';
  }

  /**
   * Map status code to color
   */
  private static mapStatusColor(status: UserDto['status']): string {
    const colorMap: Record<UserDto['status'], string> = {
      ACTIVE: 'success',
      INACTIVE: 'default',
      LOCKED: 'error',
      DELETED: 'default',
    };
    return colorMap[status] ?? 'default';
  }
}

export default UserViewModelFactory;
