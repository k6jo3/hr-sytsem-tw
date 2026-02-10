import { apiClient } from '@shared/api';
import type {
    CreateSalaryStructureRequest,
    GetMyPayslipsRequest,
    GetMyPayslipsResponse,
    GetPayslipDetailResponse,
    GetPayslipListRequest,
    GetPayslipListResponse,
    PayrollItemDefinitionDto,
    PayrollRunDto,
    SalaryStructureDto,
    StartPayrollRunRequest,
    UpdateSalaryStructureRequest,
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

  // ========== Salary Structure Management ==========

  /**
   * 查詢薪資結構列表
   */
  static async getSalaryStructures(params?: any): Promise<any> {
    return apiClient.get('/salary-structures', { params });
  }

  /**
   * 查詢員工薪資結構
   */
  static async getEmployeeSalaryStructure(employeeId: string): Promise<SalaryStructureDto> {
    return apiClient.get(`/salary-structures/employee/${employeeId}`);
  }

  /**
   * 建立薪資結構
   */
  static async createSalaryStructure(request: CreateSalaryStructureRequest): Promise<SalaryStructureDto> {
    return apiClient.post('/salary-structures', request);
  }

  /**
   * 更新薪資結構
   */
  static async updateSalaryStructure(id: string, request: UpdateSalaryStructureRequest): Promise<SalaryStructureDto> {
    return apiClient.put(`/salary-structures/${id}`, request);
  }

  // ========== Payroll Run (Batch) Management ==========

  /**
   * 查詢薪資批次列表
   */
  static async getPayrollRuns(params?: any): Promise<any> {
    return apiClient.get('/payroll-runs', { params });
  }

  /**
   * 建立薪資批次
   */
  static async startPayrollRun(request: StartPayrollRunRequest): Promise<PayrollRunDto> {
    return apiClient.post('/payroll-runs', request);
  }

  /**
   * 執行薪資計算 batch
   */
  static async executePayrollRun(runId: string): Promise<PayrollRunDto> {
    return apiClient.post(`/payroll-runs/${runId}/execute`, {});
  }

  /**
   * 核准薪資批次
   */
  static async approvePayrollRun(runId: string): Promise<PayrollRunDto> {
    return apiClient.put(`/payroll-runs/${runId}/approve`, {});
  }

  /**
   * 退回薪資批次
   */
  static async rejectPayrollRun(runId: string, reason: string): Promise<PayrollRunDto> {
    return apiClient.put(`/payroll-runs/${runId}/reject`, { reason });
  }

  /**
   * 查詢薪資批次詳情
   */
  static async getPayrollRunById(runId: string): Promise<PayrollRunDto> {
    return apiClient.get(`/payroll-runs/${runId}`);
  }

  /**
   * 查詢薪資單列表 (管理員用)
   */
  static async getPayslips(params: GetPayslipListRequest): Promise<GetPayslipListResponse> {
    return apiClient.get('/payslips', { params });
  }

  // ========== Payroll Item Definition Management ==========

  /**
   * 取得薪資項目定義列表
   */
  static async getPayrollItemDefinitions(): Promise<PayrollItemDefinitionDto[]> {
    return apiClient.get('/payroll-item-definitions');
  }

  /**
   * 建立薪資項目定義
   */
  static async createPayrollItemDefinition(data: Partial<PayrollItemDefinitionDto>): Promise<PayrollItemDefinitionDto> {
    return apiClient.post('/payroll-item-definitions', data);
  }

  /**
   * 更新薪資項目定義
   */
  static async updatePayrollItemDefinition(id: string, data: Partial<PayrollItemDefinitionDto>): Promise<PayrollItemDefinitionDto> {
    return apiClient.put(`/payroll-item-definitions/${id}`, data);
  }

  // ========== Bank Transfer Management ==========

  /**
   * 產生銀行薪轉檔案
   */
  static async generateBankTransferFile(runId: string): Promise<PayrollRunDto> {
    return apiClient.post(`/payroll-runs/${runId}/bank-transfer`, {});
  }

  /**
   * 取得銀行薪轉檔案下載 URL
   */
  static async getBankTransferDownloadUrl(runId: string): Promise<string> {
    return apiClient.get(`/payroll-runs/${runId}/bank-transfer/download`);
  }
}
