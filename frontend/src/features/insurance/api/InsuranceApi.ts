import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    AdjustLevelRequest,
    AdjustLevelResponse,
    CalculateFeesRequest,
    CalculateFeesResponse,
    CreateEnrollmentRequest,
    CreateEnrollmentResponse,
    GetEnrollmentsRequest,
    GetEnrollmentsResponse,
    GetLevelsRequest,
    GetLevelsResponse,
    GetMyInsuranceResponse,
    WithdrawEnrollmentRequest,
    WithdrawEnrollmentResponse,
} from './InsuranceTypes';
import { MockInsuranceApi } from './MockInsuranceApi';

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
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getMyInsurance();
    }
    return apiClient.get<GetMyInsuranceResponse>(`${this.BASE_PATH}/my`);
  }

  /**
   * GET /api/v1/insurance/enrollments - 查詢加退保記錄
   */
  static async getEnrollments(params: GetEnrollmentsRequest): Promise<GetEnrollmentsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getEnrollments(params);
    }
    return apiClient.get<GetEnrollmentsResponse>(`${this.BASE_PATH}/enrollments`, { params });
  }

  /**
   * GET /api/v1/insurance/enrollments/active - 查詢員工有效加保記錄
   */
  static async getActiveEnrollments(): Promise<GetEnrollmentsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getActiveEnrollments();
    }
    return apiClient.get<GetEnrollmentsResponse>(`${this.BASE_PATH}/enrollments/active`);
  }

  /**
   * POST /api/v1/insurance/enrollments - 手動加保
   */
  static async createEnrollment(request: CreateEnrollmentRequest): Promise<CreateEnrollmentResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.createEnrollment(request);
    }
    return apiClient.post<CreateEnrollmentResponse>(`${this.BASE_PATH}/enrollments`, request);
  }

  /**
   * PUT /api/v1/insurance/enrollments/{id}/withdraw - 退保
   */
  static async withdrawEnrollment(
    id: string,
    request: WithdrawEnrollmentRequest
  ): Promise<WithdrawEnrollmentResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.withdrawEnrollment(id, request);
    }
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
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.adjustLevel(id, request);
    }
    return apiClient.put<AdjustLevelResponse>(
      `${this.BASE_PATH}/enrollments/${id}/adjust-level`,
      request
    );
  }

  /**
   * POST /api/v1/insurance/fees/calculate - 計算保費
   */
  static async calculateFees(request: CalculateFeesRequest): Promise<CalculateFeesResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.calculateFees(request);
    }
    return apiClient.post<CalculateFeesResponse>(`${this.BASE_PATH}/fees/calculate`, request);
  }

  /**
   * GET /api/v1/insurance/levels - 查詢投保級距
   */
  static async getLevels(params: GetLevelsRequest): Promise<GetLevelsResponse> {
    if (MockConfig.isEnabled('INSURANCE')) {
      return MockInsuranceApi.getLevels(params);
    }
    return apiClient.get<GetLevelsResponse>(`${this.BASE_PATH}/levels`, { params });
  }
}
