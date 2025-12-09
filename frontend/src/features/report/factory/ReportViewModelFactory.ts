import type { ReportDto } from '../api/ReportTypes';
import type { ReportViewModel } from '../model/ReportViewModel';

/**
 * Report ViewModel Factory (報表分析視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class ReportViewModelFactory {
  /**
   * 將 ReportDto 轉換為 ReportViewModel
   */
  static createFromDTO(dto: ReportDto): ReportViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: ReportDto[]): ReportViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
