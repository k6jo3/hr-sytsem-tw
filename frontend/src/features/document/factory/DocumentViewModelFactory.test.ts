import { describe, it, expect } from 'vitest';
import { DocumentViewModelFactory } from './DocumentViewModelFactory';
import type {
  DocumentDto,
  DocumentTemplateDto,
  DocumentRequestDto,
} from '../api/DocumentTypes';

describe('DocumentViewModelFactory', () => {
  describe('createDocumentFromDTO', () => {
    it('應正確轉換一般文件', () => {
      const dto: DocumentDto = {
        id: 'doc-001',
        document_type: 'CONTRACT',
        business_type: 'EMPLOYEE',
        business_id: 'emp-001',
        file_name: 'contract_001.pdf',
        original_file_name: '勞動合約.pdf',
        file_size: 2048576,
        mime_type: 'application/pdf',
        storage_path: '/documents/contract_001.pdf',
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
      };

      const vm = DocumentViewModelFactory.createDocumentFromDTO(dto);

      expect(vm.documentId).toBe('doc-001');
      expect(vm.documentTypeLabel).toBe('合約');
      expect(vm.fileSizeDisplay).toContain('MB');
      expect(vm.visibilityLabel).toBe('私人');
      expect(vm.visibilityColor).toBe('default');
      expect(vm.isPdf).toBe(true);
      expect(vm.isImage).toBe(false);
      expect(vm.isEncrypted).toBe(false);
    });

    it('應正確處理加密薪資單', () => {
      const dto: DocumentDto = {
        id: 'doc-002',
        document_type: 'PAYSLIP',
        business_type: 'PAYROLL',
        business_id: 'pay-001',
        file_name: 'payslip_202603.pdf',
        original_file_name: '2026年3月薪資單.pdf',
        file_size: 51200,
        mime_type: 'application/pdf',
        storage_path: '/documents/payslip_202603.pdf',
        is_encrypted: true,
        owner_id: 'emp-001',
        owner_name: '王大明',
        visibility: 'PRIVATE',
        version: 1,
        uploaded_by: 'system',
        uploaded_by_name: '系統',
        uploaded_at: '2026-03-05T00:00:00Z',
        created_at: '2026-03-05T00:00:00Z',
        updated_at: '2026-03-05T00:00:00Z',
      };

      const vm = DocumentViewModelFactory.createDocumentFromDTO(dto);

      expect(vm.documentTypeLabel).toBe('薪資單');
      expect(vm.isEncrypted).toBe(true);
    });

    it('應正確識別圖片文件', () => {
      const dto: DocumentDto = {
        id: 'doc-003',
        document_type: 'CERTIFICATE',
        business_type: '',
        business_id: '',
        file_name: 'cert.jpg',
        original_file_name: '證書.jpg',
        file_size: 512000,
        mime_type: 'image/jpeg',
        storage_path: '/documents/cert.jpg',
        is_encrypted: false,
        owner_id: 'emp-001',
        owner_name: '王大明',
        visibility: 'DEPARTMENT',
        version: 1,
        uploaded_by: 'emp-001',
        uploaded_by_name: '王大明',
        uploaded_at: '2026-03-01T00:00:00Z',
        created_at: '2026-03-01T00:00:00Z',
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = DocumentViewModelFactory.createDocumentFromDTO(dto);

      expect(vm.isImage).toBe(true);
      expect(vm.isPdf).toBe(false);
      expect(vm.visibilityLabel).toBe('部門');
      expect(vm.visibilityColor).toBe('blue');
    });
  });

  describe('createTemplateFromDTO', () => {
    it('應正確轉換啟用中的範本', () => {
      const dto: DocumentTemplateDto = {
        id: 'tpl-001',
        template_code: 'EMPLOYMENT_CERT',
        template_name: '在職證明',
        template_type: 'EMPLOYMENT_CERTIFICATE',
        template_content: '茲證明 {{employeeName}} 目前任職於本公司',
        variables: ['employeeName', 'department', 'position'],
        is_active: true,
        created_at: '2026-01-01T00:00:00Z',
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = DocumentViewModelFactory.createTemplateFromDTO(dto);

      expect(vm.templateId).toBe('tpl-001');
      expect(vm.templateCode).toBe('EMPLOYMENT_CERT');
      expect(vm.templateTypeLabel).toBe('在職證明');
      expect(vm.variables).toHaveLength(3);
      expect(vm.variablesDisplay).toBe('employeeName, department, position');
      expect(vm.isActive).toBe(true);
      expect(vm.statusLabel).toBe('啟用');
      expect(vm.statusColor).toBe('success');
    });

    it('應正確轉換停用的範本', () => {
      const dto: DocumentTemplateDto = {
        id: 'tpl-002',
        template_code: 'OLD_TEMPLATE',
        template_name: '舊範本',
        template_type: 'CUSTOM',
        variables: [],
        is_active: false,
        created_at: '2025-01-01T00:00:00Z',
        updated_at: '2025-06-01T00:00:00Z',
      };

      const vm = DocumentViewModelFactory.createTemplateFromDTO(dto);

      expect(vm.isActive).toBe(false);
      expect(vm.statusLabel).toBe('停用');
      expect(vm.statusColor).toBe('default');
      expect(vm.variablesDisplay).toBe('');
    });
  });

  describe('createRequestFromDTO', () => {
    it('應正確轉換已完成的申請', () => {
      const dto: DocumentRequestDto = {
        id: 'req-001',
        template_id: 'tpl-001',
        template_type: 'EMPLOYMENT_CERTIFICATE',
        template_name: '在職證明',
        requester_id: 'emp-001',
        requester_name: '王大明',
        status: 'COMPLETED',
        document_id: 'doc-100',
        download_url: '/api/v1/documents/doc-100/download',
        request_date: '2026-03-05T09:00:00Z',
        generated_at: '2026-03-05T09:01:00Z',
        created_at: '2026-03-05T09:00:00Z',
        updated_at: '2026-03-05T09:01:00Z',
      };

      const vm = DocumentViewModelFactory.createRequestFromDTO(dto);

      expect(vm.requestId).toBe('req-001');
      expect(vm.templateTypeLabel).toBe('在職證明');
      expect(vm.statusLabel).toBe('已完成');
      expect(vm.statusColor).toBe('success');
      expect(vm.canDownload).toBe(true);
      expect(vm.isProcessing).toBe(false);
    });

    it('應正確轉換處理中的申請', () => {
      const dto: DocumentRequestDto = {
        id: 'req-002',
        template_id: 'tpl-002',
        template_type: 'SALARY_CERTIFICATE',
        template_name: '薪資證明',
        requester_id: 'emp-001',
        requester_name: '王大明',
        status: 'PROCESSING',
        request_date: '2026-03-05T10:00:00Z',
        created_at: '2026-03-05T10:00:00Z',
        updated_at: '2026-03-05T10:00:00Z',
      };

      const vm = DocumentViewModelFactory.createRequestFromDTO(dto);

      expect(vm.statusLabel).toBe('處理中');
      expect(vm.canDownload).toBe(false);
      expect(vm.isProcessing).toBe(true);
    });
  });

  describe('createAvailableTypesFromTemplates', () => {
    it('應正確從範本建立可申請類型', () => {
      const templates: DocumentTemplateDto[] = [
        {
          id: 'tpl-1',
          template_code: 'EMP_CERT',
          template_name: '在職證明',
          template_type: 'EMPLOYMENT_CERTIFICATE',
          variables: ['employeeName'],
          is_active: true,
          created_at: '',
          updated_at: '',
        },
        {
          id: 'tpl-2',
          template_code: 'SAL_CERT',
          template_name: '薪資證明',
          template_type: 'SALARY_CERTIFICATE',
          variables: ['employeeName', 'baseSalary'],
          is_active: true,
          created_at: '',
          updated_at: '',
        },
        {
          id: 'tpl-3',
          template_code: 'OLD',
          template_name: '舊範本',
          template_type: 'CUSTOM',
          variables: [],
          is_active: false,
          created_at: '',
          updated_at: '',
        },
      ];

      const types = DocumentViewModelFactory.createAvailableTypesFromTemplates(templates);

      expect(types).toHaveLength(2);
      expect(types[0]!.templateTypeLabel).toBe('在職證明');
      expect(types[1]!.templateTypeLabel).toBe('薪資證明');
      expect(types[1]!.requiresApproval).toBe(true);
    });
  });
});
