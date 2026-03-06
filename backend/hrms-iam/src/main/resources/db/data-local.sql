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

-- =====================================================
-- 8. 測試用部門主管帳號 (密碼: Admin@123)
-- 對應員工：陳志強 (EMP003, 資深工程師, IT 部)
-- =====================================================
MERGE INTO users (
    user_id, username, email, password_hash, display_name,
    employee_id, tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) KEY (user_id) VALUES (
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
);

MERGE INTO user_roles (user_role_id, user_id, role_id, assigned_at) KEY (user_role_id) VALUES
    ('ur-0004', '00000000-0000-0000-0000-000000000004', 'role-0003', CURRENT_TIMESTAMP);

-- =====================================================
-- 9. 測試用專案經理帳號 (密碼: Admin@123)
-- 對應員工：林雅婷 (EMP004, 初級工程師, IT 部)
-- =====================================================
MERGE INTO users (
    user_id, username, email, password_hash, display_name,
    employee_id, tenant_id, status, must_change_password, is_deleted, created_at, updated_at
) KEY (user_id) VALUES (
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
);

MERGE INTO user_roles (user_role_id, user_id, role_id, assigned_at) KEY (user_role_id) VALUES
    ('ur-0005', '00000000-0000-0000-0000-000000000005', 'role-0005', CURRENT_TIMESTAMP);

-- =====================================================
-- 10. 更新既有帳號的 employee_id 關聯
-- =====================================================
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000001' WHERE user_id = '00000000-0000-0000-0000-000000000001';
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000002' WHERE user_id = '00000000-0000-0000-0000-000000000002';
UPDATE users SET employee_id = '00000000-0000-0000-0000-000000000003' WHERE user_id = '00000000-0000-0000-0000-000000000003';

-- =====================================================
-- 11. 功能開關（預設值）
-- =====================================================
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0001', 'LATE_CHECK', '遲到判定', 'HR03', TRUE, '啟用考勤遲到自動判定功能');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0002', 'LATE_SALARY_DEDUCTION', '遲到扣薪', 'HR03', TRUE, '啟用遲到連動薪資扣款');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0003', 'SHIFT_SCHEDULING', '輪班排程', 'HR03', TRUE, '啟用輪班排程管理功能');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0004', 'SALARY_ADVANCE', '薪資預借', 'HR04', TRUE, '啟用薪資預借申請功能');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0005', 'LEGAL_DEDUCTION', '法扣款', 'HR04', TRUE, '啟用法院扣押執行功能');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0006', 'LDAP_AUTH', 'LDAP 認證', 'HR01', FALSE, '啟用 LDAP/AD 企業登入整合');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0007', 'ABSENT_DETECTION', '曠職自動判定', 'HR03', TRUE, '啟用每日下班後曠職自動判定排程');
MERGE INTO feature_toggles (id, feature_code, feature_name, module, enabled, description) KEY (id) VALUES
    ('ft-0008', 'AUTO_INSURANCE_WITHDRAW', '離職自動退保', 'HR05', TRUE, '啟用離職連動保險自動退保');

-- =====================================================
-- 12. 系統參數（預設值）
-- =====================================================
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0001', 'MAX_FAILED_LOGIN_ATTEMPTS', '登入失敗上限', '5', 'INTEGER', 'HR01', 'SECURITY', '5', '帳號鎖定前允許的最大登入失敗次數');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0002', 'ACCOUNT_LOCK_DURATION_MINUTES', '帳號鎖定時長', '30', 'INTEGER', 'HR01', 'SECURITY', '30', '帳號鎖定時長（分鐘）');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0003', 'PASSWORD_MIN_LENGTH', '密碼最短長度', '8', 'INTEGER', 'HR01', 'SECURITY', '8', '密碼最低字元數');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0004', 'SALARY_ADVANCE_MAX_RATE', '預借上限比率', '0.9', 'DECIMAL', 'HR04', 'BUSINESS', '0.9', '可預借金額佔（淨薪-法扣）的比率上限');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0005', 'GARNISHMENT_ONE_THIRD_RULE', '法扣三分之一規則', 'true', 'BOOLEAN', 'HR04', 'BUSINESS', 'true', '是否啟用強制執行法§115-1 三分之一扣薪上限');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0006', 'ABSENT_DETECTION_CRON', '曠職判定排程', '0 0 19 * * ?', 'STRING', 'HR03', 'SYSTEM', '0 0 19 * * ?', '曠職自動判定排程的 Cron 表達式');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0007', 'EMPLOYEE_NUMBER_PREFIX', '員工編號前綴', 'EMP', 'STRING', 'HR02', 'BUSINESS', 'EMP', '員工編號的前綴字串（如 EMP, E, HR）');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0008', 'EMPLOYEE_NUMBER_FORMAT', '員工編號格式', 'YYYYMM-NNNN', 'STRING', 'HR02', 'BUSINESS', 'YYYYMM-NNNN', '員工編號格式：YYYYMM-NNNN 或 NNNN');
MERGE INTO system_parameters (id, param_code, param_name, param_value, param_type, module, category, default_value, description) KEY (id) VALUES
    ('sp-0009', 'EMPLOYEE_NUMBER_SEQ_DIGITS', '員工編號流水號位數', '4', 'INTEGER', 'HR02', 'BUSINESS', '4', '流水號補零位數');

-- =====================================================
-- 13. 排程任務配置
-- =====================================================
MERGE INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) KEY (id) VALUES
    ('job-0001', 'ABSENT_DETECTION', '曠職自動判定', 'HR03', '0 0 19 * * ?', TRUE, '每日 19:00 掃描無打卡且無請假的員工');
MERGE INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) KEY (id) VALUES
    ('job-0002', 'ANNUAL_LEAVE_SETTLEMENT', '特休年度結算', 'HR03', '0 0 1 1 1 ?', TRUE, '每年 1/1 凌晨結算特休假（結轉/折薪/作廢）');
MERGE INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) KEY (id) VALUES
    ('job-0003', 'INSURANCE_DAILY_REPORT', '保險每日異動報表', 'HR05', '0 30 8 * * ?', TRUE, '每日 08:30 匯出加退保異動清單');
MERGE INTO scheduled_job_configs (id, job_code, job_name, module, cron_expression, enabled, description) KEY (id) VALUES
    ('job-0004', 'PAYROLL_MONTHLY_CLOSE', '薪資月結', 'HR04', '0 0 2 1 * ?', TRUE, '每月 1 日凌晨 2:00 執行薪資月結');
