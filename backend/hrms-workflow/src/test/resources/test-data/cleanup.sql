-- 測試資料清理腳本（依外鍵順序：子表先刪）
DELETE FROM workflow_approval_tasks;
DELETE FROM workflow_instances;
DELETE FROM workflow_definitions;
DELETE FROM workflow_user_delegations;
