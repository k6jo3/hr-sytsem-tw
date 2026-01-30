-- Department 測試資料
-- 用於 DepartmentRepositoryIntegrationTest

-- 清除舊資料 (依賴順序)
DELETE FROM employees;
DELETE FROM departments;

-- 部門測試資料 (共 10 筆)
-- 組織分布: ORG001=7, ORG002=3
-- 狀態分布: ACTIVE=8, INACTIVE=2
-- 層級分布: level 1=4, level 2=4, level 3=2
INSERT INTO departments (id, code, name, name_en, organization_id, parent_id, level, path, manager_id, status, sort_order, description, created_at, updated_at) VALUES
-- ORG001 組織的部門 (7筆)
-- 第一層 (根部門)
('D001', 'RD', '研發部', 'R&D', 'ORG001', NULL, 1, '/RD', 'E001', 'ACTIVE', 1, '研發相關工作', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('D002', 'SALES', '業務部', 'Sales', 'ORG001', NULL, 1, '/SALES', 'E003', 'ACTIVE', 2, '業務銷售工作', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('D003', 'FIN', '財務部', 'Finance', 'ORG001', NULL, 1, '/FIN', 'E005', 'ACTIVE', 3, '財務會計工作', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
-- 第二層 (研發部子部門)
('D004', 'RD-FE', '前端組', 'Frontend', 'ORG001', 'D001', 2, '/RD/RD-FE', NULL, 'ACTIVE', 1, '前端開發', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('D005', 'RD-BE', '後端組', 'Backend', 'ORG001', 'D001', 2, '/RD/RD-BE', NULL, 'ACTIVE', 2, '後端開發', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('D006', 'RD-QA', 'QA組', 'QA', 'ORG001', 'D001', 2, '/RD/RD-QA', NULL, 'INACTIVE', 3, 'QA測試 (已停用)', '2025-01-02 09:00:00', '2025-01-20 09:00:00'),
-- 第三層 (前端組子部門)
('D007', 'RD-FE-WEB', 'Web前端', 'Web Frontend', 'ORG001', 'D004', 3, '/RD/RD-FE/RD-FE-WEB', NULL, 'ACTIVE', 1, 'Web前端開發', '2025-01-03 09:00:00', '2025-01-03 09:00:00'),

-- ORG002 組織的部門 (3筆)
-- 第一層 (根部門)
('D101', 'ORG2-ADMIN', '管理部', 'Admin', 'ORG002', NULL, 1, '/ORG2-ADMIN', NULL, 'ACTIVE', 1, 'ORG002 管理部門', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
-- 第二層
('D102', 'ORG2-HR', '人資部', 'HR', 'ORG002', 'D101', 2, '/ORG2-ADMIN/ORG2-HR', NULL, 'ACTIVE', 1, 'ORG002 人資部門', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('D103', 'ORG2-IT', 'IT部', 'IT', 'ORG002', 'D101', 2, '/ORG2-ADMIN/ORG2-IT', NULL, 'INACTIVE', 2, 'ORG002 IT部門 (已停用)', '2025-01-02 09:00:00', '2025-01-20 09:00:00');

-- 測試場景說明:
-- 1. findById: 根據 id 查詢 → D001 應返回研發部
-- 2. findByCode: 根據 code 查詢 → RD 應返回研發部
-- 3. findByOrganizationId(ORG001): 預期 7 筆
-- 4. findByOrganizationId(ORG002): 預期 3 筆
-- 5. findByParentId(D001): 研發部的子部門 → 預期 3 筆 (前端組、後端組、QA組)
-- 6. findByParentId(D004): 前端組的子部門 → 預期 1 筆 (Web前端)
-- 7. findRootDepartments(ORG001): 預期 3 筆 (研發部、業務部、財務部)
-- 8. findRootDepartments(ORG002): 預期 1 筆 (管理部)
-- 9. existsByCode(RD): 預期 true
-- 10. existsByCode(NOTEXIST): 預期 false
-- 11. countByParentId(D001): 預期 3
-- 12. countByOrganizationId(ORG001): 預期 7
