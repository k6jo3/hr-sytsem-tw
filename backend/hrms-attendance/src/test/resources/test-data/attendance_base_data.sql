-- 班次基礎資料
-- 用於打卡管理 API 整合測試
INSERT INTO shifts (id, code, name, type, start_time, end_time, is_active, is_deleted, created_at, updated_at)
VALUES ('test-shift-001', 'SHIFT01', '標準班', 'REGULAR', '09:00:00', '18:00:00', 1, 0, current_timestamp, current_timestamp);
