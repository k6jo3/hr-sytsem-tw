-- HR02 組織員工服務 - V3: 建立員工表 (核心表)
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE employees (
    employee_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_number VARCHAR(50) UNIQUE NOT NULL,

    -- 基本資料
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    national_id VARCHAR(255) NOT NULL,  -- 加密欄位
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    marital_status VARCHAR(20),

    -- 聯絡方式
    personal_email VARCHAR(255),
    company_email VARCHAR(255) UNIQUE NOT NULL,
    mobile_phone VARCHAR(50),
    home_phone VARCHAR(50),

    -- 地址 (JSON)
    address JSONB,

    -- 緊急聯絡人 (JSON)
    emergency_contact JSONB,

    -- 組織關係
    organization_id UUID NOT NULL REFERENCES organizations(organization_id),
    department_id UUID NOT NULL REFERENCES departments(department_id),
    manager_id UUID REFERENCES employees(employee_id),

    -- 職務資訊
    job_title VARCHAR(255),
    job_level VARCHAR(50),
    employment_type VARCHAR(20) NOT NULL,
    employment_status VARCHAR(20) NOT NULL DEFAULT 'PROBATION',

    -- 到離職資訊
    hire_date DATE NOT NULL,
    probation_end_date DATE,
    termination_date DATE,
    termination_reason TEXT,

    -- 銀行資訊 (JSON, 加密)
    bank_account JSONB,

    -- 照片
    photo_url VARCHAR(500),

    -- 審計
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_marital_status CHECK (marital_status IN ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED')),
    CONSTRAINT chk_employment_type CHECK (employment_type IN ('FULL_TIME', 'CONTRACT', 'PART_TIME', 'INTERN')),
    CONSTRAINT chk_employment_status CHECK (employment_status IN ('PROBATION', 'ACTIVE', 'PARENTAL_LEAVE', 'UNPAID_LEAVE', 'TERMINATED')),
    CONSTRAINT chk_termination_date CHECK (termination_date IS NULL OR termination_date >= hire_date)
);

-- 索引
CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_employees_company_email ON employees(company_email);
CREATE INDEX idx_employees_national_id ON employees(national_id);
CREATE INDEX idx_employees_organization_id ON employees(organization_id);
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_manager_id ON employees(manager_id);
CREATE INDEX idx_employees_employment_status ON employees(employment_status);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_full_name ON employees(full_name);

-- 全文搜尋索引
CREATE INDEX idx_employees_fulltext ON employees USING gin(to_tsvector('simple', full_name || ' ' || employee_number || ' ' || company_email));

-- 加入部門表的主管外鍵
ALTER TABLE departments ADD CONSTRAINT fk_dept_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id);

-- 註解
COMMENT ON TABLE employees IS '員工主檔表';
COMMENT ON COLUMN employees.national_id IS '身分證號 (加密儲存)';
COMMENT ON COLUMN employees.address IS 'JSON格式: {postalCode, city, district, street}';
COMMENT ON COLUMN employees.emergency_contact IS 'JSON格式: {name, relationship, phoneNumber}';
COMMENT ON COLUMN employees.bank_account IS 'JSON格式: {bankCode, bankName, accountNumber(加密)}';
COMMENT ON COLUMN employees.employment_status IS 'PROBATION(試用)/ACTIVE(在職)/PARENTAL_LEAVE(育嬰留停)/UNPAID_LEAVE(留職停薪)/TERMINATED(離職)';
