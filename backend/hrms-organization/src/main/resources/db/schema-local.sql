-- =====================================================
-- HRMS Organization Service - Local Profile Schema (H2 相容)
-- 合併自 Flyway V1~V8, V2_1, V3_1 migration
-- 移除 H2 不支援語法：gen_random_uuid(), JSONB, GIN index,
--   to_tsvector, COMMENT ON, USING gin
-- =====================================================

-- 清除既有表（確保乾淨啟動）
DROP TABLE IF EXISTS certificate_requests CASCADE;
DROP TABLE IF EXISTS work_experiences CASCADE;
DROP TABLE IF EXISTS educations CASCADE;
DROP TABLE IF EXISTS employee_history CASCADE;
DROP TABLE IF EXISTS employee_contracts CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;

-- =====================================================
-- V1: 組織表
-- =====================================================
CREATE TABLE IF NOT EXISTS organizations (
    organization_id VARCHAR(50) PRIMARY KEY,
    organization_code VARCHAR(50) UNIQUE NOT NULL,
    organization_name VARCHAR(255) NOT NULL,
    organization_name_en VARCHAR(255),
    organization_type VARCHAR(20) NOT NULL,
    parent_organization_id VARCHAR(50),
    tax_id VARCHAR(20),
    address CLOB,
    phone_number VARCHAR(50),
    fax_number VARCHAR(50),
    email VARCHAR(100),
    established_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    description CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT chk_org_type CHECK (organization_type IN ('PARENT', 'SUBSIDIARY')),
    CONSTRAINT chk_org_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX IF NOT EXISTS idx_organizations_parent_id ON organizations(parent_organization_id);
CREATE INDEX IF NOT EXISTS idx_organizations_status ON organizations(status);
CREATE INDEX IF NOT EXISTS idx_organizations_is_deleted ON organizations(is_deleted);

-- =====================================================
-- V2 + V2_1: 部門表（含 is_deleted 欄位）
-- =====================================================
CREATE TABLE IF NOT EXISTS departments (
    department_id VARCHAR(50) PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL,
    department_name VARCHAR(255) NOT NULL,
    department_name_en VARCHAR(255),
    organization_id VARCHAR(50) NOT NULL,
    parent_department_id VARCHAR(50),
    level INTEGER NOT NULL DEFAULT 1,
    path VARCHAR(500),
    manager_id VARCHAR(50),
    display_order INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    description CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_dept_org FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
    CONSTRAINT fk_dept_parent FOREIGN KEY (parent_department_id) REFERENCES departments(department_id),
    CONSTRAINT chk_dept_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_dept_level CHECK (level >= 1 AND level <= 5)
);

CREATE INDEX IF NOT EXISTS idx_departments_org_id ON departments(organization_id);
CREATE INDEX IF NOT EXISTS idx_departments_parent_id ON departments(parent_department_id);
CREATE INDEX IF NOT EXISTS idx_departments_manager_id ON departments(manager_id);
CREATE INDEX IF NOT EXISTS idx_departments_status ON departments(status);
CREATE INDEX IF NOT EXISTS idx_departments_is_deleted ON departments(is_deleted);

-- =====================================================
-- V3 + V3_1: 員工表（含 is_deleted 欄位）
-- JSONB 欄位改為 CLOB（H2 不支援 JSONB）
-- =====================================================
CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    employee_number VARCHAR(50) UNIQUE NOT NULL,

    -- 基本資料
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    english_name VARCHAR(255),
    national_id VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    marital_status VARCHAR(20),

    -- 聯絡方式
    personal_email VARCHAR(255),
    company_email VARCHAR(255) UNIQUE NOT NULL,
    mobile_phone VARCHAR(50),
    home_phone VARCHAR(50),

    -- 地址（H2 不支援 JSONB，展開為獨立欄位）
    address_postal_code VARCHAR(10),
    address_city VARCHAR(50),
    address_district VARCHAR(50),
    address_street VARCHAR(255),

    -- 緊急聯絡人（展開為獨立欄位）
    emergency_contact_name VARCHAR(100),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(50),

    -- 銀行資訊（展開為獨立欄位）
    bank_code VARCHAR(10),
    bank_branch_code VARCHAR(10),
    bank_account_number VARCHAR(50),
    bank_account_holder_name VARCHAR(100),

    -- 組織關係
    organization_id VARCHAR(50) NOT NULL,
    department_id VARCHAR(50) NOT NULL,
    manager_id VARCHAR(50),

    -- 職務資訊
    job_title VARCHAR(255),
    job_level VARCHAR(50),
    employment_type VARCHAR(20) NOT NULL,
    employment_status VARCHAR(20) NOT NULL DEFAULT 'PROBATION',

    -- 到離職資訊
    hire_date DATE NOT NULL,
    probation_end_date DATE,
    termination_date DATE,
    termination_reason CLOB,

    -- 照片
    photo_url VARCHAR(500),

    -- 審計
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),

    CONSTRAINT fk_emp_org FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
    CONSTRAINT fk_emp_dept FOREIGN KEY (department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_emp_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id),
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_marital_status CHECK (marital_status IN ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED')),
    CONSTRAINT chk_employment_type CHECK (employment_type IN ('FULL_TIME', 'CONTRACT', 'PART_TIME', 'INTERN')),
    CONSTRAINT chk_employment_status CHECK (employment_status IN ('PROBATION', 'ACTIVE', 'PARENTAL_LEAVE', 'UNPAID_LEAVE', 'TERMINATED')),
    CONSTRAINT chk_termination_date CHECK (termination_date IS NULL OR termination_date >= hire_date)
);

