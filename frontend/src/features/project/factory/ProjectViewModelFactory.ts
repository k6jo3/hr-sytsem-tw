import type { ProjectDto } from '../api/ProjectTypes';
import type { ProjectViewModel } from '../model/ProjectViewModel';

/**
 * Project ViewModel Factory (專案管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class ProjectViewModelFactory {
  /**
   * 將 ProjectDto 轉換為 ProjectViewModel
   */
  static createFromDTO(dto: ProjectDto): ProjectViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: ProjectDto[]): ProjectViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
