import type { PayrollDto } from '../api/PayrollTypes';
import type { PayrollViewModel } from '../model/PayrollViewModel';

/**
 * Payroll ViewModel Factory (薪資管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class PayrollViewModelFactory {
  /**
   * 將 PayrollDto 轉換為 PayrollViewModel
   */
  static createFromDTO(dto: PayrollDto): PayrollViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: PayrollDto[]): PayrollViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
