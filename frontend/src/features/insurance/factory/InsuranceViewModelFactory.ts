import type { InsuranceDto } from '../api/InsuranceTypes';
import type { InsuranceViewModel } from '../model/InsuranceViewModel';

/**
 * Insurance ViewModel Factory (保險管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class InsuranceViewModelFactory {
  /**
   * 將 InsuranceDto 轉換為 InsuranceViewModel
   */
  static createFromDTO(dto: InsuranceDto): InsuranceViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: InsuranceDto[]): InsuranceViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
