import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockPerformanceApi } from './MockPerformanceApi';
import type {
    CreateCycleRequest,
    CreateCycleResponse,
    EvaluationItemDto,
    GetCyclesRequest,
    GetCyclesResponse,
    GetDistributionRequest,
    GetDistributionResponse,
    GetMyPerformanceResponse,
    GetTeamReviewsRequest,
    GetTeamReviewsResponse,
    GetTemplateResponse,
    PerformanceCycleDto,
    PerformanceDistributionDto,
    PerformanceReviewDto,
    ReviewStatus,
    SaveReviewRequest,
    SaveReviewResponse,
    SubmitReviewRequest,
    SubmitReviewResponse,
    TeamReviewItemDto,
    UpdateCycleRequest,
    UpdateTemplateRequest,
} from './PerformanceTypes';

// ========== Response Adapters ==========
// 後端 camelCase → 前端 snake_case
// 後端 ReviewStatus: PENDING_SELF/PENDING_MANAGER/PENDING_FINALIZE/FINALIZED
// 前端 ReviewStatus: DRAFT/SUBMITTED/FINALIZED

/**
 * 後端 ReviewStatus → 前端 ReviewStatus 映射
 */
function adaptReviewStatus(backendStatus: string): ReviewStatus {
  switch (backendStatus) {
    case 'PENDING_SELF': return 'DRAFT';
    case 'PENDING_MANAGER': return 'SUBMITTED';
    case 'PENDING_FINALIZE': return 'SUBMITTED';
    case 'FINALIZED': return 'FINALIZED';
    default: return backendStatus as ReviewStatus;
  }
}

/**
 * 後端 EvaluationItem → 前端 EvaluationItemDto
 */
function adaptEvaluationItem(raw: any): EvaluationItemDto {
  return {
    item_id: raw.itemId ?? raw.item_id ?? '',
    item_name: raw.itemName ?? raw.item_name ?? '',
    weight: (raw.weight ?? 0) / 100,
    score: raw.score,
    comments: raw.selfComment ?? raw.managerComment ?? raw.comments,
    max_score: raw.maxScore ?? raw.max_score ?? 5,
  };
}

/**
 * 後端 CycleSummary → 前端 PerformanceCycleDto
 */
