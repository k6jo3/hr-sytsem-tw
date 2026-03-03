-- =====================================================
-- HRMS Attendance Service - Local Profile 初始資料
-- H2 相容語法（使用 MERGE INTO 取代 ON CONFLICT）
-- 員工 ID 與 Organization Service data-local.sql 對齊
-- =====================================================

-- =====================================================
-- 1. 班別資料
-- =====================================================

-- 日班（標準班）
MERGE INTO shifts (
    id, organization_id, code, name, type,
    start_time, end_time, break_start_time, break_end_time,
    late_tolerance_minutes, early_leave_tolerance_minutes, is_active
) KEY (id) VALUES (
    '50000000-0000-0000-0000-000000000001',
    '10000000-0000-0000-0000-000000000001',
    'DAY', '日班', 'REGULAR',
    '09:00:00', '18:00:00', '12:00:00', '13:00:00',
    5, 5, 1
);

-- 彈性班
MERGE INTO shifts (
    id, organization_id, code, name, type,
    start_time, end_time, break_start_time, break_end_time,
    late_tolerance_minutes, early_leave_tolerance_minutes, is_active
) KEY (id) VALUES (
    '50000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000001',
    'FLEX', '彈性班', 'FLEXIBLE',
    '08:00:00', '17:00:00', '12:00:00', '13:00:00',
    30, 30, 1
);

-- 晚班
MERGE INTO shifts (
    id, organization_id, code, name, type,
    start_time, end_time, break_start_time, break_end_time,
    late_tolerance_minutes, early_leave_tolerance_minutes, is_active
) KEY (id) VALUES (
    '50000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000001',
    'NIGHT', '晚班', 'REGULAR',
    '14:00:00', '23:00:00', '18:00:00', '19:00:00',
    5, 5, 1
);

-- =====================================================
-- 2. 假別資料（依台灣勞基法）
-- =====================================================

-- 特休假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave, statutory_type,
    requires_proof, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000001',
    '10000000-0000-0000-0000-000000000001',
    '特休假', 'ANNUAL', 'DAY',
    TRUE, 1.00, TRUE, TRUE, 'ANNUAL',
    FALSE, 30.00, TRUE, 0
);

-- 病假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave, statutory_type,
    requires_proof, proof_description, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000001',
    '病假', 'SICK', 'DAY',
    TRUE, 0.50, TRUE, TRUE, 'SICK',
    TRUE, '需提供醫療院所開立之證明', 30.00, FALSE, 0
);

-- 事假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave,
    requires_proof, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000001',
    '事假', 'PERSONAL', 'DAY',
    FALSE, 0.00, TRUE, TRUE,
    FALSE, 14.00, FALSE, 0
);

-- 婚假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave, statutory_type,
    requires_proof, proof_description, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000001',
    '婚假', 'MARRIAGE', 'DAY',
    TRUE, 1.00, TRUE, TRUE, 'MARRIAGE',
    TRUE, '需提供結婚證書', 8.00, FALSE, 0
);

-- 喪假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave, statutory_type,
    requires_proof, proof_description, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000001',
    '喪假', 'BEREAVEMENT', 'DAY',
    TRUE, 1.00, TRUE, TRUE, 'FUNERAL',
    TRUE, '需提供相關證明', 8.00, FALSE, 0
);

-- 公假
MERGE INTO leave_types (
    id, organization_id, name, code, unit,
    is_paid, pay_rate, is_active, is_statutory_leave, statutory_type,
    requires_proof, max_days_per_year, can_carryover, is_deleted
) KEY (id) VALUES (
    '60000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000001',
    '公假', 'OFFICIAL', 'DAY',
    TRUE, 1.00, TRUE, TRUE, 'OTHER',
    TRUE, NULL, FALSE, 0
);

-- =====================================================
-- 3. 假期餘額（2026 年度）
-- =====================================================

-- 王大明：特休假 14 天（年資 6 年）、病假 30 天、事假 14 天
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', 2026, 14.00, 2.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000002', 2026, 30.00, 1.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000003', 2026, 14.00, 0.00);

-- 李小美：特休假 10 天（年資 5.5 年）、病假 30 天、事假 14 天
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', '60000000-0000-0000-0000-000000000001', 2026, 10.00, 1.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002', '60000000-0000-0000-0000-000000000002', 2026, 30.00, 0.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000002', '60000000-0000-0000-0000-000000000003', 2026, 14.00, 2.00);

-- 陳志強：特休假 7 天（年資 4.5 年）、病假 30 天、事假 14 天
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000003', '60000000-0000-0000-0000-000000000001', 2026, 7.00, 0.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000003', '60000000-0000-0000-0000-000000000002', 2026, 30.00, 0.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000003', '60000000-0000-0000-0000-000000000003', 2026, 14.00, 1.00);

-- 林雅婷：特休假 3 天（年資 < 1 年）、病假 30 天、事假 14 天
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000004', '60000000-0000-0000-0000-000000000001', 2026, 3.00, 0.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000004', '60000000-0000-0000-0000-000000000002', 2026, 30.00, 0.00);
MERGE INTO leave_balances (id, employee_id, leave_type_id, "year", total_days, used_days) KEY (id) VALUES
    ('70000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000004', '60000000-0000-0000-0000-000000000003', 2026, 14.00, 0.00);

-- =====================================================
-- 4. 考勤紀錄（近期資料）
-- =====================================================

-- 2026-03-03 考勤（今天）
MERGE INTO attendance_records (
    id, employee_id, record_date, shift_id,
    check_in_time, check_out_time, status,
    is_late, late_minutes, is_early_leave, early_leave_minutes, is_corrected
) KEY (id) VALUES (
    '80000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '2026-03-03',
    '50000000-0000-0000-0000-000000000001',
    TIMESTAMP '2026-03-03 08:55:00', NULL, 'NORMAL',
    FALSE, 0, FALSE, 0, FALSE
);

