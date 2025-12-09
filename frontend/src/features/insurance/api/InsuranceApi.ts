import { apiClient } from '@shared/api';
import type {
  GetMyInsuranceResponse,
  GetEnrollmentsRequest,
  GetEnrollmentsResponse,
  CreateEnrollmentRequest,
  CreateEnrollmentResponse,
  WithdrawEnrollmentRequest,
  WithdrawEnrollmentResponse,
  AdjustLevelRequest,
  AdjustLevelResponse,
  CalculateFeesRequest,
  CalculateFeesResponse,
  GetLevelsRequest,
  GetLevelsResponse,
} from './InsuranceTypes';

/**
 * Insurance API (保險管理 API)
 * Domain Code: HR05
 */
export class InsuranceApi {
  private static readonly BASE_PATH = '/insurance';

  /**
   * GET /api/v1/insurance/my - 查詢我的保險資訊 (ESS)
   */
  static async getMyInsurance(): Promise<GetMyInsuranceResponse> {
    return apiClient.get<GetMyInsuranceResponse>(`${this.BASE_PATH}/my`);
  }

  /**
   * GET /api/v1/insurance/enrollments - 查詢加退保記錄
   */
  static async getEnrollments(params: GetEnrollmentsRequest): Promise<GetEnrollmentsResponse> {
    return apiClient.get<GetEnrollmentsResponse>(`${this.BASE_PATH}/enrollments`, { params });
  }

  /**
   * POST /api/v1/insurance/enrollments - 手動加保
   */
  static async createEnrollment(request: CreateEnrollmentRequest): Promise<CreateEnrollmentResponse> {
    return apiClient.post<CreateEnrollmentResponse>(`${this.BASE_PATH}/enrollments`, request);
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/withdraw - 退保
   */
  static async withdrawEnrollment(
    id: string,
    request: WithdrawEnrollmentRequest
  ): Promise<WithdrawEnrollmentResponse> {
    return apiClient.put<WithdrawEnrollmentResponse>(
      `${this.BASE_PATH}/enrollments/${id}/withdraw`,
      request
    );
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/adjust-level - 調整投保級距
   */
  static async adjustLevel(
    id: string,
    request: AdjustLevelRequest
  ): Promise<AdjustLevelResponse> {
    return apiClient.put<AdjustLevelResponse>(
      `${this.BASE_PATH}/enrollments/${id}/adjust-level`,
      request
    );
  }

  /**
   * POST /api/v1/insurance/fees/calculate - 計算保費
   */
  static async calculateFees(request: CalculateFeesRequest): Promise<CalculateFeesResponse> {
    return apiClient.post<CalculateFeesResponse>(`${this.BASE_PATH}/fees/calculate`, request);
  }

  /**
   * GET /api/v1/insurance/levels - 查詢投保級距
   */
  static async getLevels(params: GetLevelsRequest): Promise<GetLevelsResponse> {
    return apiClient.get<GetLevelsResponse>(`${this.BASE_PATH}/levels`, { params });
  }
}
