-- =====================================================
-- IAM 使用者測試資料
-- =====================================================

-- 密碼說明 (BCrypt 雜湊, 12 輪):
-- Admin@123 -> $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4x5H5.8qGvhXHHe.
-- User@123  -> $2a$12$R9h/lIPzHZPZ.H7s7P.y/.y.y.y.y.y.y.y.y.y.y.y.y.y.y. (暫定，若失敗需修正)

-- 插入測試使用者
INSERT INTO users (user_id, username, email, password_hash, display_name, tenant_id, status, must_change_password) VALUES
-- 管理員 (使用預設租戶)
('user-001', 'admin', 'admin@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4x5H5.8qGvhXHHe.', '管理員', '00000000-0000-0000-0000-000000000001', 'ACTIVE', FALSE),
-- 停用使用者
('user-002', 'deactivated_user', 'deactivated@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4x5H5.8qGvhXHHe.', '停用帳號', '00000000-0000-0000-0000-000000000001', 'INACTIVE', FALSE),
-- 一般測試使用者 (對應 setupSecurity 中的 test-user-001)
('test-user-001', 'test_user', 'test@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4x5H5.8qGvhXHHe.', '一般使用者', 'T001', 'ACTIVE', FALSE);

-- 指派角色
INSERT INTO user_roles (user_role_id, user_id, role_id) VALUES
('ur-test-001', 'user-001', 'role-0001'),
('ur-test-002', 'test-user-001', 'role-0001');

-- 插入測試角色
INSERT INTO roles (role_id, role_name, role_code, description, tenant_id, is_system_role, status) VALUES
('ROLE-001', 'Test Role 1', 'TEST_ROLE_1', 'Test Role Description 1', 'T001', FALSE, 'ACTIVE'),
('ROLE-002', 'Test Role 2', 'TEST_ROLE_2', 'Test Role Description 2', 'T001', FALSE, 'ACTIVE');

-- 插入測試用的 Refresh Token
INSERT INTO refresh_tokens (token_id, user_id, token, expires_at) VALUES
('token-001', 'test-user-001', 'valid-refresh-token', '2099-01-01 00:00:00');
