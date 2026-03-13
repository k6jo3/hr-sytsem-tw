// @ts-nocheck
/**
 * InsuranceApi Adapter 測試
 * 三方欄位一致性驗證：後端 Response DTO ↔ 前端 Adapter ↔ InsuranceTypes
 *
 * 涵蓋函式：
 *   - adaptEnrollmentDto   (EnrollmentDetailResponse → EnrollmentDto)
 *   - adaptFeesResponse    (FeeCalculationResponse   → InsuranceFeesDto)
 *   - adaptHistoryDto      (EnrollmentHistoryItem    → EnrollmentHistoryDto)
 *   - adaptLevelDto        (InsuranceLevelResponse   → InsuranceLevelDto)
 *
 * 測試策略：
 *   1. 後端 camelCase 模擬（後端真實序列化）
 *   2. 前端 snake_case fallback（Mock 資料相容）
 *   3. null / undefined 欄位處理（防禦性預設值）
 *   4. 未知 enum 值（guardEnum 行為驗證）
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { InsuranceApi } from './InsuranceApi';

// ============================================================
// 取得 private adapter 函式的測試用輔助方法
// adapter 函式定義於 InsuranceApi.ts module scope，無法直接 import。
// 改以整合方式：透過 mock apiClient 觸發真實呼叫，驗證回傳結果。
// ============================================================

// Mock apiClient
vi.mock('@shared/api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

// Mock MockConfig — 確保走真實 adapter 路徑
vi.mock('../../../config/MockConfig', () => ({
  MockConfig: {
    isEnabled: vi.fn().mockReturnValue(false),
  },
}));

import { apiClient } from '@shared/api';

const mockGet = apiClient.get as ReturnType<typeof vi.fn>;
const mockPost = apiClient.post as ReturnType<typeof vi.fn>;

beforeEach(() => {
  vi.clearAllMocks();
});

// ============================================================
// adaptEnrollmentDto
// 對應後端：EnrollmentDetailResponse.java
// 對應前端：EnrollmentDto (InsuranceTypes.ts)
// ============================================================
describe('adaptEnrollmentDto — 透過 getEnrollments', () => {
  /**
   * 【已知不一致 #1】
   * EnrollmentDetailResponse 不含 insuranceUnitId，只有 insuranceUnitName。
   * 故 insurance_unit_id 永遠為 ''。
   * TODO: 後端應在 EnrollmentDetailResponse 補上 insuranceUnitId 欄位
   */

  it('後端 camelCase 正確對應所有欄位', async () => {
    // 模擬後端 EnrollmentDetailResponse 序列化結果（PageResponse.items）
    const backendItem = {
      enrollmentId: 'enroll-001',
      employeeId: 'emp-001',
      employeeName: '王小明',
      // 注意：EnrollmentDetailResponse 無 insuranceUnitId，只有 insuranceUnitName
      insuranceUnitName: 'ABC科技股份有限公司',
      insuranceType: 'LABOR',
      insuranceTypeDisplay: '勞保',
      status: 'ACTIVE',
      statusDisplay: '已加保',
      enrollDate: '2025-01-01',
      withdrawDate: null,
      monthlySalary: 48200,
      levelNumber: 15,
      // 下列欄位 EnrollmentDetailResponse 不存在，預期 fallback 預設值
      // isReported, reportedAt, createdAt, updatedAt
    };

    mockGet.mockResolvedValueOnce({
      items: [backendItem],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await InsuranceApi.getEnrollments({ page: 1, page_size: 20 });
    const enrollment = result.enrollments[0];

    // 正確對應的欄位
    expect(enrollment.enrollment_id).toBe('enroll-001');
    expect(enrollment.employee_id).toBe('emp-001');
    expect(enrollment.employee_name).toBe('王小明');
    expect(enrollment.insurance_unit_name).toBe('ABC科技股份有限公司');
    expect(enrollment.insurance_type).toBe('LABOR');
    expect(enrollment.enroll_date).toBe('2025-01-01');
    expect(enrollment.withdraw_date).toBeUndefined();
    expect(enrollment.monthly_salary).toBe(48200);
    expect(enrollment.level_number).toBe(15);
    expect(enrollment.status).toBe('ACTIVE');

    // 【已知不一致 #1】insuranceUnitId 後端未提供，預期為空字串
    expect(enrollment.insurance_unit_id).toBe('');

    // 【已知不一致 #2】isReported / reportedAt / createdAt / updatedAt 後端未提供，使用預設值
    expect(enrollment.is_reported).toBe(false);
    expect(enrollment.reported_at).toBeUndefined();
    expect(enrollment.created_at).toBe('');
    expect(enrollment.updated_at).toBe('');
  });

  it('前端 snake_case fallback 相容（Mock 資料路徑）', async () => {
    const snakeCaseItem = {
      enrollment_id: 'enroll-002',
      employee_id: 'emp-002',
      employee_name: '李美麗',
      insurance_unit_id: 'unit-002',
      insurance_unit_name: 'XYZ子公司',
      insurance_type: 'HEALTH',
      enroll_date: '2025-02-01',
      withdraw_date: '2025-12-31',
      monthly_salary: 36300,
      level_number: 10,
      status: 'WITHDRAWN',
      is_reported: true,
      reported_at: '2025-02-05T10:00:00Z',
      created_at: '2025-02-01T09:00:00Z',
      updated_at: '2025-12-31T18:00:00Z',
    };

    mockGet.mockResolvedValueOnce({
      enrollments: [snakeCaseItem],
      total: 1,
    });

    const result = await InsuranceApi.getEnrollments({});
    const enrollment = result.enrollments[0];

    expect(enrollment.enrollment_id).toBe('enroll-002');
    expect(enrollment.employee_id).toBe('emp-002');
    expect(enrollment.insurance_unit_id).toBe('unit-002');
    expect(enrollment.insurance_type).toBe('HEALTH');
    expect(enrollment.withdraw_date).toBe('2025-12-31');
    expect(enrollment.status).toBe('WITHDRAWN');
    expect(enrollment.is_reported).toBe(true);
    expect(enrollment.reported_at).toBe('2025-02-05T10:00:00Z');
    expect(enrollment.created_at).toBe('2025-02-01T09:00:00Z');
    expect(enrollment.updated_at).toBe('2025-12-31T18:00:00Z');
  });

  it('null / undefined 欄位使用安全預設值', async () => {
    // 模擬後端回傳最小化欄位（僅必要欄位）
    const minimalItem = {
      enrollmentId: 'enroll-003',
      employeeId: 'emp-003',
      status: 'ACTIVE',
    };

    mockGet.mockResolvedValueOnce({ items: [minimalItem], totalElements: 1 });

    const result = await InsuranceApi.getEnrollments({});
    const enrollment = result.enrollments[0];

    expect(enrollment.enrollment_id).toBe('enroll-003');
    expect(enrollment.employee_id).toBe('emp-003');
    expect(enrollment.employee_name).toBe('');
    expect(enrollment.insurance_unit_id).toBe('');
    expect(enrollment.insurance_unit_name).toBe('');
    expect(enrollment.insurance_type).toBe('LABOR'); // 預設值
    expect(enrollment.enroll_date).toBe('');
    expect(enrollment.withdraw_date).toBeUndefined();
    expect(enrollment.monthly_salary).toBe(0);
    expect(enrollment.level_number).toBe(0);
    expect(enrollment.status).toBe('ACTIVE');
    expect(enrollment.is_reported).toBe(false);
    expect(enrollment.reported_at).toBeUndefined();
    expect(enrollment.created_at).toBe('');
    expect(enrollment.updated_at).toBe('');
  });

  it('未知 insurance_type — adaptEnrollmentDto 直接 cast，不經 guardEnum，不觸發警告', async () => {
    /**
     * adaptEnrollmentDto 對 insurance_type 使用直接型別 cast，而非 guardEnum。
     * 因此未知 insurance_type 不會觸發 console.warn，但值會被保留。
     * 只有 status 欄位使用 guardEnum 防護。
     */
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    const item = {
      enrollmentId: 'enroll-004',
      employeeId: 'emp-004',
      insuranceType: 'ACCIDENT', // 未知值，但 adapter 直接 cast，非 guardEnum
      status: 'ACTIVE',
    };

    mockGet.mockResolvedValueOnce({ items: [item], totalElements: 1 });

    const result = await InsuranceApi.getEnrollments({});
    const enrollment = result.enrollments[0];

    // 未知 insurance_type 直接 cast 回傳原始值
    expect(enrollment.insurance_type).toBe('ACCIDENT');
    // 不觸發 guardEnum 警告（因為 insurance_type 未使用 guardEnum）
    expect(warnSpy).not.toHaveBeenCalled();

    warnSpy.mockRestore();
  });

  it('未知 status enum 值 — guardEnum 應發出警告並回傳原始值', async () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    const item = {
      enrollmentId: 'enroll-005',
      employeeId: 'emp-005',
      insuranceType: 'PENSION',
      status: 'EXPIRED', // 未知 status 值
    };

    mockGet.mockResolvedValueOnce({ items: [item], totalElements: 1 });

    const result = await InsuranceApi.getEnrollments({});
    const enrollment = result.enrollments[0];

    expect(enrollment.status).toBe('EXPIRED');
    // guardEnum 回傳單一字串訊息，無第二參數
    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('enrollment.status')
    );

    warnSpy.mockRestore();
  });

  it('PENSION 保險類型正確對應', async () => {
    const item = {
      enrollmentId: 'enroll-006',
      employeeId: 'emp-001',
      insuranceType: 'PENSION',
      status: 'ACTIVE',
      monthlySalary: 48200,
      levelNumber: 15,
    };

    mockGet.mockResolvedValueOnce({ items: [item], totalElements: 1 });

    const result = await InsuranceApi.getEnrollments({});
    expect(result.enrollments[0].insurance_type).toBe('PENSION');
  });
});

