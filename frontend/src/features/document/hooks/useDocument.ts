/**
 * Document Hooks (文件管理 Hooks)
 * Domain Code: HR13
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { message } from 'antd';
import { DocumentApi } from '../api';
import { DocumentViewModelFactory } from '../factory/DocumentViewModelFactory';
import type {
  DocumentType,
  DocumentRequestStatus,
  TemplateType,
  GenerateDocumentRequest,
} from '../api/DocumentTypes';

// ========== Query Keys ==========

const DOCUMENT_KEYS = {
  all: ['documents'] as const,
  myDocuments: (params?: { documentType?: DocumentType; keyword?: string }) =>
    [...DOCUMENT_KEYS.all, 'my', params] as const,
  documents: (params?: { ownerId?: string; documentType?: DocumentType }) =>
    [...DOCUMENT_KEYS.all, 'list', params] as const,
  templates: (params?: { templateType?: TemplateType; isActive?: boolean }) =>
    [...DOCUMENT_KEYS.all, 'templates', params] as const,
  requests: (params?: { status?: DocumentRequestStatus; templateType?: TemplateType }) =>
    [...DOCUMENT_KEYS.all, 'requests', params] as const,
};

// ========== Hooks ==========

/**
 * 取得我的文件列表
 */
export const useMyDocuments = (params?: {
  documentType?: DocumentType;
  keyword?: string;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: DOCUMENT_KEYS.myDocuments(params),
    queryFn: async () => {
      const response = await DocumentApi.getMyDocuments({
        document_type: params?.documentType,
        keyword: params?.keyword,
        page: params?.page,
        page_size: params?.pageSize,
      });
      return {
        documents: DocumentViewModelFactory.createDocumentListFromDTOs(response.documents),
        pagination: response.pagination,
      };
    },
  });
};

/**
 * 取得文件列表 (管理者)
 */
export const useDocuments = (params?: {
  ownerId?: string;
  documentType?: DocumentType;
  keyword?: string;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: DOCUMENT_KEYS.documents(params),
    queryFn: async () => {
      const response = await DocumentApi.getDocuments({
        owner_id: params?.ownerId,
        document_type: params?.documentType,
        keyword: params?.keyword,
        page: params?.page,
        page_size: params?.pageSize,
      });
      return {
        documents: DocumentViewModelFactory.createDocumentListFromDTOs(response.documents),
        pagination: response.pagination,
      };
    },
  });
};

/**
 * 取得文件範本列表
 */
export const useTemplates = (params?: {
  templateType?: TemplateType;
  isActive?: boolean;
  keyword?: string;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: DOCUMENT_KEYS.templates(params),
    queryFn: async () => {
      const response = await DocumentApi.getTemplates({
        template_type: params?.templateType,
        is_active: params?.isActive,
        keyword: params?.keyword,
        page: params?.page,
        page_size: params?.pageSize,
      });
      return {
        templates: DocumentViewModelFactory.createTemplateListFromDTOs(response.templates),
        pagination: response.pagination,
      };
    },
  });
};

/**
 * 取得我的文件申請記錄
 */
export const useMyDocumentRequests = (params?: {
  status?: DocumentRequestStatus;
  templateType?: TemplateType;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: DOCUMENT_KEYS.requests(params),
    queryFn: async () => {
      const response = await DocumentApi.getMyDocumentRequests({
        status: params?.status,
        template_type: params?.templateType,
        page: params?.page,
        page_size: params?.pageSize,
      });
      return {
        requests: DocumentViewModelFactory.createRequestListFromDTOs(response.requests),
        pagination: response.pagination,
      };
    },
  });
};

/**
 * 上傳文件
 */
export const useUploadDocument = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (params: {
      file: File;
      documentType: DocumentType;
      businessType?: string;
      businessId?: string;
    }) => {
      const formData = new FormData();
      formData.append('file', params.file);
      formData.append('document_type', params.documentType);
      if (params.businessType) {
        formData.append('business_type', params.businessType);
      }
      if (params.businessId) {
        formData.append('business_id', params.businessId);
      }
      return DocumentApi.uploadDocument(formData);
    },
    onSuccess: () => {
      message.success('文件上傳成功');
      queryClient.invalidateQueries({ queryKey: DOCUMENT_KEYS.all });
    },
    onError: () => {
      message.error('文件上傳失敗');
    },
  });
};

/**
 * 刪除文件
 */
export const useDeleteDocument = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (documentId: string) => DocumentApi.deleteDocument(documentId),
    onSuccess: () => {
      message.success('文件刪除成功');
      queryClient.invalidateQueries({ queryKey: DOCUMENT_KEYS.all });
    },
    onError: () => {
      message.error('文件刪除失敗');
    },
  });
};

/**
 * 下載文件
 */
export const useDownloadDocument = () => {
  return useMutation({
    mutationFn: async (documentId: string) => {
      const response = await DocumentApi.getDownloadUrl(documentId);
      // 開啟下載連結
      window.open(response.download_url, '_blank');
      return response;
    },
    onError: () => {
      message.error('取得下載連結失敗');
    },
  });
};

/**
 * 申請產生文件
 */
export const useGenerateDocument = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: GenerateDocumentRequest) => DocumentApi.generateDocument(request),
    onSuccess: () => {
      message.success('文件申請已送出');
      queryClient.invalidateQueries({ queryKey: DOCUMENT_KEYS.requests() });
    },
    onError: () => {
      message.error('文件申請失敗');
    },
  });
};

/**
 * 取得可申請的文件類型
 */
export const useAvailableDocumentTypes = () => {
  return useQuery({
    queryKey: [...DOCUMENT_KEYS.all, 'available-types'],
    queryFn: async () => {
      // 從範本列表取得可用類型
      const response = await DocumentApi.getTemplates({ is_active: true });
      return DocumentViewModelFactory.createAvailableTypesFromTemplates(response.templates);
    },
  });
};

// Re-export for backward compatibility
export const useDocument = () => {
  const myDocumentsQuery = useMyDocuments();
  const uploadMutation = useUploadDocument();
  const deleteMutation = useDeleteDocument();
  const downloadMutation = useDownloadDocument();

  return {
    documents: myDocumentsQuery.data?.documents ?? [],
    loading: myDocumentsQuery.isLoading,
    error: myDocumentsQuery.error,
    uploadDocument: uploadMutation.mutate,
    deleteDocument: deleteMutation.mutate,
    downloadDocument: downloadMutation.mutate,
    isUploading: uploadMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};
