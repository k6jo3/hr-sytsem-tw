-- ============================================================================
-- HR12 Notification Service - Local Schema (H2)
-- 對應 PO: NotificationPO, NotificationTemplatePO, AnnouncementPO,
--          AnnouncementReadRecordPO, NotificationPreferencePO
-- ============================================================================

-- 通知
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(50) PRIMARY KEY,
    recipient_id VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    content TEXT,
    notification_type VARCHAR(50),
    priority VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    channels TEXT,
    related_business_id VARCHAR(50),
    related_business_type VARCHAR(50),
    related_business_url VARCHAR(500),
    template_code VARCHAR(50),
    template_variables TEXT,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    failure_reason VARCHAR(500),
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 通知範本
CREATE TABLE IF NOT EXISTS notification_templates (
    id VARCHAR(50) PRIMARY KEY,
    template_code VARCHAR(50) UNIQUE,
    name VARCHAR(100),
    description VARCHAR(500),
    subject VARCHAR(200),
    body TEXT,
    notification_type VARCHAR(50),
    default_priority VARCHAR(20),
    default_channels TEXT,
    variables TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 公告
CREATE TABLE IF NOT EXISTS announcements (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20),
    target_audience VARCHAR(50),
    target_department_ids TEXT,
    target_role_ids TEXT,
    target_employee_ids TEXT,
    published_by VARCHAR(50),
    published_at TIMESTAMP,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    is_pinned BOOLEAN DEFAULT FALSE,
    attachments TEXT,
    read_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 公告已讀紀錄
CREATE TABLE IF NOT EXISTS announcement_read_records (
    id VARCHAR(50) PRIMARY KEY,
    announcement_id VARCHAR(50) NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    read_at TIMESTAMP NOT NULL
);

-- 通知偏好設定
CREATE TABLE IF NOT EXISTS notification_preferences (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE,
    in_app_enabled BOOLEAN DEFAULT TRUE,
    email_enabled BOOLEAN DEFAULT TRUE,
    push_enabled BOOLEAN DEFAULT FALSE,
    teams_enabled BOOLEAN DEFAULT FALSE,
    line_enabled BOOLEAN DEFAULT FALSE,
    quiet_hours_enabled BOOLEAN DEFAULT FALSE,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    email_address VARCHAR(100),
    push_tokens TEXT,
    line_user_id VARCHAR(100),
    teams_webhook_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE
);
