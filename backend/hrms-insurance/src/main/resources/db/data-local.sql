-- ============================================================================
-- HR05 Insurance Service - Local Seed Data
-- UUID 規則: 00000000-0000-0000-5000-00000000000x (5000 = HR05)
-- MockJwt userId: 00000000-0000-0000-0000-000000000001
-- ============================================================================

-- ===== 投保單位 =====
INSERT INTO insurance_units (unit_id, organization_id, unit_code, unit_name, labor_insurance_number, health_insurance_number, pension_number, is_active, created_at) VALUES
('00000000-0000-0000-5000-000000000001', '00000000-0000-0000-0000-000000000099', 'UNIT-HQ', '台北總公司', 'L12345678', 'H12345678', 'P12345678', TRUE, TIMESTAMP '2025-01-01 00:00:00'),
('00000000-0000-0000-5000-000000000002', '00000000-0000-0000-0000-000000000099', 'UNIT-BR1', '新竹分公司', 'L87654321', 'H87654321', 'P87654321', TRUE, TIMESTAMP '2025-01-01 00:00:00');

-- ===== 投保級距（2025 年費率）=====
-- 勞保：普通事故費率 12%（員工 20% = 2.4%，雇主 70% = 8.4%）
-- 健保：費率 5.17%（員工 30% = 1.551%，雇主 60% = 3.102%）
-- 勞退：雇主提繳 6%

-- 勞保級距
INSERT INTO insurance_levels (level_id, insurance_type, level_number, monthly_salary, labor_employee_rate, labor_employer_rate, health_employee_rate, health_employer_rate, pension_employer_rate, effective_date, end_date, is_active) VALUES
('00000000-0000-0000-5000-000000000011', 'LABOR', 1, 27470.00, 0.0240, 0.0840, NULL, NULL, NULL, '2025-01-01', NULL, TRUE),
('00000000-0000-0000-5000-000000000012', 'LABOR', 2, 28800.00, 0.0240, 0.0840, NULL, NULL, NULL, '2025-01-01', NULL, TRUE),
('00000000-0000-0000-5000-000000000013', 'LABOR', 3, 30300.00, 0.0240, 0.0840, NULL, NULL, NULL, '2025-01-01', NULL, TRUE);

-- 健保級距
INSERT INTO insurance_levels (level_id, insurance_type, level_number, monthly_salary, labor_employee_rate, labor_employer_rate, health_employee_rate, health_employer_rate, pension_employer_rate, effective_date, end_date, is_active) VALUES
('00000000-0000-0000-5000-000000000021', 'HEALTH', 1, 27470.00, NULL, NULL, 0.01551, 0.03102, NULL, '2025-01-01', NULL, TRUE),
('00000000-0000-0000-5000-000000000022', 'HEALTH', 2, 28800.00, NULL, NULL, 0.01551, 0.03102, NULL, '2025-01-01', NULL, TRUE),
('00000000-0000-0000-5000-000000000023', 'HEALTH', 3, 30300.00, NULL, NULL, 0.01551, 0.03102, NULL, '2025-01-01', NULL, TRUE);

-- ===== 加退保記錄 =====
-- 員工1 (MockJwt user): 勞保 ACTIVE + 健保 ACTIVE
INSERT INTO insurance_enrollments (enrollment_id, employee_id, insurance_unit_id, insurance_type, enroll_date, withdraw_date, insurance_level_id, monthly_salary, status, is_reported, reported_at, created_at, updated_at) VALUES
('00000000-0000-0000-5000-000000000101', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-5000-000000000001', 'LABOR',  '2025-01-15', NULL, '00000000-0000-0000-5000-000000000012', 28800.00, 'ACTIVE', TRUE,  TIMESTAMP '2025-01-16 10:00:00', TIMESTAMP '2025-01-15 09:00:00', TIMESTAMP '2025-01-16 10:00:00'),
('00000000-0000-0000-5000-000000000102', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-5000-000000000001', 'HEALTH', '2025-01-15', NULL, '00000000-0000-0000-5000-000000000022', 28800.00, 'ACTIVE', TRUE,  TIMESTAMP '2025-01-16 10:00:00', TIMESTAMP '2025-01-15 09:00:00', TIMESTAMP '2025-01-16 10:00:00');

-- 員工2: 勞保 ACTIVE
INSERT INTO insurance_enrollments (enrollment_id, employee_id, insurance_unit_id, insurance_type, enroll_date, withdraw_date, insurance_level_id, monthly_salary, status, is_reported, reported_at, created_at, updated_at) VALUES
('00000000-0000-0000-5000-000000000103', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-5000-000000000001', 'LABOR',  '2025-02-01', NULL, '00000000-0000-0000-5000-000000000013', 30300.00, 'ACTIVE', TRUE,  TIMESTAMP '2025-02-02 09:00:00', TIMESTAMP '2025-02-01 09:00:00', TIMESTAMP '2025-02-02 09:00:00');

-- 員工3: 勞保已退保
INSERT INTO insurance_enrollments (enrollment_id, employee_id, insurance_unit_id, insurance_type, enroll_date, withdraw_date, insurance_level_id, monthly_salary, status, is_reported, reported_at, created_at, updated_at) VALUES
('00000000-0000-0000-5000-000000000104', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-5000-000000000002', 'LABOR',  '2024-06-01', '2025-01-31', '00000000-0000-0000-5000-000000000011', 27470.00, 'WITHDRAWN', TRUE, TIMESTAMP '2025-02-01 09:00:00', TIMESTAMP '2024-06-01 09:00:00', TIMESTAMP '2025-02-01 09:00:00');
