-- 課程資料
INSERT INTO training_courses (course_id, course_code, course_name, course_type, delivery_mode, category, duration_hours, max_participants, start_date, end_date, status, is_deleted)
VALUES 
('C001', 'TRN-001', 'React進階開發', 'INTERNAL', 'OFFLINE', 'TECHNICAL', 8.0, 30, '2025-12-15', '2025-12-15', 'OPEN', 0),
('C002', 'TRN-002', 'AWS架構師認證班', 'EXTERNAL', 'ONLINE', 'TECHNICAL', 40.0, 10, '2026-01-10', '2026-01-30', 'CLOSED', 0),
('C003', 'TRN-003', '領導力工作坊', 'INTERNAL', 'OFFLINE', 'MANAGEMENT', 16.0, 20, '2025-12-20', '2025-12-21', 'OPEN', 0),
('C004', 'TRN-004', '必修資安課程', 'INTERNAL', 'ONLINE', 'COMPLIANCE', 2.0, 1000, '2025-01-01', '2025-12-31', 'OPEN', 0);

-- 報名資料
INSERT INTO training_enrollments (enrollment_id, course_id, employee_id, status, attendance, completed_hours, is_deleted)
VALUES 
('E001-C001', 'C001', 'E001', 'APPROVED', true, 8.0, 0),
('E001-C004', 'C004', 'E001', 'COMPLETED', true, 2.0, 0),
('E002-C001', 'C001', 'E002', 'PENDING', false, 0, 0),
('E002-C003', 'C003', 'E002', 'REGISTERED', false, 0, 0);

-- 證照資料
INSERT INTO certificates (certificate_id, employee_id, certificate_name, issuing_organization, issue_date, expiry_date, status, is_deleted)
VALUES 
('CERT-001', 'E001', 'AWS Certified Solutions Architect', 'Amazon', '2023-01-15', '2026-01-15', 'VALID', 0),
('CERT-002', 'E001', 'Java SE 21 Developer', 'Oracle', '2024-10-01', NULL, 'VALID', 0),
('CERT-003', 'E002', 'PMP', 'PMI', '2022-05-20', '2025-05-20', 'VALID', 0);
