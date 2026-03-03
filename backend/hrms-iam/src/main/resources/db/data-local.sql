-- =====================================================
-- HRMS IAM Service - Local Profile 初始資料
-- H2 相容語法（使用 MERGE INTO 取代 ON CONFLICT）
-- =====================================================

-- =====================================================
-- 1. 預設租戶
-- =====================================================
MERGE INTO tenants (tenant_id, tenant_name, tenant_code, status)
KEY (tenant_id)
VALUES ('00000000-0000-0000-0000-000000000001', '預設租戶', 'DEFAULT', 'ACTIVE');

-- =====================================================
-- 2. 系統內建權限
-- =====================================================

-- 使用者管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0001', 'user:create', '建立使用者', '允許建立新使用者', 'user', 'create');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0002', 'user:read', '查詢使用者', '允許查詢使用者資料', 'user', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0003', 'user:update', '更新使用者', '允許更新使用者資料', 'user', 'update');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0004', 'user:delete', '刪除使用者', '允許刪除使用者', 'user', 'delete');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0005', 'user:activate', '啟用使用者', '允許啟用使用者帳號', 'user', 'activate');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0006', 'user:deactivate', '停用使用者', '允許停用使用者帳號', 'user', 'deactivate');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0007', 'user:reset-password', '重置密碼', '允許重置使用者密碼', 'user', 'reset-password');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0008', 'user:assign-role', '指派角色', '允許指派角色給使用者', 'user', 'assign-role');

-- 角色管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0011', 'role:create', '建立角色', '允許建立新角色', 'role', 'create');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0012', 'role:read', '查詢角色', '允許查詢角色資料', 'role', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0013', 'role:update', '更新角色', '允許更新角色資料', 'role', 'update');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0014', 'role:delete', '刪除角色', '允許刪除角色', 'role', 'delete');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0015', 'role:assign-permission', '指派權限', '允許指派權限給角色', 'role', 'assign-permission');

-- 權限管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0021', 'permission:read', '查詢權限', '允許查詢系統權限列表', 'permission', 'read');

-- 員工管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0031', 'employee:create', '建立員工', '允許建立新員工', 'employee', 'create');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0032', 'employee:read', '查詢員工', '允許查詢員工資料', 'employee', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0033', 'employee:update', '更新員工', '允許更新員工資料', 'employee', 'update');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0034', 'employee:delete', '刪除員工', '允許刪除員工', 'employee', 'delete');

-- 考勤管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0041', 'attendance:read', '查詢考勤', '允許查詢考勤記錄', 'attendance', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0042', 'attendance:clock', '打卡', '允許打卡', 'attendance', 'clock');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0043', 'attendance:approve', '審核考勤', '允許審核考勤異常', 'attendance', 'approve');

-- 薪資管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0051', 'payroll:read', '查詢薪資', '允許查詢薪資資料', 'payroll', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0052', 'payroll:calculate', '計算薪資', '允許執行薪資計算', 'payroll', 'calculate');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0053', 'payroll:approve', '審核薪資', '允許審核薪資', 'payroll', 'approve');

-- 專案管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0061', 'project:create', '建立專案', '允許建立新專案', 'project', 'create');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0062', 'project:read', '查詢專案', '允許查詢專案資料', 'project', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0063', 'project:update', '更新專案', '允許更新專案資料', 'project', 'update');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0064', 'project:delete', '刪除專案', '允許刪除專案', 'project', 'delete');

-- 工時管理權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0071', 'timesheet:read', '查詢工時', '允許查詢工時記錄', 'timesheet', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0072', 'timesheet:submit', '提交工時', '允許提交工時', 'timesheet', 'submit');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0073', 'timesheet:approve', '審核工時', '允許審核工時', 'timesheet', 'approve');

-- 報表權限
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0081', 'report:read', '查詢報表', '允許查詢報表', 'report', 'read');
MERGE INTO permissions (permission_id, permission_code, permission_name, description, resource, action) KEY (permission_id) VALUES
    ('perm-0082', 'report:export', '匯出報表', '允許匯出報表', 'report', 'export');

