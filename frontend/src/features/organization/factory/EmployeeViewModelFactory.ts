import type { EmployeeDto } from '../api/OrganizationTypes';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';

/**
 * Employee ViewModel Factory (員工視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class EmployeeViewModelFactory {
  static createFromDTO(dto: EmployeeDto): EmployeeViewModel {
    return {
      id: dto.id,
      employeeNumber: dto.employee_number,
      fullName: `${dto.first_name} ${dto.last_name}`,
      email: dto.email,
      phone: dto.phone,
      departmentName: dto.department_name ?? '-',
      position: dto.position,
      statusLabel: this.getStatusLabel(dto.status),
      statusColor: this.getStatusColor(dto.status),
      hireDate: dto.hire_date,
    };
  }

  static createListFromDTOs(dtos: EmployeeDto[]): EmployeeViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  private static getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      ACTIVE: '在職',
      INACTIVE: '離職',
      ON_LEAVE: '留職停薪',
    };
    return labels[status] ?? '未知';
  }

  private static getStatusColor(status: string): string {
    const colors: Record<string, string> = {
      ACTIVE: 'success',
      INACTIVE: 'default',
      ON_LEAVE: 'warning',
    };
    return colors[status] ?? 'default';
  }
}
