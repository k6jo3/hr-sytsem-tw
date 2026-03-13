// @ts-nocheck
/**
 * HR08 Performance Adapter 測試
 *
 * 驗證以下 adapt 函式的映射正確性：
 *   - adaptReviewStatus      (後端 ReviewStatus → 前端 ReviewStatus)
 *   - adaptCycleDto          (後端 CycleSummary → 前端 PerformanceCycleDto)
 *   - adaptReviewDto         (後端 ReviewSummary → 前端 PerformanceReviewDto)
 *   - adaptTeamReviewItem    (後端 ReviewSummary → 前端 TeamReviewItemDto)
 *   - adaptEvaluationItem    (後端 EvaluationItem → 前端 EvaluationItemDto)
 *   - adaptDistributionData  (後端 DistributionData → 前端 PerformanceDistributionDto)
 *
 * 測試策略：由於 adapt 函式是模組私有函式，透過公開的靜態方法
 * PerformanceApi.getCycles / getMyPerformance / getTeamReviews /
 * getDistribution 注入假的 apiClient 回應來間接覆蓋。
 * 但為了聚焦測試 adapt 邏輯本身，這裡直接對
 * 內部 adapt 函式進行白箱測試（透過 vi.doMock 隔離 MockConfig）。
 *
 * 測試重點：
 *   1. 後端 camelCase 欄位正確映射至前端 snake_case
 *   2. null / undefined 時的 fallback 行為
 *   3. 未知列舉值（guardEnum）的處理
 *   4. weight × 100 / 100 的正規化轉換
 */

import { describe, it, expect, vi, beforeAll } from 'vitest';

// ---- 模擬 MockConfig（確保走真實 adapt 路徑）----
vi.mock('../../../config/MockConfig', () => ({
  MockConfig: {
    isEnabled: vi.fn().mockReturnValue(false),
  },
}));

// ---- 模擬 apiClient（避免真實 HTTP 呼叫）----
// 使用 vi.hoisted 確保 mockApiClient 在 vi.mock hoisting 後仍可存取
const mockApiClient = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
}));
vi.mock('@shared/api', () => ({
  apiClient: mockApiClient,
}));

// ---- 在 mock 設定完成後才 import 受測模組 ----
import { PerformanceApi } from './PerformanceApi';

// ============================================================
// 測試工具型別：後端回傳的原始物件
// ============================================================

/** 模擬後端 CycleSummary (camelCase) */
type BackendCycleSummary = {
  cycleId?: string;
  cycleName?: string;
  cycleType?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  selfEvalDeadline?: string;
  managerEvalDeadline?: string;
  createdAt?: string;
  hasTemplate?: boolean;
  template?: unknown;
};

/** 模擬後端 ReviewSummary (camelCase) */
type BackendReviewSummary = {
  reviewId?: string;
  cycleId?: string;
  cycleName?: string;
  employeeId?: string;
  employeeName?: string;
  reviewerId?: string;
  reviewerName?: string;
  reviewType?: string;
  evaluationItems?: BackendEvaluationItem[];
  overallScore?: number;
  overallRating?: string;
  comments?: string;
  status?: string;
  submittedAt?: string;
  createdAt?: string;
  updatedAt?: string;
};

/** 模擬後端 EvaluationItem (camelCase) */
type BackendEvaluationItem = {
  itemId?: string;
  itemName?: string;
  weight?: number;
  score?: number;
  selfComment?: string;
  managerComment?: string;
  comments?: string;
  maxScore?: number;
};

// ============================================================
// 輔助函式：建立後端 GetCyclesResponse 包裝
// ============================================================
function wrapCyclesResponse(cycles: BackendCycleSummary[], extras: Record<string, unknown> = {}) {
  return {
    cycles,
    totalCount: cycles.length,
    pageSize: 20,
    currentPage: 1,
    ...extras,
  };
}

