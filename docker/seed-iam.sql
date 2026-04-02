-- =====================================================
-- HRMS IAM Service - Docker 初始資料 (PostgreSQL 格式)
-- 從 data-local.sql (H2) 轉換而來
-- =====================================================

\connect hrms_iam

-- =====================================================
-- 0. 補充建立 MyBatis 使用但 JPA 不會自動產生的 table
--    確保 INSERT 前 table 已存在
-- =====================================================
CREATE TABLE IF NOT EXISTS permissions (
    permission_id   VARCHAR(255) NOT NULL PRIMARY KEY,
    permission_code VARCHAR(255) NOT NULL,
    permission_name VARCHAR(255),
    description     VARCHAR(1024),
    resource        VARCHAR(255),
    action          VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       VARCHAR(255) NOT NULL,
    permission_id VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id      VARCHAR(255) NOT NULL,
    role_id      VARCHAR(255) NOT NULL,
    assigned_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 1. 預設租戶
-- =====================================================
INSERT INTO tenants (tenant_id, tenant_name, tenant_code, status)
VALUES ('00000000-0000-0000-0000-000000000001', '預設租戶', 'DEFAULT', 'ACTIVE')
ON CONFLICT (tenant_id) DO NOTHING;

-- =====================================================
-- 2. 系統內建權限
-- =====================================================

-- 使用者管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0001', 'user:create', '建立使用者', '允許建立新使用者', 'user', 'create')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0002', 'user:read', '查詢使用者', '允許查詢使用者資料', 'user', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0003', 'user:update', '更新使用者', '允許更新使用者資料', 'user', 'update')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0004', 'user:delete', '刪除使用者', '允許刪除使用者', 'user', 'delete')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0005', 'user:activate', '啟用使用者', '允許啟用使用者帳號', 'user', 'activate')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0006', 'user:deactivate', '停用使用者', '允許停用使用者帳號', 'user', 'deactivate')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0007', 'user:reset-password', '重置密碼', '允許重置使用者密碼', 'user', 'reset-password')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0008', 'user:assign-role', '指派角色', '允許指派角色給使用者', 'user', 'assign-role')
ON CONFLICT (permission_id) DO NOTHING;

-- 角色管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0011', 'role:create', '建立角色', '允許建立新角色', 'role', 'create')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0012', 'role:read', '查詢角色', '允許查詢角色資料', 'role', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0013', 'role:update', '更新角色', '允許更新角色資料', 'role', 'update')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0014', 'role:delete', '刪除角色', '允許刪除角色', 'role', 'delete')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0015', 'role:assign-permission', '指派權限', '允許指派權限給角色', 'role', 'assign-permission')
ON CONFLICT (permission_id) DO NOTHING;

-- 權限管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0021', 'permission:read', '查詢權限', '允許查詢系統權限列表', 'permission', 'read')
ON CONFLICT (permission_id) DO NOTHING;

-- 員工管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0031', 'employee:create', '建立員工', '允許建立新員工', 'employee', 'create')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0032', 'employee:read', '查詢員工', '允許查詢員工資料', 'employee', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0033', 'employee:update', '更新員工', '允許更新員工資料', 'employee', 'update')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0034', 'employee:delete', '刪除員工', '允許刪除員工', 'employee', 'delete')
ON CONFLICT (permission_id) DO NOTHING;

-- 考勤管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0041', 'attendance:read', '查詢考勤', '允許查詢考勤記錄', 'attendance', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0042', 'attendance:clock', '打卡', '允許打卡', 'attendance', 'clock')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0043', 'attendance:approve', '審核考勤', '允許審核考勤異常', 'attendance', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 薪資管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0051', 'payroll:read', '查詢薪資', '允許查詢薪資資料', 'payroll', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0052', 'payroll:calculate', '計算薪資', '允許執行薪資計算', 'payroll', 'calculate')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0053', 'payroll:approve', '審核薪資', '允許審核薪資', 'payroll', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 專案管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0061', 'project:create', '建立專案', '允許建立新專案', 'project', 'create')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0062', 'project:read', '查詢專案', '允許查詢專案資料', 'project', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0063', 'project:update', '更新專案', '允許更新專案資料', 'project', 'update')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0064', 'project:delete', '刪除專案', '允許刪除專案', 'project', 'delete')
ON CONFLICT (permission_id) DO NOTHING;

-- 工時管理權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0071', 'timesheet:read', '查詢工時', '允許查詢工時記錄', 'timesheet', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0072', 'timesheet:submit', '提交工時', '允許提交工時', 'timesheet', 'submit')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0073', 'timesheet:approve', '審核工時', '允許審核工時', 'timesheet', 'approve')
ON CONFLICT (permission_id) DO NOTHING;

