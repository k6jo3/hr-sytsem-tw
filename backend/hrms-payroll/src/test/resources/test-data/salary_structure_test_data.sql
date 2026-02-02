-- Salary Structure 測試資料
-- 用於 SalaryStructure Repository 整合測試

-- 清除舊資料
DELETE FROM hr04_salary_items;
DELETE FROM hr04_salary_structures;

-- 測試 SalaryStructure 資料 (共 15 筆)
-- 狀態分布: ACTIVE=10, ARCHIVED=5

-- ACTIVE 狀態 (10 筆)
INSERT INTO hr04_salary_structures (structure_id, employee_id, monthly_salary, hourly_rate, payroll_system, payroll_cycle, effective_date, end_date, is_active, created_at, updated_at) VALUES
('SALARY-001', 'E001', 50000.00, 240.38, 'MONTHLY', 'MONTHLY', '2025-01-01', NULL, true, '2025-01-01 08:00:00', '2025-01-01 08:00:00'),
('SALARY-002', 'E002', 60000.00, 288.46, 'MONTHLY', 'MONTHLY', '2025-01-01', NULL, true, '2025-01-01 08:00:00', '2025-01-01 08:00:00'),
('SALARY-003', 'E003', 45000.00, 216.35, 'MONTHLY', 'MONTHLY', '2024-12-01', NULL, true, '2024-12-01 08:00:00', '2025-01-15 10:00:00'),
('SALARY-004', 'E010', 55000.00, 264.42, 'MONTHLY', 'MONTHLY', '2025-01-01', NULL, true, '2025-01-01 08:00:00', '2025-01-01 08:00:00'),
('SALARY-005', 'E011', 65000.00, 312.50, 'MONTHLY', 'MONTHLY', '2024-11-15', NULL, true, '2024-11-15 08:00:00', '2025-01-20 14:00:00'),
('SALARY-006', 'E020', 52000.00, 250.00, 'MONTHLY', 'MONTHLY', '2025-01-05', NULL, true, '2025-01-05 08:00:00', '2025-01-05 08:00:00'),
('SALARY-007', 'E021', 48000.00, 230.77, 'MONTHLY', 'MONTHLY', '2024-12-15', NULL, true, '2024-12-15 08:00:00', '2025-01-18 09:00:00'),
('SALARY-008', 'E022', 58000.00, 279.81, 'MONTHLY', 'MONTHLY', '2025-01-01', NULL, true, '2025-01-01 08:00:00', '2025-01-01 08:00:00'),
('SALARY-009', 'E004', 47000.00, 226.44, 'MONTHLY', 'MONTHLY', '2024-11-01', NULL, true, '2024-11-01 08:00:00', '2025-01-10 11:00:00'),
('SALARY-010', 'E012', 54000.00, 259.62, 'MONTHLY', 'MONTHLY', '2025-01-01', NULL, true, '2025-01-01 08:00:00', '2025-01-01 08:00:00'),

-- ARCHIVED 狀態 (5 筆) - end_date 設定，is_active = false
('SALARY-011', 'E005', 51000.00, 245.19, 'MONTHLY', 'MONTHLY', '2024-09-01', '2024-12-31', false, '2024-09-01 08:00:00', '2025-01-05 08:00:00'),
('SALARY-012', 'E013', 63000.00, 303.85, 'MONTHLY', 'MONTHLY', '2024-08-15', '2024-11-30', false, '2024-08-15 08:00:00', '2025-01-02 08:00:00'),
('SALARY-013', 'E023', 49000.00, 235.58, 'MONTHLY', 'MONTHLY', '2024-10-01', '2024-12-31', false, '2024-10-01 08:00:00', '2024-12-31 17:00:00'),
('SALARY-014', 'E009', 46000.00, 221.15, 'MONTHLY', 'MONTHLY', '2024-07-01', '2024-10-31', false, '2024-07-01 08:00:00', '2025-01-08 08:00:00'),
('SALARY-015', 'E014', 56000.00, 269.23, 'MONTHLY', 'MONTHLY', '2024-09-15', '2024-12-15', false, '2024-09-15 08:00:00', '2024-12-15 17:00:00');

-- 測試場景說明:
-- SAL_R001: findById - 查詢單一薪資結構
-- SAL_R002: findByEmployeeId - 查詢員工的薪資結構
-- SAL_R003: findActiveStructure - 查詢員工的有效薪資結構
-- SAL_R004: findByEffectiveDate - 查詢特定日期生效的結構
-- SAL_R005: findByActiveStatus - 查詢所有有效的薪資結構
-- SAL_R006: findArchivedStructures - 查詢所有已存檔的薪資結構
-- QueryEngine EQ: is_active = true → 預期 10 筆
-- QueryEngine EQ: is_active = false → 預期 5 筆
-- QueryEngine GTE: effective_date >= '2025-01-01' → 預期 8 筆
-- QueryEngine LT: effective_date < '2024-12-01' → 預期 3 筆
