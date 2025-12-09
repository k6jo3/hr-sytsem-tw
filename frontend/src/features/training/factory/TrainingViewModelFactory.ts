import type { TrainingDto } from '../api/TrainingTypes';
import type { TrainingViewModel } from '../model/TrainingViewModel';

/**
 * Training ViewModel Factory (訓練管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class TrainingViewModelFactory {
  /**
   * 將 TrainingDto 轉換為 TrainingViewModel
   */
  static createFromDTO(dto: TrainingDto): TrainingViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: TrainingDto[]): TrainingViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
