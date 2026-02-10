-- H2 Test Schema for Organizations, Departments and Employees
-- This schema is compatible with H2 database in PostgreSQL mode

-- Drop tables if they exist (for clean test runs)
DROP TABLE IF EXISTS employee_history CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;

-- Organizations Table
CREATE TABLE organizations (
    organization_id VARCHAR(50) PRIMARY KEY,
    organization_code VARCHAR(50) UNIQUE NOT NULL,
    organization_name VARCHAR(255) NOT NULL,
    organization_name_en VARCHAR(255),
    organization_type VARCHAR(20) NOT NULL,
    parent_organization_id VARCHAR(50),
    tax_id VARCHAR(20),
    address TEXT,
    phone_number VARCHAR(50),
    fax_number VARCHAR(50),
    email VARCHAR(100),
    established_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT chk_org_type CHECK (organization_type IN ('PARENT', 'SUBSIDIARY')),
    CONSTRAINT chk_org_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Departments Table
CREATE TABLE departments (
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
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_dept_org FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
    CONSTRAINT fk_dept_parent FOREIGN KEY (parent_department_id) REFERENCES departments(department_id),
    CONSTRAINT chk_dept_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_dept_level CHECK (level >= 1 AND level <= 5)
);

-- Employees Table
CREATE TABLE employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    employee_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Basic Info
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    english_name VARCHAR(255),
    national_id VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    marital_status VARCHAR(20),
    
    -- Contact Info
    personal_email VARCHAR(255),
    company_email VARCHAR(255) UNIQUE NOT NULL,
    mobile_phone VARCHAR(50),
    home_phone VARCHAR(50),
    
    -- Address (simplified for H2)
    address_postal_code VARCHAR(10),
    address_city VARCHAR(50),
    address_district VARCHAR(50),
    address_street VARCHAR(255),
    
    -- Emergency Contact (simplified for H2)
    emergency_contact_name VARCHAR(100),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(50),
    
    -- Bank Info (simplified for H2)
    bank_code VARCHAR(10),
    bank_branch_code VARCHAR(10),
    bank_account_number VARCHAR(50),
    bank_account_holder_name VARCHAR(100),
    
    -- Organization Relations
    organization_id VARCHAR(50) NOT NULL,
    department_id VARCHAR(50) NOT NULL,
    manager_id VARCHAR(50),
    
    -- Job Info
    job_title VARCHAR(255),
    job_level VARCHAR(50),
    employment_type VARCHAR(20) NOT NULL,
    employment_status VARCHAR(20) NOT NULL DEFAULT 'PROBATION',
    
    -- Employment Dates
    hire_date DATE NOT NULL,
    probation_end_date DATE,
    termination_date DATE,
    termination_reason TEXT,
    
    -- Photo
    photo_url VARCHAR(500),
    
    -- Audit
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

-- Employee History Table
CREATE TABLE employee_history (
    history_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    old_value TEXT,
    new_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    CONSTRAINT fk_history_emp FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Add manager foreign key to departments (after employees table is created)
ALTER TABLE departments ADD CONSTRAINT fk_dept_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id);

-- Indexes
CREATE INDEX idx_organizations_status ON organizations(status);
CREATE INDEX idx_departments_org_id ON departments(organization_id);
CREATE INDEX idx_departments_status ON departments(status);
CREATE INDEX idx_departments_is_deleted ON departments(is_deleted);
CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_employees_company_email ON employees(company_email);
CREATE INDEX idx_employees_org_id ON employees(organization_id);
CREATE INDEX idx_employees_dept_id ON employees(department_id);
CREATE INDEX idx_employees_status ON employees(employment_status);
CREATE INDEX idx_employees_is_deleted ON employees(is_deleted);
