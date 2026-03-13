import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { DocumentAdminTable } from './DocumentAdminTable';

// Mock hooks
vi.mock('../hooks', () => ({
  useDocuments: vi.fn(),
  useDeleteDocument: vi.fn(),
  useDownloadDocument: vi.fn(),
}));

import { useDocuments, useDeleteDocument, useDownloadDocument } from '../hooks';

const mockDocuments = [
  {
    documentId: 'doc-001',
    originalFileName: '員工合約.pdf',
    documentType: 'CONTRACT',
    documentTypeLabel: '合約',
    ownerName: '陳志強',
    visibilityLabel: '私人',
    visibilityColor: 'default',
    fileSizeDisplay: '2.5 MB',
    version: 1,
    uploadedAtDisplay: '2026-03-01 10:00',
    isPdf: true,
    isImage: false,
    isEncrypted: false,
  },
  {
    documentId: 'doc-002',
    originalFileName: '薪資單_202603.pdf',
    documentType: 'PAYSLIP',
    documentTypeLabel: '薪資單',
    ownerName: '王大明',
    visibilityLabel: '私人',
    visibilityColor: 'default',
    fileSizeDisplay: '150 KB',
    version: 1,
    uploadedAtDisplay: '2026-03-05 08:00',
    isPdf: true,
    isImage: false,
    isEncrypted: true,
  },
];

describe('DocumentAdminTable', () => {
  beforeEach(() => {
    vi.mocked(useDeleteDocument).mockReturnValue({ mutate: vi.fn(), isPending: false } as any);
    vi.mocked(useDownloadDocument).mockReturnValue({ mutate: vi.fn() } as any);
  });

  describe('正常渲染', () => {
    it('應顯示文件管理表格', () => {
      vi.mocked(useDocuments).mockReturnValue({
        data: { documents: mockDocuments, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentAdminTable />);

      expect(screen.getByText('員工合約.pdf')).toBeInTheDocument();
      expect(screen.getByText('薪資單_202603.pdf')).toBeInTheDocument();
      expect(screen.getByText('陳志強')).toBeInTheDocument();
      expect(screen.getByText('王大明')).toBeInTheDocument();
    });

    it('應顯示重新整理按鈕', () => {
      vi.mocked(useDocuments).mockReturnValue({
        data: { documents: mockDocuments, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentAdminTable />);

      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 loading', () => {
      vi.mocked(useDocuments).mockReturnValue({
        data: undefined,
        isLoading: true,
        refetch: vi.fn(),
      } as any);

      const { container } = render(<DocumentAdminTable />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
