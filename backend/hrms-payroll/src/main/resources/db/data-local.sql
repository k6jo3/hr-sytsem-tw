-- ============================================================================
-- HR04 Payroll Service - Local Seed Data (H2)
-- 員工 ID 對應 HR02 Organization 的 data-local.sql
-- ============================================================================

-- === 薪資結構 (4 名員工) ===
INSERT INTO hr04_salary_structures (structure_id, employee_id, monthly_salary, hourly_rate, payroll_system, payroll_cycle, effective_date, end_date, is_active, created_at, updated_at) VALUES
('SAL-001', '00000000-0000-0000-0000-000000000001', 55000.00, 264.42, 'MONTHLY', 'MONTHLY', '2024-01-01', NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
('SAL-002', '00000000-0000-0000-0000-000000000002', 48000.00, 230.77, 'MONTHLY', 'MONTHLY', '2024-03-01', NULL, TRUE, '2024-03-01 08:00:00', '2024-03-01 08:00:00'),
('SAL-003', '00000000-0000-0000-0000-000000000003', 52000.00, 250.00, 'MONTHLY', 'MONTHLY', '2024-06-01', NULL, TRUE, '2024-06-01 08:00:00', '2024-06-01 08:00:00'),
('SAL-004', '00000000-0000-0000-0000-000000000004', 60000.00, 288.46, 'MONTHLY', 'MONTHLY', '2024-01-15', NULL, TRUE, '2024-01-15 08:00:00', '2024-01-15 08:00:00');

-- === 薪資項目 (每人 3 項: 底薪、職務加給、伙食津貼) ===
INSERT INTO hr04_salary_items (structure_id, item_id, item_code, item_name, item_type, amount, is_fixed_amount, is_taxable, is_insurable) VALUES
-- 王大明
('SAL-001', 'ITEM-001', 'BASE_SALARY', '底薪', 'EARNING', 45000.00, TRUE, TRUE, TRUE),
('SAL-001', 'ITEM-002', 'POSITION_ALLOWANCE', '職務加給', 'EARNING', 8000.00, TRUE, TRUE, TRUE),
('SAL-001', 'ITEM-003', 'MEAL_ALLOWANCE', '伙食津貼', 'EARNING', 2000.00, TRUE, FALSE, FALSE),
-- 李小美
('SAL-002', 'ITEM-004', 'BASE_SALARY', '底薪', 'EARNING', 40000.00, TRUE, TRUE, TRUE),
('SAL-002', 'ITEM-005', 'POSITION_ALLOWANCE', '職務加給', 'EARNING', 6000.00, TRUE, TRUE, TRUE),
('SAL-002', 'ITEM-006', 'MEAL_ALLOWANCE', '伙食津貼', 'EARNING', 2000.00, TRUE, FALSE, FALSE),
-- 陳志強
('SAL-003', 'ITEM-007', 'BASE_SALARY', '底薪', 'EARNING', 42000.00, TRUE, TRUE, TRUE),
('SAL-003', 'ITEM-008', 'POSITION_ALLOWANCE', '職務加給', 'EARNING', 8000.00, TRUE, TRUE, TRUE),
('SAL-003', 'ITEM-009', 'MEAL_ALLOWANCE', '伙食津貼', 'EARNING', 2000.00, TRUE, FALSE, FALSE),
-- 林雅婷
('SAL-004', 'ITEM-010', 'BASE_SALARY', '底薪', 'EARNING', 50000.00, TRUE, TRUE, TRUE),
('SAL-004', 'ITEM-011', 'POSITION_ALLOWANCE', '職務加給', 'EARNING', 8000.00, TRUE, TRUE, TRUE),
('SAL-004', 'ITEM-012', 'MEAL_ALLOWANCE', '伙食津貼', 'EARNING', 2000.00, TRUE, FALSE, FALSE);

-- === 薪資批次 (2 筆: 1 月已完成 + 2 月草稿) ===
INSERT INTO hr04_payroll_runs (run_id, name, organization_id, payroll_system, period_start_date, period_end_date, pay_date, status, executor_id, approver_id, total_employees, processed_employees, failed_employees, total_gross_amount, total_net_amount, total_deductions, total_overtime_pay, executed_at, completed_at, submitted_by, submitted_at, approved_at, paid_at, bank_file_url, cancel_reason, created_by, created_at, updated_at) VALUES
('RUN-LOCAL-001', '2026年1月薪資批次', 'ORG-001', 'MONTHLY', '2026-01-01', '2026-01-31', '2026-02-05', 'PAID', 'admin-001', 'admin-001', 4, 4, 0, 215000.00, 172000.00, 43000.00, 8500.00, '2026-02-01 09:00:00', '2026-02-01 09:30:00', 'admin-001', '2026-02-02 10:00:00', '2026-02-03 14:00:00', '2026-02-05 09:00:00', NULL, NULL, 'admin-001', '2026-01-25 08:00:00', '2026-02-05 09:00:00'),
('RUN-LOCAL-002', '2026年2月薪資批次', 'ORG-001', 'MONTHLY', '2026-02-01', '2026-02-28', '2026-03-05', 'DRAFT', NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin-001', '2026-02-25 08:00:00', '2026-02-25 08:00:00');

-- === 薪資單 (4 筆: 1 月份 4 名員工) ===
INSERT INTO hr04_payslips (payslip_id, run_id, employee_id, employee_code, employee_name, period_start_date, period_end_date, pay_date, base_salary, gross_wage, net_wage, income_tax, leave_deduction, overtime_pay_total, ot_weekday_hours, ot_weekday_pay, ot_restday_hours, ot_restday_pay, ot_holiday_hours, ot_holiday_pay, ins_labor_fee, ins_health_fee, ins_pension_fee, ins_supplementary_fee, bank_code, bank_account_number, status, pdf_url, email_sent_at, created_at, updated_at) VALUES
('SLIP-LOCAL-001', 'RUN-LOCAL-001', '00000000-0000-0000-0000-000000000001', 'EMP-001', '王大明', '2026-01-01', '2026-01-31', '2026-02-05', 45000.00, 57500.00, 46000.00, 4000.00, 0.00, 2500.00, 2.00, 1500.00, 0.50, 750.00, 0.00, 0.00, 1200.00, 800.00, 2000.00, 500.00, '004', '12345678901234', 'SENT', '/payslips/SLIP-LOCAL-001.pdf', '2026-02-05 10:00:00', '2026-02-01 09:00:00', '2026-02-05 10:00:00'),
('SLIP-LOCAL-002', 'RUN-LOCAL-001', '00000000-0000-0000-0000-000000000002', 'EMP-002', '李小美', '2026-01-01', '2026-01-31', '2026-02-05', 40000.00, 50000.00, 40000.00, 3500.00, 0.00, 2000.00, 1.50, 1200.00, 0.50, 800.00, 0.00, 0.00, 1050.00, 700.00, 1750.00, 450.00, '004', '23456789012345', 'SENT', '/payslips/SLIP-LOCAL-002.pdf', '2026-02-05 10:00:00', '2026-02-01 09:00:00', '2026-02-05 10:00:00'),
('SLIP-LOCAL-003', 'RUN-LOCAL-001', '00000000-0000-0000-0000-000000000003', 'EMP-003', '陳志強', '2026-01-01', '2026-01-31', '2026-02-05', 42000.00, 54000.00, 43200.00, 3800.00, 0.00, 2000.00, 1.50, 1200.00, 0.50, 800.00, 0.00, 0.00, 1150.00, 750.00, 1900.00, 475.00, '004', '34567890123456', 'SENT', '/payslips/SLIP-LOCAL-003.pdf', '2026-02-05 10:00:00', '2026-02-01 09:00:00', '2026-02-05 10:00:00'),
('SLIP-LOCAL-004', 'RUN-LOCAL-001', '00000000-0000-0000-0000-000000000004', 'EMP-004', '林雅婷', '2026-01-01', '2026-01-31', '2026-02-05', 50000.00, 62500.00, 50000.00, 4500.00, 0.00, 2500.00, 2.00, 1500.00, 0.50, 750.00, 0.00, 0.00, 1350.00, 900.00, 2250.00, 575.00, '004', '45678901234567', 'SENT', '/payslips/SLIP-LOCAL-004.pdf', '2026-02-05 10:00:00', '2026-02-01 09:00:00', '2026-02-05 10:00:00');

-- === 薪資單項目 (每張薪資單 5 項: 底薪、職務加給、伙食津貼、勞保、健保) ===
INSERT INTO hr04_payslip_items (payslip_id, item_id, item_code, item_name, display_order, item_type, amount, source, is_taxable, is_insurable) VALUES
-- 王大明
('SLIP-LOCAL-001', 'PI-001', 'BASE_SALARY', '底薪', 1, 'EARNING', 45000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-001', 'PI-002', 'POSITION_ALLOWANCE', '職務加給', 2, 'EARNING', 8000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-001', 'PI-003', 'MEAL_ALLOWANCE', '伙食津貼', 3, 'EARNING', 2000.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-001', 'PI-004', 'LABOR_INSURANCE', '勞保費', 10, 'DEDUCTION', 1200.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-001', 'PI-005', 'HEALTH_INSURANCE', '健保費', 11, 'DEDUCTION', 800.00, 'SYSTEM', FALSE, FALSE),
-- 李小美
('SLIP-LOCAL-002', 'PI-006', 'BASE_SALARY', '底薪', 1, 'EARNING', 40000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-002', 'PI-007', 'POSITION_ALLOWANCE', '職務加給', 2, 'EARNING', 6000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-002', 'PI-008', 'MEAL_ALLOWANCE', '伙食津貼', 3, 'EARNING', 2000.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-002', 'PI-009', 'LABOR_INSURANCE', '勞保費', 10, 'DEDUCTION', 1050.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-002', 'PI-010', 'HEALTH_INSURANCE', '健保費', 11, 'DEDUCTION', 700.00, 'SYSTEM', FALSE, FALSE),
-- 陳志強
('SLIP-LOCAL-003', 'PI-011', 'BASE_SALARY', '底薪', 1, 'EARNING', 42000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-003', 'PI-012', 'POSITION_ALLOWANCE', '職務加給', 2, 'EARNING', 8000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-003', 'PI-013', 'MEAL_ALLOWANCE', '伙食津貼', 3, 'EARNING', 2000.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-003', 'PI-014', 'LABOR_INSURANCE', '勞保費', 10, 'DEDUCTION', 1150.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-003', 'PI-015', 'HEALTH_INSURANCE', '健保費', 11, 'DEDUCTION', 750.00, 'SYSTEM', FALSE, FALSE),
-- 林雅婷
('SLIP-LOCAL-004', 'PI-016', 'BASE_SALARY', '底薪', 1, 'EARNING', 50000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-004', 'PI-017', 'POSITION_ALLOWANCE', '職務加給', 2, 'EARNING', 8000.00, 'SYSTEM', TRUE, TRUE),
('SLIP-LOCAL-004', 'PI-018', 'MEAL_ALLOWANCE', '伙食津貼', 3, 'EARNING', 2000.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-004', 'PI-019', 'LABOR_INSURANCE', '勞保費', 10, 'DEDUCTION', 1350.00, 'SYSTEM', FALSE, FALSE),
('SLIP-LOCAL-004', 'PI-020', 'HEALTH_INSURANCE', '健保費', 11, 'DEDUCTION', 900.00, 'SYSTEM', FALSE, FALSE);
