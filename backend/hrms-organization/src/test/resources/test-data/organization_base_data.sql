-- Organization Base Data (English version to avoid encoding issues)
-- 先解除循環 FK (departments.manager_id -> employees) 再清除
UPDATE departments SET manager_id = NULL WHERE manager_id IS NOT NULL;
TRUNCATE TABLE employee_history CASCADE;
TRUNCATE TABLE employees CASCADE;
TRUNCATE TABLE departments CASCADE;
TRUNCATE TABLE organizations CASCADE;

-- Use standard UUID format with English names
INSERT INTO organizations (organization_id, organization_code, organization_name, organization_type, status, created_at, updated_at, is_deleted) VALUES
('11111111-1111-1111-1111-111111111111', 'COMPANY', 'Head Office', 'PARENT', 'ACTIVE', '2025-01-01 00:00:00', '2025-01-01 00:00:00', FALSE),
('22222222-2222-2222-2222-222222222222', 'SUB_A', 'Subsidiary A', 'SUBSIDIARY', 'ACTIVE', '2025-01-01 00:00:00', '2025-01-01 00:00:00', FALSE);
