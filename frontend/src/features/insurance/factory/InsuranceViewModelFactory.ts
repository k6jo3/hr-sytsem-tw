import type {
  EnrollmentDto,
  InsuranceFeesDto,
  EnrollmentHistoryDto,
  MyInsuranceInfoDto,
  InsuranceLevelDto,
  InsuranceUnitDto,
  InsuranceType,
  EnrollmentStatus,
  ChangeType,
} from '../api/InsuranceTypes';
import type {
  EnrollmentViewModel,
  InsuranceFeesViewModel,
  EnrollmentHistoryViewModel,
  MyInsuranceInfoViewModel,
  InsuranceLevelViewModel,
  InsuranceUnitViewModel,
} from '../model/InsuranceViewModel';

/**
 * Insurance ViewModel Factory (保險管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class InsuranceViewModelFactory {
  /**
   * 將 EnrollmentDto 轉換為 EnrollmentViewModel
   */
  static createEnrollmentViewModel(dto: EnrollmentDto): EnrollmentViewModel {
    return {
      enrollmentId: dto.enrollment_id,
      employeeName: dto.employee_name,
      insuranceTypeLabel: this.mapInsuranceTypeLabel(dto.insurance_type),
      insuranceTypeColor: this.mapInsuranceTypeColor(dto.insurance_type),
      enrollDateDisplay: dto.enroll_date,
      withdrawDateDisplay: dto.withdraw_date,
      monthlySalaryDisplay: this.formatCurrency(dto.monthly_salary),
      levelDisplay: `第${dto.level_number}級`,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      isActive: dto.status === 'ACTIVE',
      isPending: dto.status === 'PENDING',
      isWithdrawn: dto.status === 'WITHDRAWN',
      isReported: dto.is_reported,
    };
  }

  /**
   * 將 InsuranceFeesDto 轉換為 InsuranceFeesViewModel
   */
  static createFeesViewModel(dto: InsuranceFeesDto): InsuranceFeesViewModel {
    const grandTotal = dto.total_employee + dto.total_employer;

    return {
      laborEmployeeDisplay: this.formatCurrency(dto.labor_employee),
      laborEmployerDisplay: this.formatCurrency(dto.labor_employer),
      healthEmployeeDisplay: this.formatCurrency(dto.health_employee),
      healthEmployerDisplay: this.formatCurrency(dto.health_employer),
      pensionEmployerDisplay: this.formatCurrency(dto.pension_employer),
      totalEmployeeDisplay: this.formatCurrency(dto.total_employee),
      totalEmployerDisplay: this.formatCurrency(dto.total_employer),
      grandTotalDisplay: this.formatCurrency(grandTotal),
    };
  }

  /**
   * 將 EnrollmentHistoryDto 轉換為 EnrollmentHistoryViewModel
   */
  static createHistoryViewModel(dto: EnrollmentHistoryDto): EnrollmentHistoryViewModel {
    return {
      historyId: dto.history_id,
      changeDateDisplay: dto.change_date,
      changeTypeLabel: this.mapChangeTypeLabel(dto.change_type),
      changeTypeColor: this.mapChangeTypeColor(dto.change_type),
      insuranceTypeLabel: this.mapInsuranceTypeLabel(dto.insurance_type),
      monthlySalaryDisplay: this.formatCurrency(dto.monthly_salary),
      levelDisplay: `第${dto.level_number}級`,
      reason: dto.reason,
      operatorName: dto.operator_name,
    };
  }

  /**
   * 將 MyInsuranceInfoDto 轉換為 MyInsuranceInfoViewModel
   */
  static createMyInsuranceInfoViewModel(dto: MyInsuranceInfoDto): MyInsuranceInfoViewModel {
    const enrollments = dto.enrollments.map((e) => this.createEnrollmentViewModel(e));
    const fees = this.createFeesViewModel(dto.fees);
    const history = dto.history.map((h) => this.createHistoryViewModel(h));

    // Find active labor insurance enrollment for convenience fields
    const activeLaborEnrollment = dto.enrollments.find(
      (e) => e.insurance_type === 'LABOR' && e.status === 'ACTIVE'
    );

    return {
      employeeName: dto.employee_name,
      employeeCode: dto.employee_code,
      unitName: dto.unit_name,
      enrollments,
      fees,
      history,
      hasActiveEnrollment: dto.has_active_enrollment,
      statusMessage: dto.has_active_enrollment ? '✅ 正常投保中' : '⚠️ 目前無投保記錄',
      statusType: dto.has_active_enrollment ? 'success' : 'warning',
      currentEnrollDate: activeLaborEnrollment?.enroll_date,
      currentSalaryDisplay: activeLaborEnrollment
        ? this.formatCurrency(activeLaborEnrollment.monthly_salary)
        : undefined,
      currentLevelDisplay: activeLaborEnrollment
        ? `第${activeLaborEnrollment.level_number}級`
        : undefined,
    };
  }

  /**
   * 將 InsuranceLevelDto 轉換為 InsuranceLevelViewModel
   */
  static createLevelViewModel(dto: InsuranceLevelDto): InsuranceLevelViewModel {
    return {
      levelId: dto.level_id,
      insuranceTypeLabel: this.mapInsuranceTypeLabel(dto.insurance_type),
      levelNumber: dto.level_number,
      monthlySalaryDisplay: this.formatCurrency(dto.monthly_salary),
      effectiveDateDisplay: dto.effective_date,
      endDateDisplay: dto.end_date,
      isActive: dto.is_active,
      laborEmployeeRateDisplay: dto.labor_employee_rate
        ? this.formatPercentage(dto.labor_employee_rate)
        : undefined,
      laborEmployerRateDisplay: dto.labor_employer_rate
        ? this.formatPercentage(dto.labor_employer_rate)
        : undefined,
      healthEmployeeRateDisplay: dto.health_employee_rate
        ? this.formatPercentage(dto.health_employee_rate)
        : undefined,
      healthEmployerRateDisplay: dto.health_employer_rate
        ? this.formatPercentage(dto.health_employer_rate)
        : undefined,
      pensionEmployerRateDisplay: dto.pension_employer_rate
        ? this.formatPercentage(dto.pension_employer_rate)
        : undefined,
    };
  }

  /**
   * 將 InsuranceUnitDto 轉換為 InsuranceUnitViewModel
   */
  static createUnitViewModel(dto: InsuranceUnitDto): InsuranceUnitViewModel {
    return {
      unitId: dto.unit_id,
      unitCode: dto.unit_code,
      unitName: dto.unit_name,
      laborInsuranceNumber: dto.labor_insurance_number,
      healthInsuranceNumber: dto.health_insurance_number,
      pensionNumber: dto.pension_number,
      isActive: dto.is_active,
      displayName: `${dto.unit_code} - ${dto.unit_name}`,
    };
  }

  /**
   * 批量轉換投保記錄
   */
  static createListFromDTOs(dtos: EnrollmentDto[]): EnrollmentViewModel[] {
    return dtos.map((dto) => this.createEnrollmentViewModel(dto));
  }

  /**
   * 格式化貨幣
   */
  private static formatCurrency(amount: number): string {
    return `$${amount.toLocaleString('en-US')}`;
  }

  /**
   * 格式化百分比
   */
  private static formatPercentage(rate: number): string {
    return `${(rate * 100).toFixed(2)}%`;
  }

  /**
   * 映射保險類型標籤
   */
  private static mapInsuranceTypeLabel(type: InsuranceType): string {
    const labelMap: Record<InsuranceType, string> = {
      LABOR: '勞保',
      HEALTH: '健保',
      PENSION: '勞退',
    };
    return labelMap[type];
  }

  /**
   * 映射保險類型顏色
   */
  private static mapInsuranceTypeColor(type: InsuranceType): string {
    const colorMap: Record<InsuranceType, string> = {
      LABOR: 'blue',
      HEALTH: 'green',
      PENSION: 'orange',
    };
    return colorMap[type];
  }

  /**
   * 映射投保狀態標籤
   */
  private static mapStatusLabel(status: EnrollmentStatus): string {
    const labelMap: Record<EnrollmentStatus, string> = {
      PENDING: '待處理',
      ACTIVE: '已加保',
      WITHDRAWN: '已退保',
    };
    return labelMap[status];
  }

  /**
   * 映射投保狀態顏色
   */
  private static mapStatusColor(status: EnrollmentStatus): string {
    const colorMap: Record<EnrollmentStatus, string> = {
      PENDING: 'warning',
      ACTIVE: 'success',
      WITHDRAWN: 'default',
    };
    return colorMap[status];
  }

  /**
   * 映射異動類型標籤
   */
  private static mapChangeTypeLabel(type: ChangeType): string {
    const labelMap: Record<ChangeType, string> = {
      ENROLL: '加保',
      WITHDRAW: '退保',
      ADJUST_LEVEL: '調整級距',
    };
    return labelMap[type];
  }

  /**
   * 映射異動類型顏色
   */
  private static mapChangeTypeColor(type: ChangeType): string {
    const colorMap: Record<ChangeType, string> = {
      ENROLL: 'success',
      WITHDRAW: 'error',
      ADJUST_LEVEL: 'warning',
    };
    return colorMap[type];
  }
}
