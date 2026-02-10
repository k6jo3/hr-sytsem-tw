-- Organization 測試資料清理
-- 測試結束後清除所有資料

SET REFERENTIAL_INTEGRITY FALSE;
DELETE FROM employee_history;
DELETE FROM employees;
DELETE FROM departments;
DELETE FROM organizations;
SET REFERENTIAL_INTEGRITY TRUE;
