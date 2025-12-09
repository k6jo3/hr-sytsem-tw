import { apiClient } from '@shared/api';
import type {
  GetMyPerformanceResponse,
  SaveReviewRequest,
  SaveReviewResponse,
  SubmitReviewRequest,
  SubmitReviewResponse,
  GetTeamReviewsRequest,
  GetTeamReviewsResponse,
  GetCyclesRequest,
  GetCyclesResponse,
  CreateCycleRequest,
  CreateCycleResponse,
  GetDistributionRequest,
  GetDistributionResponse,
} from './PerformanceTypes';

/**
 * Performance API (績效管理 API)
 * Domain Code: HR08
 */
export class PerformanceApi {
  private static readonly BASE_PATH = '/performance';

  /**
   * GET /api/v1/performance/my - 查詢我的考核（ESS）
   */
  static async getMyPerformance(): Promise<GetMyPerformanceResponse> {
    return apiClient.get<GetMyPerformanceResponse>(`${this.BASE_PATH}/my`);
  }

  /**
   * POST /api/v1/performance/reviews - 新增/更新考核
   */
  static async saveReview(request: SaveReviewRequest): Promise<SaveReviewResponse> {
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

  /**
   * GET /api/v1/performance/team - 查詢團隊考核（主管）
   */
  static async getTeamReviews(params: GetTeamReviewsRequest): Promise<GetTeamReviewsResponse> {
    return apiClient.get<GetTeamReviewsResponse>(`${this.BASE_PATH}/team`, { params });
  }

  /**
   * GET /api/v1/performance/cycles - 查詢考核週期列表
   */
  static async getCycles(params: GetCyclesRequest): Promise<GetCyclesResponse> {
    return apiClient.get<GetCyclesResponse>(`${this.BASE_PATH}/cycles`, { params });
  }

  /**
   * POST /api/v1/performance/cycles - 建立考核週期
   */
  static async createCycle(request: CreateCycleRequest): Promise<CreateCycleResponse> {
    return apiClient.post<CreateCycleResponse>(`${this.BASE_PATH}/cycles`, request);
  }

  /**
   * PUT /api/v1/performance/cycles/{id}/start - 啟動考核週期
   */
  static async startCycle(cycleId: string): Promise<{ message: string }> {
    return apiClient.put<{ message: string }>(`${this.BASE_PATH}/cycles/${cycleId}/start`, {});
  }

  /**
   * GET /api/v1/performance/reports/distribution - 績效分佈報表
   */
  static async getDistribution(params: GetDistributionRequest): Promise<GetDistributionResponse> {
    return apiClient.get<GetDistributionResponse>(`${this.BASE_PATH}/reports/distribution`, {
      params,
    });
  }
}