function wrapReviewsResponse(reviews: BackendReviewSummary[], extras: Record<string, unknown> = {}) {
  return {
    reviews,
    totalCount: reviews.length,
    ...extras,
  };
}

// ============================================================
// 1. adaptCycleDto 測試
// ============================================================

describe('adaptCycleDto (透過 PerformanceApi.getCycles)', () => {
  it('應正確映射後端完整 camelCase 欄位至前端 snake_case', async () => {
    const backendCycle: BackendCycleSummary = {
      cycleId: 'CYC001',
      cycleName: '2025年度考核',
      cycleType: 'ANNUAL',
      status: 'IN_PROGRESS',
      startDate: '2025-12-01',
      endDate: '2026-01-31',
      selfEvalDeadline: '2025-12-31',
      managerEvalDeadline: '2026-01-15',
      createdAt: '2025-11-01T00:00:00',
    };

    mockApiClient.get.mockResolvedValueOnce(wrapCyclesResponse([backendCycle]));

    const result = await PerformanceApi.getCycles({});
    const cycle = result.cycles[0];

    expect(cycle.cycle_id).toBe('CYC001');
    expect(cycle.cycle_name).toBe('2025年度考核');
    expect(cycle.cycle_type).toBe('ANNUAL');
    expect(cycle.status).toBe('IN_PROGRESS');
    expect(cycle.start_date).toBe('2025-12-01');
    expect(cycle.end_date).toBe('2026-01-31');
    expect(cycle.self_eval_deadline).toBe('2025-12-31');
    expect(cycle.manager_eval_deadline).toBe('2026-01-15');
    expect(cycle.created_at).toBe('2025-11-01T00:00:00');
  });

  it('應能處理後端 items 欄位（PageResponse 結構）', async () => {
    const backendCycle: BackendCycleSummary = {
      cycleId: 'CYC002',
      cycleName: '季度考核',
      cycleType: 'QUARTERLY',
      status: 'DRAFT',
      startDate: '2025-01-01',
      endDate: '2025-03-31',
    };

    // 後端有時以 items 包裝（PageResponse 格式）
    mockApiClient.get.mockResolvedValueOnce({
      items: [backendCycle],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].cycle_id).toBe('CYC002');
    expect(result.total).toBe(1);
  });

  it('selfEvalDeadline 與 managerEvalDeadline 缺失時應回傳 undefined', async () => {
    const backendCycle: BackendCycleSummary = {
      cycleId: 'CYC003',
      cycleName: '試用期考核',
      cycleType: 'PROBATION',
      status: 'DRAFT',
      startDate: '2025-06-01',
      endDate: '2025-08-31',
      // selfEvalDeadline 與 managerEvalDeadline 故意省略
    };

    mockApiClient.get.mockResolvedValueOnce(wrapCyclesResponse([backendCycle]));

    const result = await PerformanceApi.getCycles({});
    const cycle = result.cycles[0];

    expect(cycle.self_eval_deadline).toBeUndefined();
    expect(cycle.manager_eval_deadline).toBeUndefined();
  });

  it('createdAt 缺失時應 fallback 為空字串', async () => {
    const backendCycle: BackendCycleSummary = {
      cycleId: 'CYC004',
      cycleName: '無時間戳週期',
      cycleType: 'ANNUAL',
      status: 'COMPLETED',
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      // createdAt 故意省略
    };

    mockApiClient.get.mockResolvedValueOnce(wrapCyclesResponse([backendCycle]));

    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].created_at).toBe('');
  });

  it('週期列表為空時應回傳空陣列', async () => {
    mockApiClient.get.mockResolvedValueOnce({ cycles: [], totalCount: 0 });

    const result = await PerformanceApi.getCycles({});
    expect(result.cycles).toHaveLength(0);
    expect(result.total).toBe(0);
  });
});

// ============================================================
// 2. CycleStatus guardEnum 測試
// ============================================================