MERGE INTO attendance_records (
    id, employee_id, record_date, shift_id,
    check_in_time, check_out_time, status,
    is_late, late_minutes, is_early_leave, early_leave_minutes, is_corrected
) KEY (id) VALUES (
    '80000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000002',
    '2026-03-03',
    '50000000-0000-0000-0000-000000000001',
    TIMESTAMP '2026-03-03 09:12:00', NULL, 'LATE',
    TRUE, 12, FALSE, 0, FALSE
);

MERGE INTO attendance_records (
    id, employee_id, record_date, shift_id,
    check_in_time, check_out_time, status,
    is_late, late_minutes, is_early_leave, early_leave_minutes, is_corrected
) KEY (id) VALUES (
    '80000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000003',
    '2026-03-03',
    '50000000-0000-0000-0000-000000000002',
    TIMESTAMP '2026-03-03 08:30:00', TIMESTAMP '2026-03-03 17:35:00', 'NORMAL',
    FALSE, 0, FALSE, 0, FALSE
);

-- 2026-03-02 歷史考勤
MERGE INTO attendance_records (
    id, employee_id, record_date, shift_id,
    check_in_time, check_out_time, status,
    is_late, late_minutes, is_early_leave, early_leave_minutes, is_corrected
) KEY (id) VALUES (
    '80000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    '2026-03-02',
    '50000000-0000-0000-0000-000000000001',
    TIMESTAMP '2026-03-02 08:50:00', TIMESTAMP '2026-03-02 18:10:00', 'NORMAL',
    FALSE, 0, FALSE, 0, FALSE
);

MERGE INTO attendance_records (
    id, employee_id, record_date, shift_id,
    check_in_time, check_out_time, status,
    is_late, late_minutes, is_early_leave, early_leave_minutes, is_corrected
) KEY (id) VALUES (
    '80000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000002',
    '2026-03-02',
    '50000000-0000-0000-0000-000000000001',
    TIMESTAMP '2026-03-02 09:00:00', TIMESTAMP '2026-03-02 18:00:00', 'NORMAL',
    FALSE, 0, FALSE, 0, FALSE
);

-- =====================================================
-- 5. 請假申請
-- =====================================================

-- 王大明：已核准特休 2 天
MERGE INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date,
    status, reason, department_id, hours, approver_id, is_deleted
) KEY (id) VALUES (
    '90000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    '2026-02-14', '2026-02-15',
    'APPROVED', '春節前返鄉',
    '20000000-0000-0000-0000-000000000001',
    16.0, '00000000-0000-0000-0000-000000000002', 0
);

-- 李小美：待審核病假 1 天
MERGE INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date,
    status, reason, department_id, hours, is_deleted
) KEY (id) VALUES (
    '90000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000002',
    '2026-03-05', '2026-03-05',
    'PENDING', '身體不適需就醫',
    '20000000-0000-0000-0000-000000000002',
    8.0, 0
);

-- 陳志強：已駁回事假
MERGE INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date,
    status, reason, rejection_reason, department_id, hours, approver_id, is_deleted
) KEY (id) VALUES (
    '90000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000003',
    '60000000-0000-0000-0000-000000000003',
    '2026-03-10', '2026-03-10',
    'REJECTED', '個人事務處理',
    '當天有重要會議，建議改期',
    '20000000-0000-0000-0000-000000000003',
    8.0, '00000000-0000-0000-0000-000000000001', 0
);

-- =====================================================
-- 6. 加班申請
-- =====================================================

-- 陳志強：已核准加班
MERGE INTO overtime_applications (
    id, employee_id, overtime_date, start_time, end_time,
    hours, status, reason, overtime_type,
    department_id, approver_id, is_deleted
) KEY (id) VALUES (
    'A0000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000003',
    '2026-02-28',
    TIME '18:00:00', TIME '21:00:00',
    3.0, 'APPROVED', '專案交付前緊急修復', 'WORKDAY',
    '20000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001', 0
);

-- 林雅婷：待審核加班
MERGE INTO overtime_applications (
    id, employee_id, overtime_date, start_time, end_time,
    hours, status, reason, overtime_type,
    department_id, is_deleted
) KEY (id) VALUES (
    'A0000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000004',
    '2026-03-04',
    TIME '18:00:00', TIME '20:00:00',
    2.0, 'PENDING', '功能開發進度追趕', 'WORKDAY',
    '20000000-0000-0000-0000-000000000003', 0
);

-- =====================================================
-- 7. 年假政策
-- =====================================================
MERGE INTO annual_leave_policies (id, name, active, effective_date) KEY (id) VALUES
    ('B0000000-0000-0000-0000-000000000001', '台灣勞基法標準', TRUE, TIMESTAMP '2020-01-01 00:00:00');

MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000001', 'B0000000-0000-0000-0000-000000000001', 0, 1, 3);
MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000002', 'B0000000-0000-0000-0000-000000000001', 1, 2, 7);
MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000003', 'B0000000-0000-0000-0000-000000000001', 2, 3, 10);
MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000004', 'B0000000-0000-0000-0000-000000000001', 3, 5, 14);
MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000005', 'B0000000-0000-0000-0000-000000000001', 5, 10, 15);
MERGE INTO annual_leave_rules (id, policy_id, min_service_years, max_service_years, days) KEY (id) VALUES
    ('C0000000-0000-0000-0000-000000000006', 'B0000000-0000-0000-0000-000000000001', 10, 99, 30);
