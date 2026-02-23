-- Training 測試資料
-- 用於 TrainingCourseRepositoryIntegrationTest

DELETE FROM training_enrollments;
DELETE FROM training_courses;
DELETE FROM certificates;

-- 課程資料 (共 8 筆)
-- 狀態分布: OPEN=4, CLOSED=2, DRAFT=1, CANCELLED=1
INSERT INTO training_courses (course_id, course_code, course_name, course_type, delivery_mode, category, duration_hours, max_participants, min_participants, current_enrollments, start_date, end_date, status, is_mandatory, is_deleted, created_at, updated_at) VALUES
-- OPEN
('C001', 'TRN-2025-001', 'React進階開發', 'INTERNAL', 'OFFLINE', 'TECHNICAL', 8.0, 30, 5, 12, '2025-02-15', '2025-02-15', 'OPEN', false, 0, '2025-01-10 09:00:00', '2025-01-10 09:00:00'),
('C002', 'TRN-2025-002', 'AWS架構師認證班', 'EXTERNAL', 'ONLINE', 'TECHNICAL', 40.0, 10, 3, 8, '2025-03-01', '2025-03-20', 'OPEN', false, 0, '2025-01-15 09:00:00', '2025-01-15 09:00:00'),
('C003', 'TRN-2025-003', '領導力工作坊', 'INTERNAL', 'OFFLINE', 'MANAGEMENT', 16.0, 20, 8, 15, '2025-02-20', '2025-02-21', 'OPEN', false, 0, '2025-01-18 09:00:00', '2025-01-18 09:00:00'),
('C004', 'TRN-2025-004', '必修資安課程', 'INTERNAL', 'ONLINE', 'COMPLIANCE', 2.0, 1000, 1, 450, '2025-01-01', '2025-12-31', 'OPEN', true, 0, '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
-- CLOSED
('C005', 'TRN-2024-005', 'Java基礎課程', 'INTERNAL', 'OFFLINE', 'TECHNICAL', 24.0, 25, 5, 20, '2024-11-01', '2024-11-03', 'CLOSED', false, 0, '2024-10-01 09:00:00', '2024-12-01 09:00:00'),
('C006', 'TRN-2024-006', '新人訓練', 'INTERNAL', 'OFFLINE', 'ORIENTATION', 8.0, 50, 1, 35, '2024-12-01', '2024-12-01', 'CLOSED', true, 0, '2024-11-15 09:00:00', '2024-12-15 09:00:00'),
-- DRAFT
('C007', 'TRN-2025-007', 'AI應用開發', 'EXTERNAL', 'ONLINE', 'TECHNICAL', 32.0, 15, 5, 0, '2025-04-01', '2025-04-15', 'DRAFT', false, 0, '2025-01-25 09:00:00', '2025-01-25 09:00:00'),
-- CANCELLED
('C008', 'TRN-2025-008', '專案管理進階', 'INTERNAL', 'OFFLINE', 'MANAGEMENT', 16.0, 20, 10, 5, '2025-02-10', '2025-02-11', 'CANCELLED', false, 0, '2025-01-08 09:00:00', '2025-01-20 09:00:00');

-- 報名資料 (共 10 筆)
INSERT INTO training_enrollments (enrollment_id, course_id, employee_id, status, attendance, completed_hours, is_deleted, created_at, updated_at) VALUES
('E001-C001', 'C001', 'E001', 'APPROVED', false, 0, 0, '2025-01-15 09:00:00', '2025-01-15 09:00:00'),
('E001-C004', 'C004', 'E001', 'COMPLETED', true, 2.0, 0, '2025-01-05 09:00:00', '2025-01-10 09:00:00'),
('E002-C001', 'C001', 'E002', 'REGISTERED', false, 0, 0, '2025-01-20 09:00:00', '2025-01-20 09:00:00'),
('E002-C003', 'C003', 'E002', 'APPROVED', false, 0, 0, '2025-01-22 09:00:00', '2025-01-23 09:00:00'),
('E003-C004', 'C004', 'E003', 'COMPLETED', true, 2.0, 0, '2025-01-02 09:00:00', '2025-01-08 09:00:00'),
('E003-C002', 'C002', 'E003', 'APPROVED', false, 0, 0, '2025-01-25 09:00:00', '2025-01-26 09:00:00'),
('E001-C005', 'C005', 'E001', 'COMPLETED', true, 24.0, 0, '2024-10-20 09:00:00', '2024-11-05 09:00:00'),
('E002-C005', 'C005', 'E002', 'COMPLETED', true, 24.0, 0, '2024-10-22 09:00:00', '2024-11-05 09:00:00'),
('E001-C008', 'C008', 'E001', 'CANCELLED', false, 0, 0, '2025-01-10 09:00:00', '2025-01-20 09:00:00'),
('E004-C004', 'C004', 'E004', 'REGISTERED', false, 0, 0, '2025-01-28 09:00:00', '2025-01-28 09:00:00');

-- 證照資料 (共 5 筆)
INSERT INTO certificates (certificate_id, employee_id, certificate_name, issuing_organization, issue_date, expiry_date, status, is_deleted, created_at, updated_at) VALUES
('CERT-001', 'E001', 'AWS Certified Solutions Architect', 'Amazon', '2023-01-15', '2026-01-15', 'VALID', 0, '2023-01-15 09:00:00', '2023-01-15 09:00:00'),
('CERT-002', 'E001', 'Java SE 21 Developer', 'Oracle', '2024-10-01', NULL, 'VALID', 0, '2024-10-01 09:00:00', '2024-10-01 09:00:00'),
('CERT-003', 'E002', 'PMP', 'PMI', '2022-05-20', '2025-05-20', 'VALID', 0, '2022-05-20 09:00:00', '2022-05-20 09:00:00'),
('CERT-004', 'E003', 'CISSP', 'ISC2', '2021-03-10', '2024-03-10', 'EXPIRED', 0, '2021-03-10 09:00:00', '2024-03-15 09:00:00'),
('CERT-005', 'E001', 'Kubernetes Administrator', 'CNCF', '2024-06-01', '2027-06-01', 'VALID', 0, '2024-06-01 09:00:00', '2024-06-01 09:00:00');

-- 測試場景說明:
-- 1. findById: 查詢特定課程
-- 2. existsByCourseCode: 課程代碼存在性檢查
-- 3. 課程狀態統計: OPEN=4, CLOSED=2, DRAFT=1, CANCELLED=1
