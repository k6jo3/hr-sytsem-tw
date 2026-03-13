/**
 * Performance DTOs (績效管理 資料傳輸物件)
 * Domain Code: HR08
 */

/**
 * 考核週期類型
 */
export type CycleType = 'PROBATION' | 'QUARTERLY' | 'ANNUAL';

/**
 * 考核週期狀態
 */
export type CycleStatus = 'DRAFT' | 'ACTIVE' | 'IN_PROGRESS' | 'COMPLETED' | 'CLOSED';

/**
 * 考核類型
 */
export type ReviewType = 'SELF' | 'MANAGER' | 'PEER';

/**
 * 考核狀態
 */
export type ReviewStatus = 'DRAFT' | 'SUBMITTED' | 'FINALIZED';

/**
 * 績效評等
 */
export type PerformanceRating = 'A' | 'B' | 'C' | 'D';

/**
 * 評估項目 DTO
 */
export interface EvaluationItemDto {
  item_id: string;
  item_name: string;
  weight: number; // 權重 (0.0 - 1.0)
  score?: number; // 分數 (1-5)
  comments?: string; // 評估說明
  max_score: number; // 最高分數，通常是 5
}

/**
 * 考核週期 DTO
 */
export interface PerformanceCycleDto {
  cycle_id: string;
  cycle_name: string;
  cycle_type: CycleType;
  start_date: string;
  end_date: string;
  self_eval_deadline?: string;
  manager_eval_deadline?: string;
  status: CycleStatus;
  created_at: string;
}

/**
 * 考核記錄 DTO
 */
export interface PerformanceReviewDto {
  review_id: string;
  cycle_id: string;
  cycle_name: string;
  employee_id: string;
  employee_name: string;
  reviewer_id: string;
  reviewer_name: string;
  review_type: ReviewType;
  evaluation_items: EvaluationItemDto[];
  overall_score?: number; // 加權總分
  overall_rating?: PerformanceRating; // 綜合評等
  comments?: string; // 整體評語
  status: ReviewStatus;
  submitted_at?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 我的考核資訊 DTO
 */
export interface MyPerformanceDto {
  current_cycle?: PerformanceCycleDto;
  self_review?: PerformanceReviewDto;
  manager_review?: PerformanceReviewDto;
  history: PerformanceReviewDto[];
  can_submit_self_eval: boolean;
}

/**
 * 團隊考核列表項 DTO
 */
export interface TeamReviewItemDto {
  employee_id: string;
  employee_name: string;
  employee_code: string;
  department_name: string;
  position_name: string;
  self_review_status: ReviewStatus;
  manager_review_status: ReviewStatus;
  overall_score?: number;
  overall_rating?: PerformanceRating;
  self_submitted_at?: string;
  manager_submitted_at?: string;
}

/**
 * 績效分佈統計 DTO
 */
export interface PerformanceDistributionDto {
  rating: PerformanceRating;
  count: number;
  percentage: number;
}

/**
 * GET /api/v1/performance/my - 查詢我的考核（ESS）
 */
export interface GetMyPerformanceRequest {
  // No parameters - gets current user's performance from JWT
}

export interface GetMyPerformanceResponse {
  performance: MyPerformanceDto;
}

/**
 * POST /api/v1/performance/reviews - 新增/更新考核
 */
export interface SaveReviewRequest {
  cycle_id: string;
  review_type: ReviewType;
  evaluation_items: EvaluationItemDto[];
  comments?: string;
}

export interface SaveReviewResponse {
  review_id: string;
  message: string;
}

/**
 * PUT /api/v1/performance/reviews/{id}/submit - 提交考核
 */
export interface SubmitReviewRequest {
  // Empty body - just submit the review
}

export interface SubmitReviewResponse {
  review_id: string;
  overall_score: number;
  overall_rating: PerformanceRating;
  message: string;
}

/**
 * GET /api/v1/performance/team - 查詢團隊考核（主管）
 */
export interface GetTeamReviewsRequest {
  cycle_id?: string;
  status?: ReviewStatus;
  page?: number;
  page_size?: number;
}

export interface GetTeamReviewsResponse {
  reviews: TeamReviewItemDto[];
  total: number;
  page: number;
  page_size: number;
}

/**
 * GET /api/v1/performance/cycles - 查詢考核週期列表
 */
export interface GetCyclesRequest {
  status?: CycleStatus;
  cycle_type?: CycleType;
}

export interface GetCyclesResponse {
  cycles: PerformanceCycleDto[];
  total: number;
}

/**
 * POST /api/v1/performance/cycles - 建立考核週期
 */
export interface CreateCycleRequest {
  cycle_name: string;
  cycle_type: CycleType;
  start_date: string;
  end_date: string;
  self_eval_deadline?: string;
  manager_eval_deadline?: string;
}

export interface CreateCycleResponse {
  cycle_id: string;
  message: string;
}

export interface UpdateCycleRequest {
  cycle_name?: string;
  start_date?: string;
  end_date?: string;
  self_eval_deadline?: string;
  manager_eval_deadline?: string;
  status?: CycleStatus;
}

/**
 * GET /api/v1/performance/reports/distribution - 績效分佈報表
 */
export interface GetDistributionRequest {
  cycle_id: string;
}

export interface GetDistributionResponse {
  distribution: PerformanceDistributionDto[];
  total_employees: number;
  average_score: number;
}

// ========== Template Types ==========

export interface EvaluationTemplateDto {
  form_name: string;
  scoring_system: 'FIVE_POINT' | 'FIVE_LEVEL' | 'PERCENTAGE';
  forced_distribution: boolean;
  distribution_rules?: Record<PerformanceRating, number>;
  evaluation_items: EvaluationItemDto[];
}

export interface UpdateTemplateRequest {
  form_name: string;
  scoring_system: 'FIVE_POINT' | 'FIVE_LEVEL' | 'PERCENTAGE';
  forced_distribution: boolean;
  distribution_rules?: Record<PerformanceRating, number>;
  evaluation_items: EvaluationItemDto[];
}

export interface GetTemplateResponse {
  template: EvaluationTemplateDto;
}
