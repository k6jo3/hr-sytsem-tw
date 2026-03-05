-- =====================================================
-- HRMS IAM Service - Local Profile Schema (H2 相容)
-- 基於 schema.sql 改寫，移除 H2 不支援的語法
-- =====================================================

-- =====================================================
-- 租戶表 (多租戶支援)
-- =====================================================
CREATE TABLE IF NOT EXISTS tenants (
    tenant_id VARCHAR(36) PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL UNIQUE,
    tenant_code VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tenants_code ON tenants(tenant_code);

-- =====================================================
-- 使用者表
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    display_name VARCHAR(100) NOT NULL,
    employee_id VARCHAR(36),
    tenant_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    last_logout_at TIMESTAMP,
    preferred_language VARCHAR(20) DEFAULT 'zh-TW',
    timezone VARCHAR(50) DEFAULT 'Asia/Taipei',
    password_changed_at TIMESTAMP,
    auth_source VARCHAR(20) DEFAULT 'LOCAL',
    ldap_dn VARCHAR(500),
    must_change_password BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE (username, tenant_id),
    UNIQUE (email, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_users_employee_id ON users(employee_id);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users(is_deleted);

-- =====================================================
-- 角色表
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    role_id VARCHAR(36) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    tenant_id VARCHAR(36),
    is_system_role BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE (role_code, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_roles_code ON roles(role_code);
CREATE INDEX IF NOT EXISTS idx_roles_tenant_id ON roles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_roles_is_deleted ON roles(is_deleted);

-- =====================================================
-- 權限表
-- =====================================================
CREATE TABLE IF NOT EXISTS permissions (
    permission_id VARCHAR(36) PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,
    resource_type VARCHAR(50),
    parent_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_permissions_code ON permissions(permission_code);
CREATE INDEX IF NOT EXISTS idx_permissions_resource ON permissions(resource);

-- =====================================================
-- 使用者角色關聯表
-- =====================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- =====================================================
-- 角色權限關聯表
-- =====================================================
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- =====================================================
-- Refresh Token 表
-- =====================================================
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

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- =====================================================
-- 登入歷史表 (相容 login_history + login_logs)
-- =====================================================
CREATE TABLE IF NOT EXISTS login_history (
    login_history_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    login_ip VARCHAR(50),
    user_agent VARCHAR(500),
    login_status VARCHAR(20),
    logout_time TIMESTAMP,
    session_duration_seconds INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_login_history_user_id ON login_history(user_id);
CREATE INDEX IF NOT EXISTS idx_login_history_login_time ON login_history(login_time);

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

CREATE INDEX IF NOT EXISTS idx_login_logs_user_id ON login_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_login_logs_login_at ON login_logs(login_at);
CREATE INDEX IF NOT EXISTS idx_login_logs_result ON login_logs(login_result);

-- =====================================================
-- 密碼重設令牌表
-- =====================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);

-- =====================================================
-- SSO 帳號連結表
-- =====================================================
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

CREATE INDEX IF NOT EXISTS idx_user_sso_links_user_id ON user_sso_links(user_id);

-- =====================================================
-- 密碼歷史表
-- =====================================================
CREATE TABLE IF NOT EXISTS password_history (
    history_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_history_user_id ON password_history(user_id);

-- =====================================================
-- 功能開關表
-- =====================================================
CREATE TABLE IF NOT EXISTS feature_toggles (
    id VARCHAR(36) PRIMARY KEY,
    feature_code VARCHAR(100) NOT NULL,
    feature_name VARCHAR(200) NOT NULL,
    module VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    description VARCHAR(500),
    tenant_id VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (feature_code, tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_feature_toggle_code ON feature_toggles(feature_code);
CREATE INDEX IF NOT EXISTS idx_feature_toggle_module ON feature_toggles(module);

-- =====================================================
-- 系統參數表
-- =====================================================
CREATE TABLE IF NOT EXISTS system_parameters (
    id VARCHAR(36) PRIMARY KEY,
    param_code VARCHAR(100) NOT NULL,
    param_name VARCHAR(200) NOT NULL,
    param_value VARCHAR(2000),
    param_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    module VARCHAR(20) NOT NULL DEFAULT 'GLOBAL',
    category VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',
    description VARCHAR(500),
    default_value VARCHAR(2000),
    tenant_id VARCHAR(36),
    is_encrypted BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (param_code, tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_param_code ON system_parameters(param_code);
CREATE INDEX IF NOT EXISTS idx_sys_param_module ON system_parameters(module);

-- =====================================================
-- 系統參數異動記錄表
-- =====================================================
CREATE TABLE IF NOT EXISTS parameter_change_logs (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    param_code VARCHAR(100) NOT NULL,
    old_value VARCHAR(2000),
    new_value VARCHAR(2000),
    operator VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_param_change_code ON parameter_change_logs(param_code);

-- =====================================================
-- 排程任務配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS scheduled_job_configs (
    id VARCHAR(36) PRIMARY KEY,
    job_code VARCHAR(100) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    module VARCHAR(20) NOT NULL,
    cron_expression VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    description VARCHAR(500),
    last_executed_at TIMESTAMP,
    last_execution_status VARCHAR(20),
    last_error_message VARCHAR(2000),
    consecutive_failures INTEGER DEFAULT 0,
    tenant_id VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (job_code, tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_job_config_code ON scheduled_job_configs(job_code);
CREATE INDEX IF NOT EXISTS idx_job_config_module ON scheduled_job_configs(module);