describe('CycleStatus guardEnum（未知狀態值）', () => {
  it('已知狀態 DRAFT 應直接通過', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C1', cycleName: 'X', cycleType: 'ANNUAL', status: 'DRAFT', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].status).toBe('DRAFT');
  });

  it('已知狀態 IN_PROGRESS 應直接通過', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C2', cycleName: 'X', cycleType: 'ANNUAL', status: 'IN_PROGRESS', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].status).toBe('IN_PROGRESS');
  });

  it('已知狀態 COMPLETED 應直接通過', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C3', cycleName: 'X', cycleType: 'ANNUAL', status: 'COMPLETED', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].status).toBe('COMPLETED');
  });

  /**
   * TODO: 不一致點 #2 ─ ACTIVE 不在後端 CycleStatus 列舉中
   * 後端僅有 DRAFT / IN_PROGRESS / COMPLETED
   * 前端 CycleStatus 型別多出 ACTIVE 與 CLOSED
   * guardEnum 的 allowedValues 包含 ACTIVE，因此不會警告
   * 但實際上後端永遠不會傳回 ACTIVE；此測試驗證 passthrough 行為
   */
  it('ACTIVE（前端多餘狀態）應通過 guardEnum 並原樣回傳', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C4', cycleName: 'X', cycleType: 'ANNUAL', status: 'ACTIVE', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    // ACTIVE 在 allowedValues 中，直接回傳
    expect(result.cycles[0].status).toBe('ACTIVE');
  });

  it('CLOSED（前端多餘狀態）應通過 guardEnum 並原樣回傳', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C5', cycleName: 'X', cycleType: 'ANNUAL', status: 'CLOSED', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].status).toBe('CLOSED');
  });

  it('完全未知狀態 ARCHIVED 應透過 guardEnum 原樣回傳並輸出 console.warn', async () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C6', cycleName: 'X', cycleType: 'ANNUAL', status: 'ARCHIVED', startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    // guardEnum passthrough：原樣回傳未知值
    expect(result.cycles[0].status).toBe('ARCHIVED');
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('ARCHIVED'));

    warnSpy.mockRestore();
  });

  it('status 為 null 應 fallback 為 DRAFT', async () => {
    mockApiClient.get.mockResolvedValueOnce(
      wrapCyclesResponse([{ cycleId: 'C7', cycleName: 'X', cycleType: 'ANNUAL', status: undefined, startDate: '2025-01-01', endDate: '2025-12-31' }])
    );
    const result = await PerformanceApi.getCycles({});
    expect(result.cycles[0].status).toBe('DRAFT');
  });
});

// ============================================================
// 3. adaptReviewStatus 測試
// ============================================================

describe('adaptReviewStatus（透過 PerformanceApi.getMyPerformance）', () => {
  it('後端 PENDING_SELF 應映射為前端 DRAFT', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV001',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewerId: '',
      reviewerName: '',
      reviewType: 'SELF',
      evaluationItems: [],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    // PENDING_SELF 的 review 會被識別為 selfReview（review_type === 'SELF' 且 status !== 'FINALIZED'）
    expect(result.performance.self_review?.status).toBe('DRAFT');
  });

  it('後端 PENDING_MANAGER 應映射為前端 SUBMITTED', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV002',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'MANAGER',
      evaluationItems: [],
      status: 'PENDING_MANAGER',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    expect(result.performance.manager_review?.status).toBe('SUBMITTED');
  });

  it('後端 PENDING_FINALIZE 應映射為前端 SUBMITTED', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV003',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [],
      status: 'PENDING_FINALIZE',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    // PENDING_FINALIZE → SUBMITTED，但 status !== 'FINALIZED'，仍會是 selfReview
    expect(result.performance.self_review?.status).toBe('SUBMITTED');
  });

  it('後端 FINALIZED 應映射為前端 FINALIZED 並進入 history', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV004',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [],
      status: 'FINALIZED',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    expect(result.performance.history[0]?.status).toBe('FINALIZED');
    // FINALIZED 的 review 不應出現在 selfReview
    expect(result.performance.self_review).toBeUndefined();
  });

  it('status 未定義時應預設使用 PENDING_SELF → DRAFT', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV005',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [],
      // status 故意省略
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    expect(result.performance.self_review?.status).toBe('DRAFT');
  });
});

