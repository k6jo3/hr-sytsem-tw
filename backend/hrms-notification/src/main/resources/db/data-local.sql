-- ============================================================================
-- HR12 Notification Service - Local Seed Data (H2)
-- ============================================================================

-- 通知範本 (2 筆: 系統通知 + 審核通知)
INSERT INTO notification_templates (id, template_code, name, description, subject, body, notification_type, default_priority, default_channels, variables, status, created_at, updated_at, created_by, updated_by, version, is_deleted) VALUES
('TPL-001', 'SYSTEM_NOTICE', '系統通知', '一般系統通知範本', '系統通知：{{title}}', '親愛的 {{employeeName}}，{{content}}', 'SYSTEM_NOTICE', 'NORMAL', '["IN_APP","EMAIL"]', '{"title":"string","employeeName":"string","content":"string"}', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', 0, FALSE),
('TPL-002', 'APPROVAL_NOTICE', '審核通知', '流程審核相關通知範本', '審核通知：{{flowName}}', '您有一筆待審核事項「{{summary}}」，申請人：{{applicantName}}，請前往處理。', 'APPROVAL_REQUEST', 'HIGH', '["IN_APP","EMAIL","PUSH"]', '{"flowName":"string","summary":"string","applicantName":"string"}', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', 0, FALSE);

-- 通知 (4 筆: SENT, READ, FAILED, PENDING)
INSERT INTO notifications (id, recipient_id, title, content, notification_type, priority, status, channels, related_business_id, related_business_type, related_business_url, template_code, template_variables, sent_at, read_at, failure_reason, retry_count, created_at, updated_at, created_by, updated_by, version, is_deleted) VALUES
('NTF-001', '00000000-0000-0000-0000-000000000001', '請假審核通知', '您有一筆待審核的請假申請，申請人：張小明，請假日期：3/10-3/12', 'APPROVAL_REQUEST', 'HIGH', 'SENT', '["IN_APP"]', 'WF-INS-001', 'WORKFLOW', '/workflow/instances/WF-INS-001', 'APPROVAL_NOTICE', '{"flowName":"請假簽核","summary":"特休假3天","applicantName":"張小明"}', CURRENT_TIMESTAMP, NULL, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0, FALSE),
('NTF-002', '00000000-0000-0000-0000-000000000001', '系統維護通知', '系統將於 2026/3/15 02:00-06:00 進行維護，届時服務將暫停。', 'SYSTEM_NOTICE', 'NORMAL', 'READ', '["IN_APP","EMAIL"]', NULL, NULL, NULL, 'SYSTEM_NOTICE', '{"title":"系統維護","employeeName":"張小明","content":"系統維護通知"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0, FALSE),
('NTF-003', '00000000-0000-0000-0000-000000000002', '薪資單通知', '您的 2026 年 2 月薪資單已產生，請至薪資系統查看。', 'REMINDER', 'NORMAL', 'FAILED', '["EMAIL"]', NULL, 'PAYROLL', '/payroll/payslips', NULL, NULL, NULL, NULL, 'SMTP connection timeout', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0, FALSE),
('NTF-004', '00000000-0000-0000-0000-000000000001', '訓練報名確認', '您已成功報名「Spring Boot 進階開發」課程，開課日期：2026/4/1', 'REMINDER', 'LOW', 'SENT', '["IN_APP"]', 'TRN-E001', 'TRAINING', '/training/courses/TRN-C001', NULL, NULL, CURRENT_TIMESTAMP, NULL, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 0, FALSE);

-- 公告 (2 筆: PUBLISHED + DRAFT)
INSERT INTO announcements (id, title, content, status, priority, target_audience, target_department_ids, target_role_ids, target_employee_ids, published_by, published_at, effective_from, effective_to, is_pinned, attachments, read_count, created_at, updated_at, created_by, updated_by, version, is_deleted) VALUES
('ANN-001', '2026 年度員工旅遊公告', '今年度員工旅遊將於 5 月 16-17 日舉辦，地點：花蓮太魯閣。請於 4/15 前完成報名。', 'PUBLISHED', 'HIGH', 'ALL', NULL, NULL, NULL, 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2026-04-15 23:59:59', TRUE, NULL, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', 0, FALSE),
('ANN-002', '新進人員注意事項 (草稿)', '歡迎加入公司！以下為新進人員須知事項...', 'DRAFT', 'NORMAL', 'DEPARTMENT', '["D003"]', NULL, NULL, NULL, NULL, NULL, NULL, FALSE, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', 0, FALSE);

-- 通知偏好設定 (1 筆)
INSERT INTO notification_preferences (id, employee_id, in_app_enabled, email_enabled, push_enabled, teams_enabled, line_enabled, quiet_hours_enabled, quiet_hours_start, quiet_hours_end, email_address, push_tokens, line_user_id, teams_webhook_url, created_at, updated_at, created_by, updated_by, version, is_deleted) VALUES
('PREF-001', '00000000-0000-0000-0000-000000000001', TRUE, TRUE, FALSE, FALSE, FALSE, TRUE, '22:00:00', '08:00:00', 'zhangxm@company.com', NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 0, FALSE);
