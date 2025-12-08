import type { DocumentDto } from '../api/DocumentTypes';
import type { DocumentViewModel } from '../model/DocumentViewModel';

/**
 * Document ViewModel Factory (文件管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class DocumentViewModelFactory {
  /**
   * 將 DocumentDto 轉換為 DocumentViewModel
   */
  static createFromDTO(dto: DocumentDto): DocumentViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: DocumentDto[]): DocumentViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