-- =====================================================
-- 3. 系統內建角色
-- =====================================================
MERGE INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) KEY (role_id) VALUES
    ('role-0001', '系統管理員', 'SYSTEM_ADMIN', '系統最高權限管理員', NULL, TRUE, 'ACTIVE');
MERGE INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) KEY (role_id) VALUES
    ('role-0002', 'HR 管理員', 'HR_ADMIN', '人力資源管理員', NULL, TRUE, 'ACTIVE');
MERGE INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) KEY (role_id) VALUES
    ('role-0003', '部門主管', 'MANAGER', '部門主管，可審核下屬', NULL, TRUE, 'ACTIVE');
MERGE INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) KEY (role_id) VALUES
    ('role-0004', '一般員工', 'EMPLOYEE', '一般員工基本權限', NULL, TRUE, 'ACTIVE');
MERGE INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) KEY (role_id) VALUES
    ('role-0005', '專案經理', 'PROJECT_MANAGER', '專案經理，可管理專案', NULL, TRUE, 'ACTIVE');

-- =====================================================
-- 4. 角色權限對應
-- =====================================================

-- 系統管理員 - 所有權限
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id)
SELECT 'role-0001', permission_id FROM permissions;

-- HR 管理員權限
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0001');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0002');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0003');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0005');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0006');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0007');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0008');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0012');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0021');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0031');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0032');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0033');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0034');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0041');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0043');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0051');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0052');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0053');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0081');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0002', 'perm-0082');

-- 部門主管權限
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0002');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0032');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0041');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0043');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0062');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0071');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0073');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0003', 'perm-0081');

-- 一般員工權限
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0004', 'perm-0042');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0004', 'perm-0062');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0004', 'perm-0071');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0004', 'perm-0072');

-- 專案經理權限
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0002');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0032');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0061');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0062');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0063');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0071');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0073');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0081');
MERGE INTO role_permissions (role_id, permission_id) KEY (role_id, permission_id) VALUES ('role-0005', 'perm-0082');

-- =====================================================
-- 5. 預設管理員帳號 (密碼: Admin@123)
-- BCrypt hash with strength 12
-- =====================================================
MERGE INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) KEY (user_id) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    'admin@company.com',
    '$2a$12$ay7Ck9tjsekCgXckaXiVFO4op2tlVz3fLcyxG1DhLtCkqJ68TlPe.',
    '系統管理員',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 指派系統管理員角色給 admin
MERGE INTO user_roles (user_role_id, user_id, role_id, assigned_at) KEY (user_role_id) VALUES
    ('ur-0001', '00000000-0000-0000-0000-000000000001', 'role-0001', CURRENT_TIMESTAMP);

-- =====================================================
-- 6. 測試用 HR 帳號 (密碼: Hr@12345)
-- =====================================================
MERGE INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) KEY (user_id) VALUES (
    '00000000-0000-0000-0000-000000000002',
    'hr_admin',
    'hr@company.com',
    '$2a$12$ay7Ck9tjsekCgXckaXiVFO4op2tlVz3fLcyxG1DhLtCkqJ68TlPe.',
    'HR 管理員',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

MERGE INTO user_roles (user_role_id, user_id, role_id, assigned_at) KEY (user_role_id) VALUES
    ('ur-0002', '00000000-0000-0000-0000-000000000002', 'role-0002', CURRENT_TIMESTAMP);

-- =====================================================
-- 7. 測試用一般員工帳號 (密碼: Admin@123)
-- =====================================================
MERGE INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) KEY (user_id) VALUES (
    '00000000-0000-0000-0000-000000000003',
    'employee',
    'employee@company.com',
    '$2a$12$ay7Ck9tjsekCgXckaXiVFO4op2tlVz3fLcyxG1DhLtCkqJ68TlPe.',
    '測試員工',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

MERGE INTO user_roles (user_role_id, user_id, role_id, assigned_at) KEY (user_role_id) VALUES
    ('ur-0003', '00000000-0000-0000-0000-000000000003', 'role-0004', CURRENT_TIMESTAMP);
