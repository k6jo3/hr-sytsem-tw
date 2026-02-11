import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockPerformanceApi } from './MockPerformanceApi';
import type {
    CreateCycleRequest,
    CreateCycleResponse,
    GetCyclesRequest,
    GetCyclesResponse,
    GetDistributionRequest,
    GetDistributionResponse,
    GetMyPerformanceResponse,
    GetTeamReviewsRequest,
    GetTeamReviewsResponse,
    GetTemplateResponse,
    SaveReviewRequest,
    SaveReviewResponse,
    SubmitReviewRequest,
    SubmitReviewResponse,
    UpdateCycleRequest,
    UpdateTemplateRequest,
} from './PerformanceTypes';


/**
 * Performance API (績效管理 API)
 * Domain Code: HR08
 */
export class PerformanceApi {
  private static readonly BASE_PATH = '/performance';

  // ========== My Performance (ESS) ==========

  /**
   * GET /api/v1/performance/my - 查詢我的考核（ESS）
   */
  static async getMyPerformance(): Promise<GetMyPerformanceResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getMyPerformance();
    return apiClient.get<GetMyPerformanceResponse>(`${this.BASE_PATH}/my`);
  }

  /**
   * POST /api/v1/performance/reviews - 新增/更新考核
   */
  static async saveReview(request: SaveReviewRequest): Promise<SaveReviewResponse> {
    // Mock impl not fully complete for write ops, can extend MockPerformanceApi if needed
    return apiClient.post<SaveReviewResponse>(`${this.BASE_PATH}/reviews`, request);
  }

  /**
   * PUT /api/v1/performance/reviews/{id}/submit - 提交考核
   */
  static async submitReview(
    reviewId: string,
    request: SubmitReviewRequest
  ): Promise<SubmitReviewResponse> {
    return apiClient.put<SubmitReviewResponse>(
      `${this.BASE_PATH}/reviews/${reviewId}/submit`,
      request
    );
  }

  // ========== Team Performance (Manager) ==========

  /**
   * GET /api/v1/performance/team - 查詢團隊考核（主管）
   */
  static async getTeamReviews(params: GetTeamReviewsRequest): Promise<GetTeamReviewsResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getTeamReviews(params);
    return apiClient.get<GetTeamReviewsResponse>(`${this.BASE_PATH}/team`, { params });
  }

  // ========== Cycle Management (Admin) ==========

  /**
   * GET /api/v1/performance/cycles - 查詢考核週期列表
   */
  static async getCycles(params: GetCyclesRequest): Promise<GetCyclesResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getCycles(params);
    return apiClient.get<GetCyclesResponse>(`${this.BASE_PATH}/cycles`, { params });
  }

  /**
   * POST /api/v1/performance/cycles - 建立考核週期
   */
  static async createCycle(request: CreateCycleRequest): Promise<CreateCycleResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.createCycle(request);
    return apiClient.post<CreateCycleResponse>(`${this.BASE_PATH}/cycles`, request);
  }

  /**
   * PUT /api/v1/performance/cycles/{id} - 更新考核週期
   */
  static async updateCycle(id: string, request: UpdateCycleRequest): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.updateCycle(id, request);
    return apiClient.put<void>(`${this.BASE_PATH}/cycles/${id}`, request);
  }

  /**
   * DELETE /api/v1/performance/cycles/{id} - 刪除考核週期
   */
  static async deleteCycle(id: string): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.deleteCycle(id);
    return apiClient.delete<void>(`${this.BASE_PATH}/cycles/${id}`);
  }

  /**
   * PUT /api/v1/performance/cycles/{id}/start - 啟動考核週期
   */
  static async startCycle(cycleId: string): Promise<{ message: string }> {
    if (MockConfig.isEnabled('PERFORMANCE')) {
        await MockPerformanceApi.startCycle(cycleId);
        return { message: 'Cycle started' };
    }
    return apiClient.put<{ message: string }>(`${this.BASE_PATH}/cycles/${cycleId}/start`, {});
  }

  /**
   * POST /api/v1/performance/cycles/{id}/sync - 同步上年度設定
   */
  static async syncLastYearCycle(cycleId: string, lastYearCycleId: string): Promise<void> {
    return apiClient.post<void>(`${this.BASE_PATH}/cycles/${cycleId}/sync`, { lastYearCycleId });
  }

  // ========== Template Management (Admin) ==========

  /**
   * GET /api/v1/performance/cycles/{id}/template - 取得考核表單設定
   */
  static async getTemplate(cycleId: string): Promise<GetTemplateResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getTemplate(cycleId);
    return apiClient.get<GetTemplateResponse>(`${this.BASE_PATH}/cycles/${cycleId}/template`);
  }

  /**
   * PUT /api/v1/performance/cycles/{id}/template - 更新考核表單設定
   */
  static async updateTemplate(cycleId: string, request: UpdateTemplateRequest): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.updateTemplate(cycleId, request);
    return apiClient.put<void>(`${this.BASE_PATH}/cycles/${cycleId}/template`, request);
  }

  /**
   * PUT /api/v1/performance/cycles/{id}/publish - 發布考核表單
   */
  static async publishTemplate(cycleId: string): Promise<void> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.publishTemplate(cycleId);
    return apiClient.put<void>(`${this.BASE_PATH}/cycles/${cycleId}/publish`, {});
  }

  // ========== Reports (Admin) ==========

  /**
   * GET /api/v1/performance/reports/distribution - 績效分佈報表
   */
  static async getDistribution(params: GetDistributionRequest): Promise<GetDistributionResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) return MockPerformanceApi.getDistribution(params);
    return apiClient.get<GetDistributionResponse>(`${this.BASE_PATH}/reports/distribution`, {
      params,
    });
  }
}