// ============================================================
// adaptFeesResponse
// 對應後端：FeeCalculationResponse.java
// 對應前端：InsuranceFeesDto (InsuranceTypes.ts)
// ============================================================
describe('adaptFeesResponse — 透過 calculateFees', () => {
  /**
   * 【已知不一致 #3】
   * FeeCalculationResponse 含 pensionSelfContribution（個人自提），
   * InsuranceFeesDto 無此欄位，故該值在 adapter 中被丟棄。
   * TODO: 若前端需顯示個人勞退自提，需在 InsuranceFeesDto 補上 pension_self_contribution
   *
   * 【已知不一致 #4】
   * calculateFees 方法的費用對應使用 raw.labor_employee_fee（帶 _fee 後綴 snake_case），
   * 而 adaptFeesResponse 函式使用 raw.labor_employee（無 _fee 後綴）。
   * 兩個對應路徑不一致，會導致 calculateFees 的 camelCase fallback 缺失。
   * TODO: calculateFees 的 fee 對應應改為使用 adaptFeesResponse 統一邏輯
   */

  it('後端 camelCase 正確對應所有費用欄位', async () => {
    // 模擬後端 FeeCalculationResponse 序列化
    const backendResponse = {
      levelNumber: 15,
      monthlySalary: 48200,
      laborEmployeeFee: 1109,
      laborEmployerFee: 3885,
      healthEmployeeFee: 747,
      healthEmployerFee: 1493,
      pensionEmployerFee: 2892,
      pensionSelfContribution: 0, // 此欄位前端未對應
      totalEmployeeFee: 1856,
      totalEmployerFee: 8270,
    };

    mockPost.mockResolvedValueOnce(backendResponse);

    const result = await InsuranceApi.calculateFees({ monthly_salary: 48200 });

    expect(result.fees.labor_employee).toBe(1109);
    expect(result.fees.labor_employer).toBe(3885);
    expect(result.fees.health_employee).toBe(747);
    expect(result.fees.health_employer).toBe(1493);
    expect(result.fees.pension_employer).toBe(2892);
    expect(result.fees.total_employee).toBe(1856);
    expect(result.fees.total_employer).toBe(8270);
    expect(result.level_number).toBe(15);

    // 【已知不一致 #3】pensionSelfContribution 被丟棄，結果中不存在
    expect((result.fees as any).pension_self_contribution).toBeUndefined();
  });

  it('前端 snake_case fallback 相容（calculateFees 路徑）', async () => {
    /**
     * 注意：calculateFees 的 fallback 使用 raw.labor_employee_fee（帶 _fee 後綴），
     * 這與 adaptFeesResponse 的 raw.labor_employee 不同。
     * 本測試驗證 calculateFees 的 snake_case fallback 路徑可正常運作。
     */
    const snakeCaseResponse = {
      levelNumber: 10,
      labor_employee_fee: 831,
      labor_employer_fee: 2909,
      health_employee_fee: 561,
      health_employer_fee: 1122,
      pension_employer_fee: 2178,
      total_employee_fee: 1392,
      total_employer_fee: 6209,
    };

    mockPost.mockResolvedValueOnce(snakeCaseResponse);

    const result = await InsuranceApi.calculateFees({ monthly_salary: 36300 });

    expect(result.fees.labor_employee).toBe(831);
    expect(result.fees.labor_employer).toBe(2909);
    expect(result.fees.health_employee).toBe(561);
    expect(result.fees.health_employer).toBe(1122);
    expect(result.fees.pension_employer).toBe(2178);
    expect(result.fees.total_employee).toBe(1392);
    expect(result.fees.total_employer).toBe(6209);
  });

  it('null / undefined 費用欄位使用 0 作為安全預設值', async () => {
    // 模擬後端回傳空的 FeeCalculationResponse（欄位全部缺失）
    mockPost.mockResolvedValueOnce({ levelNumber: 0 });

    const result = await InsuranceApi.calculateFees({ monthly_salary: 0 });

    expect(result.fees.labor_employee).toBe(0);
    expect(result.fees.labor_employer).toBe(0);
    expect(result.fees.health_employee).toBe(0);
    expect(result.fees.health_employer).toBe(0);
    expect(result.fees.pension_employer).toBe(0);
    expect(result.fees.total_employee).toBe(0);
    expect(result.fees.total_employer).toBe(0);
    expect(result.level_number).toBe(0);
  });

  it('getMyInsurance 中的 fees 透過 adaptFeesResponse 正確對應', async () => {
    // 模擬 MyInsuranceDetailResponse 的 fees 巢狀物件（FeeCalculationResponse 型別）
    const backendResponse = {
      employeeName: '王小明',
      unitName: 'ABC科技股份有限公司',
      enrollments: [],
      fees: {
        laborEmployeeFee: 1109,
        laborEmployerFee: 3885,
        healthEmployeeFee: 747,
        healthEmployerFee: 1493,
        pensionEmployerFee: 2892,
        totalEmployeeFee: 1856,
        totalEmployerFee: 8270,
      },
      history: [],
    };

    mockGet.mockResolvedValueOnce(backendResponse);

    const result = await InsuranceApi.getMyInsurance();
    const fees = result.insurance_info.fees;

    expect(fees.labor_employee).toBe(1109);
    expect(fees.labor_employer).toBe(3885);
    expect(fees.health_employee).toBe(747);
    expect(fees.health_employer).toBe(1493);
    expect(fees.pension_employer).toBe(2892);
    expect(fees.total_employee).toBe(1856);
    expect(fees.total_employer).toBe(8270);
  });

  it('getMyInsurance — fees 為 null 時使用全零預設值', async () => {
    mockGet.mockResolvedValueOnce({
      employeeName: '王小明',
      unitName: 'ABC科技',
      enrollments: [],
      fees: null,
      history: [],
    });

    const result = await InsuranceApi.getMyInsurance();
    const fees = result.insurance_info.fees;

    expect(fees.labor_employee).toBe(0);
    expect(fees.total_employee).toBe(0);
    expect(fees.total_employer).toBe(0);
  });
});

