-- Notification 測試資料
-- 用於 NotificationRepositoryIntegrationTest

DELETE FROM notifications;

-- 通知 (共 12 筆)
-- 狀態分布: PENDING=2, SENT=4, READ=4, FAILED=2
-- 收件人: E001 收到 5 筆, E002 收到 4 筆, E003 收到 3 筆
--
-- NotificationType 有效值:
--   APPROVAL_REQUEST(審核請求), APPROVAL_RESULT(審核結果),
--   REMINDER(提醒), ANNOUNCEMENT(公告), ALERT(警示),
--   SYSTEM_NOTICE(系統通知), SYSTEM_ALERT(系統警示)
INSERT INTO notifications (id, recipient_id, title, content, notification_type, priority, status, template_code, channels, sent_at, read_at, retry_count, is_deleted, created_at, updated_at, version) VALUES
-- E001 的通知 (5筆: PENDING=1, SENT=1, READ=3)
-- 未讀 (PENDING+SENT, 不含 FAILED) = 2 筆
('ntf-00000000-0001', 'E001', '請假申請已核准', '您的請假申請已核准',   'APPROVAL_RESULT', 'NORMAL', 'READ',    'LEAVE_APPROVED',   '["IN_APP"]', '2025-01-15 09:00:00', '2025-01-15 10:00:00', 0, false, '2025-01-15 09:00:00', '2025-01-15 10:00:00', 0),
('ntf-00000000-0002', 'E001', '考核自評提醒',   '請完成2025年度自評',   'REMINDER',        'HIGH',   'READ',    'PERF_SELF_REMIND',  '["IN_APP"]', '2025-01-16 09:00:00', '2025-01-16 11:00:00', 0, false, '2025-01-16 09:00:00', '2025-01-16 11:00:00', 0),
('ntf-00000000-0003', 'E001', '工時填報提醒',   '請填寫本週工時',       'REMINDER',        'NORMAL', 'SENT',    'TIMESHEET_REMIND',  '["IN_APP"]', '2025-01-20 09:00:00', NULL,                  0, false, '2025-01-20 09:00:00', '2025-01-20 09:00:00', 0),
('ntf-00000000-0004', 'E001', '課程報名成功',   '已報名 React進階開發', 'REMINDER',        'LOW',    'READ',    'TRAINING_ENROLL',   '["IN_APP"]', '2025-01-21 09:00:00', '2025-01-21 14:00:00', 0, false, '2025-01-21 09:00:00', '2025-01-21 14:00:00', 0),
('ntf-00000000-0005', 'E001', '系統公告',       '系統將於週末維護',     'ANNOUNCEMENT',    'NORMAL', 'PENDING', 'ANNOUNCEMENT',      '["IN_APP"]', NULL,                  NULL,                  0, false, '2025-01-25 09:00:00', '2025-01-25 09:00:00', 0),
-- E002 的通知 (4筆: SENT=1, PENDING=1, READ=1, FAILED=1)
-- 未讀 (PENDING+SENT, 不含 FAILED) = 2 筆
('ntf-00000000-0006', 'E002', '請假申請被駁回', '您的請假申請被駁回',   'APPROVAL_RESULT', 'NORMAL', 'READ',    'LEAVE_REJECTED',    '["IN_APP"]', '2025-01-14 09:00:00', '2025-01-14 15:00:00', 0, false, '2025-01-14 09:00:00', '2025-01-14 15:00:00', 0),
('ntf-00000000-0007', 'E002', '考核主管評提醒', '請完成員工考核',       'REMINDER',        'HIGH',   'SENT',    'PERF_MANAGER_REMIND','["IN_APP"]', '2025-01-18 09:00:00', NULL,                  0, false, '2025-01-18 09:00:00', '2025-01-18 09:00:00', 0),
('ntf-00000000-0008', 'E002', '專案指派通知',   '已指派至數位轉型專案', 'REMINDER',        'NORMAL', 'PENDING', 'PROJECT_ASSIGN',    '["IN_APP"]', NULL,                  NULL,                  0, false, '2025-01-22 09:00:00', '2025-01-22 09:00:00', 0),
('ntf-00000000-0009', 'E002', '系統公告',       '年度調薪通知',         'ANNOUNCEMENT',    'HIGH',   'FAILED',  'ANNOUNCEMENT',      '["EMAIL"]',  '2025-01-23 09:00:00', NULL,                  2, false, '2025-01-23 09:00:00', '2025-01-23 09:10:00', 0),
-- E003 的通知 (3筆: SENT=1, READ=1, FAILED=1)
-- 未讀 (PENDING+SENT, 不含 FAILED) = 1 筆
('ntf-00000000-0010', 'E003', '請假申請已核准', '您的特休申請已核准',   'APPROVAL_RESULT', 'NORMAL', 'READ',    'LEAVE_APPROVED',    '["IN_APP"]', '2025-01-17 09:00:00', '2025-01-17 16:00:00', 0, false, '2025-01-17 09:00:00', '2025-01-17 16:00:00', 0),
('ntf-00000000-0011', 'E003', '工時填報提醒',   '請填寫本週工時',       'REMINDER',        'NORMAL', 'SENT',    'TIMESHEET_REMIND',  '["IN_APP"]', '2025-01-20 09:00:00', NULL,                  0, false, '2025-01-20 09:00:00', '2025-01-20 09:00:00', 0),
('ntf-00000000-0012', 'E003', '證照到期提醒',   'PMP證照即將到期',      'REMINDER',        'URGENT', 'FAILED',  'CERT_EXPIRE',       '["EMAIL"]',  '2025-01-24 09:00:00', NULL,                  3, false, '2025-01-24 09:00:00', '2025-01-24 09:05:00', 0);

-- 測試場景說明:
-- 1. findById(ntf-001): title=請假申請已核准, status=READ ✓
-- 2. findById(ntf-005): status=PENDING ✓
-- 3. findByRecipientId(E001): 預期 5 筆 ✓
-- 4. findByRecipientId(E002): 預期 4 筆 ✓
-- 5. findByRecipientId(E003): 預期 3 筆 ✓
-- 6. findUnreadByRecipientId(E001): 預期 2 筆 (ntf-003 SENT, ntf-005 PENDING) ✓
-- 7. findUnreadByRecipientId(E002): 預期 2 筆 (ntf-007 SENT, ntf-008 PENDING) ✓
-- 8. findUnreadByRecipientId(E003): 預期 1 筆 (ntf-011 SENT) ✓
-- 9. countUnreadByRecipientId(E001): 預期 2 ✓
-- 10. countUnreadByRecipientId(E002): 預期 2 (不含 FAILED ntf-009) ✓
-- 11. countUnreadByRecipientId(E003): 預期 1 (不含 FAILED ntf-012) ✓
-- 12. existsByTemplateCodeAndStatus('LEAVE_APPROVED', READ): TRUE (ntf-001, ntf-010) ✓
-- 13. existsByTemplateCodeAndStatus('ANNOUNCEMENT', PENDING): TRUE (ntf-005) ✓
-- 14. existsByTemplateCodeAndStatus('ANNOUNCEMENT', FAILED): TRUE (ntf-009) ✓
-- 15. existsByTemplateCodeAndStatus('NON_EXISTING_TEMPLATE', READ): FALSE ✓
