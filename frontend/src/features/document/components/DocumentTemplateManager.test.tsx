import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { DocumentTemplateManager } from './DocumentTemplateManager';

// Mock hooks
vi.mock('../hooks', () => ({
  useTemplates: vi.fn(),
}));

// Mock DocumentApi
vi.mock('../api', () => ({
  DocumentApi: {
    createTemplate: vi.fn(),
    updateTemplate: vi.fn(),
    deleteTemplate: vi.fn(),
  },
}));

import { useTemplates } from '../hooks';

const mockTemplates = [
  {
    templateId: 'tmpl-001',
    templateCode: 'EMPLOYMENT_CERT',
    templateName: '在職證明範本',
    templateType: 'EMPLOYMENT_CERTIFICATE',
    templateTypeLabel: '在職證明',
    templateContent: '茲證明 {{employeeName}} 目前在本公司任職',
    variables: ['employeeName', 'department'],
    variablesDisplay: 'employeeName, department',
    isActive: true,
    statusLabel: '啟用中',
    statusColor: 'success',
    createdAtDisplay: '2026-01-15',
  },
  {
    templateId: 'tmpl-002',
    templateCode: 'SALARY_CERT',
    templateName: '薪資證明範本',
    templateType: 'SALARY_CERTIFICATE',
    templateTypeLabel: '薪資證明',
    templateContent: '',
    variables: [],
    variablesDisplay: '',
    isActive: false,
    statusLabel: '已停用',
    statusColor: 'default',
    createdAtDisplay: '2026-02-01',
  },
];

describe('DocumentTemplateManager', () => {
  describe('正常渲染', () => {
    it('應顯示範本列表', () => {
      vi.mocked(useTemplates).mockReturnValue({
        data: { templates: mockTemplates, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentTemplateManager />);

      expect(screen.getByText('EMPLOYMENT_CERT')).toBeInTheDocument();
      expect(screen.getByText('在職證明範本')).toBeInTheDocument();
      expect(screen.getByText('啟用中')).toBeInTheDocument();

      expect(screen.getByText('SALARY_CERT')).toBeInTheDocument();
      expect(screen.getByText('已停用')).toBeInTheDocument();
    });

    it('應顯示新增範本與重新整理按鈕', () => {
      vi.mocked(useTemplates).mockReturnValue({
        data: { templates: mockTemplates, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentTemplateManager />);

      expect(screen.getByText('新增範本')).toBeInTheDocument();
      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });

    it('應顯示編輯與刪除按鈕', () => {
      vi.mocked(useTemplates).mockReturnValue({
        data: { templates: mockTemplates, pagination: { total: 2 } },
        isLoading: false,
        refetch: vi.fn(),
      } as any);

      render(<DocumentTemplateManager />);

      const editButtons = screen.getAllByText('編輯');
      expect(editButtons.length).toBe(2);
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 loading', () => {
      vi.mocked(useTemplates).mockReturnValue({
        data: undefined,
        isLoading: true,
        refetch: vi.fn(),
      } as any);

      const { container } = render(<DocumentTemplateManager />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
