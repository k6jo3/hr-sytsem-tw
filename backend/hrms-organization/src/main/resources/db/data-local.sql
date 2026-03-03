-- =====================================================
-- HRMS Organization Service - Local Profile 初始資料
-- H2 相容語法（使用 MERGE INTO 取代 ON CONFLICT）
-- =====================================================

-- =====================================================
-- 1. 預設組織（母公司）
-- =====================================================
MERGE INTO organizations (
    organization_id, organization_code, organization_name, organization_name_en,
    organization_type, tax_id, address, phone_number, established_date, status
) KEY (organization_id) VALUES (
    'org-0001',
    'COMPANY',
    '範例科技股份有限公司',
    'Example Technology Co., Ltd.',
    'PARENT',
    '12345678',
    '台北市信義區信義路五段7號',
    '02-12345678',
    '2020-01-01',
    'ACTIVE'
);

-- =====================================================
-- 2. 預設部門
-- =====================================================

-- 總經理室
MERGE INTO departments (
    department_id, department_code, department_name, department_name_en,
    organization_id, level, display_order, status
) KEY (department_id) VALUES (
    'dept-0001',
    'CEO',
    '總經理室',
    'CEO Office',
    'org-0001',
    1,
    1,
    'ACTIVE'
);

-- 人力資源部
MERGE INTO departments (
    department_id, department_code, department_name, department_name_en,
    organization_id, parent_department_id, level, display_order, status
) KEY (department_id) VALUES (
    'dept-0002',
    'HR',
    '人力資源部',
    'Human Resources',
    'org-0001',
    'dept-0001',
    2,
    2,
    'ACTIVE'
);

-- 資訊技術部
MERGE INTO departments (
    department_id, department_code, department_name, department_name_en,
    organization_id, parent_department_id, level, display_order, status
) KEY (department_id) VALUES (
    'dept-0003',
    'IT',
    '資訊技術部',
    'Information Technology',
    'org-0001',
    'dept-0001',
    2,
    3,
    'ACTIVE'
);

-- 財務部
MERGE INTO departments (
    department_id, department_code, department_name, department_name_en,
    organization_id, parent_department_id, level, display_order, status
) KEY (department_id) VALUES (
    'dept-0004',
    'FIN',
    '財務部',
    'Finance',
    'org-0001',
    'dept-0001',
    2,
    4,
    'ACTIVE'
);

-- 業務部
MERGE INTO departments (
    department_id, department_code, department_name, department_name_en,
    organization_id, parent_department_id, level, display_order, status
) KEY (department_id) VALUES (
    'dept-0005',
    'SALES',
    '業務部',
    'Sales',
    'org-0001',
    'dept-0001',
    2,
    5,
    'ACTIVE'
);

-- =====================================================
-- 3. 測試用員工資料
-- =====================================================

-- 總經理
MERGE INTO employees (
    employee_id, employee_number, first_name, last_name, full_name, english_name,
    national_id, date_of_birth, gender, marital_status,
    company_email, mobile_phone,
    organization_id, department_id,
    job_title, job_level, employment_type, employment_status,
    hire_date
) KEY (employee_id) VALUES (
    'emp-0001',
    'EMP001',
    '大明',
    '王',
    '王大明',
    'David Wang',
    'A123456789',
    '1975-03-15',
    'MALE',
    'MARRIED',
    'david.wang@company.com',
    '0912345678',
    'org-0001',
    'dept-0001',
    '總經理',
    'C-LEVEL',
    'FULL_TIME',
    'ACTIVE',
    '2020-01-01'
);

-- HR 主管
MERGE INTO employees (
    employee_id, employee_number, first_name, last_name, full_name, english_name,
    national_id, date_of_birth, gender, marital_status,
    company_email, mobile_phone,
    organization_id, department_id, manager_id,
    job_title, job_level, employment_type, employment_status,
    hire_date
) KEY (employee_id) VALUES (
    'emp-0002',
    'EMP002',
    '小美',
    '李',
    '李小美',
    'Mary Lee',
    'B234567890',
    '1985-07-22',
    'FEMALE',
    'SINGLE',
    'mary.lee@company.com',
    '0923456789',
    'org-0001',
    'dept-0002',
    'emp-0001',
    'HR 經理',
    'MANAGER',
    'FULL_TIME',
    'ACTIVE',
    '2020-06-01'
);

-- IT 工程師
MERGE INTO employees (
    employee_id, employee_number, first_name, last_name, full_name, english_name,
    national_id, date_of_birth, gender,
    company_email, mobile_phone,
    organization_id, department_id, manager_id,
    job_title, job_level, employment_type, employment_status,
    hire_date
) KEY (employee_id) VALUES (
    'emp-0003',
    'EMP003',
    '志強',
    '陳',
    '陳志強',
    'John Chen',
    'C345678901',
    '1990-11-08',
    'MALE',
    'john.chen@company.com',
    '0934567890',
    'org-0001',
    'dept-0003',
    'emp-0001',
    '資深工程師',
    'SENIOR',
    'FULL_TIME',
    'ACTIVE',
    '2021-03-15'
);

-- 試用期員工
MERGE INTO employees (
    employee_id, employee_number, first_name, last_name, full_name, english_name,
    national_id, date_of_birth, gender,
    company_email, mobile_phone,
    organization_id, department_id, manager_id,
    job_title, job_level, employment_type, employment_status,
    hire_date, probation_end_date
) KEY (employee_id) VALUES (
    'emp-0004',
    'EMP004',
    '雅婷',
    '林',
    '林雅婷',
    'Yating Lin',
    'D456789012',
    '1995-04-18',
    'FEMALE',
    'yating.lin@company.com',
    '0945678901',
    'org-0001',
    'dept-0003',
    'emp-0003',
    '初級工程師',
    'JUNIOR',
    'FULL_TIME',
    'PROBATION',
    '2026-01-15',
    '2026-04-15'
);

-- =====================================================
-- 4. 設定部門主管
-- =====================================================
UPDATE departments SET manager_id = 'emp-0001' WHERE department_id = 'dept-0001';
UPDATE departments SET manager_id = 'emp-0002' WHERE department_id = 'dept-0002';
UPDATE departments SET manager_id = 'emp-0003' WHERE department_id = 'dept-0003';

-- =====================================================
-- 5. 測試用合約資料
-- =====================================================
MERGE INTO employee_contracts (
    contract_id, employee_id, contract_type, contract_number,
    start_date, working_hours, status
) KEY (contract_id) VALUES (
    'contract-0001',
    'emp-0001',
    'INDEFINITE',
    'CTR-2020-001',
    '2020-01-01',
    40,
    'ACTIVE'
);

MERGE INTO employee_contracts (
    contract_id, employee_id, contract_type, contract_number,
    start_date, working_hours, status
) KEY (contract_id) VALUES (
    'contract-0002',
    'emp-0002',
    'INDEFINITE',
    'CTR-2020-002',
    '2020-06-01',
    40,
    'ACTIVE'
);

-- =====================================================
-- 6. 測試用人事歷程
-- =====================================================
MERGE INTO employee_history (
    history_id, employee_id, event_type, event_date, description, created_by
) KEY (history_id) VALUES (
    'hist-0001',
    'emp-0001',
    'ONBOARDING',
    '2020-01-01',
    '到職報到',
    'system'
);

MERGE INTO employee_history (
    history_id, employee_id, event_type, event_date, description, created_by
) KEY (history_id) VALUES (
    'hist-0002',
    'emp-0004',
    'ONBOARDING',
    '2026-01-15',
    '到職報到（試用期）',
    'system'
);
