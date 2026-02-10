-- 退保測試資料
INSERT INTO insurance_enrollments (enrollment_id, employee_id, insurance_unit_id, insurance_type, enroll_date, withdraw_date, insurance_level_id, monthly_salary, status, is_reported, created_at, updated_at) VALUES
-- 待退保記錄 (ACTIVE)
('11111111-1111-1111-1111-000000000001', 'test-emp-001', '00000000-0000-0000-0000-000000000001', 'LABOR', '2025-01-01', NULL, '00000000-0000-0000-0000-000000000101', 35000.00, 'ACTIVE', true, '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
-- 已退保記錄 (WITHDRAWN)
('11111111-1111-1111-1111-000000000002', 'test-emp-001', '00000000-0000-0000-0000-000000000001', 'LABOR', '2024-01-01', '2026-01-15', '00000000-0000-0000-0000-000000000101', 32000.00, 'WITHDRAWN', true, '2024-01-01 09:00:00', '2026-01-15 09:00:00');