// ============================================================
// 4. adaptEvaluationItem 測試（weight 除以 100）
// ============================================================

describe('adaptEvaluationItem（weight 正規化）', () => {
  it('後端 weight=40 應映射為前端 0.4', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV010',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        { itemId: 'ITEM001', itemName: '工作績效', weight: 40, score: 4, selfComment: '表現良好' },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    expect(items[0].weight).toBeCloseTo(0.4);
    expect(items[0].item_id).toBe('ITEM001');
    expect(items[0].item_name).toBe('工作績效');
  });

  it('後端 weight=0 應映射為 0（不是 NaN）', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV011',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        { itemId: 'ITEM002', itemName: '備用項目', weight: 0, score: undefined },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    expect(items[0].weight).toBe(0);
  });

  it('selfComment 優先於 managerComment 優先於 comments', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV012',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        {
          itemId: 'ITEM003',
          itemName: '專業能力',
          weight: 30,
          selfComment: '自評說明',
          managerComment: '主管說明',
          comments: '通用說明',
        },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    // selfComment 優先
    expect(items[0].comments).toBe('自評說明');
  });

  it('selfComment 缺失時應使用 managerComment', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV013',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        {
          itemId: 'ITEM004',
          itemName: '工作態度',
          weight: 30,
          // selfComment 故意省略
          managerComment: '主管說明',
          comments: '通用說明',
        },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    expect(items[0].comments).toBe('主管說明');
  });

  it('所有 comment 欄位缺失時 comments 應為 undefined', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV014',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        { itemId: 'ITEM005', itemName: '溝通協作', weight: 20 },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    expect(items[0].comments).toBeUndefined();
  });

  it('maxScore 缺失時應 fallback 為 5', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV015',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [
        { itemId: 'ITEM006', itemName: '創新力', weight: 20 },
      ],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const items = result.performance.self_review?.evaluation_items ?? [];
    expect(items[0].max_score).toBe(5);
  });
});

// ============================================================
// 5. adaptTeamReviewItem 測試
// ============================================================

