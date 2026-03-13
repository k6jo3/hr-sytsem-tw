import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { DocumentListPanel } from './DocumentListPanel';

// Mock hooks
vi.mock('../hooks', () => ({
  useMyDocuments: vi.fn(),
  useUploadDocument: vi.fn(),
  useDeleteDocument: vi.fn(),
  useDownloadDocument: vi.fn(),
}));

import { useMyDocuments, useUploadDocument, useDeleteDocument, useDownloadDocument } from '../hooks';

const mockDocuments = [
  {
    documentId: 'doc-001',
    originalFileName: '在職證明.pdf',
    documentType: 'CERTIFICATE',
    documentTypeLabel: '證明文件',
    fileSizeDisplay: '125 KB',
    visibilityLabel: '私人',
    visibilityColor: 'default',
    uploadedAtDisplay: '2026-03-05 09:00',
    canDownload: true,
    canDelete: true,
    isPdf: true,
    isImage: false,
    isEncrypted: false,
  },
  {
    documentId: 'doc-002',
    originalFileName: '大頭照.jpg',
    documentType: 'UPLOADED',
    documentTypeLabel: '上傳文件',
    fileSizeDisplay: '2.1 MB',
    visibilityLabel: '部門',
    visibilityColor: 'blue',
    uploadedAtDisplay: '2026-03-04 15:00',
    canDownload: true,
    canDelete: true,
    isPdf: false,
    isImage: true,
    isEncrypted: false,
  },
];

describe('DocumentListPanel', () => {
  beforeEach(() => {
    vi.mocked(useUploadDocument).mockReturnValue({ mutate: vi.fn(), isPending: false } as any);
    vi.mocked(useDeleteDocument).mockReturnValue({ mutate: vi.fn(), isPending: false } as any);
    vi.mocked(useDownloadDocument).mockReturnValue({ mutate: vi.fn() } as any);
  });

  describe('正常渲染', () => {
    it('應顯示文件列表', () => {
      vi.mocked(useMyDocuments).mockReturnValue({
        data: { documents: mockDocuments, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentListPanel />);

      expect(screen.getByText('在職證明.pdf')).toBeInTheDocument();
      expect(screen.getByText('大頭照.jpg')).toBeInTheDocument();
      expect(screen.getByText('證明文件')).toBeInTheDocument();
      expect(screen.getByText('125 KB')).toBeInTheDocument();
    });

    it('應顯示上傳按鈕與重新整理按鈕', () => {
      vi.mocked(useMyDocuments).mockReturnValue({
        data: { documents: mockDocuments, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentListPanel />);

      // '上傳文件' 同時出現在 Select option 和 Button，使用 role 區分
      expect(screen.getByRole('button', { name: /上傳文件/ })).toBeInTheDocument();
      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中表格應顯示 loading', () => {
      vi.mocked(useMyDocuments).mockReturnValue({
        data: undefined,
        isLoading: true,
        refetch: vi.fn(),
      } as any);

      const { container } = render(<DocumentListPanel />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
