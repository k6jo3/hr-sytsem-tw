import { CustomerDto } from '../api/ProjectTypes';
import { CustomerViewModel } from '../model/CustomerViewModel';

/**
 * 客戶 ViewModel 工廠
 */
export class CustomerViewModelFactory {
  /**
   * 將 DTO 轉換為 ViewModel
   */
  static createFromDTO(dto: CustomerDto): CustomerViewModel {
    return {
      id: dto.id,
      customerCode: dto.customer_code,
      customerName: dto.customer_name,
      taxId: dto.tax_id,
      industry: dto.industry,
      email: dto.email,
      phoneNumber: dto.phone_number,
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      createdAt: dto.created_at,
    };
  }

  /**
   * 將 DTO 列表轉換為 ViewModel 列表
   */
  static createListFromDTOs(dtos: CustomerDto[]): CustomerViewModel[] {
    if (!dtos) return [];
    return dtos.map(dto => this.createFromDTO(dto));
  }

  /**
   * 狀態文字對應
   */
  private static mapStatusLabel(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return '啟用';
      case 'INACTIVE':
        return '停用';
      default:
        return '未知';
    }
  }

  /**
   * 狀態顏色對應 (Ant Design Tag colors)
   */
  private static mapStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'green';
      case 'INACTIVE':
        return 'red';
      default:
        return 'default';
    }
  }
}
