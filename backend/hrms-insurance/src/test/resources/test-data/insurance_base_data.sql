-- 保險單位基礎資料
INSERT INTO insurance_units (unit_id, organization_id, unit_code, unit_name, labor_insurance_number, health_insurance_number, pension_number, is_active, created_at) VALUES
('00000000-0000-0000-0000-000000000001', 'ORG001', 'UNIT001', '總公司', 'L12345678', 'H12345678', 'P12345678', true, '2025-01-01 09:00:00'),
('00000000-0000-0000-0000-000000000002', 'ORG001', 'UNIT-TEST', 'Test Unit', 'L-001', 'H-001', 'P-001', true, CURRENT_TIMESTAMP);

-- 投保級距基礎資料
INSERT INTO insurance_levels (level_id, insurance_type, level_number, monthly_salary, labor_employee_rate, labor_employer_rate, health_employee_rate, health_employer_rate, pension_employer_rate, effective_date, is_active) VALUES
('00000000-0000-0000-0000-000000000101', 'LABOR', 1, 27470.00, 0.0220, 0.0770, NULL, NULL, 0.0600, '2025-01-01', true),
('00000000-0000-0000-0000-000000000102', 'HEALTH', 1, 27470.00, NULL, NULL, 0.0517, 0.0350, NULL, '2025-01-01', true),
('00000000-0000-0000-0000-000000000201', 'LABOR', 1, 45800.00, 0.02, 0.07, NULL, NULL, 0.06, '2025-01-01', true),
('00000000-0000-0000-0000-000000000202', 'HEALTH', 1, 45800.00, NULL, NULL, 0.03, 0.04, NULL, '2025-01-01', true);
