import type {
    AdjustLevelResponse,
    CalculateFeesResponse,
    CreateEnrollmentResponse,
    EnrollmentDto,
    GetEnrollmentsResponse,
    GetLevelsResponse,
    GetMyInsuranceResponse,
    InsuranceLevelDto,
    WithdrawEnrollmentResponse,
} from './InsuranceTypes';

/**
 * Mock Insurance API (保險管理 Mock API)
 * Domain Code: HR05
 */
export class MockInsuranceApi {
  // Mock 投保級距資料
  private static mockLevels: InsuranceLevelDto[] = [
    {
      level_id: 'level-1',
      insurance_type: 'LABOR',
      level_number: 1,
      monthly_salary: 27470,
      labor_employee_rate: 0.11,
      labor_employer_rate: 0.07,
      effective_date: '2025-01-01',
      is_active: true,
    },
    {
      level_id: 'level-2',
      insurance_type: 'LABOR',
      level_number: 2,
      monthly_salary: 28800,
      labor_employee_rate: 0.11,
      labor_employer_rate: 0.07,
      effective_date: '2025-01-01',
      is_active: true,
    },
    {
      level_id: 'level-3',
      insurance_type: 'HEALTH',
      level_number: 1,
      monthly_salary: 27470,
      health_employee_rate: 0.0517,
      health_employer_rate: 0.0517,
      effective_date: '2025-01-01',
      is_active: true,
    },
    {
      level_id: 'level-4',
      insurance_type: 'PENSION',
      level_number: 1,
      monthly_salary: 27470,
      pension_employer_rate: 0.06,
      effective_date: '2025-01-01',
      is_active: true,
    },
  ];

  // Mock 投保記錄資料
  private static mockEnrollments: EnrollmentDto[] = [
    {
      enrollment_id: 'enroll-1',
      employee_id: 'emp-001',
      employee_name: '王小明',
      insurance_unit_id: 'unit-1',
      insurance_unit_name: '總公司',
      insurance_type: 'LABOR',
      enroll_date: '2025-01-01',
      monthly_salary: 50000,
      level_number: 10,
      status: 'ACTIVE',
      is_reported: true,
      reported_at: '2025-01-05T10:00:00Z',
      created_at: '2025-01-01T09:00:00Z',
      updated_at: '2025-01-05T10:00:00Z',
    },
    {
      enrollment_id: 'enroll-2',
      employee_id: 'emp-001',
      employee_name: '王小明',
      insurance_unit_id: 'unit-1',
      insurance_unit_name: '總公司',
      insurance_type: 'HEALTH',
      enroll_date: '2025-01-01',
      monthly_salary: 50000,
      level_number: 10,
      status: 'ACTIVE',
      is_reported: true,
      reported_at: '2025-01-05T10:00:00Z',
      created_at: '2025-01-01T09:00:00Z',
      updated_at: '2025-01-05T10:00:00Z',
    },
    {
      enrollment_id: 'enroll-3',
      employee_id: 'emp-001',
      employee_name: '王小明',
      insurance_unit_id: 'unit-1',
      insurance_unit_name: '總公司',
      insurance_type: 'PENSION',
      enroll_date: '2025-01-01',
      monthly_salary: 50000,
      level_number: 10,
      status: 'ACTIVE',
      is_reported: true,
      reported_at: '2025-01-05T10:00:00Z',
      created_at: '2025-01-01T09:00:00Z',
      updated_at: '2025-01-05T10:00:00Z',
    },
  ];

  /**
   * GET /api/v1/insurance/my - 查詢我的保險資訊 (ESS)
   */
  static async getMyInsurance(): Promise<GetMyInsuranceResponse> {
    await this.delay(300);

    return {
      insurance_info: {
        employee_id: 'emp-001',
        employee_name: '王小明',
        employee_code: 'E001',
        unit_name: '總公司',
        enrollments: this.mockEnrollments,
        fees: {
          labor_employee: 5500,
          labor_employer: 3500,
          health_employee: 2585,
          health_employer: 2585,
          pension_employer: 3000,
          total_employee: 8085,
          total_employer: 9085,
        },
        history: [
          {
            history_id: 'hist-1',
            change_date: '2025-01-01',
            change_type: 'ENROLL',
            insurance_type: 'LABOR',
            monthly_salary: 50000,
            level_number: 10,
            reason: '新進員工加保',
            operator_name: 'HR Manager',
            created_at: '2025-01-01T09:00:00Z',
          },
        ],
        has_active_enrollment: true,
      },
    };
  }

