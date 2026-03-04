-- ============================================================================
-- HR13 Document Service - Local Seed Data (H2)
-- ============================================================================

-- 文件 (3 筆: PDF, DOCX, XLSX)
INSERT INTO hrs_documents (document_id, document_type, business_type, business_id, file_name, mime_type, file_size, storage_path, visibility, classification, is_encrypted, owner_id, is_deleted, folder_id, tags, uploaded_at, updated_at) VALUES
('DOC-001', 'CONTRACT', 'EMPLOYEE', '00000000-0000-0000-0000-000000000001', '勞動契約_張小明.pdf', 'application/pdf', 524288, '/documents/contracts/DOC-001.pdf', 'PRIVATE', 'CONFIDENTIAL', FALSE, '00000000-0000-0000-0000-000000000001', FALSE, NULL, '勞動契約,HR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DOC-002', 'POLICY', 'COMPANY', 'COMPANY-001', '員工手冊_v3.2.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1048576, '/documents/policies/DOC-002.docx', 'PUBLIC', 'INTERNAL', FALSE, 'admin', FALSE, NULL, '員工手冊,政策', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DOC-003', 'REPORT', 'PAYROLL', 'PAY-2026-02', '2026年2月薪資總表.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 262144, '/documents/reports/DOC-003.xlsx', 'DEPARTMENT', 'RESTRICTED', TRUE, 'admin', FALSE, 'FOLDER-HR', '薪資,月報', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 文件版本 (3 筆)
INSERT INTO hrs_document_versions (version_id, document_id, version_number, file_name, file_size, storage_path, uploader_id, uploaded_at, change_note) VALUES
('VER-001', 'DOC-001', 1, '勞動契約_張小明.pdf', 524288, '/documents/contracts/DOC-001_v1.pdf', 'admin', CURRENT_TIMESTAMP, '初始版本'),
('VER-002', 'DOC-002', 1, '員工手冊_v3.1.docx', 1000000, '/documents/policies/DOC-002_v1.docx', 'admin', CURRENT_TIMESTAMP, '初始版本'),
('VER-003', 'DOC-002', 2, '員工手冊_v3.2.docx', 1048576, '/documents/policies/DOC-002_v2.docx', 'admin', CURRENT_TIMESTAMP, '更新差勤規定章節');

-- 文件範本 (2 筆: 在職證明 + 離職證明)
INSERT INTO hrs_document_templates (template_id, template_code, name, content, category, status, created_at, updated_at, is_deleted) VALUES
('TMPL-001', 'EMPLOYMENT_CERT', '在職證明', '茲證明 {{employeeName}} 先生/女士，身分證字號 {{nationalId}}，自 {{hireDate}} 起任職於本公司 {{departmentName}} 部門，擔任 {{positionName}} 一職，目前仍在職中。特此證明。', 'HR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('TMPL-002', 'RESIGNATION_CERT', '離職證明', '茲證明 {{employeeName}} 先生/女士，身分證字號 {{nationalId}}，於 {{hireDate}} 至 {{resignationDate}} 期間任職於本公司 {{departmentName}} 部門，擔任 {{positionName}} 一職。特此證明。', 'HR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 文件申請 (2 筆: COMPLETED + PENDING)
INSERT INTO hrs_document_requests (request_id, template_code, requester_id, purpose, status, requested_at, document_id) VALUES
('REQ-001', 'EMPLOYMENT_CERT', '00000000-0000-0000-0000-000000000001', '辦理房貸需在職證明', 'COMPLETED', CURRENT_TIMESTAMP, 'DOC-001'),
('REQ-002', 'EMPLOYMENT_CERT', '00000000-0000-0000-0000-000000000002', '子女就學需在職證明', 'PENDING', CURRENT_TIMESTAMP, NULL);
