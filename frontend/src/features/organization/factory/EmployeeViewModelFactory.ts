import type { EmployeeDto } from '../api/OrganizationTypes';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';

/**
 * Employee ViewModel Factory
 * 將 API 回傳的員工 DTO 轉換為前端 ViewModel
 */
export class EmployeeViewModelFactory {
  /**
   * 將單一員工 DTO 轉換為 ViewModel
   */
  static createFromDTO(dto: EmployeeDto): EmployeeViewModel {
    return {
      id: dto.id,
      employeeNumber: dto.employee_number,
      fullName: dto.full_name,
      email: dto.email,
      phone: dto.phone,
      departmentName: dto.department_name,
      position: dto.position,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      hireDate: dto.hire_date,
    };
  }

  /**
   * 批量轉換員工 DTO 列表
   */
  static createListFromDTOs(dtos: EmployeeDto[]): EmployeeViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * 將狀態代碼對應為中文標籤
   */
  private static mapStatusLabel(status: EmployeeDto['status']): string {
    const statusMap: Record<EmployeeDto['status'], string> = {
      ACTIVE: '在職',
      INACTIVE: '停用',
      ON_LEAVE: '留職停薪',
      TERMINATED: '離職',
      PROBATION: '試用期',
    };
    return statusMap[status];
  }

  /**
   * 將狀態對應為顏色 (Ant Design Tag color)
   */
  private static mapStatusColor(status: EmployeeDto['status']): string {
    const colorMap: Record<EmployeeDto['status'], string> = {
      ACTIVE: 'success',
      INACTIVE: 'default',
      ON_LEAVE: 'warning',
      TERMINATED: 'error',
      PROBATION: 'processing',
    };
    return colorMap[status];
  }
}
