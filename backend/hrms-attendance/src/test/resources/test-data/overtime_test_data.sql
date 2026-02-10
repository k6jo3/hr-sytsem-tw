-- Overtime Test Data
-- For OvertimeApiIntegrationTest

DELETE FROM overtime_applications;

-- 1. test-overtime-001: PENDING, WORKDAY, for detail and approval test
INSERT INTO overtime_applications (
    id, employee_id, overtime_date, hours, status, 
    reason, overtime_type, rejection_reason, is_deleted, created_at, updated_at
) VALUES (
    'test-overtime-001', 'test-emp-001', '2026-02-10', 2.0, 'PENDING', 
    'Project Rush', 'WORKDAY', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 2. test-overtime-002: APPROVED, REST_DAY, for list test
INSERT INTO overtime_applications (
    id, employee_id, overtime_date, hours, status, 
    reason, overtime_type, rejection_reason, is_deleted, created_at, updated_at
) VALUES (
    'test-overtime-002', 'test-emp-001', '2026-02-15', 4.0, 'APPROVED', 
    'Weekend Work', 'REST_DAY', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 3. test-overtime-003: REJECTED, HOLIDAY, for list test
INSERT INTO overtime_applications (
    id, employee_id, overtime_date, hours, status, 
    reason, overtime_type, rejection_reason, is_deleted, created_at, updated_at
) VALUES (
    'test-overtime-003', 'test-emp-001', '2026-02-20', 8.0, 'REJECTED', 
    'Holiday Work', 'HOLIDAY', 'Not approved', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
