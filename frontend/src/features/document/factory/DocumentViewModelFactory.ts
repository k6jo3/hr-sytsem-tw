/**
 * Document ViewModel Factory (文件管理視圖模型工廠)
 * Domain Code: HR13
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */

import dayjs from 'dayjs';
import type {
  DocumentDto,
  DocumentTemplateDto,
  DocumentRequestDto,
  DocumentType,
  DocumentVisibility,
  TemplateType,
  DocumentRequestStatus,
} from '../api/DocumentTypes';
import type {
  DocumentViewModel,
  DocumentTemplateViewModel,
  DocumentRequestViewModel,
  AvailableDocumentTypeViewModel,
} from '../model/DocumentViewModel';

// ========== Label Mappings ==========

const DOCUMENT_TYPE_LABELS: Record<DocumentType, string> = {
  CONTRACT: '合約',
  CERTIFICATE: '證明文件',
  PAYSLIP: '薪資單',
  TAX_FORM: '稅務表單',
  GENERATED: '系統產生',
  UPLOADED: '上傳文件',
  ATTACHMENT: '附件',
};

const DOCUMENT_TYPE_ICONS: Record<DocumentType, string> = {
  CONTRACT: 'FileTextOutlined',
  CERTIFICATE: 'SafetyCertificateOutlined',
  PAYSLIP: 'DollarOutlined',
  TAX_FORM: 'AuditOutlined',
  GENERATED: 'FileAddOutlined',
  UPLOADED: 'UploadOutlined',
  ATTACHMENT: 'PaperClipOutlined',
};

const VISIBILITY_LABELS: Record<DocumentVisibility, string> = {
  PRIVATE: '私人',
  DEPARTMENT: '部門',
  COMPANY: '公司',
  PUBLIC: '公開',
};

const VISIBILITY_COLORS: Record<DocumentVisibility, string> = {
  PRIVATE: 'default',
  DEPARTMENT: 'blue',
  COMPANY: 'green',
  PUBLIC: 'orange',
};

const TEMPLATE_TYPE_LABELS: Record<TemplateType, string> = {
  EMPLOYMENT_CERTIFICATE: '在職證明',
  SALARY_CERTIFICATE: '薪資證明',
  RESIGNATION_CERTIFICATE: '離職證明',
  ATTENDANCE_RECORD: '出勤紀錄',
  PAYSLIP: '薪資單',
  TAX_WITHHOLDING: '扣繳憑單',
  CUSTOM: '自訂範本',
};

const TEMPLATE_TYPE_ICONS: Record<TemplateType, string> = {
  EMPLOYMENT_CERTIFICATE: 'IdcardOutlined',
  SALARY_CERTIFICATE: 'DollarOutlined',
  RESIGNATION_CERTIFICATE: 'UserDeleteOutlined',
  ATTENDANCE_RECORD: 'ClockCircleOutlined',
  PAYSLIP: 'FileTextOutlined',
  TAX_WITHHOLDING: 'AuditOutlined',
  CUSTOM: 'FormOutlined',
};

const REQUEST_STATUS_LABELS: Record<DocumentRequestStatus, string> = {
  PENDING: '待處理',
  PROCESSING: '處理中',
  COMPLETED: '已完成',
  FAILED: '失敗',
  CANCELLED: '已取消',
};

const REQUEST_STATUS_COLORS: Record<DocumentRequestStatus, string> = {
  PENDING: 'default',
  PROCESSING: 'processing',
  COMPLETED: 'success',
  FAILED: 'error',
  CANCELLED: 'warning',
};

// ========== Helper Functions ==========

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

const formatDateTime = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm');
};

const isImageFile = (mimeType: string): boolean => {
  return mimeType.startsWith('image/');
};

const isPdfFile = (mimeType: string): boolean => {
  return mimeType === 'application/pdf';
};

// ========== Factory Class ==========

