-- ============================================================================
-- HR06 Project Service - Local Schema (H2)
-- 對應 Entity: CustomerEntity, ProjectEntity, ProjectMemberEntity, TaskEntity
-- ============================================================================

-- 客戶
CREATE TABLE IF NOT EXISTS customers (
    customer_id     VARCHAR(36) PRIMARY KEY,
    customer_code   VARCHAR(50)  NOT NULL UNIQUE,
    customer_name   VARCHAR(200) NOT NULL,
    tax_id          VARCHAR(20),
    industry        VARCHAR(100),
    email           VARCHAR(200),
    phone_number    VARCHAR(50),
    status          VARCHAR(20)  NOT NULL,
    is_deleted      INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT       NOT NULL DEFAULT 0
);

-- 專案
CREATE TABLE IF NOT EXISTS projects (
    project_id        VARCHAR(36) PRIMARY KEY,
    project_code      VARCHAR(50)  NOT NULL UNIQUE,
    project_name      VARCHAR(200) NOT NULL,
    project_type      VARCHAR(20),
    start_date        DATE,
    end_date          DATE,
    description       TEXT,
    status            VARCHAR(20),
    customer_id       VARCHAR(36),
    planned_start_date DATE,
    planned_end_date   DATE,
    actual_start_date  DATE,
    actual_end_date    DATE,
    budget_type       VARCHAR(30),
    budget_amount     DECIMAL(14,2),
    budget_hours      DECIMAL(10,2),
    actual_hours      DECIMAL(10,2),
    actual_cost       DECIMAL(14,2),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    version           BIGINT       NOT NULL DEFAULT 0
);

-- 專案成員
CREATE TABLE IF NOT EXISTS project_members (
    member_id       VARCHAR(36) PRIMARY KEY,
    project_id      VARCHAR(36)  NOT NULL,
    employee_id     VARCHAR(36)  NOT NULL,
    role            VARCHAR(50)  NOT NULL,
    allocated_hours DECIMAL(10,2),
    hourly_rate     DECIMAL(10,2),
    join_date       DATE         NOT NULL,
    leave_date      DATE,
    CONSTRAINT fk_member_project FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- 工項 (WBS)
CREATE TABLE IF NOT EXISTS tasks (
    task_id         VARCHAR(36) PRIMARY KEY,
    project_id      VARCHAR(36)  NOT NULL,
    parent_task_id  VARCHAR(36),
    task_code       VARCHAR(50)  NOT NULL,
    task_name       VARCHAR(200) NOT NULL,
    description     TEXT,
    start_date      DATE,
    end_date        DATE,
    level           INTEGER      NOT NULL,
    estimated_hours DECIMAL(10,2),
    actual_hours    DECIMAL(10,2),
    assignee_id     VARCHAR(36),
    status          VARCHAR(20),
    progress        INTEGER      DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(project_id)
);