// ============================================================
// adaptHistoryDto
// 對應後端：MyInsuranceDetailResponse.EnrollmentHistoryItem
// 對應前端：EnrollmentHistoryDto (InsuranceTypes.ts)
// ============================================================
describe('adaptHistoryDto — 透過 getMyInsurance', () => {
  /**
   * EnrollmentHistoryItem 欄位完整清單：
   *   historyId, changeDate, changeType, insuranceType,
   *   monthlySalary, levelNumber, reason
   *
   * 前端 EnrollmentHistoryDto 額外有 operator_name?（後端未提供）。
   */

  it('後端 camelCase 正確對應所有歷程欄位', async () => {
    const historyItem = {
      historyId: 'hist-001',
      changeDate: '2025-01-01',
      changeType: 'ENROLL',
      insuranceType: 'LABOR',
      monthlySalary: 48200,
      levelNumber: 15,
      reason: '到職加保',
    };

    mockGet.mockResolvedValueOnce({
      employeeName: '王小明',
      unitName: 'ABC科技',
      enrollments: [],
      fees: null,
      history: [historyItem],
    });

    const result = await InsuranceApi.getMyInsurance();
    const history = result.insurance_info.history[0];

    expect(history.history_id).toBe('hist-001');
    expect(history.change_date).toBe('2025-01-01');
    expect(history.change_type).toBe('ENROLL');
    expect(history.insurance_type).toBe('LABOR');
    expect(history.monthly_salary).toBe(48200);
    expect(history.level_number).toBe(15);
    expect(history.reason).toBe('到職加保');
    expect(history.created_at).toBe('');    // 後端 historyItem 無此欄位，使用預設值
    expect(history.operator_name).toBeUndefined(); // 後端未提供
  });

  it('前端 snake_case fallback 相容', async () => {
    const historyItem = {
      history_id: 'hist-002',
      change_date: '2025-07-01',
      change_type: 'ADJUST_LEVEL',
      insurance_type: 'HEALTH',
      monthly_salary: 53000,
      level_number: 17,
      reason: '年度調薪',
      created_at: '2025-07-01T09:00:00Z',
    };

    mockGet.mockResolvedValueOnce({
      enrollments: [],
      fees: null,
      history: [historyItem],
    });

    const result = await InsuranceApi.getMyInsurance();
    const history = result.insurance_info.history[0];

    expect(history.history_id).toBe('hist-002');
    expect(history.change_type).toBe('ADJUST_LEVEL');
    expect(history.insurance_type).toBe('HEALTH');
    expect(history.monthly_salary).toBe(53000);
    expect(history.created_at).toBe('2025-07-01T09:00:00Z');
  });

  it('null / undefined 歷程欄位使用安全預設值', async () => {
    // 模擬後端回傳空的歷程項目
    const emptyHistoryItem = {};

    mockGet.mockResolvedValueOnce({
      enrollments: [],
      fees: null,
      history: [emptyHistoryItem],
    });

    const result = await InsuranceApi.getMyInsurance();
    const history = result.insurance_info.history[0];

    expect(history.history_id).toBe('');
    expect(history.change_date).toBe('');
    expect(history.change_type).toBe('ENROLL'); // 預設值
    expect(history.insurance_type).toBe('LABOR'); // 預設值
    expect(history.monthly_salary).toBe(0);
    expect(history.level_number).toBe(0);
    expect(history.reason).toBe('');
    expect(history.created_at).toBe('');
  });

  it('WITHDRAW 異動類型正確對應', async () => {
    const historyItem = {
      historyId: 'hist-003',
      changeDate: '2025-12-31',
      changeType: 'WITHDRAW',
      insuranceType: 'PENSION',
      monthlySalary: 27470,
      levelNumber: 1,
      reason: '離職退保',
    };

    mockGet.mockResolvedValueOnce({
      enrollments: [],
      fees: null,
      history: [historyItem],
    });

    const result = await InsuranceApi.getMyInsurance();
    expect(result.insurance_info.history[0].change_type).toBe('WITHDRAW');
    expect(result.insurance_info.history[0].insurance_type).toBe('PENSION');
  });

  it('history 為空陣列時正確回傳空陣列', async () => {
    mockGet.mockResolvedValueOnce({
      enrollments: [],
      fees: null,
      history: [],
    });

    const result = await InsuranceApi.getMyInsurance();
    expect(result.insurance_info.history).toEqual([]);
  });
});

