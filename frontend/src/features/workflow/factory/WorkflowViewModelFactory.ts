import type { WorkflowDto } from '../api/WorkflowTypes';
import type { WorkflowViewModel } from '../model/WorkflowViewModel';

/**
 * Workflow ViewModel Factory (簽核流程視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class WorkflowViewModelFactory {
  /**
   * 將 WorkflowDto 轉換為 WorkflowViewModel
   */
  static createFromDTO(dto: WorkflowDto): WorkflowViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: WorkflowDto[]): WorkflowViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
