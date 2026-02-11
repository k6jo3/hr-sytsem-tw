import type {
    ChangeType,
    EnrollmentDto,
    EnrollmentHistoryDto,
    EnrollmentStatus,
    InsuranceFeesDto,
    InsuranceType,
    MyInsuranceInfoDto
} from '../api/InsuranceTypes';
import type {
    EnrollmentHistoryViewModel,
    EnrollmentViewModel,
    InsuranceFeesViewModel,
    MyInsuranceInfoViewModel
} from '../model/InsuranceViewModel';

/**
 * Insurance ViewModel Factory
 * 負責將 DTO 轉換為前端使用的 ViewModel
 */
export class InsuranceViewModelFactory {
  /**
   * 轉換加退保記錄
   */
  static createEnrollmentViewModel(dto: EnrollmentDto): EnrollmentViewModel {
    return {
      id: dto.enrollment_id,
      employeeId: dto.employee_id,
      employeeName: dto.employee_name,
      insuranceUnitName: dto.insurance_unit_name,
      insuranceType: dto.insurance_type,
      insuranceTypeLabel: this.mapInsuranceType(dto.insurance_type),
      insuranceTypeColor: this.mapInsuranceTypeColor(dto.insurance_type),
      enrollDate: dto.enroll_date,
      enrollDateDisplay: dto.enroll_date.replace(/-/g, '/'),
      withdrawDate: dto.withdraw_date,
      withdrawDateDisplay: dto.withdraw_date?.replace(/-/g, '/'),
      monthlySalary: dto.monthly_salary,
      monthlySalaryDisplay: `$${dto.monthly_salary.toLocaleString()}`,
      levelNumber: dto.level_number,
      levelDisplay: `第 ${dto.level_number} 級`,
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      isActive: dto.status === 'ACTIVE',
      isPending: dto.status === 'PENDING',
      isWithdrawn: dto.status === 'WITHDRAWN',
      isReported: dto.is_reported || false
    };
  }

  /**
   * 轉換保費計費結果
   */
  static createFeesViewModel(dto: InsuranceFeesDto): InsuranceFeesViewModel {
    const laborEmp = dto.labor_employee || 0;
    const laborComp = dto.labor_employer || 0;
    const healthEmp = dto.health_employee || 0;
    const healthComp = dto.health_employer || 0;
    const pensionComp = dto.pension_employer || 0;
    const totalEmp = dto.total_employee || (laborEmp + healthEmp);
    const totalComp = dto.total_employer || (laborComp + healthComp + pensionComp);

    return {
      laborEmployee: laborEmp,
      laborEmployer: laborComp,
      healthEmployee: healthEmp,
      healthEmployer: healthComp,
      pensionEmployer: pensionComp,
      totalEmployee: totalEmp,
      totalEmployer: totalComp,
      laborEmployeeDisplay: `$${laborEmp.toLocaleString()}`,
      laborEmployerDisplay: `$${laborComp.toLocaleString()}`,
      healthEmployeeDisplay: `$${healthEmp.toLocaleString()}`,
      healthEmployerDisplay: `$${healthComp.toLocaleString()}`,
      pensionEmployerDisplay: `$${pensionComp.toLocaleString()}`,
      totalEmployeeDisplay: `$${totalEmp.toLocaleString()}`,
      totalEmployerDisplay: `$${totalComp.toLocaleString()}`,
      grandTotalDisplay: `$${(totalEmp + totalComp).toLocaleString()}`
    };
  }

  /**
   * 轉換投保歷程
   */
  static createHistoryViewModel(dto: EnrollmentHistoryDto): EnrollmentHistoryViewModel {
    return {
      historyId: dto.history_id,
      changeDate: dto.change_date,
      changeDateDisplay: dto.change_date.replace(/-/g, '/'),
      changeType: dto.change_type,
      changeTypeLabel: this.mapChangeTypeLabel(dto.change_type),
      changeTypeColor: this.mapChangeTypeColor(dto.change_type),
      insuranceTypeLabel: this.mapInsuranceType(dto.insurance_type),
      monthlySalaryDisplay: `$${dto.monthly_salary.toLocaleString()}`,
      levelDisplay: `第 ${dto.level_number} 級`,
      reason: dto.reason,
      operatorName: dto.operator_name
    };
  }

  /**
   * 轉換個人保險資訊 (ESS)
   */
  static createMyInsuranceInfoViewModel(dto: MyInsuranceInfoDto): MyInsuranceInfoViewModel {
    const enrollments = (dto.enrollments || []).map(e => this.createEnrollmentViewModel(e));
    const activeEnrollment = enrollments.find(e => e.isActive);

    return {
      employeeName: dto.employee_name,
      employeeCode: dto.employee_code,
      unitName: dto.unit_name,
      enrollments: enrollments,
      fees: this.createFeesViewModel(dto.fees),
      history: (dto.history || []).map(h => this.createHistoryViewModel(h)),
      hasActiveEnrollment: dto.has_active_enrollment,
      statusMessage: dto.has_active_enrollment ? '正常投保中' : '目前無有效投保項目',
      statusType: dto.has_active_enrollment ? 'success' : 'warning',
      currentEnrollDate: activeEnrollment?.enrollDateDisplay,
      currentSalaryDisplay: activeEnrollment?.monthlySalaryDisplay,
      currentLevelDisplay: activeEnrollment?.levelDisplay
    };
  }

  /**
   * 轉換列表
   */
  static createEnrollmentList(dtos: EnrollmentDto[]): EnrollmentViewModel[] {
    return (dtos || []).map(dto => this.createEnrollmentViewModel(dto));
  }

  private static mapInsuranceType(type: InsuranceType): string {
    const map: Record<InsuranceType, string> = {
      LABOR: '勞工保險',
      HEALTH: '全民健保',
      PENSION: '勞退提撥'
    };
    return map[type] || type;
  }

  private static mapInsuranceTypeColor(type: InsuranceType): string {
    const map: Record<InsuranceType, string> = {
      LABOR: 'blue',
      HEALTH: 'green',
      PENSION: 'orange'
    };
    return map[type] || 'default';
  }

  private static mapStatusLabel(status: EnrollmentStatus): string {
    const map: Record<EnrollmentStatus, string> = {
      PENDING: '審核中',
      ACTIVE: '投保中',
      WITHDRAWN: '已退保'
    };
    return map[status] || status;
  }

  private static mapStatusColor(status: EnrollmentStatus): string {
    const map: Record<EnrollmentStatus, string> = {
      PENDING: 'processing',
      ACTIVE: 'success',
      WITHDRAWN: 'default'
    };
    return map[status] || 'default';
  }

  private static mapChangeTypeLabel(type: ChangeType): string {
    const map: Record<ChangeType, string> = {
      ENROLL: '加保',
      WITHDRAW: '退保',
      ADJUST_LEVEL: '調整級距'
    };
    return map[type] || type;
  }

  private static mapChangeTypeColor(type: ChangeType): string {
    const map: Record<ChangeType, string> = {
      ENROLL: 'green',
      WITHDRAW: 'red',
      ADJUST_LEVEL: 'blue'
    };
    return map[type] || 'default';
  }
}
