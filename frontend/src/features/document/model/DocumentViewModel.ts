/**
 * Document ViewModels (文件管理 前端視圖模型)
 * Domain Code: HR13
 */

import type {
  DocumentRequestStatus,
  DocumentType,
  DocumentVisibility,
  TemplateType,
} from '../api/DocumentTypes';

/**
 * 文件 ViewModel
 */
export interface DocumentViewModel {
  documentId: string;
  documentType: DocumentType;
  documentTypeLabel: string;
  documentTypeIcon: string;
  businessType: string;
  businessId: string;
  fileName: string;
  originalFileName: string;
  fileSize: number;
  fileSizeDisplay: string;
  mimeType: string;
  storagePath: string;
  isEncrypted: boolean;
  ownerId: string;
  ownerName: string;
  visibility: DocumentVisibility;
  visibilityLabel: string;
  visibilityColor: string;
  version: number;
  uploadedBy: string;
  uploadedByName: string;
  uploadedAt: string;
  uploadedAtDisplay: string;
  canDownload: boolean;
  canDelete: boolean;
  isImage: boolean;
  isPdf: boolean;
}

/**
 * 文件範本 ViewModel
 */
export interface DocumentTemplateViewModel {
  templateId: string;
  templateCode: string;
  templateName: string;
  templateType: TemplateType;
  templateTypeLabel: string;
  templateContent?: string;
  templateFilePath?: string;
  variables: string[];
  variablesDisplay: string;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  createdAt: string;
  createdAtDisplay: string;
}

/**
 * 文件申請 ViewModel
 */
export interface DocumentRequestViewModel {
  requestId: string;
  templateType: TemplateType;
  templateTypeLabel: string;
  templateName: string;
  requesterId: string;
  requesterName: string;
  status: DocumentRequestStatus;
  statusLabel: string;
  statusColor: string;
  documentId?: string;
  downloadUrl?: string;
  requestDate: string;
  requestDateDisplay: string;
  generatedAt?: string;
  generatedAtDisplay?: string;
  canDownload: boolean;
  isProcessing: boolean;
}

/**
 * 可申請文件類型 ViewModel
 */
export interface AvailableDocumentTypeViewModel {
  templateType: TemplateType;
  templateTypeLabel: string;
  description: string;
  icon: string;
  requiresApproval: boolean;
}

/**
 * 文件統計 ViewModel
 */
export interface DocumentSummaryViewModel {
  totalDocuments: number;
  contractCount: number;
  certificateCount: number;
  payslipCount: number;
  generatedCount: number;
}
