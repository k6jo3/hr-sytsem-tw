-- Payroll 測試資料清理腳本
-- 在每個測試完成後執行，清理測試產生的資料

-- 清除測試資料 (軟刪除或物理刪除)
DELETE FROM hr04_payslips;
DELETE FROM hr04_salary_items;
DELETE FROM hr04_salary_structures;
DELETE FROM hr04_payroll_runs;

-- 重置自增序列 (如果資料庫支援)
-- ALTER SEQUENCE hr04_payroll_runs_seq RESTART WITH 1;
-- ALTER SEQUENCE hr04_payslips_seq RESTART WITH 1;
-- ALTER SEQUENCE hr04_salary_structures_seq RESTART WITH 1;
