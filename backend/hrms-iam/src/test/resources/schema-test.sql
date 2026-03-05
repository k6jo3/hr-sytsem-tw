-- IAM Service Test Schema
-- 用於合約測試的資料庫結構定義

-- ============================================================================
-- 租戶表 (tenants)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tenants (
    tenant_id VARCHAR(36) PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL UNIQUE,
    tenant_code VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 使用者表 (users)
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    display_name VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    tenant_id VARCHAR(50),
    employee_id UUID,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    last_logout_at TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    password_changed_at TIMESTAMP,
    preferred_language VARCHAR(20) DEFAULT 'zh-TW',
    timezone VARCHAR(50) DEFAULT 'Asia/Taipei',
    auth_source VARCHAR(20) DEFAULT 'LOCAL',
    ldap_dn VARCHAR(500),
    must_change_password BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ============================================================================
-- 角色表 (roles)
-- ============================================================================
CREATE TABLE IF NOT EXISTS roles (
    role_id VARCHAR(36) PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    tenant_id VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(role_code, tenant_id)
);

-- ============================================================================
-- 使用者角色關聯表 (user_roles)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    UNIQUE(user_id, role_id)
);

-- ============================================================================
-- 權限表 (permissions)
-- ============================================================================
CREATE TABLE IF NOT EXISTS permissions (
    permission_id UUID PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(200) NOT NULL,
    resource_type VARCHAR(50),
    action VARCHAR(50),
    description TEXT,
    parent_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_permissions_parent FOREIGN KEY (parent_id) REFERENCES permissions(permission_id) ON DELETE SET NULL
);

-- ============================================================================
-- 角色權限關聯表 (role_permissions)
-- ============================================================================
CREATE TABLE IF NOT EXISTS role_permissions (
    role_permission_id UUID PRIMARY KEY,
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE,
    UNIQUE(role_id, permission_id)
);

-- ============================================================================
-- 登入歷史表 (login_history) - 用於 AUTH_CMD_001
-- ============================================================================
CREATE TABLE IF NOT EXISTS login_history (
    login_history_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    login_ip VARCHAR(50),
    user_agent VARCHAR(500),
    login_status VARCHAR(20),
    logout_time TIMESTAMP,
    session_duration_seconds INTEGER,
    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================================
-- 密碼重設令牌表 (password_reset_tokens) - 用於 AUTH_CMD_004, 005
-- ============================================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================================
-- 建立索引 - 提升查詢效能
-- ============================================================================

-- users 表索引
CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users(is_deleted);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_employee_id ON users(employee_id);

-- roles 表索引
CREATE INDEX IF NOT EXISTS idx_roles_tenant_id ON roles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_roles_is_deleted ON roles(is_deleted);
CREATE INDEX IF NOT EXISTS idx_roles_is_system_role ON roles(is_system_role);
CREATE INDEX IF NOT EXISTS idx_roles_role_name ON roles(role_name);

-- user_roles 表索引
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- role_permissions 表索引
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);

-- permissions 表索引
CREATE INDEX IF NOT EXISTS idx_permissions_parent_id ON permissions(parent_id);
CREATE INDEX IF NOT EXISTS idx_permissions_resource_type ON permissions(resource_type);
CREATE INDEX IF NOT EXISTS idx_permissions_action ON permissions(action);

-- login_history 表索引
CREATE INDEX IF NOT EXISTS idx_login_history_user_id ON login_history(user_id);
CREATE INDEX IF NOT EXISTS idx_login_history_login_time ON login_history(login_time);

-- password_reset_tokens 表索引
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

-- ============================================================================
-- 註解說明
-- ============================================================================

COMMENT ON TABLE users IS 'IAM 使用者表';
COMMENT ON TABLE roles IS 'IAM 角色表';
COMMENT ON TABLE user_roles IS '使用者角色關聯表';
COMMENT ON TABLE permissions IS 'IAM 權限表';
COMMENT ON TABLE role_permissions IS '角色權限關聯表';
COMMENT ON TABLE login_history IS '登入歷史記錄表';
COMMENT ON TABLE password_reset_tokens IS '密碼重設令牌表';

COMMENT ON COLUMN users.is_deleted IS '軟刪除標記';
COMMENT ON COLUMN users.status IS '使用者狀態: ACTIVE, LOCKED, INACTIVE';
COMMENT ON COLUMN users.tenant_id IS '租戶 ID（多租戶隔離）';
COMMENT ON COLUMN roles.is_system_role IS '系統角色標記（true=系統預設，false=自訂）';
COMMENT ON COLUMN roles.is_deleted IS '軟刪除標記';