// ============================================================
// adaptLevelDto
// 對應後端：InsuranceLevelResponse.java
// 對應前端：InsuranceLevelDto (InsuranceTypes.ts)
// ============================================================
describe('adaptLevelDto — 透過 getLevels', () => {
  /**
   * 【已知不一致 #5】
   * InsuranceLevelResponse 使用 `levelId`（plain String），
   * 但 adaptLevelDto 同時處理 domain object 的 `id.value` 物件型態。
   * 當後端回傳 InsuranceLevelResponse 時，id 欄位為 undefined，
   * 正確的欄位名稱是 levelId，adapter 已有相容路徑。
   *
   * 【已知不一致 #6】
   * InsuranceLevelResponse 不含費率欄位（laborEmployeeRate 等），
   * 對應 InsuranceLevelDto 中的 optional rate 欄位均為 undefined，符合設計。
   *
   * 【已知不一致 #7】
   * Lombok @Data boolean isActive 欄位，Jackson 序列化為 "active"（非 "isActive"）。
   * adapter 已正確處理：raw.active ?? raw.isActive ?? raw.is_active
   */

  it('後端 camelCase 正確對應所有級距欄位（InsuranceLevelResponse 序列化）', async () => {
    // InsuranceLevelResponse 序列化：levelId 為 String，isActive 序列化為 "active"
    const backendLevel = {
      levelId: 'level-001',
      insuranceType: 'LABOR',
      levelNumber: 15,
      monthlySalary: 48200,
      effectiveDate: '2025-01-01',
      endDate: null,
      active: true, // Lombok boolean isActive → Jackson 序列化為 "active"
    };

    mockGet.mockResolvedValueOnce([backendLevel]);

    const result = await InsuranceApi.getLevels({ insurance_type: 'LABOR' });
    const level = result.levels[0];

    expect(level.level_id).toBe('level-001');
    expect(level.insurance_type).toBe('LABOR');
    expect(level.level_number).toBe(15);
    expect(level.monthly_salary).toBe(48200);
    expect(level.effective_date).toBe('2025-01-01');
    expect(level.end_date).toBeUndefined();
    expect(level.is_active).toBe(true);

    // InsuranceLevelResponse 無費率欄位，應為 undefined
    expect(level.labor_employee_rate).toBeUndefined();
    expect(level.labor_employer_rate).toBeUndefined();
    expect(level.health_employee_rate).toBeUndefined();
    expect(level.health_employer_rate).toBeUndefined();
    expect(level.pension_employer_rate).toBeUndefined();
  });

  it('domain object 格式 — id 為物件時正確取 id.value', async () => {
    // 模擬後端直接回傳 domain InsuranceLevel（id 為 LevelId value object）
    const domainLevel = {
      id: { value: 'level-uuid-abc' }, // LevelId value object
      insuranceType: 'HEALTH',
      levelNumber: 10,
      monthlySalary: 36300,
      effectiveDate: '2025-01-01',
      active: false,
    };

    mockGet.mockResolvedValueOnce([domainLevel]);

    const result = await InsuranceApi.getLevels({ insurance_type: 'HEALTH' });
    expect(result.levels[0].level_id).toBe('level-uuid-abc');
    expect(result.levels[0].is_active).toBe(false);
  });

  it('前端 snake_case fallback 相容', async () => {
    const snakeCaseLevel = {
      level_id: 'level-002',
      insurance_type: 'PENSION',
      level_number: 1,
      monthly_salary: 27470,
      effective_date: '2025-01-01',
      end_date: '2025-12-31',
      is_active: true,
      labor_employee_rate: 0.023,
      labor_employer_rate: 0.0805,
    };

    mockGet.mockResolvedValueOnce([snakeCaseLevel]);

    const result = await InsuranceApi.getLevels({});
    const level = result.levels[0];

    expect(level.level_id).toBe('level-002');
    expect(level.insurance_type).toBe('PENSION');
    expect(level.level_number).toBe(1);
    expect(level.monthly_salary).toBe(27470);
    expect(level.effective_date).toBe('2025-01-01');
    expect(level.end_date).toBe('2025-12-31');
    expect(level.is_active).toBe(true);
    expect(level.labor_employee_rate).toBe(0.023);
    expect(level.labor_employer_rate).toBe(0.0805);
  });

  it('null / undefined 欄位使用安全預設值', async () => {
    const emptyLevel = {};

    mockGet.mockResolvedValueOnce([emptyLevel]);

    const result = await InsuranceApi.getLevels({});
    const level = result.levels[0];

    expect(level.level_id).toBe('');
    expect(level.insurance_type).toBe('LABOR'); // 預設值
    expect(level.level_number).toBe(0);
    expect(level.monthly_salary).toBe(0);
    expect(level.effective_date).toBe('');
    expect(level.end_date).toBeUndefined();
    expect(level.is_active).toBe(true); // 預設值
  });

  it('isActive 使用 isActive 序列化名稱（相容非標準 Jackson 設定）', async () => {
    // 部分後端框架或自訂序列化器可能用 "isActive" 而非 "active"
    const level = {
      levelId: 'level-003',
      insuranceType: 'LABOR',
      levelNumber: 17,
      monthlySalary: 53000,
      effectiveDate: '2025-01-01',
      isActive: false, // 非標準 Jackson，仍應被 adapter 正確處理
    };

    mockGet.mockResolvedValueOnce([level]);

    const result = await InsuranceApi.getLevels({});
    expect(result.levels[0].is_active).toBe(false);
  });

  it('未知 insurance_type enum 值 — 應直接回傳原始值', async () => {
    const level = {
      levelId: 'level-004',
      insuranceType: 'OCCUPATIONAL_ACCIDENT', // 未來可能新增的類型
      levelNumber: 1,
      monthlySalary: 27470,
      effectiveDate: '2025-01-01',
      active: true,
    };

    mockGet.mockResolvedValueOnce([level]);

    const result = await InsuranceApi.getLevels({});
    // adaptLevelDto 未使用 guardEnum，直接 cast，故回傳原始值
    expect(result.levels[0].insurance_type).toBe('OCCUPATIONAL_ACCIDENT');
  });

  it('getLevels 回傳 total 等於 levels 長度', async () => {
    mockGet.mockResolvedValueOnce([
      { levelId: 'l1', insuranceType: 'LABOR', levelNumber: 1, monthlySalary: 27470, effectiveDate: '2025-01-01', active: true },
      { levelId: 'l2', insuranceType: 'LABOR', levelNumber: 10, monthlySalary: 36300, effectiveDate: '2025-01-01', active: true },
      { levelId: 'l3', insuranceType: 'LABOR', levelNumber: 15, monthlySalary: 48200, effectiveDate: '2025-01-01', active: true },
    ]);

    const result = await InsuranceApi.getLevels({ insurance_type: 'LABOR' });
    expect(result.total).toBe(3);
    expect(result.levels).toHaveLength(3);
  });
});

