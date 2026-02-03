-- Organization 基礎資料
DELETE FROM employee_history;
DELETE FROM employees;
DELETE FROM departments;
DELETE FROM organizations;

INSERT INTO organizations (id, code, name, type, status, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'COMPANY', '母公司', 'PARENT', 'ACTIVE', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
('22222222-2222-2222-2222-222222222222', 'SUB_A', '子公司A', 'SUBSIDIARY', 'ACTIVE', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
