-- Leave Test Data
-- For LeaveApiIntegrationTest

DELETE FROM leave_applications;

-- 1. test-leave-001: PENDING, ANNUAL, for detail and approval test
INSERT INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date, 
    status, reason, start_period, end_period, 
    proof_attachment_url, rejection_reason, created_at, updated_at
) VALUES (
    'test-leave-001', 'test-emp-001', 'ANNUAL', 
    DATEADD('DAY', 1, CURRENT_DATE), DATEADD('DAY', 3, CURRENT_DATE), 
    'PENDING', 'Initial Pending Leave', 'FULL_DAY', 'FULL_DAY', 
    NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 2. test-leave-002: APPROVED, SICK, for list test
INSERT INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date, 
    status, reason, start_period, end_period, 
    proof_attachment_url, rejection_reason, created_at, updated_at
) VALUES (
    'test-leave-002', 'test-emp-001', 'SICK', 
    DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', -3, CURRENT_DATE), 
    'APPROVED', 'Approved Sick Leave', 'FULL_DAY', 'FULL_DAY', 
    NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 3. test-leave-003: REJECTED, PERSONAL, for list test
INSERT INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date, 
    status, reason, start_period, end_period, 
    proof_attachment_url, rejection_reason, created_at, updated_at
) VALUES (
    'test-leave-003', 'test-emp-001', 'PERSONAL', 
    DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', -10, CURRENT_DATE), 
    'REJECTED', 'Rejected Personal Leave', 'FULL_DAY', 'FULL_DAY', 
    NULL, 'Busy time', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 4. test-leave-004: PENDING, ANNUAL, for filter test (Date Range: 2026-02-01 to 2026-02-28, used in test)
-- Note: The test filterByDateRange uses 2026-02-01 to 2026-02-28. 
-- We should ensure we have data in that range.
INSERT INTO leave_applications (
    id, employee_id, leave_type_id, start_date, end_date, 
    status, reason, start_period, end_period, 
    proof_attachment_url, rejection_reason, created_at, updated_at
) VALUES (
    'test-leave-004', 'test-emp-001', 'ANNUAL', 
    '2026-02-10', '2026-02-12', 
    'PENDING', 'Feb Leave', 'FULL_DAY', 'FULL_DAY', 
    NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
