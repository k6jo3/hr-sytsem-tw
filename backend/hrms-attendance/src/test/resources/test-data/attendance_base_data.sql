-- HR03 考勤服務基礎資料
-- 用於合約測試 (AttendanceContractTest) 及其他整合測試
-- 先清空所有資料，再插入基礎資料

-- 清空順序考慮外鍵約束
DELETE FROM attendance_corrections;
DELETE FROM attendance_records;
DELETE FROM leave_applications;
DELETE FROM overtime_applications;
DELETE FROM leave_types;
DELETE FROM shifts;

-- ==================== 班別基礎資料 ====================
INSERT INTO shifts (id, organization_id, code, name, type, start_time, end_time, late_tolerance_minutes, early_leave_tolerance_minutes, is_active, is_deleted, created_at, updated_at)
VALUES
('SHIFT-STD-001', 'ORG001', 'STD-01', 'Standard Shift', 'REGULAR', '09:00:00', '18:00:00', 5, 0, 1, 0, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
('test-shift-001', 'ORG001', 'SHIFT01', 'Standard Test', 'REGULAR', '09:00:00', '18:00:00', 5, 0, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==================== 假別基礎資料 ====================
INSERT INTO leave_types (id, organization_id, name, code, unit, is_paid, is_active, is_statutory_leave, requires_proof, max_days_per_year, can_carryover, is_deleted, created_at, updated_at)
VALUES
('ANNUAL', 'ORG001', 'Annual Leave', 'ANNUAL', 'DAY', true, true, true, false, 30.00, false, 0, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
('SICK', 'ORG001', 'Sick Leave', 'SICK', 'DAY', false, true, true, true, 30.00, false, 0, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
('PERSONAL', 'ORG001', 'Personal Leave', 'PERSONAL', 'DAY', false, true, false, false, 14.00, false, 0, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
