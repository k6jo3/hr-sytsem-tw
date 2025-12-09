import { apiClient } from '@shared/api';
import type {
  GetMyPayslipsRequest,
  GetMyPayslipsResponse,
  GetPayslipDetailResponse,
} from './PayrollTypes';

/**
 * Payroll API (薪資管理 API)
 * Domain Code: HR04
 */
export class PayrollApi {
  private static readonly BASE_PATH = '/payslips';

  /**
   * 取得我的薪資單列表
   */
  static async getMyPayslips(params: GetMyPayslipsRequest): Promise<GetMyPayslipsResponse> {
    return apiClient.get<GetMyPayslipsResponse>(`${this.BASE_PATH}/my`, { params });
  }

  /**
   * 取得薪資單詳情
   */
  static async getPayslipDetail(id: string): Promise<GetPayslipDetailResponse> {
    return apiClient.get<GetPayslipDetailResponse>(`${this.BASE_PATH}/${id}`);
  }

  /**
   * 下載薪資單PDF
   */
  static async downloadPayslipPdf(id: string): Promise<Blob> {
    const response = await apiClient.get<Blob>(`${this.BASE_PATH}/${id}/pdf`, {
      responseType: 'blob',
    });
    return response as unknown as Blob;
  }
}
