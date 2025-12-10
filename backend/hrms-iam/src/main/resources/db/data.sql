-- =====================================================
-- HRMS IAM Service - Initial Data
-- Version: 2.0
-- Last Updated: 2025-12-10
-- =====================================================

-- =====================================================
-- 1. 預設租戶
-- =====================================================
INSERT INTO tenants (tenant_id, tenant_name, tenant_code, status) VALUES
    ('00000000-0000-0000-0000-000000000001', '預設租戶', 'DEFAULT', 'ACTIVE')
ON CONFLICT (tenant_id) DO NOTHING;

-- =====================================================
-- 2. 系統內建權限
-- =====================================================

-- 使用者管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0001', 'user:create', '建立使用者', '允許建立新使用者', 'user', 'create'),
    ('perm-0002', 'user:read', '查詢使用者', '允許查詢使用者資料', 'user', 'read'),
    ('perm-0003', 'user:update', '更新使用者', '允許更新使用者資料', 'user', 'update'),
    ('perm-0004', 'user:delete', '刪除使用者', '允許刪除使用者', 'user', 'delete'),
    ('perm-0005', 'user:activate', '啟用使用者', '允許啟用使用者帳號', 'user', 'activate'),
    ('perm-0006', 'user:deactivate', '停用使用者', '允許停用使用者帳號', 'user', 'deactivate'),
    ('perm-0007', 'user:reset-password', '重置密碼', '允許重置使用者密碼', 'user', 'reset-password'),
    ('perm-0008', 'user:assign-role', '指派角色', '允許指派角色給使用者', 'user', 'assign-role')
ON CONFLICT (permission_id) DO NOTHING;

-- 角色管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0011', 'role:create', '建立角色', '允許建立新角色', 'role', 'create'),
    ('perm-0012', 'role:read', '查詢角色', '允許查詢角色資料', 'role', 'read'),
    ('perm-0013', 'role:update', '更新角色', '允許更新角色資料', 'role', 'update'),
    ('perm-0014', 'role:delete', '刪除角色', '允許刪除角色', 'role', 'delete'),
    ('perm-0015', 'role:assign-permission', '指派權限', '允許指派權限給角色', 'role', 'assign-permission')
ON CONFLICT (permission_id) DO NOTHING;

-- 權限管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0021', 'permission:read', '查詢權限', '允許查詢系統權限列表', 'permission', 'read')
ON CONFLICT (permission_id) DO NOTHING;

-- 員工管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0031', 'employee:create', '建立員工', '允許建立新員工', 'employee', 'create'),
    ('perm-0032', 'employee:read', '查詢員工', '允許查詢員工資料', 'employee', 'read'),
    ('perm-0033', 'employee:update', '更新員工', '允許更新員工資料', 'employee', 'update'),
    ('perm-0034', 'employee:delete', '刪除員工', '允許刪除員工', 'employee', 'delete')
ON CONFLICT (permission_id) DO NOTHING;

