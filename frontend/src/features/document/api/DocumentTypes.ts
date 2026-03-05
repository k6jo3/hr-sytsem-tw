/**
 * Document Types (文件管理 API 類型定義)
 * Domain Code: HR13
 */

// ========== Enums ==========

/**
 * 文件類型
 */
export type DocumentType =
  | 'CONTRACT'
  | 'CERTIFICATE'
  | 'PAYSLIP'
  | 'TAX_FORM'
  | 'GENERATED'
  | 'UPLOADED'
  | 'ATTACHMENT';

/**
 * 文件可見性
 */
export type DocumentVisibility = 'PRIVATE' | 'DEPARTMENT' | 'COMPANY' | 'PUBLIC';

/**
 * 範本類型
 */
export type TemplateType =
  | 'EMPLOYMENT_CERTIFICATE'
  | 'SALARY_CERTIFICATE'
  | 'RESIGNATION_CERTIFICATE'
  | 'ATTENDANCE_RECORD'
  | 'PAYSLIP'
  | 'TAX_WITHHOLDING'
  | 'CUSTOM';

/**
 * 文件申請狀態
 */
export type DocumentRequestStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

// ========== DTOs ==========

/**
 * 文件 DTO
 */
export interface DocumentDto {
  id: string;
  document_type: DocumentType;
  business_type: string;
  business_id: string;
  file_name: string;
  original_file_name: string;
  file_size: number;
  mime_type: string;
  storage_path: string;
  is_encrypted: boolean;
  owner_id: string;
  owner_name: string;
  visibility: DocumentVisibility;
  version: number;
  uploaded_by: string;
  uploaded_by_name: string;
  uploaded_at: string;
  created_at: string;
  updated_at: string;
}

/**
 * 文件範本 DTO
 */
export interface DocumentTemplateDto {
  id: string;
  template_code: string;
  template_name: string;
  template_type: TemplateType;
  template_content?: string;
  template_file_path?: string;
  variables: string[];
  is_active: boolean;
  created_at: string;
  updated_at: string;
}

/**
 * 文件申請 DTO
 */
export interface DocumentRequestDto {
  id: string;
  template_id: string;
  template_type: TemplateType;
  template_name: string;
  requester_id: string;
  requester_name: string;
  status: DocumentRequestStatus;
  document_id?: string;
  download_url?: string;
  request_date: string;
  generated_at?: string;
  created_at: string;
  updated_at: string;
}

// ========== Request Types ==========

/**
 * 取得我的文件列表請求
 */
export interface GetMyDocumentsRequest {
  document_type?: DocumentType;
  keyword?: string;
  page?: number;
  page_size?: number;
}

/**
 * 取得文件列表請求 (管理者)
 */
export interface GetDocumentsRequest {
  owner_id?: string;
  document_type?: DocumentType;
  visibility?: DocumentVisibility;
  keyword?: string;
  page?: number;
  page_size?: number;
}

/**
 * 取得文件範本列表請求
 */
export interface GetTemplatesRequest {
  template_type?: TemplateType;
  is_active?: boolean;
  keyword?: string;
  page?: number;
  page_size?: number;
}

/**
 * 取得文件申請記錄請求
 */
export interface GetDocumentRequestsRequest {
  status?: DocumentRequestStatus;
  template_type?: TemplateType;
  page?: number;
  page_size?: number;
}

/**
 * 產生文件請求
 */
export interface GenerateDocumentRequest {
  template_id: string;
  template_type: TemplateType;
  variables?: Record<string, string>;
}

/**
 * 建立文件範本請求
 */
export interface CreateTemplateRequest {
  template_code: string;
  template_name: string;
  template_type: TemplateType;
  template_content?: string;
  variables: string[];
}

/**
 * 更新文件範本請求
 */
export interface UpdateTemplateRequest {
  template_name?: string;
  template_type?: TemplateType;
  template_content?: string;
  variables?: string[];
  is_active?: boolean;
}

/**
 * 下載記錄 DTO
 */
export interface DownloadLogDto {
  id: string;
  document_id: string;
  document_name: string;
  downloaded_by: string;
  downloaded_by_name: string;
  downloaded_at: string;
  ip_address: string;
}

/**
 * 取得下載記錄請求
 */
export interface GetDownloadLogsRequest {
  document_id?: string;
  start_date?: string;
  end_date?: string;
  page?: number;
  page_size?: number;
}

/**
 * 取得下載記錄回應
 */
export interface GetDownloadLogsResponse {
  logs: DownloadLogDto[];
  pagination: PaginationInfo;
}

/**
 * 文件版本 DTO
 */
export interface DocumentVersionDto {
  id: string;
  document_id: string;
  version: number;
  file_name: string;
  file_size: number;
  uploaded_by_name: string;
  uploaded_at: string;
}

/**
 * 取得版本歷史回應
 */
export interface GetDocumentVersionsResponse {
  versions: DocumentVersionDto[];
}

// ========== Response Types ==========

/**
 * 分頁資訊
 */
export interface PaginationInfo {
  page: number;
  page_size: number;
  total: number;
  total_pages: number;
}

/**
 * 取得我的文件列表回應
 */
export interface GetMyDocumentsResponse {
  documents: DocumentDto[];
  pagination: PaginationInfo;
}

/**
 * 取得文件列表回應
 */
export interface GetDocumentsResponse {
  documents: DocumentDto[];
  pagination: PaginationInfo;
}

/**
 * 取得文件範本列表回應
 */
export interface GetTemplatesResponse {
  templates: DocumentTemplateDto[];
  pagination: PaginationInfo;
}

/**
 * 取得文件申請記錄回應
 */
export interface GetDocumentRequestsResponse {
  requests: DocumentRequestDto[];
  pagination: PaginationInfo;
}

/**
 * 上傳文件回應
 */
export interface UploadDocumentResponse {
  document: DocumentDto;
  message: string;
}

/**
 * 取得下載連結回應
 */
export interface GetDownloadUrlResponse {
  download_url: string;
  expires_at: string;
}

/**
 * 產生文件回應
 */
export interface GenerateDocumentResponse {
  request: DocumentRequestDto;
  message: string;
}

/**
 * 建立文件範本回應
 */
export interface CreateTemplateResponse {
  template: DocumentTemplateDto;
  message: string;
}