describe('adaptTeamReviewItem（透過 PerformanceApi.getTeamReviews）', () => {
  it('應正確映射後端完整欄位', async () => {
    const raw = {
      employeeId: 'E001',
      employeeName: '張三',
      employeeCode: 'EMP001',
      departmentName: '研發部',
      positionName: '高級工程師',
      selfReviewStatus: 'PENDING_SELF',
      managerReviewStatus: 'PENDING_MANAGER',
      overallScore: 85,
      overallRating: 'A',
      selfSubmittedAt: '2025-12-31',
      managerSubmittedAt: '2026-01-15',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [raw], totalElements: 1 });

    const result = await PerformanceApi.getTeamReviews({});
    const item = result.reviews[0];

    expect(item.employee_id).toBe('E001');
    expect(item.employee_name).toBe('張三');
    expect(item.employee_code).toBe('EMP001');
    expect(item.department_name).toBe('研發部');
    expect(item.position_name).toBe('高級工程師');
    expect(item.self_review_status).toBe('DRAFT');       // PENDING_SELF → DRAFT
    expect(item.manager_review_status).toBe('SUBMITTED'); // PENDING_MANAGER → SUBMITTED
    expect(item.overall_score).toBe(85);
    expect(item.overall_rating).toBe('A');
    expect(item.self_submitted_at).toBe('2025-12-31');
    expect(item.manager_submitted_at).toBe('2026-01-15');
  });

  /**
   * TODO: 不一致點 #7 ─ 後端 GetReviewsResponse.ReviewSummary 未提供
   * employeeCode / departmentName / positionName / selfReviewStatus /
   * managerReviewStatus / selfSubmittedAt / managerSubmittedAt
   * 若後端未回傳這些欄位，前端將靜默 fallback 至空字串 / undefined
   */
  it('後端缺少 employeeCode / departmentName / positionName 時應 fallback 為空字串', async () => {
    const raw = {
      employeeId: 'E002',
      employeeName: '李四',
      // employeeCode 故意省略
      // departmentName 故意省略
      // positionName 故意省略
      status: 'PENDING_SELF',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [raw], totalElements: 1 });

    const result = await PerformanceApi.getTeamReviews({});
    const item = result.reviews[0];

    expect(item.employee_code).toBe('');
    expect(item.department_name).toBe('');
    expect(item.position_name).toBe('');
  });

  it('selfReviewStatus 缺失時應從 status 退化並映射', async () => {
    const raw = {
      employeeId: 'E003',
      employeeName: '王五',
      // selfReviewStatus 故意省略，改由 status 退化
      status: 'PENDING_SELF',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [raw], totalElements: 1 });

    const result = await PerformanceApi.getTeamReviews({});
    const item = result.reviews[0];

    // 退化邏輯：raw.selfReviewStatus ?? raw.status → PENDING_SELF → DRAFT
    expect(item.self_review_status).toBe('DRAFT');
  });

  it('managerReviewStatus 缺失時 fallback 邏輯應預設 PENDING_MANAGER → SUBMITTED', async () => {
    const raw = {
      employeeId: 'E004',
      employeeName: '趙六',
      status: 'FINALIZED',
      // managerReviewStatus 故意省略
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [raw], totalElements: 1 });

    const result = await PerformanceApi.getTeamReviews({});
    const item = result.reviews[0];

    // adapter 程式碼：adaptReviewStatus(raw.managerReviewStatus ?? 'PENDING_MANAGER')
    // → PENDING_MANAGER → SUBMITTED
    expect(item.manager_review_status).toBe('SUBMITTED');
  });

  it('整個 reviews 陣列為空時應回傳空列表', async () => {
    mockApiClient.get.mockResolvedValueOnce({ items: [], totalElements: 0 });

    const result = await PerformanceApi.getTeamReviews({});
    expect(result.reviews).toHaveLength(0);
    expect(result.total).toBe(0);
  });
});

// ============================================================
// 6. adaptDistributionData 測試
// ============================================================

describe('adaptDistributionData（透過 PerformanceApi.getDistribution）', () => {
  it('應正確從 Map<String, DistributionData> 解析各評等', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      cycleId: 'CYC001',
      totalReviews: 100,
      completedReviews: 80,
      totalEmployees: 50,
      averageScore: 3.75,
      distribution: {
        S: { rating: 'S', count: 5, percentage: 5.0 },
        A: { rating: 'A', count: 20, percentage: 20.0 },
        B: { rating: 'B', count: 50, percentage: 50.0 },
        C: { rating: 'C', count: 20, percentage: 20.0 },
        D: { rating: 'D', count: 5, percentage: 5.0 },
      },
    });

    const result = await PerformanceApi.getDistribution({ cycle_id: 'CYC001' });

    expect(result.distribution).toHaveLength(5);
    expect(result.total_employees).toBe(50);
    expect(result.average_score).toBe(3.75);

    const ratings = result.distribution.map(d => d.rating);
    expect(ratings).toContain('S');
    expect(ratings).toContain('A');
    expect(ratings).toContain('B');

    const aItem = result.distribution.find(d => d.rating === 'A');
    expect(aItem?.count).toBe(20);
    expect(aItem?.percentage).toBe(20.0);
  });

  it('distribution 為 null 時應回傳空陣列', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      totalReviews: 0,
      totalEmployees: 0,
      averageScore: 0,
      distribution: null,
    });

    const result = await PerformanceApi.getDistribution({ cycle_id: 'CYC002' });
    expect(result.distribution).toHaveLength(0);
  });

  it('distribution 為空物件時應回傳空陣列', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      totalReviews: 10,
      totalEmployees: 10,
      averageScore: 3.0,
      distribution: {},
    });

    const result = await PerformanceApi.getDistribution({ cycle_id: 'CYC003' });
    expect(result.distribution).toHaveLength(0);
  });

  it('totalEmployees 缺失時應 fallback 至 totalReviews', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      totalReviews: 80,
      // totalEmployees 故意省略
      averageScore: 3.5,
      distribution: {
        A: { rating: 'A', count: 10, percentage: 12.5 },
      },
    });

    const result = await PerformanceApi.getDistribution({ cycle_id: 'CYC004' });
    // adapter: raw.totalEmployees ?? raw.totalReviews → 80
    expect(result.total_employees).toBe(80);
  });

  it('averageScore 缺失時應 fallback 為 0', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      totalReviews: 10,
      totalEmployees: 10,
      // averageScore 故意省略
      distribution: {},
    });

    const result = await PerformanceApi.getDistribution({ cycle_id: 'CYC005' });
    expect(result.average_score).toBe(0);
  });
});