-- 考勤管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0041', 'attendance:read', '查詢考勤', '允許查詢考勤記錄', 'attendance', 'read'),
    ('perm-0042', 'attendance:clock', '打卡', '允許打卡', 'attendance', 'clock'),
    ('perm-0043', 'attendance:approve', '審核考勤', '允許審核考勤異常', 'attendance', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 薪資管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0051', 'payroll:read', '查詢薪資', '允許查詢薪資資料', 'payroll', 'read'),
    ('perm-0052', 'payroll:calculate', '計算薪資', '允許執行薪資計算', 'payroll', 'calculate'),
    ('perm-0053', 'payroll:approve', '審核薪資', '允許審核薪資', 'payroll', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 專案管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0061', 'project:create', '建立專案', '允許建立新專案', 'project', 'create'),
    ('perm-0062', 'project:read', '查詢專案', '允許查詢專案資料', 'project', 'read'),
    ('perm-0063', 'project:update', '更新專案', '允許更新專案資料', 'project', 'update'),
    ('perm-0064', 'project:delete', '刪除專案', '允許刪除專案', 'project', 'delete')
ON CONFLICT (permission_id) DO NOTHING;

-- 工時管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0071', 'timesheet:read', '查詢工時', '允許查詢工時記錄', 'timesheet', 'read'),
    ('perm-0072', 'timesheet:submit', '提交工時', '允許提交工時', 'timesheet', 'submit'),
    ('perm-0073', 'timesheet:approve', '審核工時', '允許審核工時', 'timesheet', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 報表權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0081', 'report:read', '查詢報表', '允許查詢報表', 'report', 'read'),
    ('perm-0082', 'report:export', '匯出報表', '允許匯出報表', 'report', 'export')
ON CONFLICT (permission_id) DO NOTHING;

-- =====================================================
-- 3. 系統內建角色
-- =====================================================

-- 系統管理員 (擁有所有權限)
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0001', '系統管理員', 'SYSTEM_ADMIN', '系統最高權限管理員', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- HR 管理員
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0002', 'HR 管理員', 'HR_ADMIN', '人力資源管理員', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- 部門主管
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0003', '部門主管', 'MANAGER', '部門主管，可審核下屬', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- 一般員工
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0004', '一般員工', 'EMPLOYEE', '一般員工基本權限', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- 專案經理
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0005', '專案經理', 'PROJECT_MANAGER', '專案經理，可管理專案', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- =====================================================
-- 4. 角色權限對應
-- =====================================================

-- 系統管理員 - 所有權限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 'role-0001', permission_id FROM permissions
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- HR 管理員權限
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-0002', 'perm-0001'),  -- user:create
    ('role-0002', 'perm-0002'),  -- user:read
    ('role-0002', 'perm-0003'),  -- user:update
    ('role-0002', 'perm-0005'),  -- user:activate
    ('role-0002', 'perm-0006'),  -- user:deactivate
    ('role-0002', 'perm-0007'),  -- user:reset-password
    ('role-0002', 'perm-0008'),  -- user:assign-role
    ('role-0002', 'perm-0012'),  -- role:read
    ('role-0002', 'perm-0021'),  -- permission:read
    ('role-0002', 'perm-0031'),  -- employee:create
    ('role-0002', 'perm-0032'),  -- employee:read
    ('role-0002', 'perm-0033'),  -- employee:update
    ('role-0002', 'perm-0034'),  -- employee:delete
    ('role-0002', 'perm-0041'),  -- attendance:read
    ('role-0002', 'perm-0043'),  -- attendance:approve
    ('role-0002', 'perm-0051'),  -- payroll:read
    ('role-0002', 'perm-0052'),  -- payroll:calculate
    ('role-0002', 'perm-0053'),  -- payroll:approve
    ('role-0002', 'perm-0081'),  -- report:read
    ('role-0002', 'perm-0082')   -- report:export
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 部門主管權限
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-0003', 'perm-0002'),  -- user:read
    ('role-0003', 'perm-0032'),  -- employee:read
    ('role-0003', 'perm-0041'),  -- attendance:read
    ('role-0003', 'perm-0043'),  -- attendance:approve
    ('role-0003', 'perm-0062'),  -- project:read
    ('role-0003', 'perm-0071'),  -- timesheet:read
    ('role-0003', 'perm-0073'),  -- timesheet:approve
    ('role-0003', 'perm-0081')   -- report:read
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 一般員工權限
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-0004', 'perm-0042'),  -- attendance:clock
    ('role-0004', 'perm-0062'),  -- project:read
    ('role-0004', 'perm-0071'),  -- timesheet:read
    ('role-0004', 'perm-0072')   -- timesheet:submit
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 專案經理權限
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-0005', 'perm-0002'),  -- user:read
    ('role-0005', 'perm-0032'),  -- employee:read
    ('role-0005', 'perm-0061'),  -- project:create
    ('role-0005', 'perm-0062'),  -- project:read
    ('role-0005', 'perm-0063'),  -- project:update
    ('role-0005', 'perm-0071'),  -- timesheet:read
    ('role-0005', 'perm-0073'),  -- timesheet:approve
    ('role-0005', 'perm-0081'),  -- report:read
    ('role-0005', 'perm-0082')   -- report:export
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- =====================================================
-- 5. 預設管理員帳號 (密碼: Admin@123)
-- 注意: 密碼為 BCrypt 雜湊
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    'admin@company.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4x5H5.8qGvhXHHe.', -- Admin@123
    '系統管理員',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (user_id) DO NOTHING;

-- 指派系統管理員角色給 admin
INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0001', '00000000-0000-0000-0000-000000000001', 'role-0001', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;
