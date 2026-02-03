-- Organization 測試資料
-- 涵蓋 14 個員工，滿足 OrganizationContractTest 的各種查詢場景

-- 1. 清理資料 (已由 cleanup.sql 處理，此處為雙重保險)
DELETE FROM employees;
DELETE FROM departments;

-- 2. 插入部門資料
-- id, code, name, organization_id, parent_id, level, status, sort_order, is_deleted
INSERT INTO departments (id, code, name, organization_id, parent_id, level, status, sort_order, created_at, updated_at, is_deleted)
VALUES 
('D001', 'RD', '研發部', 'ORG001', NULL, 1, 'ACTIVE', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('D002', 'SALES', '業務部', 'ORG001', NULL, 1, 'ACTIVE', 2, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('D003', 'FIN', '財務部', 'ORG001', NULL, 1, 'ACTIVE', 3, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('D004', 'HR', '人事部', 'ORG001', NULL, 1, 'ACTIVE', 4, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('D005', 'RD-FE', '前端組', 'ORG001', 'D001', 2, 'ACTIVE', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('D006', 'RD-BE', '後端組', 'ORG001', 'D001', 2, 'INACTIVE', 2, '2025-01-01 09:00:00', '2025-01-20 09:00:00', FALSE);

-- 3. 插入員工資料
-- id, employee_number, first_name, last_name, gender, email, department_id, employment_type, employment_status, hire_date, is_deleted

-- 8 個 ACTIVE 員工 (其中 5 個在 D001, 1 個姓王, 1 個是 EMP202501-001)
INSERT INTO employees (id, employee_number, first_name, last_name, gender, email, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
-- D001 (5人)
('E001', 'EMP202501-001', '小明', '王', 'MALE', 'wang.xm@company.com', 'D001', 'REGULAR', 'ACTIVE', '2025-01-01', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('E002', 'EMP202501-002', '志強', '李', 'MALE', 'li.zq@company.com', 'D001', 'REGULAR', 'ACTIVE', '2025-01-02', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('E003', 'EMP202501-003', '美麗', '張', 'FEMALE', 'zhang.ml@company.com', 'D001', 'REGULAR', 'ACTIVE', '2025-01-03', '2025-01-01 09:00:00', '2025-01-01 09:00:00', FALSE),
('E004', 'EMP202401-001', '大為', '劉', 'MALE', 'liu.dw@company.com', 'D001', 'REGULAR', 'ACTIVE', '2024-01-01', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('E005', 'EMP202401-002', '淑芬', '陳', 'FEMALE', 'chen.sf@company.com', 'D001', 'REGULAR', 'ACTIVE', '2024-01-02', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
-- 其他部門
('E006', 'EMP202401-003', '建國', '趙', 'MALE', 'zhao.jg@company.com', 'D002', 'REGULAR', 'ACTIVE', '2024-01-03', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('E007', 'EMP202401-004', '愛華', '孫', 'FEMALE', 'sun.ah@company.com', 'D003', 'REGULAR', 'ACTIVE', '2024-01-04', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE),
('E008', 'EMP202401-005', '自強', '黃', 'MALE', 'huang.zq@company.com', 'D004', 'REGULAR', 'ACTIVE', '2024-01-05', '2024-01-01 09:00:00', '2024-01-01 09:00:00', FALSE);

-- 3 個 PROBATION 員工 (試用期)
INSERT INTO employees (id, employee_number, first_name, last_name, gender, email, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
('E009', 'EMP202502-001', '俊傑', '周', 'MALE', 'zhou.jj@company.com', 'D002', 'REGULAR', 'PROBATION', '2025-02-01', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE),
('E010', 'EMP202502-002', '若蘭', '林', 'FEMALE', 'lin.rl@company.com', 'D003', 'REGULAR', 'PROBATION', '2025-02-02', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE),
('E011', 'EMP202502-003', '志明', '吳', 'MALE', 'wu.zm@company.com', 'D004', 'CONTRACT', 'PROBATION', '2025-02-03', '2025-02-01 09:00:00', '2025-02-01 09:00:00', FALSE);

-- 2 個 TERMINATED 員工 (離職)
INSERT INTO employees (id, employee_number, first_name, last_name, gender, email, department_id, employment_type, employment_status, hire_date, termination_date, created_at, updated_at, is_deleted)
VALUES 
('E012', 'EMP202001-099', '老王', '張', 'MALE', 'zhang.lw@company.com', 'D002', 'REGULAR', 'TERMINATED', '2020-01-01', '2024-12-31', '2020-01-01 09:00:00', '2024-12-31 17:00:00', FALSE),
('E013', 'EMP202101-088', '小紅', '李', 'FEMALE', 'li.xh@company.com', 'D003', 'CONTRACT', 'TERMINATED', '2021-01-01', '2024-11-30', '2021-01-01 09:00:00', '2024-11-30 17:00:00', FALSE);

-- 1 個 UNPAID_LEAVE 員工 (留職停薪)
INSERT INTO employees (id, employee_number, first_name, last_name, gender, email, department_id, employment_type, employment_status, hire_date, created_at, updated_at, is_deleted)
VALUES 
('E014', 'EMP202201-077', '阿豪', '鄭', 'MALE', 'zheng.ah@company.com', 'D004', 'CONTRACT', 'UNPAID_LEAVE', '2022-01-01', '2022-01-01 09:00:00', '2024-01-01 09:00:00', FALSE);
