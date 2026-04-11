-- Organization 測試資料清理
-- 測試結束後清除所有資料
-- 先解除循環 FK (departments.manager_id -> employees) 再清除

UPDATE departments SET manager_id = NULL WHERE manager_id IS NOT NULL;
TRUNCATE TABLE employee_history CASCADE;
TRUNCATE TABLE employees CASCADE;
TRUNCATE TABLE departments CASCADE;
TRUNCATE TABLE organizations CASCADE;
