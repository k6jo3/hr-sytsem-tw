-- ============================================================================
-- HR13 Document Service - Local Schema (H2)
-- 對應 PO: DocumentPO, DocumentVersionPO, DocumentAccessLogPO,
--          DocumentRequestPO, DocumentTemplatePO
-- 注意: 表名前綴為 hrs_ (與 Entity @Table 一致)
-- ============================================================================

-- 文件
CREATE TABLE IF NOT EXISTS hrs_documents (
    document_id VARCHAR(36) PRIMARY KEY,
    document_type VARCHAR(50),
    business_type VARCHAR(50),
    business_id VARCHAR(50),
    file_name VARCHAR(200),
    mime_type VARCHAR(100),
    file_size BIGINT DEFAULT 0,
    storage_path VARCHAR(500),
    visibility VARCHAR(20),
    classification VARCHAR(20),
    is_encrypted BOOLEAN DEFAULT FALSE,
    owner_id VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE,
    folder_id VARCHAR(50),
    tags VARCHAR(500),
    uploaded_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 文件版本
CREATE TABLE IF NOT EXISTS hrs_document_versions (
    version_id VARCHAR(36) PRIMARY KEY,
    document_id VARCHAR(36),
    version_number INT DEFAULT 1,
    file_name VARCHAR(200),
    file_size BIGINT DEFAULT 0,
    storage_path VARCHAR(500),
    uploader_id VARCHAR(50),
    uploaded_at TIMESTAMP,
    change_note VARCHAR(500)
);

-- 文件存取紀錄
CREATE TABLE IF NOT EXISTS hrs_document_access_logs (
    log_id VARCHAR(36) PRIMARY KEY,
    document_id VARCHAR(36),
    user_id VARCHAR(50),
    action VARCHAR(20),
    ip_address VARCHAR(50),
    accessed_at TIMESTAMP
);

-- 文件申請
CREATE TABLE IF NOT EXISTS hrs_document_requests (
    request_id VARCHAR(36) PRIMARY KEY,
    template_code VARCHAR(50),
    requester_id VARCHAR(50),
    purpose VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP,
    document_id VARCHAR(36)
);

-- 文件範本
CREATE TABLE IF NOT EXISTS hrs_document_templates (
    template_id VARCHAR(36) PRIMARY KEY,
    template_code VARCHAR(50) UNIQUE,
    name VARCHAR(200),
    content TEXT,
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
