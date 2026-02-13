-- InsuranceEnrollment 整合測試專用資料
-- 員工:
-- test-emp-existing: 已有加保
-- Enrollment for test-emp-existing
-- 關聯到 insurance_base_data.sql 中的 UNIT-TEST (00000000-0000-0000-0000-000000000002) 和 LEVEL (00000000-0000-0000-0000-000000000201)

INSERT INTO insurance_enrollments (enrollment_id, employee_id, insurance_unit_id, insurance_type, enroll_date, withdraw_date, insurance_level_id, monthly_salary, status, is_reported, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000301', 'test-emp-existing', '00000000-0000-0000-0000-000000000002', 'LABOR', '2025-01-01', NULL, '00000000-0000-0000-0000-000000000201', 45800.00, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000302', 'test-emp-existing', '00000000-0000-0000-0000-000000000002', 'HEALTH', '2025-01-01', NULL, '00000000-0000-0000-0000-000000000202', 45800.00, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
