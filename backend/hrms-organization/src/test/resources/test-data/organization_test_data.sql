-- Organization 測試資料
-- 涵蓋 14 個員工，滿足 OrganizationContractTest 的各種查詢場景

-- 1. 清理資料 (已由 cleanup.sql 處理，此處為雙重保險)
DELETE FROM employees;
DELETE FROM departments;

-- 2. 插入部門資料 (使用 UUID 格式)
-- department_id, department_code, department_name, organization_id, parent_department_id, level, status, display_order, created_at, updated_at, is_deleted
INSERT INTO departments (department_id, department_code, department_name, organization_id, parent_department_id, level, status, display_order, created_at, updated_at, is_deleted)
VALUES 
('d0000001-0001-0001-0001-000000000001', 'RD', '研發部', '11111111-1111-1111-1111-111111111111', NULL, 1, 'ACTIVE', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('d0000002-0002-0002-0002-000000000002', 'SALES', '業務部', '11111111-1111-1111-1111-111111111111', NULL, 1, 'ACTIVE', 2, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('d0000003-0003-0003-0003-000000000003', 'FIN', '財務部', '11111111-1111-1111-1111-111111111111', NULL, 1, 'ACTIVE', 3, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('d0000004-0004-0004-0004-000000000004', 'HR', '人事部', '11111111-1111-1111-1111-111111111111', NULL, 1, 'ACTIVE', 4, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('d0000005-0005-0005-0005-000000000005', 'RD-FE', '前端組', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 2, 'ACTIVE', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('d0000006-0006-0006-0006-000000000006', 'RD-BE', '後端組', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 2, 'INACTIVE', 2, '2025-01-01 09:00:00', '2025-01-20 09:00:00', FALSE);

-- 3. 插入員工資料 (使用 UUID 格式)
-- 所有必要欄位：employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, 
--                company_email, mobile_phone, organization_id, department_id, employment_type, employment_status, hire_date

-- 8 個 ACTIVE 員工 (其中 5 個在研發部, 1 個姓王, 1 個是 EMP202501-001)
INSERT INTO employees (employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, company_email, mobile_phone, organization_id, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
-- 研發部 (5人)
('e0000001-0001-0001-0001-000000000001', 'EMP202501-001', '小明', '王', '王小明', 'A123456789', '1990-01-01', 'MALE', 'wang.xm@company.com', '0912345678', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 'FULL_TIME', 'ACTIVE', '2025-01-01', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('e0000002-0002-0002-0002-000000000002', 'EMP202501-002', '志強', '李', '李志強', 'A123456716', '1995-02-01', 'MALE', 'li.zq@company.com', '0923456789', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 'FULL_TIME', 'ACTIVE', '2025-01-02', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('e0000003-0003-0003-0003-000000000003', 'EMP202501-003', '美麗', '張', '張美麗', 'A223456709', '1992-03-15', 'FEMALE', 'zhang.ml@company.com', '0934567890', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 'FULL_TIME', 'ACTIVE', '2025-01-03', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('e0000004-0004-0004-0004-000000000004', 'EMP202401-001', '大為', '劉', '劉大為', 'A123456725', '1988-05-20', 'MALE', 'liu.dw@company.com', '0945678901', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 'FULL_TIME', 'ACTIVE', '2024-01-01', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('e0000005-0005-0005-0005-000000000005', 'EMP202401-002', '淑芬', '陳', '陳淑芬', 'A223456718', '1993-07-10', 'FEMALE', 'chen.sf@company.com', '0956789012', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 'FULL_TIME', 'ACTIVE', '2024-01-02', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
-- 其他部門
('e0000006-0006-0006-0006-000000000006', 'EMP202401-003', '建國', '趙', '趙建國', 'A123456734', '1985-08-25', 'MALE', 'zhao.jg@company.com', '0967890123', '11111111-1111-1111-1111-111111111111', 'd0000002-0002-0002-0002-000000000002', 'FULL_TIME', 'ACTIVE', '2024-01-03', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('e0000007-0007-0007-0007-000000000007', 'EMP202401-004', '愛華', '孫', '孫愛華', 'A223456727', '1991-09-30', 'FEMALE', 'sun.ah@company.com', '0978901234', '11111111-1111-1111-1111-111111111111', 'd0000003-0003-0003-0003-000000000003', 'FULL_TIME', 'ACTIVE', '2024-01-04', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('e0000008-0008-0008-0008-000000000008', 'EMP202401-005', '自強', '黃', '黃自強', 'A123456743', '1987-11-11', 'MALE', 'huang.zq@company.com', '0989012345', '11111111-1111-1111-1111-111111111111', 'd0000004-0004-0004-0004-000000000004', 'FULL_TIME', 'ACTIVE', '2024-01-05', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE);

-- 3 個 PROBATION 員工 (試用期)
INSERT INTO employees (employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, company_email, mobile_phone, organization_id, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
('e0000009-0009-0009-0009-000000000009', 'EMP202502-001', '俊傑', '周', '周俊傑', 'A123456752', '1994-01-15', 'MALE', 'zhou.jj@company.com', '0990123456', '11111111-1111-1111-1111-111111111111', 'd0000002-0002-0002-0002-000000000002', 'FULL_TIME', 'PROBATION', '2025-02-01', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE),
('e0000010-0010-0010-0010-000000000010', 'EMP202502-002', '若蘭', '林', '林若蘭', 'A223456736', '1996-03-20', 'FEMALE', 'lin.rl@company.com', '0901234567', '11111111-1111-1111-1111-111111111111', 'd0000003-0003-0003-0003-000000000003', 'FULL_TIME', 'PROBATION', '2025-02-02', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE),
('e0000011-0011-0011-0011-000000000011', 'EMP202502-003', '志明', '吳', '吳志明', 'A123456761', '1989-06-25', 'MALE', 'wu.zm@company.com', '0912345679', '11111111-1111-1111-1111-111111111111', 'd0000004-0004-0004-0004-000000000004', 'CONTRACT', 'PROBATION', '2025-02-03', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE);

-- 2 個 TERMINATED 員工 (離職)
INSERT INTO employees (employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, company_email, mobile_phone, organization_id, department_id, employment_type, employment_status, hire_date, termination_date, created_at, updated_at, is_deleted)
VALUES 
('e0000012-0012-0012-0012-000000000012', 'EMP202001-099', '老王', '張', '張老王', 'A123456770', '1975-12-31', 'MALE', 'zhang.lw@company.com', '0923456780', '11111111-1111-1111-1111-111111111111', 'd0000002-0002-0002-0002-000000000002', 'FULL_TIME', 'TERMINATED', '2020-01-01', '2024-12-31', '2020-01-01 09:00:00', '2024-12-31 17:00:00', FALSE),
('e0000013-0013-0013-0013-000000000013', 'EMP202101-088', '小紅', '李', '李小紅', 'A223456745', '1982-04-18', 'FEMALE', 'li.xh@company.com', '0934567891', '11111111-1111-1111-1111-111111111111', 'd0000003-0003-0003-0003-000000000003', 'CONTRACT', 'TERMINATED', '2021-01-01', '2024-11-30', '2021-01-01 09:00:00', '2024-11-30 17:00:00', FALSE);

-- 1 個 UNPAID_LEAVE 員工 (留職停薪)
INSERT INTO employees (employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, company_email, mobile_phone, organization_id, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
('e0000014-0014-0014-0014-000000000014', 'EMP202201-077', '阿豪', '鄭', '鄭阿豪', 'A123456798', '1990-07-07', 'MALE', 'zheng.ah@company.com', '0945678902', '11111111-1111-1111-1111-111111111111', 'd0000004-0004-0004-0004-000000000004', 'CONTRACT', 'UNPAID_LEAVE', '2022-01-01', '2022-01-01 09:00:00', '2024-01-01 09:00:00', FALSE);