// ============================================================
// 7. adaptReviewDto 完整欄位映射測試
// ============================================================

describe('adaptReviewDto 完整欄位（透過 PerformanceApi.getMyPerformance）', () => {
  it('應映射全部必要欄位', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV100',
      cycleId: 'CYC001',
      cycleName: '2025年度考核',
      employeeId: 'E001',
      employeeName: '林大明',
      reviewerId: 'MGR001',
      reviewerName: '陳主管',
      reviewType: 'SELF',
      evaluationItems: [],
      overallScore: 88,
      overallRating: 'A',
      comments: '整體表現優秀',
      status: 'PENDING_SELF',
      submittedAt: '2025-12-31T00:00:00',
      createdAt: '2025-11-01T00:00:00',
      updatedAt: '2025-12-01T00:00:00',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const dto = result.performance.self_review!;

    expect(dto.review_id).toBe('REV100');
    expect(dto.cycle_id).toBe('CYC001');
    expect(dto.cycle_name).toBe('2025年度考核');
    expect(dto.employee_id).toBe('E001');
    expect(dto.employee_name).toBe('林大明');
    expect(dto.reviewer_id).toBe('MGR001');
    expect(dto.reviewer_name).toBe('陳主管');
    expect(dto.review_type).toBe('SELF');
    expect(dto.overall_score).toBe(88);
    expect(dto.overall_rating).toBe('A');
    expect(dto.comments).toBe('整體表現優秀');
    expect(dto.status).toBe('DRAFT'); // PENDING_SELF → DRAFT
    expect(dto.submitted_at).toBe('2025-12-31T00:00:00');
    expect(dto.created_at).toBe('2025-11-01T00:00:00');
    expect(dto.updated_at).toBe('2025-12-01T00:00:00');
  });

  /**
   * TODO: 不一致點 #3 ─ 後端 ReviewSummary 缺少欄位
   * 後端 GetReviewsResponse.ReviewSummary 沒有提供：
   *   - reviewerName (僅有 reviewerId 概念上無此欄)
   *   - evaluationItems（列表 API 不回傳詳細 items）
   *   - comments
   *   - submittedAt
   *   - updatedAt
   * 若後端不補充這些欄位，以下測試驗證靜默 fallback 行為
   */
  it('後端缺少 reviewerName / comments / updatedAt 時應 fallback 為空字串', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV101',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      evaluationItems: [],
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      // reviewerName / comments / updatedAt 故意省略
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const dto = result.performance.self_review!;

    expect(dto.reviewer_name).toBe('');
    expect(dto.comments).toBe('');
    expect(dto.updated_at).toBe('');
  });

  it('evaluationItems 缺失時應回傳空陣列', async () => {
    const review: BackendReviewSummary = {
      reviewId: 'REV102',
      cycleId: 'CYC001',
      cycleName: '年度考核',
      employeeId: 'E001',
      employeeName: '測試員工',
      reviewType: 'SELF',
      // evaluationItems 故意省略
      status: 'PENDING_SELF',
      createdAt: '2025-11-01',
      updatedAt: '2025-11-01',
    };

    mockApiClient.get.mockResolvedValueOnce({ items: [review] });

    const result = await PerformanceApi.getMyPerformance();
    const dto = result.performance.self_review!;

    expect(dto.evaluation_items).toHaveLength(0);
    expect(Array.isArray(dto.evaluation_items)).toBe(true);
  });
});

