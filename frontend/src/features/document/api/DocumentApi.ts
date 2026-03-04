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
    DocumentDto,
    DocumentRequestDto,
    DocumentTemplateDto,
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
    PaginationInfo,
    UploadDocumentResponse,
} from './DocumentTypes';

const BASE_URL = '/documents';

// ========== Response Adapter ==========

/** 分頁參數轉換（前端 1-indexed → 後端 0-indexed） */
function adaptPageParams(params?: { page?: number; page_size?: number; [key: string]: any }) {
  if (!params) return params;
  const { page, page_size, ...rest } = params;
  return {
    ...rest,
    ...(page != null ? { page: page - 1 } : {}),
    ...(page_size != null ? { size: page_size } : {}),
  };
}

/** Spring Page → 前端分頁格式 */
function adaptPagination(raw: any): PaginationInfo {
  return {
    page: (raw.pageable?.pageNumber ?? raw.number ?? 0) + 1,
    page_size: raw.pageable?.pageSize ?? raw.size ?? 10,
    total: raw.totalElements ?? raw.total ?? 0,
    total_pages: raw.totalPages ?? 0,
  };
}

/** 後端 camelCase → 前端 DocumentDto */
function adaptDocument(raw: any): DocumentDto {
  return {
    id: raw.documentId ?? raw.id ?? '',
    document_type: raw.documentType ?? raw.document_type ?? 'UPLOADED',
    business_type: raw.businessType ?? raw.business_type ?? '',
    business_id: raw.businessId ?? raw.business_id ?? '',
    file_name: raw.fileName ?? raw.file_name ?? '',
    original_file_name: raw.fileName ?? raw.originalFileName ?? raw.file_name ?? '',
    file_size: raw.fileSize ?? raw.file_size ?? 0,
    mime_type: raw.mimeType ?? raw.mime_type ?? '',
    storage_path: raw.storagePath ?? raw.storage_path ?? '',
    is_encrypted: raw.encrypted ?? raw.isEncrypted ?? raw.is_encrypted ?? false,
    owner_id: raw.ownerId ?? raw.owner_id ?? '',
    owner_name: raw.ownerName ?? raw.owner_name ?? '',
    visibility: raw.visibility ?? 'PRIVATE',
    version: raw.version ?? 1,
    uploaded_by: raw.uploadedBy ?? raw.uploaded_by ?? '',
    uploaded_by_name: raw.uploadedByName ?? raw.uploaded_by_name ?? '',
    uploaded_at: raw.uploadedAt ?? raw.uploaded_at ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? raw.uploadedAt ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/** 後端 camelCase → 前端 DocumentTemplateDto */
function adaptTemplate(raw: any): DocumentTemplateDto {
  return {
    id: raw.templateId ?? raw.id ?? '',
    template_code: raw.templateCode ?? raw.template_code ?? '',
    template_name: raw.name ?? raw.templateName ?? raw.template_name ?? '',
    template_type: raw.category ?? raw.templateType ?? raw.template_type ?? 'CUSTOM',
    template_content: raw.content ?? raw.templateContent ?? raw.template_content ?? '',
    template_file_path: raw.templateFilePath ?? raw.template_file_path ?? '',
    variables: raw.variables ?? [],
    is_active: raw.status === 'ACTIVE' || (raw.isActive ?? raw.is_active ?? true),
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/** 後端 camelCase → 前端 DocumentRequestDto */
function adaptRequest(raw: any): DocumentRequestDto {
  return {
    id: raw.requestId ?? raw.id ?? '',
    template_id: raw.templateCode ?? raw.templateId ?? raw.template_id ?? '',
    template_type: raw.templateType ?? raw.template_type ?? 'CUSTOM',
    template_name: raw.templateName ?? raw.template_name ?? '',
    requester_id: raw.requesterId ?? raw.requester_id ?? '',
    requester_name: raw.requesterName ?? raw.requester_name ?? '',
    status: raw.status ?? 'PENDING',
    document_id: raw.documentId ?? raw.document_id ?? '',
    download_url: raw.downloadUrl ?? raw.download_url ?? '',
    request_date: raw.requestedAt ?? raw.request_date ?? '',
    generated_at: raw.generatedAt ?? raw.generated_at ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

export const DocumentApi = {
  // ========== My Documents ==========

  /**
   * 取得我的文件列表
   */
  getMyDocuments: async (params?: GetMyDocumentsRequest): Promise<GetMyDocumentsResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { documents: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const raw = await apiClient.get<any>(`${BASE_URL}/my`, { params: adaptPageParams(params) });
    const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
    return {
      documents: content.map(adaptDocument),
      pagination: adaptPagination(raw),
    };
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
    const raw = await apiClient.get<any>(BASE_URL, { params: adaptPageParams(params) });
    const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
    return {
      documents: content.map(adaptDocument),
      pagination: adaptPagination(raw),
    };
  },

  /**
   * 上傳文件
   */
  uploadDocument: async (formData: FormData): Promise<UploadDocumentResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { document: {} as any, message: '文件已上傳 (Mock)' };
    const raw = await apiClient.post<any>(`${BASE_URL}/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return { document: adaptDocument(raw), message: raw.message ?? '文件已上傳' };
  },

  /**
   * 取得文件下載連結
   */
  getDownloadUrl: async (documentId: string): Promise<GetDownloadUrlResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { download_url: '#', expires_at: '' };
    const raw = await apiClient.get<any>(`${BASE_URL}/${documentId}/download`);
    return { download_url: raw.downloadUrl ?? raw.download_url ?? '#', expires_at: raw.expiresAt ?? raw.expires_at ?? '' };
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
    const raw = await apiClient.post<any>(`${BASE_URL}/generate`, request);
    return { request: adaptRequest(raw), message: raw.message ?? '申請已提交' };
  },

  /**
   * 取得我的文件申請記錄
   */
  getMyDocumentRequests: async (
    params?: GetDocumentRequestsRequest
  ): Promise<GetDocumentRequestsResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { requests: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const raw = await apiClient.get<any>(`${BASE_URL}/requests`, { params: adaptPageParams(params) });
    const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
    return {
      requests: content.map(adaptRequest),
      pagination: adaptPagination(raw),
    };
  },

  // ========== Templates (Admin) ==========

  /**
   * 取得文件範本列表
   */
  getTemplates: async (params?: GetTemplatesRequest): Promise<GetTemplatesResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { templates: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const raw = await apiClient.get<any>(`${BASE_URL}/templates`, { params: adaptPageParams(params) });
    const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
    return {
      templates: content.map(adaptTemplate),
      pagination: adaptPagination(raw),
    };
  },

  /**
   * 建立文件範本
   */
  createTemplate: async (request: CreateTemplateRequest): Promise<CreateTemplateResponse> => {
    if (MockConfig.isEnabled('DOCUMENT')) return { template: {} as any, message: '範本已建立 (Mock)' };
    const raw = await apiClient.post<any>(`${BASE_URL}/templates`, request);
    return { template: adaptTemplate(raw), message: raw.message ?? '範本已建立' };
  },
};
