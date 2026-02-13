/**
 * Document API (文件管理 API)
 * Domain Code: HR13
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockDocumentApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    CreateTemplateRequest,
    CreateTemplateResponse,
    GenerateDocumentRequest,
    GenerateDocumentResponse,
    GetDocumentRequestsRequest,
    GetDocumentRequestsResponse,
    GetDocumentsRequest,
    GetDocumentsResponse,
    GetDownloadUrlResponse,
    GetMyDocumentsRequest,
    GetMyDocumentsResponse,
    GetTemplatesRequest,
    GetTemplatesResponse,
    UploadDocumentResponse,
} from './DocumentTypes';

const BASE_URL = '/documents';

export const DocumentApi = {
  // ========== My Documents ==========

  /**
   * 取得我的文件列表
   */
  getMyDocuments: async (params?: GetMyDocumentsRequest): Promise<GetMyDocumentsResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { documents: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return apiClient.get<GetMyDocumentsResponse>(`${BASE_URL}/my`, { params });
  },

  // ========== Documents (Admin) ==========

  /**
   * 取得文件列表
   */
  getDocuments: async (params?: GetDocumentsRequest): Promise<GetDocumentsResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) {
      const res = await MockDocumentApi.getDocuments();
      return { documents: res.documents, pagination: { page: 1, page_size: 10, total: res.total, total_pages: 1 } };
    }
    return apiClient.get<GetDocumentsResponse>(BASE_URL, { params });
  },

  /**
   * 上傳文件
   */
  uploadDocument: async (formData: FormData): Promise<UploadDocumentResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { document: {} as any, message: '文件已上傳 (Mock)' };
    return apiClient.post<UploadDocumentResponse>(`${BASE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  /**
   * 取得文件下載連結
   */
  getDownloadUrl: async (documentId: string): Promise<GetDownloadUrlResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { download_url: '#', expires_at: '' };
    return apiClient.get<GetDownloadUrlResponse>(`${BASE_URL}/${documentId}/download`);
  },

  /**
   * 刪除文件
   */
  deleteDocument: async (documentId: string): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { message: '文件已刪除 (Mock)' };
    return apiClient.delete<{ message: string }>(`${BASE_URL}/${documentId}`);
  },

  // ========== Document Generation ==========

  /**
   * 申請產生文件
   */
  generateDocument: async (request: GenerateDocumentRequest): Promise<GenerateDocumentResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { request: {} as any, message: '申請已提交 (Mock)' };
    return apiClient.post<GenerateDocumentResponse>(`${BASE_URL}/generate`, request);
  },

  /**
   * 取得我的文件申請記錄
   */
  getMyDocumentRequests: async (
    params?: GetDocumentRequestsRequest
  ): Promise<GetDocumentRequestsResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { requests: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return apiClient.get<GetDocumentRequestsResponse>(`${BASE_URL}/request`, { params });
  },

  // ========== Templates (Admin) ==========

  /**
   * 取得文件範本列表
   */
  getTemplates: async (params?: GetTemplatesRequest): Promise<GetTemplatesResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { templates: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return apiClient.get<GetTemplatesResponse>(`${BASE_URL}/templates`, { params });
  },

  /**
   * 建立文件範本
   */
  createTemplate: async (request: CreateTemplateRequest): Promise<CreateTemplateResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { template: {} as any, message: '範本已建立 (Mock)' };
    return apiClient.post<CreateTemplateResponse>(`${BASE_URL}/templates`, request);
  },
};
