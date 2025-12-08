import type { PerformanceDto } from '../api/PerformanceTypes';
import type { PerformanceViewModel } from '../model/PerformanceViewModel';

/**
 * Performance ViewModel Factory (績效管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class PerformanceViewModelFactory {
  /**
   * 將 PerformanceDto 轉換為 PerformanceViewModel
   */
  static createFromDTO(dto: PerformanceDto): PerformanceViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: PerformanceDto[]): PerformanceViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
