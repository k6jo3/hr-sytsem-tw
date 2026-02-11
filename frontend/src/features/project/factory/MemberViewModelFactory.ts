import { ProjectMemberDto } from '../api/ProjectTypes';
import { ProjectMemberViewModel } from '../model/ProjectViewModel';

/**
 * 專案成員 ViewModel 工廠
 */
export class MemberViewModelFactory {
  /**
   * 將 DTO 轉換為 ViewModel
   */
  static createFromDTO(dto: ProjectMemberDto): ProjectMemberViewModel {
    return {
      memberId: dto.member_id,
      employeeId: dto.employee_id,
      employeeName: dto.employee_name,
      role: dto.role,
      allocatedHours: dto.allocated_hours,
      actualHours: dto.actual_hours,
      utilization: this.calculateUtilization(dto.allocated_hours, dto.actual_hours),
      joinDate: dto.join_date,
    };
  }

  /**
   * 批量轉換
   */
  static createListFromDTOs(dtos: ProjectMemberDto[]): ProjectMemberViewModel[] {
    if (!dtos) return [];
    return dtos.map(dto => this.createFromDTO(dto));
  }

  /**
   * 計算投入率
   */
  private static calculateUtilization(allocated: number, actual: number): number {
    if (allocated === 0) return 0;
    return Math.round((actual / allocated) * 100);
  }
}
