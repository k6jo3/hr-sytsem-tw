-- 團體保險方案測試資料
-- 關聯到 insurance_base_data.sql 中的組織 ORG001

-- 團體壽險方案（啟用中）
INSERT INTO group_insurance_plans (plan_id, organization_id, plan_name, plan_code, insurance_type, insurer_name, policy_number, contract_start_date, contract_end_date, is_active, created_at, updated_at) VALUES
('GP-001', 'ORG001', '2025年度團體壽險方案', 'GLP-2025-001', 'GROUP_LIFE', '台灣人壽', 'POL-2025-001', '2025-01-01', '2025-12-31', true, '2025-01-01 09:00:00', '2025-01-01 09:00:00');

-- 團體傷害險方案（啟用中）
INSERT INTO group_insurance_plans (plan_id, organization_id, plan_name, plan_code, insurance_type, insurer_name, policy_number, contract_start_date, contract_end_date, is_active, created_at, updated_at) VALUES
('GP-002', 'ORG001', '2025年度團體傷害險方案', 'GAP-2025-001', 'GROUP_ACCIDENT', '國泰人壽', 'POL-2025-002', '2025-01-01', '2025-12-31', true, '2025-01-01 09:00:00', '2025-01-01 09:00:00');

-- 團體醫療險方案（已停用）
INSERT INTO group_insurance_plans (plan_id, organization_id, plan_name, plan_code, insurance_type, insurer_name, policy_number, contract_start_date, contract_end_date, is_active, created_at, updated_at) VALUES
('GP-003', 'ORG001', '2024年度團體醫療險方案', 'GMP-2024-001', 'GROUP_MEDICAL', '富邦人壽', 'POL-2024-003', '2024-01-01', '2024-12-31', false, '2024-01-01 09:00:00', '2024-12-31 09:00:00');

-- 團體壽險方案的職等對應
INSERT INTO group_insurance_plan_tiers (tier_id, plan_id, job_grade, coverage_amount, monthly_premium, employer_share_rate) VALUES
('GT-001', 'GP-001', 'M1', 5000000.00, 2500.00, 0.7000),
('GT-002', 'GP-001', 'M2', 3000000.00, 1800.00, 0.6000),
('GT-003', 'GP-001', 'E1', 2000000.00, 1200.00, 0.5000);

-- 團體傷害險方案的職等對應
INSERT INTO group_insurance_plan_tiers (tier_id, plan_id, job_grade, coverage_amount, monthly_premium, employer_share_rate) VALUES
('GT-004', 'GP-002', 'M1', 3000000.00, 1500.00, 0.8000),
('GT-005', 'GP-002', 'E1', 1500000.00, 800.00, 0.6000);
