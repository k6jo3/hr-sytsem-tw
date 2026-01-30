-- Role 測試資料
-- 用於 RoleRepositoryIntegrationTest

-- 清除舊資料 (依賴順序)
DELETE FROM role_permissions;
DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM tenants;

-- 建立租戶資料
INSERT INTO tenants (tenant_id, tenant_name, tenant_code, status, created_at, updated_at) VALUES
('T001', '測試租戶一', 'TENANT001', 'ACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('T002', '測試租戶二', 'TENANT002', 'ACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00');

-- 測試 Role 資料 (共 10 筆)
-- 系統角色 (3 筆): SUPER_ADMIN, SYSTEM_ADMIN, SYSTEM_AUDITOR
-- T001 租戶角色 (4 筆): ADMIN, HR_MANAGER, EMPLOYEE, GUEST
-- T002 租戶角色 (3 筆): ADMIN, MANAGER, USER
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status, created_at, updated_at) VALUES
-- 系統角色 (tenant_id = NULL)
('ROLE-SYS-001', '超級管理員', 'SUPER_ADMIN', '系統最高權限管理員', NULL, true, 'ACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('ROLE-SYS-002', '系統管理員', 'SYSTEM_ADMIN', '系統管理員', NULL, true, 'ACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('ROLE-SYS-003', '系統稽核員', 'SYSTEM_AUDITOR', '系統稽核員', NULL, true, 'INACTIVE', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),

-- T001 租戶角色
('ROLE-T001-001', '管理員', 'ADMIN', 'T001 租戶管理員', 'T001', false, 'ACTIVE', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('ROLE-T001-002', 'HR 經理', 'HR_MANAGER', 'T001 人力資源經理', 'T001', false, 'ACTIVE', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('ROLE-T001-003', '一般員工', 'EMPLOYEE', 'T001 一般員工', 'T001', false, 'ACTIVE', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('ROLE-T001-004', '訪客', 'GUEST', 'T001 訪客角色', 'T001', false, 'INACTIVE', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),

-- T002 租戶角色
('ROLE-T002-001', '管理員', 'ADMIN', 'T002 租戶管理員', 'T002', false, 'ACTIVE', '2025-01-03 09:00:00', '2025-01-03 09:00:00'),
('ROLE-T002-002', '經理', 'MANAGER', 'T002 部門經理', 'T002', false, 'ACTIVE', '2025-01-03 09:00:00', '2025-01-03 09:00:00'),
('ROLE-T002-003', '一般使用者', 'USER', 'T002 一般使用者', 'T002', false, 'DELETED', '2025-01-03 09:00:00', '2025-01-03 09:00:00');

-- 測試場景說明:
-- 1. findById: 根據 role_id 查詢 → 預期找到對應角色
-- 2. findByRoleCode: 根據 role_code 查詢 → ADMIN 應返回 2 筆 (T001, T002)
-- 3. findByRoleCodeAndTenantId: 根據 role_code + tenant_id 查詢 → ADMIN + T001 應返回 1 筆
-- 4. findByStatus(ACTIVE): 預期 7 筆 (SYS 2 + T001 3 + T002 2)
-- 5. findByStatus(INACTIVE): 預期 2 筆 (SYS 1 + T001 1)
-- 6. findByTenantId(T001): 預期 4 筆
-- 7. findByTenantId(T002): 預期 3 筆
-- 8. findSystemRoles: 預期 3 筆 (is_system_role = true)
-- 9. findAll: 預期 10 筆