// ============================================================
// 8. createCycle 請求映射測試（snake_case → camelCase）
// ============================================================

describe('PerformanceApi.createCycle（請求 adapt snake_case → camelCase）', () => {
  it('應正確將前端 snake_case 轉換為後端 camelCase', async () => {
    mockApiClient.post.mockResolvedValueOnce({ cycleId: 'CYC999' });

    await PerformanceApi.createCycle({
      cycle_name: '2026年度考核',
      cycle_type: 'ANNUAL',
      start_date: '2026-01-01',
      end_date: '2026-12-31',
      self_eval_deadline: '2026-12-15',
      manager_eval_deadline: '2026-12-25',
    });

    const [, sentBody] = mockApiClient.post.mock.calls[0];
    expect(sentBody.cycleName).toBe('2026年度考核');
    expect(sentBody.cycleType).toBe('ANNUAL');
    expect(sentBody.startDate).toBe('2026-01-01');
    expect(sentBody.endDate).toBe('2026-12-31');
    expect(sentBody.selfEvalDeadline).toBe('2026-12-15');
    expect(sentBody.managerEvalDeadline).toBe('2026-12-25');
  });

  it('應正確解析後端回傳的 cycleId', async () => {
    mockApiClient.post.mockResolvedValueOnce({ cycleId: 'CYC-NEW-001' });

    const result = await PerformanceApi.createCycle({
      cycle_name: '新週期',
      cycle_type: 'QUARTERLY',
      start_date: '2026-01-01',
      end_date: '2026-03-31',
    });

    expect(result.cycle_id).toBe('CYC-NEW-001');
  });

  /**
   * TODO: 不一致點 #1b ─ CreateCycleResponse 缺少 message 欄位
   * 後端 CreateCycleResponse 只有 cycleId，沒有 message 欄位
   * 前端 adapter 使用 raw.message ?? '週期已建立' 的靜默 fallback
   */
  it('後端 CreateCycleResponse 缺少 message 時應 fallback 為 "週期已建立"', async () => {
    mockApiClient.post.mockResolvedValueOnce({ cycleId: 'CYC-NOMSG-001' });

    const result = await PerformanceApi.createCycle({
      cycle_name: '無訊息週期',
      cycle_type: 'ANNUAL',
      start_date: '2026-01-01',
      end_date: '2026-12-31',
    });

    expect(result.message).toBe('週期已建立');
  });
});

// ============================================================
// 9. getCycles 總計欄位映射測試
// ============================================================

describe('getCycles 總計欄位（totalCount vs totalElements）', () => {
  it('後端 totalCount 應正確映射至前端 total', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      cycles: [],
      totalCount: 42,
      pageSize: 20,
      currentPage: 1,
    });

    const result = await PerformanceApi.getCycles({});
    // adapter: raw.totalElements ?? raw.totalCount → 42
    expect(result.total).toBe(42);
  });

  it('後端 totalElements 優先於 totalCount', async () => {
    mockApiClient.get.mockResolvedValueOnce({
      items: [],
      totalElements: 99,
      totalCount: 1,
    });

    const result = await PerformanceApi.getCycles({});
    expect(result.total).toBe(99);
  });

  it('兩者皆缺失時應 fallback 為實際陣列長度', async () => {
    mockApiClient.get.mockResolvedValueOnce({ cycles: [] });

    const result = await PerformanceApi.getCycles({});
    expect(result.total).toBe(0);
  });
});
