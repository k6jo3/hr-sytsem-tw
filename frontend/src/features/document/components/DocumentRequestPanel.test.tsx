import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { DocumentRequestPanel } from './DocumentRequestPanel';

// Mock hooks
vi.mock('../hooks', () => ({
  useAvailableDocumentTypes: vi.fn(),
  useMyDocumentRequests: vi.fn(),
  useDownloadDocument: vi.fn(),
  useGenerateDocument: vi.fn(),
}));

import { useAvailableDocumentTypes, useMyDocumentRequests, useDownloadDocument, useGenerateDocument } from '../hooks';

const mockAvailableTypes = [
  {
    templateType: 'EMPLOYMENT_CERTIFICATE',
    templateTypeLabel: '在職證明',
    description: '證明員工目前在職狀態',
    icon: 'IdcardOutlined',
    requiresApproval: false,
  },
  {
    templateType: 'SALARY_CERTIFICATE',
    templateTypeLabel: '薪資證明',
    description: '證明員工薪資所得',
    icon: 'DollarOutlined',
    requiresApproval: true,
  },
];

describe('DocumentRequestPanel', () => {
  beforeEach(() => {
    vi.mocked(useDownloadDocument).mockReturnValue({ mutate: vi.fn() } as any);
    vi.mocked(useGenerateDocument).mockReturnValue({ mutate: vi.fn(), isPending: false } as any);
    vi.mocked(useMyDocumentRequests).mockReturnValue({
      data: { requests: [], pagination: { total: 0 } },
      isLoading: false,
      refetch: vi.fn(),
    } as any);
  });

  describe('正常渲染', () => {
    it('應顯示可申請的文件類型', () => {
      vi.mocked(useAvailableDocumentTypes).mockReturnValue({
        data: mockAvailableTypes,
        isLoading: false,
      } as any);

      render(<DocumentRequestPanel />);

      expect(screen.getByText('在職證明')).toBeInTheDocument();
      expect(screen.getByText('薪資證明')).toBeInTheDocument();
      expect(screen.getByText('證明員工目前在職狀態')).toBeInTheDocument();
    });

    it('需審核的類型應顯示需審核標籤', () => {
      vi.mocked(useAvailableDocumentTypes).mockReturnValue({
        data: mockAvailableTypes,
        isLoading: false,
      } as any);

      render(<DocumentRequestPanel />);

      expect(screen.getByText('需審核')).toBeInTheDocument();
    });

    it('應包含申請文件和申請記錄兩個 Tab', () => {
      vi.mocked(useAvailableDocumentTypes).mockReturnValue({
        data: mockAvailableTypes,
        isLoading: false,
      } as any);

      render(<DocumentRequestPanel />);

      expect(screen.getByText('申請文件')).toBeInTheDocument();
      expect(screen.getByText('申請記錄')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無可申請類型時應顯示空狀態', () => {
      vi.mocked(useAvailableDocumentTypes).mockReturnValue({
        data: [],
        isLoading: false,
      } as any);

      render(<DocumentRequestPanel />);

      expect(screen.getByText('目前沒有可申請的文件類型')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useAvailableDocumentTypes).mockReturnValue({
        data: undefined,
        isLoading: true,
      } as any);

      const { container } = render(<DocumentRequestPanel />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
