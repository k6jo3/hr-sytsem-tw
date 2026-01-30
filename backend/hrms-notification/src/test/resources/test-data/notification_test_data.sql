-- Notification 測試資料
-- 用於 NotificationRepositoryIntegrationTest

DELETE FROM notifications;

-- 通知 (共 12 筆)
-- 狀態分布: PENDING=3, SENT=4, READ=3, FAILED=2
-- 收件人: E001 收到 5 筆, E002 收到 4 筆, E003 收到 3 筆
INSERT INTO notifications (id, recipient_id, title, content, notification_type, priority, status, template_code, channels, sent_at, read_at, retry_count, is_deleted, created_at, updated_at, version) VALUES
-- E001 的通知 (5筆: 2未讀, 3已讀)
('ntf-00000000-0001', 'E001', '請假申請已核准', '您的請假申請已核准', 'LEAVE', 'NORMAL', 'READ', 'LEAVE_APPROVED', '["IN_APP"]', '2025-01-15 09:00:00', '2025-01-15 10:00:00', 0, false, '2025-01-15 09:00:00', '2025-01-15 10:00:00', 0),
('ntf-00000000-0002', 'E001', '考核自評提醒', '請完成2025年度自評', 'PERFORMANCE', 'HIGH', 'READ', 'PERF_SELF_REMIND', '["IN_APP"]', '2025-01-16 09:00:00', '2025-01-16 11:00:00', 0, false, '2025-01-16 09:00:00', '2025-01-16 11:00:00', 0),
('ntf-00000000-0003', 'E001', '工時填報提醒', '請填寫本週工時', 'TIMESHEET', 'NORMAL', 'SENT', 'TIMESHEET_REMIND', '["IN_APP"]', '2025-01-20 09:00:00', NULL, 0, false, '2025-01-20 09:00:00', '2025-01-20 09:00:00', 0),
('ntf-00000000-0004', 'E001', '課程報名成功', '已報名 React進階開發', 'TRAINING', 'LOW', 'READ', 'TRAINING_ENROLL', '["IN_APP"]', '2025-01-21 09:00:00', '2025-01-21 14:00:00', 0, false, '2025-01-21 09:00:00', '2025-01-21 14:00:00', 0),
('ntf-00000000-0005', 'E001', '系統公告', '系統將於週末維護', 'ANNOUNCEMENT', 'NORMAL', 'PENDING', 'ANNOUNCEMENT', '["IN_APP"]', NULL, NULL, 0, false, '2025-01-25 09:00:00', '2025-01-25 09:00:00', 0),
-- E002 的通知 (4筆: 3未讀, 1已讀)
('ntf-00000000-0006', 'E002', '請假申請被駁回', '您的請假申請被駁回', 'LEAVE', 'NORMAL', 'READ', 'LEAVE_REJECTED', '["IN_APP"]', '2025-01-14 09:00:00', '2025-01-14 15:00:00', 0, false, '2025-01-14 09:00:00', '2025-01-14 15:00:00', 0),
('ntf-00000000-0007', 'E002', '考核主管評提醒', '請完成員工考核', 'PERFORMANCE', 'HIGH', 'SENT', 'PERF_MANAGER_REMIND', '["IN_APP"]', '2025-01-18 09:00:00', NULL, 0, false, '2025-01-18 09:00:00', '2025-01-18 09:00:00', 0),
('ntf-00000000-0008', 'E002', '專案指派通知', '已指派至數位轉型專案', 'PROJECT', 'NORMAL', 'PENDING', 'PROJECT_ASSIGN', '["IN_APP"]', NULL, NULL, 0, false, '2025-01-22 09:00:00', '2025-01-22 09:00:00', 0),
('ntf-00000000-0009', 'E002', '系統公告', '年度調薪通知', 'ANNOUNCEMENT', 'HIGH', 'FAILED', 'ANNOUNCEMENT', '["EMAIL"]', '2025-01-23 09:00:00', NULL, 2, false, '2025-01-23 09:00:00', '2025-01-23 09:10:00', 0),
-- E003 的通知 (3筆: 2未讀, 1已讀)
('ntf-00000000-0010', 'E003', '請假申請已核准', '您的特休申請已核准', 'LEAVE', 'NORMAL', 'READ', 'LEAVE_APPROVED', '["IN_APP"]', '2025-01-17 09:00:00', '2025-01-17 16:00:00', 0, false, '2025-01-17 09:00:00', '2025-01-17 16:00:00', 0),
('ntf-00000000-0011', 'E003', '工時填報提醒', '請填寫本週工時', 'TIMESHEET', 'NORMAL', 'SENT', 'TIMESHEET_REMIND', '["IN_APP"]', '2025-01-20 09:00:00', NULL, 0, false, '2025-01-20 09:00:00', '2025-01-20 09:00:00', 0),
('ntf-00000000-0012', 'E003', '證照到期提醒', 'PMP證照即將到期', 'CERTIFICATE', 'URGENT', 'FAILED', 'CERT_EXPIRE', '["EMAIL"]', '2025-01-24 09:00:00', NULL, 3, false, '2025-01-24 09:00:00', '2025-01-24 09:05:00', 0);

-- 測試場景說明:
-- 1. findById: 查詢特定通知
-- 2. findByRecipientId(E001): 預期 5 筆
-- 3. findUnreadByRecipientId(E001): 預期 2 筆 (PENDING + SENT)
-- 4. countUnreadByRecipientId(E001): 預期 2
-- 5. countUnreadByRecipientId(E002): 預期 3
-- 6. existsByTemplateCodeAndStatus: 測試特定範本狀態查詢
