import type { NotificationDto } from '../api/NotificationTypes';
import type { NotificationViewModel } from '../model/NotificationViewModel';

/**
 * Notification ViewModel Factory (通知服務視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class NotificationViewModelFactory {
  /**
   * 將 NotificationDto 轉換為 NotificationViewModel
   */
  static createFromDTO(dto: NotificationDto): NotificationViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: NotificationDto[]): NotificationViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
