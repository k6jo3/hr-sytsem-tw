import type { RecruitmentDto } from '../api/RecruitmentTypes';
import type { RecruitmentViewModel } from '../model/RecruitmentViewModel';

/**
 * Recruitment ViewModel Factory (招募管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class RecruitmentViewModelFactory {
  /**
   * 將 RecruitmentDto 轉換為 RecruitmentViewModel
   */
  static createFromDTO(dto: RecruitmentDto): RecruitmentViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: RecruitmentDto[]): RecruitmentViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