-- 報表權限
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0081', 'report:read', '查詢報表', '允許查詢報表', 'report', 'read')
ON CONFLICT (permission_id) DO NOTHING;
INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
    ('perm-0082', 'report:export', '匯出報表', '允許匯出報表', 'report', 'export')
ON CONFLICT (permission_id) DO NOTHING;

-- =====================================================
-- 3. 系統內建角色
-- =====================================================
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0001', '系統管理員', 'SYSTEM_ADMIN', '系統最高權限管理員', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0002', 'HR 管理員', 'HR_ADMIN', '人力資源管理員', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0003', '部門主管', 'MANAGER', '部門主管，可審核下屬', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0004', '一般員工', 'EMPLOYEE', '一般員工基本權限', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
    ('role-0005', '專案經理', 'PROJECT_MANAGER', '專案經理，可管理專案', NULL, TRUE, 'ACTIVE')
ON CONFLICT (role_id) DO NOTHING;

-- =====================================================
-- 4. 角色權限對應
-- =====================================================

-- 系統管理員 - 所有權限（動態 SELECT）
INSERT INTO role_permissions (role_id, permission_id)
SELECT 'role-0001', permission_id FROM permissions
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- HR 管理員權限
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0001') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0002') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0003') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0005') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0006') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0007') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0008') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0012') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0021') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0031') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0032') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0033') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0034') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0041') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0043') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0051') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0052') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0053') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0081') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0002', 'perm-0082') ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 部門主管權限
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0002') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0032') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0041') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0043') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0062') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0071') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0073') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0003', 'perm-0081') ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 一般員工權限
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0004', 'perm-0042') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0004', 'perm-0062') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0004', 'perm-0071') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0004', 'perm-0072') ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 專案經理權限
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0002') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0032') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0061') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0062') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0063') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0071') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0073') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0081') ON CONFLICT (role_id, permission_id) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id) VALUES ('role-0005', 'perm-0082') ON CONFLICT (role_id, permission_id) DO NOTHING;

-- =====================================================
-- 5. 預設管理員帳號 (密碼: Admin@123)
-- BCrypt hash with strength 12
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) VALUES (
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
) ON CONFLICT (user_id) DO NOTHING;

-- 指派系統管理員角色給 admin
INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0001', '00000000-0000-0000-0000-000000000001', 'role-0001', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;

-- =====================================================
-- 6. 測試用 HR 帳號 (密碼: Hr@12345)
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) VALUES (
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
) ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0002', '00000000-0000-0000-0000-000000000002', 'role-0002', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;

-- =====================================================
-- 7. 測試用一般員工帳號 (密碼: Admin@123)
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) VALUES (
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
) ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0003', '00000000-0000-0000-0000-000000000003', 'role-0004', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;

-- =====================================================
-- 8. 測試用部門主管帳號 (密碼: Admin@123)
-- 對應員工：陳志強 (EMP003, 資深工程師, IT 部)
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    employee_id, tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000004',
    'manager',
    'manager@company.com',
    '$2a$12$ay7Ck9tjsekCgXckaXiVFO4op2tlVz3fLcyxG1DhLtCkqJ68TlPe.',
    '陳志強',
    '00000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0004', '00000000-0000-0000-0000-000000000004', 'role-0003', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;

-- =====================================================
-- 9. 測試用專案經理帳號 (密碼: Admin@123)
-- 對應員工：林雅婷 (EMP004, 初級工程師, IT 部)
-- =====================================================
INSERT INTO users (
    user_id, username, email, password_hash, display_name,
    employee_id, tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000005',
    'pm',
    'pm@company.com',
    '$2a$12$ay7Ck9tjsekCgXckaXiVFO4op2tlVz3fLcyxG1DhLtCkqJ68TlPe.',
    '林雅婷',
    '00000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    'ACTIVE',
    FALSE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_roles (user_role_id, user_id, role_id, assigned_at) VALUES
    ('ur-0005', '00000000-0000-0000-0000-000000000005', 'role-0005', CURRENT_TIMESTAMP)
ON CONFLICT (user_role_id) DO NOTHING;

-- =====================================================
-- 10. 更新既有帳號的 employee_id 關聯
-- =====================================================
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000001' WHERE user_id = '00000000-0000-0000-0000-000000000001';
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000002' WHERE user_id = '00000000-0000-0000-0000-000000000002';
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000003' WHERE user_id = '00000000-0000-0000-0000-000000000003';

-- =====================================================
-- 11. 功能開關（預設值）
-- =====================================================
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0001', 'LATE_CHECK', '遲到判定', 'HR03', TRUE, '啟用考勤遲到自動判定功能')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0002', 'LATE_SALARY_DEDUCTION', '遲到扣薪', 'HR03', TRUE, '啟用遲到連動薪資扣款')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0003', 'SHIFT_SCHEDULING', '輪班排程', 'HR03', TRUE, '啟用輪班排程管理功能')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0004', 'SALARY_ADVANCE', '薪資預借', 'HR04', TRUE, '啟用薪資預借申請功能')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0005', 'LEGAL_DEDUCTION', '法扣款', 'HR04', TRUE, '啟用法院扣押執行功能')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0006', 'LDAP_AUTH', 'LDAP 認證', 'HR01', FALSE, '啟用 LDAP/AD 企業登入整合')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0007', 'ABSENT_DETECTION', '曠職自動判定', 'HR03', TRUE, '啟用每日下班後曠職自動判定排程')
ON CONFLICT (id) DO NOTHING;
INSERT INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) VALUES
    ('ft-0008', 'AUTO_INSURANCE_WITHDRAW', '離職自動退保', 'HR05', TRUE, '啟用離職連動保險自動退保')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 12. 系統參數（預設值）
