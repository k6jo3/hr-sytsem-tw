-- =====================================================
-- HRMS IAM Service - Base Data for Tests
-- =====================================================

-- 1. Create Tables (if not exists)
CREATE TABLE IF NOT EXISTS tenants (
    tenant_id VARCHAR(36) PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL UNIQUE,
    tenant_code VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    display_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    employee_id VARCHAR(36),
    tenant_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login_at TIMESTAMP,
    last_logout_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    password_changed_at TIMESTAMP,
    preferred_language VARCHAR(20) DEFAULT 'zh-TW',
    timezone VARCHAR(50) DEFAULT 'Asia/Taipei',
    auth_source VARCHAR(20) DEFAULT 'LOCAL',
    ldap_dn VARCHAR(500),
    must_change_password BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (username, tenant_id),
    UNIQUE (email, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS roles (
    role_id VARCHAR(36) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    tenant_id VARCHAR(36),
    is_system_role BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (role_code, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS permissions (
    permission_id VARCHAR(36) PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(36),
    UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    token_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS login_logs (
    log_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    username VARCHAR(50) NOT NULL,
    login_result VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    device_info VARCHAR(255),
    login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_sso_links (
    link_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255),
    linked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    UNIQUE (provider, provider_user_id),
    UNIQUE (user_id, provider),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_history (
    history_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 2. Clear existing test data (H2 相容語法：關閉外鍵檢查 → 刪除資料 → 重啟外鍵檢查)
SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE password_reset_tokens;
TRUNCATE TABLE role_permissions;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE login_logs;
TRUNCATE TABLE user_sso_links;
TRUNCATE TABLE password_history;
TRUNCATE TABLE users;
TRUNCATE TABLE permissions;
TRUNCATE TABLE roles;
TRUNCATE TABLE tenants;
SET REFERENTIAL_INTEGRITY TRUE;

-- 3. Insert Basic Data (因為已經 TRUNCATE，直接 INSERT)
INSERT INTO tenants (tenant_id, tenant_name, tenant_code, status) VALUES
('T001', '測試租戶一', 'TENANT001', 'ACTIVE'),
('00000000-0000-0000-0000-000000000001', '預設租戶', 'DEFAULT', 'ACTIVE');

INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, is_deleted, status) VALUES
('a0000000-0000-0000-0000-000000000001', '系統管理員', 'ADMIN', '系統最高權限管理員', NULL, TRUE, FALSE, 'ACTIVE'),
('a0000000-0000-0000-0000-000000000002', '員工', 'EMPLOYEE', '一般員工角色', 'T001', FALSE, FALSE, 'ACTIVE'),
('a0000000-0000-0000-0000-000000000003', '主管', 'MANAGER', '部門主管角色', 'T001', FALSE, FALSE, 'ACTIVE'),
('a0000000-0000-0000-0000-000000000111', '測試角色', 'TEST_ROLE', '測試用角色', 'T001', FALSE, FALSE, 'ACTIVE'),
('00000000-0000-0000-0000-000000000007', 'HR 專員', 'HR', '人力資源專員角色', 'T001', FALSE, FALSE, 'ACTIVE');

INSERT INTO permissions (permission_id, permission_code, permission_name, description, resource, action) VALUES
('perm-0001', 'user:create', '建立使用者', '允許建立新使用者', 'user', 'create'),
('perm-0002', 'user:read', '查詢使用者', '允許查詢使用者資料', 'user', 'read'),
('perm-0003', 'user:update', '更新使用者', '允許更新使用者資料', 'user', 'update'),
('perm-0004', 'user:activate', '啟用使用者', '允許啟用使用者', 'user', 'activate'),
('perm-0005', 'user:deactivate', '停用使用者', '允許停用使用者', 'user', 'deactivate'),
('perm-0006', 'user:assign-role', '指派角色', '允許指派角色給使用者', 'user', 'assign-role'),
('perm-0007', 'role:create', '建立角色', '允許建立新角色', 'role', 'create'),
('perm-0008', 'role:read', '查詢角色', '允許查詢角色資料', 'role', 'read'),
('perm-0009', 'role:update', '更新角色', '允許更新角色資料', 'role', 'update'),
('perm-0010', 'role:delete', '刪除角色', '允許刪除角色', 'role', 'delete'),
('perm-0011', 'role:assign-permission', '指派權限', '允許指派權限給角色', 'role', 'assign-permission');

INSERT INTO role_permissions (role_id, permission_id) VALUES
('a0000000-0000-0000-0000-000000000001', 'perm-0001'),
('a0000000-0000-0000-0000-000000000001', 'perm-0002'),
('a0000000-0000-0000-0000-000000000001', 'perm-0003'),
('a0000000-0000-0000-0000-000000000001', 'perm-0004'),
('a0000000-0000-0000-0000-000000000001', 'perm-0005'),
('a0000000-0000-0000-0000-000000000001', 'perm-0006'),
('a0000000-0000-0000-0000-000000000001', 'perm-0007'),
('a0000000-0000-0000-0000-000000000001', 'perm-0008'),
('a0000000-0000-0000-0000-000000000001', 'perm-0009'),
('a0000000-0000-0000-0000-000000000001', 'perm-0010'),
('a0000000-0000-0000-0000-000000000001', 'perm-0011');

-- 插入測試使用者（因為已經 TRUNCATE，直接 INSERT）
-- 密碼都是 'password123' (BCrypt 加密, strength=12)
-- 明確指定 created_at 和 updated_at 為固定值，避免快照比對問題
INSERT INTO users (user_id, username, email, password_hash, display_name, employee_id, tenant_id, status, last_login_ip, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000999', 'admin@company.com', 'admin@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'Admin User', '00000000-0000-0000-0000-000000000E00', 'T001', 'ACTIVE', '127.0.0.1', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('00000000-0000-0000-0000-000000000001', 'system.admin@company.com', 'system.admin@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'System Admin', '00000000-0000-0000-0000-000000000E01', '00000000-0000-0000-0000-000000000001', 'ACTIVE', '127.0.0.1', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('00000000-0000-0000-0000-000000000111', 'test.user@company.com', 'test.user@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'Test User', '00000000-0000-0000-0000-000000000E11', 'T001', 'ACTIVE', '127.0.0.1', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('00000000-0000-0000-0000-000000000222', 'inactive.user@company.com', 'inactive.user@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'Inactive User', '00000000-0000-0000-0000-000000000E22', 'T001', 'INACTIVE', NULL, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('00000000-0000-0000-0000-000000000333', 'batch1@company.com', 'batch1@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'Batch User 1', '00000000-0000-0000-0000-000000000E33', 'T001', 'ACTIVE', '127.0.0.1', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('00000000-0000-0000-0000-000000000444', 'batch2@company.com', 'batch2@company.com', '$2a$12$FeuZsVoN4ljUZu.Z8.Q5wOsCr7fqKfXpl3fKgUXP4653RSf8aVaZ2', 'Batch User 2', '00000000-0000-0000-0000-000000000E44', 'T001', 'ACTIVE', '127.0.0.1', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- 指派角色給使用者
INSERT INTO user_roles (user_role_id, user_id, role_id) VALUES
('ur-current', '00000000-0000-0000-0000-000000000999', 'a0000000-0000-0000-0000-000000000001'),
('ur-admin', '00000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
('ur-test', '00000000-0000-0000-0000-000000000111', 'a0000000-0000-0000-0000-000000000002'),
('ur-test2', '00000000-0000-0000-0000-000000000222', 'a0000000-0000-0000-0000-000000000002'),
('ur-test3', '00000000-0000-0000-0000-000000000333', 'a0000000-0000-0000-0000-000000000002'),
('ur-test4', '00000000-0000-0000-0000-000000000444', 'a0000000-0000-0000-0000-000000000002');