CREATE INDEX IF NOT EXISTS idx_employees_employee_number ON employees(employee_number);
CREATE INDEX IF NOT EXISTS idx_employees_company_email ON employees(company_email);
CREATE INDEX IF NOT EXISTS idx_employees_national_id ON employees(national_id);
CREATE INDEX IF NOT EXISTS idx_employees_organization_id ON employees(organization_id);
CREATE INDEX IF NOT EXISTS idx_employees_department_id ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_employees_manager_id ON employees(manager_id);
CREATE INDEX IF NOT EXISTS idx_employees_employment_status ON employees(employment_status);
CREATE INDEX IF NOT EXISTS idx_employees_hire_date ON employees(hire_date);
CREATE INDEX IF NOT EXISTS idx_employees_full_name ON employees(full_name);
CREATE INDEX IF NOT EXISTS idx_employees_is_deleted ON employees(is_deleted);

-- 加入部門表的主管外鍵（員工表建立後才能加）
ALTER TABLE departments ADD CONSTRAINT fk_dept_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id);

-- =====================================================
-- V4: 員工合約表
-- =====================================================
CREATE TABLE IF NOT EXISTS employee_contracts (
    contract_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    contract_type VARCHAR(20) NOT NULL,
    contract_number VARCHAR(100) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    working_hours DECIMAL(5,2) NOT NULL DEFAULT 40,
    trial_period_months INTEGER DEFAULT 0,
    attachment_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contract_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    CONSTRAINT chk_contract_type CHECK (contract_type IN ('INDEFINITE', 'FIXED_TERM')),
    CONSTRAINT chk_contract_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED')),
    CONSTRAINT chk_end_date CHECK (end_date IS NULL OR end_date > start_date)
);

CREATE INDEX IF NOT EXISTS idx_contracts_employee_id ON employee_contracts(employee_id);
CREATE INDEX IF NOT EXISTS idx_contracts_status ON employee_contracts(status);
CREATE INDEX IF NOT EXISTS idx_contracts_end_date ON employee_contracts(end_date);

-- =====================================================
-- V5: 員工人事歷程表
-- JSONB 欄位改為 CLOB
-- =====================================================
CREATE TABLE IF NOT EXISTS employee_history (
    history_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    effective_date DATE,
    event_date DATE,
    old_value CLOB,
    new_value CLOB,
    reason CLOB,
    description CLOB,
    approved_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    CONSTRAINT fk_history_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_history_employee_id ON employee_history(employee_id);
CREATE INDEX IF NOT EXISTS idx_history_event_type ON employee_history(event_type);
CREATE INDEX IF NOT EXISTS idx_history_effective_date ON employee_history(effective_date);

-- =====================================================
-- V6: 學歷表
-- =====================================================
CREATE TABLE IF NOT EXISTS educations (
    education_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    degree VARCHAR(50) NOT NULL,
    school VARCHAR(255) NOT NULL,
    major VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_highest_degree BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_edu_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    CONSTRAINT chk_degree CHECK (degree IN ('HIGH_SCHOOL', 'ASSOCIATE', 'BACHELOR', 'MASTER', 'DOCTORATE'))
);

CREATE INDEX IF NOT EXISTS idx_educations_employee_id ON educations(employee_id);

-- =====================================================
-- V7: 工作經歷表
-- =====================================================
CREATE TABLE IF NOT EXISTS work_experiences (
    experience_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    company VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    description CLOB,
    CONSTRAINT fk_exp_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_work_experiences_employee_id ON work_experiences(employee_id);

-- =====================================================
-- V8: 證明文件申請表
-- =====================================================
CREATE TABLE IF NOT EXISTS certificate_requests (
    request_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    certificate_type VARCHAR(50) NOT NULL,
    purpose VARCHAR(500),
    quantity INTEGER DEFAULT 1,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    processed_by VARCHAR(50),
    processed_at TIMESTAMP,
    document_url VARCHAR(500),
    CONSTRAINT fk_cert_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    CONSTRAINT chk_certificate_type CHECK (certificate_type IN (
        'EMPLOYMENT_CERTIFICATE', 'SALARY_CERTIFICATE', 'TAX_WITHHOLDING'
    )),
    CONSTRAINT chk_request_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED'))
);

CREATE INDEX IF NOT EXISTS idx_certificate_requests_employee_id ON certificate_requests(employee_id);
CREATE INDEX IF NOT EXISTS idx_certificate_requests_status ON certificate_requests(status);
CREATE INDEX IF NOT EXISTS idx_certificate_requests_request_date ON certificate_requests(request_date);
