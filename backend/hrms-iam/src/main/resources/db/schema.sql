-- =====================================================
-- HRMS IAM Service - Database Schema
-- =====================================================

-- 使用者表
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    role_id VARCHAR(36) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 使用者角色關聯表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- 權限表
CREATE TABLE IF NOT EXISTS permissions (
    permission_id VARCHAR(36) PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 角色權限關聯表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- =====================================================
-- 初始資料
-- =====================================================

-- 預設角色
INSERT INTO roles (role_id, role_name, description) VALUES 
    ('role-admin', 'ADMIN', '系統管理員'),
    ('role-user', 'USER', '一般使用者'),
    ('role-manager', 'MANAGER', '主管')
ON CONFLICT (role_id) DO NOTHING;

-- 預設權限
INSERT INTO permissions (permission_id, permission_name, description, resource, action) VALUES
    ('perm-user-create', 'USER_CREATE', '建立使用者', 'USER', 'CREATE'),
    ('perm-user-read', 'USER_READ', '查詢使用者', 'USER', 'READ'),
    ('perm-user-update', 'USER_UPDATE', '更新使用者', 'USER', 'UPDATE'),
    ('perm-user-delete', 'USER_DELETE', '刪除使用者', 'USER', 'DELETE')
ON CONFLICT (permission_id) DO NOTHING;

-- 管理員角色擁有所有權限
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-admin', 'perm-user-create'),
    ('role-admin', 'perm-user-read'),
    ('role-admin', 'perm-user-update'),
    ('role-admin', 'perm-user-delete')
ON CONFLICT (role_id, permission_id) DO NOTHING;
