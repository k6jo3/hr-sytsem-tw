import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import type {
    AdjustLevelRequest,
    AdjustLevelResponse,
    BatchAdjustLevelsRequest,
    BatchAdjustLevelsResponse,
    CalculateFeesRequest,
    CalculateFeesResponse,
    CreateEnrollmentRequest,
    CreateEnrollmentResponse,
    EnrollmentDto,
    EnrollmentHistoryDto,
    GetEnrollmentsRequest,
    GetEnrollmentsResponse,
    GetLevelsRequest,
    GetLevelsResponse,
    GetMyInsuranceResponse,
    InsuranceFeesDto,
    InsuranceLevelDto,
    InsuranceType,
    WithdrawEnrollmentRequest,
    WithdrawEnrollmentResponse,
} from './InsuranceTypes';
import { MockInsuranceApi } from './MockInsuranceApi';

// ========== Response Adapters ==========
// 後端 camelCase → 前端 snake_case

/**
 * 後端 EnrollmentDetailResponse → 前端 EnrollmentDto
 */
function adaptEnrollmentDto(raw: any): EnrollmentDto {
  return {
    enrollment_id: raw.enrollmentId ?? raw.enrollment_id ?? '',
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    employee_name: raw.employeeName ?? raw.employee_name ?? '',
    insurance_unit_id: raw.insuranceUnitId ?? raw.insurance_unit_id ?? '',
    insurance_unit_name: raw.insuranceUnitName ?? raw.insurance_unit_name ?? '',
    insurance_type: (raw.insuranceType ?? raw.insurance_type ?? 'LABOR') as InsuranceType,
    enroll_date: raw.enrollDate ?? raw.enroll_date ?? '',
    withdraw_date: raw.withdrawDate ?? raw.withdraw_date,
    monthly_salary: raw.monthlySalary ?? raw.monthly_salary ?? 0,
    level_number: raw.levelNumber ?? raw.level_number ?? 0,
    status: guardEnum('enrollment.status', raw.status, ['ACTIVE', 'WITHDRAWN', 'PENDING'] as const, 'ACTIVE'),
    is_reported: raw.isReported ?? raw.is_reported ?? false,
    reported_at: raw.reportedAt ?? raw.reported_at,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/**
 * 後端 FeeCalculationResponse → 前端 InsuranceFeesDto
 */
function adaptFeesResponse(raw: any): InsuranceFeesDto {
  return {
    labor_employee: raw.laborEmployeeFee ?? raw.labor_employee ?? 0,
    labor_employer: raw.laborEmployerFee ?? raw.labor_employer ?? 0,
    health_employee: raw.healthEmployeeFee ?? raw.health_employee ?? 0,
    health_employer: raw.healthEmployerFee ?? raw.health_employer ?? 0,
    pension_employer: raw.pensionEmployerFee ?? raw.pension_employer ?? 0,
    total_employee: raw.totalEmployeeFee ?? raw.total_employee ?? 0,
    total_employer: raw.totalEmployerFee ?? raw.total_employer ?? 0,
  };
}

/**
 * 後端 EnrollmentHistoryItem → 前端 EnrollmentHistoryDto
 */
function adaptHistoryDto(raw: any): EnrollmentHistoryDto {
  return {
    history_id: raw.historyId ?? raw.history_id ?? '',
    change_date: raw.changeDate ?? raw.change_date ?? '',
    change_type: raw.changeType ?? raw.change_type ?? 'ENROLL',
    insurance_type: (raw.insuranceType ?? raw.insurance_type ?? 'LABOR') as InsuranceType,
    monthly_salary: raw.monthlySalary ?? raw.monthly_salary ?? 0,
    level_number: raw.levelNumber ?? raw.level_number ?? 0,
    reason: raw.reason ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

/**
 * 後端 InsuranceLevel (domain object) → 前端 InsuranceLevelDto
 * domain 的 id 是 LevelId value object: { value: "uuid" }
 */
function adaptLevelDto(raw: any): InsuranceLevelDto {
  const levelId = typeof raw.id === 'object' ? raw.id?.value : (raw.id ?? raw.levelId ?? raw.level_id ?? '');
  return {
    level_id: levelId,
    insurance_type: (raw.insuranceType ?? raw.insurance_type ?? 'LABOR') as InsuranceType,
    level_number: raw.levelNumber ?? raw.level_number ?? 0,
    monthly_salary: raw.monthlySalary ?? raw.monthly_salary ?? 0,
    labor_employee_rate: raw.laborEmployeeRate ?? raw.labor_employee_rate,
    labor_employer_rate: raw.laborEmployerRate ?? raw.labor_employer_rate,
    health_employee_rate: raw.healthEmployeeRate ?? raw.health_employee_rate,
    health_employer_rate: raw.healthEmployerRate ?? raw.health_employer_rate,
    pension_employer_rate: raw.pensionEmployerRate ?? raw.pension_employer_rate,
    effective_date: raw.effectiveDate ?? raw.effective_date ?? '',
    end_date: raw.endDate ?? raw.end_date,
    is_active: raw.active ?? raw.isActive ?? raw.is_active ?? true,
  };
}

/**
 * Insurance API (保險管理 API)
 * Domain Code: HR05
 */
export class InsuranceApi {
  private static readonly BASE_PATH = '/insurance';

  /**
   * GET /api/v1/insurance/my - 查詢我的保險資訊 (ESS)
   * 後端回傳 MyInsuranceDetailResponse，前端期望 { insurance_info: MyInsuranceInfoDto }
   */
  static async getMyInsurance(): Promise<GetMyInsuranceResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getMyInsurance();
    }
    const raw: any = await apiClient.get(`${this.BASE_PATH}/my`);

    // 後端現在回傳 MyInsuranceDetailResponse（非 list）
    const enrollments = (raw.enrollments ?? []).map(adaptEnrollmentDto);
    const history = (raw.history ?? []).map(adaptHistoryDto);
    const first = enrollments[0];

    return {
      insurance_info: {
        employee_id: first?.employee_id ?? '',
        employee_name: raw.employeeName ?? raw.employee_name ?? first?.employee_name ?? '',
        employee_code: '',
        unit_name: raw.unitName ?? raw.unit_name ?? first?.insurance_unit_name ?? '',
        enrollments,
        fees: raw.fees ? adaptFeesResponse(raw.fees) : {
          labor_employee: 0,
          labor_employer: 0,
          health_employee: 0,
          health_employer: 0,
          pension_employer: 0,
          total_employee: 0,
          total_employer: 0,
        },
        history,
        has_active_enrollment: enrollments.some((e: any) => e.status === 'ACTIVE'),
      },
    };
  }

  /**
   * GET /api/v1/insurance/enrollments - 查詢加退保記錄
   * 後端 PageResponse: { items, totalElements, page(1-indexed), size }
   * 前端: { enrollments, total, page(1-indexed), page_size }
   */
  static async getEnrollments(params: GetEnrollmentsRequest): Promise<GetEnrollmentsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getEnrollments(params);
    }
    // 轉換參數：snake_case → camelCase
    const backendParams: any = {};
    if (params.employee_id) backendParams.employeeId = params.employee_id;
    if (params.insurance_type) backendParams.insuranceType = params.insurance_type;
    if (params.status) backendParams.status = params.status;
    if (params.start_date) backendParams.startDate = params.start_date;
    if (params.end_date) backendParams.endDate = params.end_date;
    if (params.page != null) backendParams.page = params.page;
    if (params.page_size) backendParams.size = params.page_size;

    const raw: any = await apiClient.get(`${this.BASE_PATH}/enrollments`, { params: backendParams });
    const enrollments = (raw.items ?? raw.enrollments ?? []).map(adaptEnrollmentDto);
    return {
      enrollments,
      total: raw.totalElements ?? raw.total ?? enrollments.length,
      page: raw.page ?? 1,
      page_size: raw.size ?? raw.pageSize ?? params.page_size ?? 20,
    };
  }

  /**
   * GET /api/v1/insurance/enrollments/active - 查詢員工有效加保記錄
   * 後端回傳 List<EnrollmentDetailResponse>，包裝為前端分頁格式
   */
  static async getActiveEnrollments(): Promise<GetEnrollmentsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getActiveEnrollments();
    }
    const rawList: any = await apiClient.get(`${this.BASE_PATH}/enrollments/active`);
    const list = Array.isArray(rawList) ? rawList : [];
    const enrollments = list.map(adaptEnrollmentDto);
    return {
      enrollments,
      total: enrollments.length,
      page: 1,
      page_size: enrollments.length || 20,
    };
  }

  /**
   * POST /api/v1/insurance/enrollments - 手動加保
   * 後端 Request: { employeeId, insuranceUnitId, monthlySalary, enrollDate }
   * 後端 Response: EnrollmentDetailResponse (單筆)
   */
  static async createEnrollment(request: CreateEnrollmentRequest): Promise<CreateEnrollmentResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.createEnrollment(request);
    }
    const backendRequest: any = {
      employeeId: request.employee_id,
      insuranceUnitId: request.insurance_unit_id,
      monthlySalary: request.monthly_salary,
      enrollDate: request.enroll_date,
    };
    const raw: any = await apiClient.post(`${this.BASE_PATH}/enrollments`, backendRequest);
    return {
      enrollment_ids: [raw.enrollmentId ?? raw.enrollment_id ?? ''],
      message: raw.message ?? '加保成功',
    };
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/withdraw - 退保
   * 後端 Request: { withdrawDate, reason }
   */
  static async withdrawEnrollment(
    id: string,
    request: WithdrawEnrollmentRequest
  ): Promise<WithdrawEnrollmentResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.withdrawEnrollment(id, request);
    }
    const backendRequest = {
      withdrawDate: request.withdraw_date,
      reason: request.reason,
    };
    const raw: any = await apiClient.put(
      `${this.BASE_PATH}/enrollments/${id}/withdraw`,
      backendRequest
    );
    return {
      enrollment_id: raw.enrollmentId ?? raw.enrollment_id ?? id,
      message: raw.message ?? '退保成功',
    };
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/adjust-level - 調整投保級距
   * 後端 Request: { newMonthlySalary, effectiveDate, reason }
   */
  static async adjustLevel(
    id: string,
    request: AdjustLevelRequest
  ): Promise<AdjustLevelResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.adjustLevel(id, request);
    }
    const backendRequest = {
      newMonthlySalary: request.new_monthly_salary,
      effectiveDate: request.effective_date,
      reason: request.reason,
    };
    const raw: any = await apiClient.put(
      `${this.BASE_PATH}/enrollments/${id}/adjust-level`,
      backendRequest
    );
    return {
      enrollment_id: raw.enrollmentId ?? raw.enrollment_id ?? id,
      new_level_number: raw.levelNumber ?? raw.level_number ?? 0,
      new_monthly_salary: raw.monthlySalary ?? raw.monthly_salary ?? 0,
      message: raw.message ?? '調整成功',
    };
  }

  /**
   * POST /api/v1/insurance/fees/calculate - 計算保費
   * 後端 Request: { monthlySalary, selfContributionRate }
   * 後端 Response: FeeCalculationResponse → 前端 { fees, level_number }
   */
  static async calculateFees(request: CalculateFeesRequest): Promise<CalculateFeesResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.calculateFees(request);
    }
    const backendRequest = {
      monthlySalary: request.monthly_salary,
    };
    const raw: any = await apiClient.post(`${this.BASE_PATH}/fees/calculate`, backendRequest);
    return {
      fees: {
        labor_employee: raw.laborEmployeeFee ?? raw.labor_employee_fee ?? 0,
        labor_employer: raw.laborEmployerFee ?? raw.labor_employer_fee ?? 0,
        health_employee: raw.healthEmployeeFee ?? raw.health_employee_fee ?? 0,
        health_employer: raw.healthEmployerFee ?? raw.health_employer_fee ?? 0,
        pension_employer: raw.pensionEmployerFee ?? raw.pension_employer_fee ?? 0,
        total_employee: raw.totalEmployeeFee ?? raw.total_employee_fee ?? 0,
        total_employer: raw.totalEmployerFee ?? raw.total_employer_fee ?? 0,
      },
      level_number: raw.levelNumber ?? raw.level_number ?? 0,
    };
  }

  /**
   * GET /api/v1/insurance/levels - 查詢投保級距
   * 後端回傳 List<InsuranceLevel> (domain object)
   * 前端期望 { levels, total }
   */
  static async getLevels(params: GetLevelsRequest): Promise<GetLevelsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getLevels(params);
    }
    const backendParams: any = {};
    if (params.insurance_type) backendParams.insuranceType = params.insurance_type;
    if (params.effective_date) backendParams.effectiveDate = params.effective_date;

    const rawList: any = await apiClient.get(`${this.BASE_PATH}/levels`, { params: backendParams });
    const list = Array.isArray(rawList) ? rawList : [];
    const levels = list.map(adaptLevelDto);
    return {
      levels,
      total: levels.length,
    };
  }

  /**
   * POST /api/v1/insurance/levels/batch-adjust - 批量調整投保級距
   */
  static async batchAdjustLevels(request: BatchAdjustLevelsRequest): Promise<BatchAdjustLevelsResponse> {
    const backendRequest = {
      insuranceTypes: request.insurance_types,
      adjustmentAmount: request.adjustment_amount,
      effectiveDate: request.effective_date,
      newHighestLevelSalary: request.new_highest_level_salary,
    };
    const raw: any = await apiClient.post(`${this.BASE_PATH}/levels/batch-adjust`, backendRequest);
    return {
      old_levels_deactivated: raw.oldLevelsDeactivated ?? raw.old_levels_deactivated ?? 0,
      new_levels_created: raw.newLevelsCreated ?? raw.new_levels_created ?? 0,
      message: raw.message ?? '調整完成',
    };
  }
}