  /**
   * GET /api/v1/insurance/enrollments - 查詢加退保記錄
   */
  static async getEnrollments(params?: any): Promise<GetEnrollmentsResponse> {
    await this.delay(300);

    let filteredEnrollments = [...this.mockEnrollments];

    // 篩選條件
    if (params?.insurance_type) {
      filteredEnrollments = filteredEnrollments.filter(
        (e) => e.insurance_type === params.insurance_type
      );
    }

    if (params?.status) {
      filteredEnrollments = filteredEnrollments.filter((e) => e.status === params.status);
    }

    return {
      enrollments: filteredEnrollments,
      total: filteredEnrollments.length,
      page: params?.page || 1,
      page_size: params?.page_size || 10,
    };
  }

  /**
   * GET /api/v1/insurance/enrollments/active - 查詢員工有效加保記錄
   */
  static async getActiveEnrollments(): Promise<GetEnrollmentsResponse> {
    await this.delay(300);

    const activeEnrollments = this.mockEnrollments.filter((e) => e.status === 'ACTIVE');

    return {
      enrollments: activeEnrollments,
      total: activeEnrollments.length,
      page: 1,
      page_size: 100,
    };
  }

  /**
   * POST /api/v1/insurance/enrollments - 手動加保
   */
  static async createEnrollment(request: any): Promise<CreateEnrollmentResponse> {
    await this.delay(500);

    const enrollmentIds = request.insurance_types.map(
      (type: string) => `enroll-${Date.now()}-${type}`
    );

    return {
      enrollment_ids: enrollmentIds,
      message: `已成功加保 ${request.insurance_types.length} 項保險`,
    };
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/withdraw - 退保
   */
  static async withdrawEnrollment(
    id: string,
    _request: any
  ): Promise<WithdrawEnrollmentResponse> {
    await this.delay(500);

    return {
      enrollment_id: id,
      message: '退保成功',
    };
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/adjust-level - 調整投保級距
   */
  static async adjustLevel(id: string, request: any): Promise<AdjustLevelResponse> {
    await this.delay(500);

    // 簡單計算新級距 (實際應根據投保級距表)
    const newLevelNumber = Math.floor(request.new_monthly_salary / 5000);

    return {
      enrollment_id: id,
      new_level_number: newLevelNumber,
      new_monthly_salary: request.new_monthly_salary,
      message: '投保級距調整成功',
    };
  }

  /**
   * POST /api/v1/insurance/fees/calculate - 計算保費
   */
  static async calculateFees(_request: any): Promise<CalculateFeesResponse> {
    await this.delay(300);

    const monthlySalary = _request.monthly_salary;
    const levelNumber = Math.floor(monthlySalary / 5000);

    // 簡化的保費計算 (實際應根據投保級距表)
    const laborEmployee = Math.round(monthlySalary * 0.11);
    const laborEmployer = Math.round(monthlySalary * 0.07);
    const healthEmployee = Math.round(monthlySalary * 0.0517);
    const healthEmployer = Math.round(monthlySalary * 0.0517);
    const pensionEmployer = Math.round(monthlySalary * 0.06);

    return {
      fees: {
        labor_employee: laborEmployee,
        labor_employer: laborEmployer,
        health_employee: healthEmployee,
        health_employer: healthEmployer,
        pension_employer: pensionEmployer,
        total_employee: laborEmployee + healthEmployee,
        total_employer: laborEmployer + healthEmployer + pensionEmployer,
      },
      level_number: levelNumber,
    };
  }

  /**
   * GET /api/v1/insurance/levels - 查詢投保級距
   */
  static async getLevels(params?: any): Promise<GetLevelsResponse> {
    await this.delay(300);

    let filteredLevels = [...this.mockLevels];

    if (params?.insurance_type) {
      filteredLevels = filteredLevels.filter((l) => l.insurance_type === params.insurance_type);
    }

    return {
      levels: filteredLevels,
      total: filteredLevels.length,
    };
  }

  /**
   * 模擬延遲
   */
  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
