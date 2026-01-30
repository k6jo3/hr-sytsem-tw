-- Document 測試資料
-- 用於 QueryEngine 契約測試與業務合約測試

-- 清除舊資料
DELETE FROM hrs_documents;

-- 測試 Document 資料 (共 15 筆)
-- 文件類型分布: CONTRACT=4, POLICY=3, CERTIFICATE=3, PERSONAL=3, REPORT=2
-- 可見度分布: PUBLIC=5, PRIVATE=6, DEPARTMENT=4
-- 分類分布: CONFIDENTIAL=4, INTERNAL=6, PUBLIC=5
INSERT INTO hrs_documents (document_id, document_type, business_type, business_id, file_name, mime_type, file_size, storage_path, visibility, classification, is_encrypted, owner_id, is_deleted, folder_id, tags, uploaded_at, updated_at) VALUES
-- 合約文件 (CONTRACT)
('DOC-001', 'CONTRACT', 'EMPLOYEE', 'E001', '勞動契約_張三.pdf', 'application/pdf', 524288, '/documents/contracts/E001/contract.pdf', 'PRIVATE', 'CONFIDENTIAL', true, 'E001', false, 'F-CONTRACT', '合約,員工', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('DOC-002', 'CONTRACT', 'EMPLOYEE', 'E002', '勞動契約_李四.pdf', 'application/pdf', 512000, '/documents/contracts/E002/contract.pdf', 'PRIVATE', 'CONFIDENTIAL', true, 'E002', false, 'F-CONTRACT', '合約,員工', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('DOC-003', 'CONTRACT', 'VENDOR', 'V001', '供應商合約_ABC公司.pdf', 'application/pdf', 1048576, '/documents/contracts/V001/contract.pdf', 'DEPARTMENT', 'CONFIDENTIAL', true, 'E010', false, 'F-CONTRACT', '合約,供應商', '2025-01-03 09:00:00', '2025-01-03 09:00:00'),
('DOC-004', 'CONTRACT', 'PROJECT', 'P001', '專案合約_數位轉型.pdf', 'application/pdf', 2097152, '/documents/contracts/P001/contract.pdf', 'DEPARTMENT', 'CONFIDENTIAL', true, 'E010', false, 'F-CONTRACT', '合約,專案', '2025-01-04 09:00:00', '2025-01-04 09:00:00'),

-- 政策文件 (POLICY)
('DOC-005', 'POLICY', 'COMPANY', 'POLICY-001', '員工手冊_2025版.pdf', 'application/pdf', 3145728, '/documents/policies/handbook_2025.pdf', 'PUBLIC', 'PUBLIC', false, 'E020', false, 'F-POLICY', '政策,員工手冊', '2025-01-05 09:00:00', '2025-01-05 09:00:00'),
('DOC-006', 'POLICY', 'COMPANY', 'POLICY-002', '請假規則.pdf', 'application/pdf', 262144, '/documents/policies/leave_rules.pdf', 'PUBLIC', 'PUBLIC', false, 'E020', false, 'F-POLICY', '政策,請假', '2025-01-06 09:00:00', '2025-01-06 09:00:00'),
('DOC-007', 'POLICY', 'COMPANY', 'POLICY-003', '資安政策.pdf', 'application/pdf', 524288, '/documents/policies/security_policy.pdf', 'DEPARTMENT', 'INTERNAL', false, 'E020', false, 'F-POLICY', '政策,資安', '2025-01-07 09:00:00', '2025-01-07 09:00:00'),

-- 證書文件 (CERTIFICATE)
('DOC-008', 'CERTIFICATE', 'EMPLOYEE', 'E001', '學歷證書_張三.pdf', 'application/pdf', 131072, '/documents/certificates/E001/degree.pdf', 'PRIVATE', 'INTERNAL', false, 'E001', false, 'F-CERT', '證書,學歷', '2025-01-08 09:00:00', '2025-01-08 09:00:00'),
('DOC-009', 'CERTIFICATE', 'EMPLOYEE', 'E002', '專業證照_李四.pdf', 'application/pdf', 156672, '/documents/certificates/E002/license.pdf', 'PRIVATE', 'INTERNAL', false, 'E002', false, 'F-CERT', '證書,專業', '2025-01-09 09:00:00', '2025-01-09 09:00:00'),
('DOC-010', 'CERTIFICATE', 'EMPLOYEE', 'E003', 'PMP證照_王五.pdf', 'application/pdf', 163840, '/documents/certificates/E003/pmp.pdf', 'PRIVATE', 'INTERNAL', false, 'E003', false, 'F-CERT', '證書,PMP', '2025-01-10 09:00:00', '2025-01-10 09:00:00'),

-- 個人文件 (PERSONAL)
('DOC-011', 'PERSONAL', 'EMPLOYEE', 'E001', '身分證影本_張三.jpg', 'image/jpeg', 204800, '/documents/personal/E001/id.jpg', 'PRIVATE', 'INTERNAL', true, 'E001', false, 'F-PERSONAL', '個人,身分證', '2025-01-11 09:00:00', '2025-01-11 09:00:00'),
('DOC-012', 'PERSONAL', 'EMPLOYEE', 'E002', '戶籍謄本_李四.pdf', 'application/pdf', 256000, '/documents/personal/E002/household.pdf', 'PRIVATE', 'INTERNAL', true, 'E002', false, 'F-PERSONAL', '個人,戶籍', '2025-01-12 09:00:00', '2025-01-12 09:00:00'),
('DOC-013', 'PERSONAL', 'EMPLOYEE', 'E003', '體檢報告_王五.pdf', 'application/pdf', 512000, '/documents/personal/E003/health.pdf', 'PRIVATE', 'INTERNAL', true, 'E003', false, 'F-PERSONAL', '個人,體檢', '2025-01-13 09:00:00', '2025-01-13 09:00:00'),

-- 報表文件 (REPORT)
('DOC-014', 'REPORT', 'HR', 'RPT-2024-12', '月度人力報表_202412.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 1048576, '/documents/reports/hr/2024-12.xlsx', 'DEPARTMENT', 'PUBLIC', false, 'E020', false, 'F-REPORT', '報表,人力', '2025-01-14 09:00:00', '2025-01-14 09:00:00'),
('DOC-015', 'REPORT', 'FINANCE', 'RPT-2024-Q4', '季度財務報表_2024Q4.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 2097152, '/documents/reports/finance/2024-Q4.xlsx', 'PUBLIC', 'PUBLIC', false, 'E030', false, 'F-REPORT', '報表,財務', '2025-01-15 09:00:00', '2025-01-15 09:00:00'),

-- 已刪除文件 (soft delete)
('DOC-DEL-001', 'CONTRACT', 'EMPLOYEE', 'E099', '已刪除文件.pdf', 'application/pdf', 100000, '/documents/deleted.pdf', 'PRIVATE', 'CONFIDENTIAL', false, 'E099', true, 'F-DELETED', '已刪除', '2025-01-01 09:00:00', '2025-01-16 09:00:00');

-- 測試場景說明:
-- DOC_T001: 查詢 CONTRACT 類型 → 預期 4 筆
-- DOC_T002: 查詢 PRIVATE 可見度 → 預期 6 筆
-- DOC_T003: 查詢 CONFIDENTIAL 分類 → 預期 4 筆
-- DOC_T004: 查詢已加密文件 → 預期 5 筆
-- DOC_T005: 查詢未刪除文件 → 預期 15 筆 (軟刪除過濾)
-- QueryEngine EQ: document_type = 'CONTRACT' → 預期 4 筆
-- QueryEngine IN: visibility IN ('PUBLIC', 'DEPARTMENT') → 預期 9 筆
-- QueryEngine LIKE: file_name LIKE '%張三%' → 預期 2 筆
-- QueryEngine GTE: file_size >= 1000000 → 預期 5 筆
