import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMyDocuments, useTemplates } from './useDocument';
import { DocumentApi } from '../api';

vi.mock('../api', () => ({
  DocumentApi: {
    getMyDocuments: vi.fn(),
    getDocuments: vi.fn(),
    getTemplates: vi.fn(),
    getMyDocumentRequests: vi.fn(),
    uploadDocument: vi.fn(),
    deleteDocument: vi.fn(),
    getDownloadUrl: vi.fn(),
    generateDocument: vi.fn(),
    updateTemplate: vi.fn(),
    deleteTemplate: vi.fn(),
    getDocumentVersions: vi.fn(),
    getDownloadLogs: vi.fn(),
  },
}));

vi.mock('antd', async () => {
  const actual = await vi.importActual('antd');
  return { ...actual, message: { success: vi.fn(), error: vi.fn() } };
});

const mockMyDocumentsResponse = {
  documents: [
    {
      id: 'doc-001',
      document_type: 'CONTRACT',
      business_type: 'EMPLOYEE',
      business_id: 'emp-001',
      file_name: 'contract.pdf',
      original_file_name: '勞動合約.pdf',
      file_size: 2048576,
      mime_type: 'application/pdf',
      storage_path: '/documents/contract.pdf',
      is_encrypted: false,
      owner_id: 'emp-001',
      owner_name: '王大明',
      visibility: 'PRIVATE',
      version: 1,
      uploaded_by: 'hr-001',
      uploaded_by_name: '李小美',
      uploaded_at: '2026-03-01T10:00:00Z',
      created_at: '2026-03-01T10:00:00Z',
      updated_at: '2026-03-01T10:00:00Z',
    },
  ],
  pagination: { page: 1, page_size: 10, total: 1, total_pages: 1 },
};

const mockTemplatesResponse = {
  templates: [
    {
      id: 'tpl-001',
      template_code: 'EMPLOYMENT_CERT',
      template_name: '在職證明',
      template_type: 'EMPLOYMENT_CERTIFICATE',
      variables: ['employeeName'],
      is_active: true,
      created_at: '2026-01-01T00:00:00Z',
      updated_at: '2026-03-01T00:00:00Z',
    },
  ],
  pagination: { page: 1, page_size: 10, total: 1, total_pages: 1 },
};

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return ({ children }: { children: React.ReactNode }) =>
    React.createElement(QueryClientProvider, { client: queryClient }, children);
}

describe('useMyDocuments', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入我的文件列表', async () => {
    vi.mocked(DocumentApi.getMyDocuments).mockResolvedValue(mockMyDocumentsResponse as any);

    const { result } = renderHook(() => useMyDocuments(), { wrapper: createWrapper() });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.documents).toHaveLength(1);
    expect(result.current.data?.documents[0]!.documentId).toBe('doc-001');
    expect(result.current.data?.documents[0]!.documentTypeLabel).toBe('合約');
  });

  it('應正確處理錯誤', async () => {
    vi.mocked(DocumentApi.getMyDocuments).mockRejectedValue(new Error('載入失敗'));

    const { result } = renderHook(() => useMyDocuments(), { wrapper: createWrapper() });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });
  });
});

describe('useTemplates', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入範本列表', async () => {
    vi.mocked(DocumentApi.getTemplates).mockResolvedValue(mockTemplatesResponse as any);

    const { result } = renderHook(() => useTemplates(), { wrapper: createWrapper() });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.templates).toHaveLength(1);
    expect(result.current.data?.templates[0]!.templateId).toBe('tpl-001');
    expect(result.current.data?.templates[0]!.templateTypeLabel).toBe('在職證明');
  });
});