function adaptCycleDto(raw: any): PerformanceCycleDto {
  return {
    cycle_id: raw.cycleId ?? raw.cycle_id ?? '',
    cycle_name: raw.cycleName ?? raw.cycle_name ?? '',
    cycle_type: raw.cycleType ?? raw.cycle_type ?? 'ANNUAL',
    start_date: raw.startDate ?? raw.start_date ?? '',
    end_date: raw.endDate ?? raw.end_date ?? '',
    self_eval_deadline: raw.selfEvalDeadline ?? raw.self_eval_deadline,
    manager_eval_deadline: raw.managerEvalDeadline ?? raw.manager_eval_deadline,
    status: raw.status ?? 'DRAFT',
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

/**
 * 後端 ReviewSummary → 前端 PerformanceReviewDto
 */
function adaptReviewDto(raw: any): PerformanceReviewDto {
  return {
    review_id: raw.reviewId ?? raw.review_id ?? '',
    cycle_id: raw.cycleId ?? raw.cycle_id ?? '',
    cycle_name: raw.cycleName ?? raw.cycle_name ?? '',
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    employee_name: raw.employeeName ?? raw.employee_name ?? '',
    reviewer_id: raw.reviewerId ?? raw.reviewer_id ?? '',
    reviewer_name: raw.reviewerName ?? raw.reviewer_name ?? '',
    review_type: raw.reviewType ?? raw.review_type ?? 'SELF',
    evaluation_items: (raw.evaluationItems ?? raw.evaluation_items ?? []).map(adaptEvaluationItem),
    overall_score: raw.overallScore ?? raw.overall_score,
    overall_rating: raw.overallRating ?? raw.overall_rating,
    comments: raw.comments ?? '',
    status: adaptReviewStatus(raw.status ?? 'PENDING_SELF'),
    submitted_at: raw.submittedAt ?? raw.submitted_at,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/**
 * 後端 ReviewSummary → 前端 TeamReviewItemDto
 * 團隊考核列表的每一筆
 */
function adaptTeamReviewItem(raw: any): TeamReviewItemDto {
  return {
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    employee_name: raw.employeeName ?? raw.employee_name ?? '',
    employee_code: raw.employeeCode ?? raw.employee_code ?? '',
    department_name: raw.departmentName ?? raw.department_name ?? '',
    position_name: raw.positionName ?? raw.position_name ?? '',
    self_review_status: adaptReviewStatus(raw.selfReviewStatus ?? raw.status ?? 'PENDING_SELF'),
    manager_review_status: adaptReviewStatus(raw.managerReviewStatus ?? 'PENDING_MANAGER'),
    overall_score: raw.overallScore ?? raw.overall_score,
    overall_rating: raw.overallRating ?? raw.overall_rating,
    self_submitted_at: raw.selfSubmittedAt ?? raw.submittedAt,
    manager_submitted_at: raw.managerSubmittedAt,
  };
}

/**
 * 後端 DistributionData → 前端 PerformanceDistributionDto
 */
function adaptDistributionData(raw: any): PerformanceDistributionDto {
  return {
    rating: raw.rating ?? '',
    count: raw.count ?? 0,
    percentage: raw.percentage ?? 0,
  };
}

/**
 * Performance API (績效管理 API)
 * Domain Code: HR08
 */
export class PerformanceApi {
  private static readonly BASE_PATH = '/performance';

  // ========== My Performance (ESS) ==========

  /**
   * 查詢我的考核（ESS）
   * 前端 GET /performance/my → 後端 GET /performance/reviews/my
   */
  static async getMyPerformance(): Promise<GetMyPerformanceResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getMyPerformance();
    const raw: any = await apiClient.get(`${this.BASE_PATH}/reviews/my`);
    // 後端回傳 PageResponse<ReviewSummary>，items 為列表
    const reviews = (raw.items ?? raw.reviews ?? []).map(adaptReviewDto);
    const selfReview = reviews.find((r: PerformanceReviewDto) => r.review_type === 'SELF' && r.status !== 'FINALIZED');
    const managerReview = reviews.find((r: PerformanceReviewDto) => r.review_type === 'MANAGER');
    const history = reviews.filter((r: PerformanceReviewDto) => r.status === 'FINALIZED');
    return {
      performance: {
        current_cycle: undefined,
        self_review: selfReview,
        manager_review: managerReview,
        history,
        can_submit_self_eval: selfReview?.status === 'DRAFT',
      },
    };
  }

  /**
   * POST /api/v1/performance/reviews/{id}/submit - 提交考核
   * 後端是 POST，前端原本用 PUT
   */
  static async saveReview(request: SaveReviewRequest): Promise<SaveReviewResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return { review_id: 'mock-review-id', message: '考核已儲存 (Mock)' };
    // 轉換 snake_case → camelCase
    const backendRequest: any = {
      cycleId: request.cycle_id,
      reviewType: request.review_type,
      evaluationItems: request.evaluation_items.map(item => ({
        itemId: item.item_id,
        itemName: item.item_name,
        weight: Math.round(item.weight * 100),
        score: item.score,
        selfComment: item.comments,
      })),
      comments: request.comments,
    };
    const raw: any = await apiClient.post(`${this.BASE_PATH}/reviews`, backendRequest);
    return {
      review_id: raw.reviewId ?? raw.review_id ?? '',
      message: raw.message ?? '考核已儲存',
    };
  }

  /**
   * 提交考核
   * 後端 POST /reviews/{id}/submit，前端原本 PUT
   */
  static async submitReview(
    reviewId: string,
    request: SubmitReviewRequest
  ): Promise<SubmitReviewResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return { review_id: reviewId, overall_score: 4.5, overall_rating: 'A', message: '考核已提交 (Mock)' };
    const raw: any = await apiClient.post(
      `${this.BASE_PATH}/reviews/${reviewId}/submit`,
      request
    );
    return {
      review_id: raw.reviewId ?? reviewId,
      overall_score: raw.overallScore ?? 0,
      overall_rating: raw.overallRating ?? 'C',
      message: raw.message ?? '考核已提交',
    };
  }

  // ========== Team Performance (Manager) ==========

  /**
   * 查詢團隊考核（主管）
   * 前端 GET /performance/team → 後端 GET /performance/reviews/team
   */
  static async getTeamReviews(params: GetTeamReviewsRequest): Promise<GetTeamReviewsResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getTeamReviews(params);
    // 轉換參數
    const backendParams: any = {};
    if (params.cycle_id) backendParams.cycleId = params.cycle_id;
    if (params.status) {
      // 前端 DRAFT → 後端 PENDING_SELF, SUBMITTED → PENDING_MANAGER
      if (params.status === 'DRAFT') backendParams.status = 'PENDING_SELF';
      else if (params.status === 'SUBMITTED') backendParams.status = 'PENDING_MANAGER';
      else backendParams.status = params.status;
    }
    if (params.page != null && params.page > 0) backendParams.page = params.page;
    if (params.page_size) backendParams.size = params.page_size;

    const raw: any = await apiClient.get(`${this.BASE_PATH}/reviews/team`, { params: backendParams });
    // 後端 PageResponse 使用 items 欄位
    const reviews = (raw.items ?? raw.reviews ?? []).map(adaptTeamReviewItem);
    return {
      reviews,
      total: raw.totalElements ?? raw.totalCount ?? raw.total ?? reviews.length,
      page: raw.page ?? raw.currentPage ?? 1,
      page_size: raw.size ?? raw.pageSize ?? 20,
    };
  }

  // ========== Cycle Management (Admin) ==========

  /**
   * GET /api/v1/performance/cycles - 查詢考核週期列表
   */
  static async getCycles(params: GetCyclesRequest): Promise<GetCyclesResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getCycles(params);
    // 轉換參數: snake_case → camelCase
    const backendParams: any = {};
    if (params.status) backendParams.status = params.status;
    if (params.cycle_type) backendParams.cycleType = params.cycle_type;

    const raw: any = await apiClient.get(`${this.BASE_PATH}/cycles`, { params: backendParams });
    // 後端 PageResponse 使用 items 欄位
    const cycles = (raw.items ?? raw.cycles ?? []).map(adaptCycleDto);
    return {
      cycles,
      total: raw.totalElements ?? raw.totalCount ?? raw.total ?? cycles.length,
    };
  }

  /**
   * POST /api/v1/performance/cycles - 建立考核週期
   */
  static async createCycle(request: CreateCycleRequest): Promise<CreateCycleResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.createCycle(request);
    // 轉換 snake_case → camelCase
    const backendRequest: any = {
      cycleName: request.cycle_name,
      cycleType: request.cycle_type,
      startDate: request.start_date,
      endDate: request.end_date,
      selfEvalDeadline: request.self_eval_deadline,
      managerEvalDeadline: request.manager_eval_deadline,
    };
    const raw: any = await apiClient.post(`${this.BASE_PATH}/cycles`, backendRequest);
    return {
      cycle_id: raw.cycleId ?? raw.cycle_id ?? '',
      message: raw.message ?? '週期已建立',
    };
  }

  /**
   * PUT /api/v1/performance/cycles/{id} - 更新考核週期
   */
  static async updateCycle(id: string, request: UpdateCycleRequest): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.updateCycle(id, request);
    // 轉換 snake_case → camelCase
    const backendRequest: any = {
      cycleId: id,
      cycleName: request.cycle_name,
      startDate: request.start_date,
      endDate: request.end_date,
      selfEvalDeadline: request.self_eval_deadline,
      managerEvalDeadline: request.manager_eval_deadline,
    };
    return apiClient.put(`${this.BASE_PATH}/cycles/${id}`, backendRequest);
  }

  /**
   * DELETE /api/v1/performance/cycles/{id} - 刪除考核週期
   */
  static async deleteCycle(id: string): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.deleteCycle(id);
    return apiClient.delete(`${this.BASE_PATH}/cycles/${id}`);
  }

  /**
   * PUT /api/v1/performance/cycles/{id}/start - 啟動考核週期
   */
  static async startCycle(cycleId: string): Promise<{ message: string }> {
    if (MockConfig.isEnabled('PERFORMANCE')) {
        await MockPerformanceApi.startCycle(cycleId);
        return { message: 'Cycle started' };
    }
    const raw: any = await apiClient.put(`${this.BASE_PATH}/cycles/${cycleId}/start`, {});
    return { message: raw.message ?? '週期已啟動' };
  }

  /**
   * POST /api/v1/performance/cycles/{id}/sync - 同步上年度設定
   * 後端無此 endpoint，暫不支援
   */
  static async syncLastYearCycle(_cycleId: string, _lastYearCycleId: string): Promise<void> {
    console.warn('[PerformanceApi] syncLastYearCycle: 後端尚未實作此 endpoint');
  }

  // ========== Template Management (Admin) ==========

  /**
   * 取得考核表單設定
   * 後端無直接 GET /template，改用 getCycleDetail 提取 template
   * 後端 GET /cycles/{id} → 回傳包含 template 的 CycleSummary
   */
  static async getTemplate(cycleId: string): Promise<GetTemplateResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getTemplate(cycleId);
    const raw: any = await apiClient.get(`${this.BASE_PATH}/cycles/${cycleId}`);
    // 從 cycle detail 中提取 template
    const templateJson = raw.templateJson ?? raw.template;
    let template;
    if (typeof templateJson === 'string' && templateJson) {
      try {
        template = JSON.parse(templateJson);
      } catch {
        template = null;
      }
    } else {
      template = templateJson;
    }
    if (!template) {
      return {
        template: {
          form_name: '',
          scoring_system: 'FIVE_POINT',
          forced_distribution: false,
          evaluation_items: [],
        },
      };
    }
    return {
      template: {
        form_name: template.formName ?? template.form_name ?? '',
        scoring_system: template.scoringSystem ?? template.scoring_system ?? 'FIVE_POINT',
        forced_distribution: template.forcedDistribution ?? template.forced_distribution ?? false,
        distribution_rules: template.distributionRules ?? template.distribution_rules,
        evaluation_items: (template.evaluationItems ?? template.evaluation_items ?? []).map(adaptEvaluationItem),
      },
    };
  }

  /**
   * 更新考核表單設定
   * 前端 PUT → 後端 POST /cycles/{id}/template
   */
  static async updateTemplate(cycleId: string, request: UpdateTemplateRequest): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.updateTemplate(cycleId, request);
    // 轉換 snake_case → camelCase
    const backendRequest: any = {
      cycleId,
      templateName: request.form_name,
      scoringSystem: request.scoring_system,
      enableDistribution: request.forced_distribution,
      items: request.evaluation_items.map(item => ({
        itemName: item.item_name,
        weight: Math.round(item.weight * 100),
        description: '',
        criteria: '',
      })),
    };
    return apiClient.post(`${this.BASE_PATH}/cycles/${cycleId}/template`, backendRequest);
  }

  /**
   * 發布考核表單
   * 前端 PUT .../publish → 後端 PUT .../template/publish
   */
  static async publishTemplate(cycleId: string): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.publishTemplate(cycleId);
    return apiClient.put(`${this.BASE_PATH}/cycles/${cycleId}/template/publish`, {});
  }

  // ========== Reports (Admin) ==========

  /**
   * 績效分佈報表
   * 前端 GET /reports/distribution?cycle_id=x → 後端 GET /reports/distribution/{cycleId}
   */
  static async getDistribution(params: GetDistributionRequest): Promise<GetDistributionResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getDistribution(params);
    const raw: any = await apiClient.get(`${this.BASE_PATH}/reports/distribution/${params.cycle_id}`);
    // 後端回傳 Map<String, DistributionData>，前端期望 PerformanceDistributionDto[]
    const distribution: PerformanceDistributionDto[] = [];
    if (raw.distribution && typeof raw.distribution === 'object') {
      Object.values(raw.distribution).forEach((item: any) => {
        distribution.push(adaptDistributionData(item));
      });
    }
    return {
      distribution,
      total_employees: raw.totalEmployees ?? raw.totalReviews ?? raw.totalCount ?? 0,
      average_score: raw.averageScore ?? 0,
    };
  }
}
