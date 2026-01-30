-- 測試資料清理腳本
-- 每個測試執行後清理資料

DELETE FROM role_permissions;
DELETE FROM user_roles;
DELETE FROM refresh_tokens;
DELETE FROM login_logs;
DELETE FROM password_history;
DELETE FROM user_sso_links;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM permissions;
DELETE FROM tenants;