// ============================================================
// getEnrollments 分頁參數對應
// ============================================================
describe('getEnrollments — 分頁與過濾參數正確轉換為 backend camelCase', () => {
  it('前端 snake_case 參數正確轉換為 backend camelCase', async () => {
    mockGet.mockResolvedValueOnce({ items: [], totalElements: 0, page: 2, size: 10 });

    await InsuranceApi.getEnrollments({
      employee_id: 'emp-001',
      insurance_type: 'LABOR',
      status: 'ACTIVE',
      start_date: '2025-01-01',
      end_date: '2025-12-31',
      page: 2,
      page_size: 10,
    });

    expect(mockGet).toHaveBeenCalledWith(
      '/insurance/enrollments',
      expect.objectContaining({
        params: expect.objectContaining({
          employeeId: 'emp-001',
          insuranceType: 'LABOR',
          status: 'ACTIVE',
          startDate: '2025-01-01',
          endDate: '2025-12-31',
          page: 2,
          size: 10,
        }),
      })
    );
  });

  it('空參數不加入 backend params', async () => {
    mockGet.mockResolvedValueOnce({ items: [], totalElements: 0 });

    await InsuranceApi.getEnrollments({});

    const callArgs = mockGet.mock.calls[0];
    const params = callArgs[1]?.params ?? {};
    expect(Object.keys(params)).toHaveLength(0);
  });

  it('後端 PageResponse 格式正確對應前端分頁欄位', async () => {
    mockGet.mockResolvedValueOnce({
      items: [],
      totalElements: 100,
      page: 3,
      size: 20,
    });

    const result = await InsuranceApi.getEnrollments({ page: 3, page_size: 20 });

    expect(result.total).toBe(100);
    expect(result.page).toBe(3);
    expect(result.page_size).toBe(20);
  });
});