-- =====================================================
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0001', 'MAX_FAILED_LOGIN_ATTEMPTS', '登入失敗上限', '5', 'INTEGER', 'HR01', 'SECURITY', '5', '帳號鎖定前允許的最大登入失敗次數')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0002', 'ACCOUNT_LOCK_DURATION_MINUTES', '帳號鎖定時長', '30', 'INTEGER', 'HR01', 'SECURITY', '30', '帳號鎖定時長（分鐘）')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0003', 'PASSWORD_MIN_LENGTH', '密碼最短長度', '8', 'INTEGER', 'HR01', 'SECURITY', '8', '密碼最低字元數')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0004', 'SALARY_ADVANCE_MAX_RATE', '預借上限比率', '0.9', 'DECIMAL', 'HR04', 'BUSINESS', '0.9', '可預借金額佔（淨薪-法扣）的比率上限')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0005', 'GARNISHMENT_ONE_THIRD_RULE', '法扣三分之一規則', 'true', 'BOOLEAN', 'HR04', 'BUSINESS', 'true', '是否啟用強制執行法§115-1 三分之一扣薪上限')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0006', 'ABSENT_DETECTION_CRON', '曠職判定排程', '0 0 19 * * ?', 'STRING', 'HR03', 'SYSTEM', '0 0 19 * * ?', '曠職自動判定排程的 Cron 表達式')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0007', 'EMPLOYEE_NUMBER_PREFIX', '員工編號前綴', 'EMP', 'STRING', 'HR02', 'BUSINESS', 'EMP', '員工編號的前綴字串（如 EMP, E, HR）')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0008', 'EMPLOYEE_NUMBER_FORMAT', '員工編號格式', 'YYYYMM-NNNN', 'STRING', 'HR02', 'BUSINESS', 'YYYYMM-NNNN', '員工編號格式：YYYYMM-NNNN 或 NNNN')
ON CONFLICT (id) DO NOTHING;
INSERT INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) VALUES
    ('sp-0009', 'EMPLOYEE_NUMBER_SEQ_DIGITS', '員工編號流水號位數', '4', 'INTEGER', 'HR02', 'BUSINESS', '4', '流水號補零位數')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 13. 排程任務配置
-- =====================================================
INSERT INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) VALUES
    ('job-0001', 'ABSENT_DETECTION', '曠職自動判定', 'HR03', '0 0 19 * * ?', TRUE, '每日 19:00 掃描無打卡且無請假的員工')
ON CONFLICT (id) DO NOTHING;
INSERT INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) VALUES
    ('job-0002', 'ANNUAL_LEAVE_SETTLEMENT', '特休年度結算', 'HR03', '0 0 1 1 1 ?', TRUE, '每年 1/1 凌晨結算特休假（結轉/折薪/作廢）')
ON CONFLICT (id) DO NOTHING;
INSERT INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) VALUES
    ('job-0003', 'INSURANCE_DAILY_REPORT', '保險每日異動報表', 'HR05', '0 30 8 * * ?', TRUE, '每日 08:30 匯出加退保異動清單')
ON CONFLICT (id) DO NOTHING;
INSERT INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) VALUES
    ('job-0004', 'PAYROLL_MONTHLY_CLOSE', '薪資月結', 'HR04', '0 0 2 1 * ?', TRUE, '每月 1 日凌晨 2:00 執行薪資月結')
ON CONFLICT (id) DO NOTHING;