export class DocumentViewModelFactory {
  /**
   * 將 DocumentDto 轉換為 DocumentViewModel
   */
  static createDocumentFromDTO(dto: DocumentDto): DocumentViewModel {
    return {
      documentId: dto.id,
      documentType: dto.document_type,
      documentTypeLabel: DOCUMENT_TYPE_LABELS[dto.document_type] || dto.document_type,
      documentTypeIcon: DOCUMENT_TYPE_ICONS[dto.document_type] || 'FileOutlined',
      businessType: dto.business_type,
      businessId: dto.business_id,
      fileName: dto.file_name,
      originalFileName: dto.original_file_name,
      fileSize: dto.file_size,
      fileSizeDisplay: formatFileSize(dto.file_size),
      mimeType: dto.mime_type,
      storagePath: dto.storage_path,
      isEncrypted: dto.is_encrypted,
      ownerId: dto.owner_id,
      ownerName: dto.owner_name,
      visibility: dto.visibility,
      visibilityLabel: VISIBILITY_LABELS[dto.visibility] || dto.visibility,
      visibilityColor: VISIBILITY_COLORS[dto.visibility] || 'default',
      version: dto.version,
      uploadedBy: dto.uploaded_by,
      uploadedByName: dto.uploaded_by_name,
      uploadedAt: dto.uploaded_at,
      uploadedAtDisplay: formatDateTime(dto.uploaded_at),
      canDownload: true,
      canDelete: true,
      isImage: isImageFile(dto.mime_type),
      isPdf: isPdfFile(dto.mime_type),
    };
  }

  /**
   * 批量轉換文件
   */
  static createDocumentListFromDTOs(dtos: DocumentDto[]): DocumentViewModel[] {
    return dtos.map((dto) => this.createDocumentFromDTO(dto));
  }

  /**
   * 將 DocumentTemplateDto 轉換為 DocumentTemplateViewModel
   */
  static createTemplateFromDTO(dto: DocumentTemplateDto): DocumentTemplateViewModel {
    return {
      templateId: dto.id,
      templateCode: dto.template_code,
      templateName: dto.template_name,
      templateType: dto.template_type,
      templateTypeLabel: TEMPLATE_TYPE_LABELS[dto.template_type] || dto.template_type,
      templateContent: dto.template_content,
      templateFilePath: dto.template_file_path,
      variables: dto.variables,
      variablesDisplay: dto.variables.join(', '),
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '啟用' : '停用',
      statusColor: dto.is_active ? 'success' : 'default',
      createdAt: dto.created_at,
      createdAtDisplay: formatDateTime(dto.created_at),
    };
  }

  /**
   * 批量轉換範本
   */
  static createTemplateListFromDTOs(dtos: DocumentTemplateDto[]): DocumentTemplateViewModel[] {
    return dtos.map((dto) => this.createTemplateFromDTO(dto));
  }

  /**
   * 將 DocumentRequestDto 轉換為 DocumentRequestViewModel
   */
  static createRequestFromDTO(dto: DocumentRequestDto): DocumentRequestViewModel {
    return {
      requestId: dto.id,
      templateType: dto.template_type,
      templateTypeLabel: TEMPLATE_TYPE_LABELS[dto.template_type] || dto.template_type,
      templateName: dto.template_name,
      requesterId: dto.requester_id,
      requesterName: dto.requester_name,
      status: dto.status,
      statusLabel: REQUEST_STATUS_LABELS[dto.status] || dto.status,
      statusColor: REQUEST_STATUS_COLORS[dto.status] || 'default',
      documentId: dto.document_id,
      downloadUrl: dto.download_url,
      requestDate: dto.request_date,
      requestDateDisplay: formatDateTime(dto.request_date),
      generatedAt: dto.generated_at,
      generatedAtDisplay: dto.generated_at ? formatDateTime(dto.generated_at) : undefined,
      canDownload: dto.status === 'COMPLETED' && !!dto.download_url,
      isProcessing: dto.status === 'PENDING' || dto.status === 'PROCESSING',
    };
  }

  /**
   * 批量轉換申請記錄
   */
  static createRequestListFromDTOs(dtos: DocumentRequestDto[]): DocumentRequestViewModel[] {
    return dtos.map((dto) => this.createRequestFromDTO(dto));
  }

  /**
   * 從範本列表建立可申請文件類型
   */
  static createAvailableTypesFromTemplates(
    templates: DocumentTemplateDto[]
  ): AvailableDocumentTypeViewModel[] {
    const typeMap = new Map<TemplateType, DocumentTemplateDto>();

    templates.forEach((template) => {
      if (template.is_active && !typeMap.has(template.template_type)) {
        typeMap.set(template.template_type, template);
      }
    });

    return Array.from(typeMap.entries()).map(([type, template]) => ({
      templateType: type,
      templateTypeLabel: TEMPLATE_TYPE_LABELS[type] || type,
      description: template.template_name,
      icon: TEMPLATE_TYPE_ICONS[type] || 'FileOutlined',
      requiresApproval: type === 'SALARY_CERTIFICATE' || type === 'RESIGNATION_CERTIFICATE',
    }));
  }
}
