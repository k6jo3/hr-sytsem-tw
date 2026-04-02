-- =====================================================
-- IAM 模組補充 Schema
-- MyBatis 使用但 JPA 不會自動建立的 table
-- 用於 Azure PostgreSQL 部署時手動執行
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
