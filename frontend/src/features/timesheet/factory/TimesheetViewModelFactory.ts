import type { TimesheetDto } from '../api/TimesheetTypes';
import type { TimesheetViewModel } from '../model/TimesheetViewModel';

/**
 * Timesheet ViewModel Factory (工時管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class TimesheetViewModelFactory {
  /**
   * 將 TimesheetDto 轉換為 TimesheetViewModel
   */
  static createFromDTO(dto: TimesheetDto): TimesheetViewModel {
    return {
      id: dto.id,
      // TODO: Map additional properties
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: TimesheetDto[]): TimesheetViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }
}
