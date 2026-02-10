-- 打卡測試資料
-- 用於打卡管理 API 整合測試

-- test-emp-checkin: 今日無打卡記錄 (用於 ATT_CHECKIN_API_001)

-- test-emp-checkout: 今日已有上班打卡 (用於 ATT_CHECKIN_API_002)
INSERT INTO attendance_records (id, employee_id, record_date, check_in_time, check_out_time, status, is_late, late_minutes, is_early_leave, early_leave_minutes, anomaly_type, is_corrected, created_at, updated_at)
VALUES ('test-record-checkout', 'test-emp-checkout', CURRENT_DATE, CURRENT_TIMESTAMP, NULL, 'NORMAL', false, 0, false, 0, 'NORMAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- test-emp-duplicate: 今日已完成上班打卡 (用於 ATT_CHECKIN_API_003)
INSERT INTO attendance_records (id, employee_id, record_date, check_in_time, check_out_time, status, is_late, late_minutes, is_early_leave, early_leave_minutes, anomaly_type, is_corrected, created_at, updated_at)
VALUES ('test-record-duplicate', 'test-emp-duplicate', CURRENT_DATE, CURRENT_TIMESTAMP, NULL, 'NORMAL', false, 0, false, 0, 'NORMAL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 補卡測試資料
INSERT INTO attendance_records (id, employee_id, record_date, check_in_time, check_out_time, status, is_late, late_minutes, is_early_leave, early_leave_minutes, anomaly_type, is_corrected, created_at, updated_at)
VALUES ('test-record-001', 'test-emp-001', '2026-02-04', '2026-02-04 09:00:00', NULL, 'MISSING_CHECK_OUT', false, 0, false, 0, 'MISSING_CHECK_OUT', false, current_timestamp, current_timestamp);

INSERT INTO attendance_corrections (id, employee_id, attendance_record_id, correction_type, reason, status, created_at, updated_at)
VALUES ('test-correction-001', 'test-emp-001', 'test-record-001', 'FORGET_CHECK_OUT', '忘記下班打卡', 'PENDING', current_timestamp, current_timestamp);
