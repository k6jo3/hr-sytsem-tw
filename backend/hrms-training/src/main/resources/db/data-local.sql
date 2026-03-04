-- ============================================================================
-- HR10 Training Service - Local Seed Data (H2)
-- PK 為 VARCHAR (UUID 格式字串)
-- ============================================================================

-- 訓練課程 (4 筆: OPEN x2, DRAFT x1, COMPLETED x1)
INSERT INTO training_courses (course_id, course_code, course_name, course_type, delivery_mode, category, description, instructor, instructor_info, duration_hours, max_participants, min_participants, current_enrollments, start_date, end_date, start_time, end_time, location, cost, is_mandatory, target_audience, prerequisites, enrollment_deadline, status, created_by, created_at, updated_at, is_deleted) VALUES
('TRN-C001', 'TC-2026-001', 'Spring Boot 進階開發', 'INTERNAL', 'OFFLINE', 'TECHNICAL', '深入學習 Spring Boot 3.x 進階功能，包含 Security、Kafka、Redis 整合', '王技術長', '15年Java開發經驗，Spring 官方認證講師', 16.0, 30, 5, 12, '2026-04-01', '2026-04-02', '09:00:00', '17:00:00', '台北辦公室 3F 訓練教室', 0, FALSE, '後端開發人員', '具備 Spring Boot 基礎知識', '2026-03-25', 'OPEN', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-C002', 'TC-2026-002', '新人到職訓練', 'INTERNAL', 'HYBRID', 'ORIENTATION', '公司文化介紹、制度說明、系統操作教學', '人資部', '人力資源部門統一辦理', 8.0, 50, 1, 3, '2026-03-15', '2026-03-15', '09:00:00', '17:00:00', '台北辦公室 1F 會議室', 0, TRUE, '全體新進員工', NULL, '2026-03-14', 'OPEN', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-C003', 'TC-2026-003', 'AWS 雲端架構師認證', 'EXTERNAL', 'ONLINE', 'TECHNICAL', 'AWS Solutions Architect Associate 認證準備課程', '外部講師', 'AWS 認證講師', 40.0, 20, 3, 0, '2026-05-01', '2026-05-31', '19:00:00', '21:00:00', '線上', 15000, FALSE, 'DevOps 與後端工程師', '具備基本雲端知識', '2026-04-20', 'DRAFT', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-C004', 'TC-2025-010', '資訊安全意識訓練', 'INTERNAL', 'ONLINE', 'COMPLIANCE', '年度資安意識宣導，涵蓋社交工程、密碼管理、資料保護', '資安部', '公司資訊安全部門', 4.0, 200, 1, 150, '2025-12-01', '2025-12-15', '14:00:00', '16:00:00', '線上', 0, TRUE, '全體員工', NULL, '2025-11-30', 'COMPLETED', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 訓練報名 (5 筆: REGISTERED, APPROVED, COMPLETED, REJECTED, CANCELLED)
INSERT INTO training_enrollments (enrollment_id, course_id, employee_id, status, reason, remarks, approved_by, approved_at, rejected_by, rejected_at, reject_reason, cancelled_by, cancelled_at, cancel_reason, attendance, attended_hours, attended_at, completed_hours, score, passed, feedback, completed_at, created_at, updated_at, is_deleted) VALUES
('TRN-E001', 'TRN-C001', '00000000-0000-0000-0000-000000000001', 'REGISTERED', '提升Spring Boot進階技能', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-E002', 'TRN-C001', '00000000-0000-0000-0000-000000000002', 'APPROVED', '工作需要', NULL, 'admin', CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-E003', 'TRN-C004', '00000000-0000-0000-0000-000000000001', 'COMPLETED', '必修課程', NULL, 'admin', CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL, TRUE, 4.0, CURRENT_TIMESTAMP, 4.0, 92.50, TRUE, '課程內容充實，受益良多', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-E004', 'TRN-C003', '00000000-0000-0000-0000-000000000003', 'REJECTED', '想學習AWS', NULL, NULL, NULL, 'admin', CURRENT_TIMESTAMP, '目前工作職責不需要此認證，建議下期再申請', NULL, NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-E005', 'TRN-C002', '00000000-0000-0000-0000-000000000004', 'CANCELLED', '新人訓練', NULL, 'admin', CURRENT_TIMESTAMP, NULL, NULL, NULL, '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP, '因個人因素無法參加', FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 證照 (3 筆: VALID, EXPIRING, EXPIRED)
INSERT INTO certificates (certificate_id, employee_id, certificate_name, issuing_organization, certificate_number, issue_date, expiry_date, category, is_required, attachment_url, remarks, is_verified, verified_by, verified_at, status, created_at, updated_at, is_deleted) VALUES
('TRN-CT001', '00000000-0000-0000-0000-000000000001', 'Oracle Certified Professional Java SE 17', 'Oracle', 'OCP-2025-001234', '2025-06-15', '2028-06-15', 'TECHNICAL', FALSE, 'https://cert.example.com/ocp001', NULL, TRUE, 'admin', CURRENT_TIMESTAMP, 'VALID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-CT002', '00000000-0000-0000-0000-000000000002', 'PMP 專案管理師', 'PMI', 'PMP-2024-005678', '2024-03-01', '2026-03-31', 'MANAGEMENT', TRUE, 'https://cert.example.com/pmp001', '即將到期，需安排續證', TRUE, 'admin', CURRENT_TIMESTAMP, 'EXPIRING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
('TRN-CT003', '00000000-0000-0000-0000-000000000003', 'AWS Solutions Architect Associate', 'Amazon Web Services', 'AWS-SAA-2023-009876', '2023-01-10', '2026-01-10', 'TECHNICAL', FALSE, 'https://cert.example.com/aws001', '已過期', TRUE, 'admin', CURRENT_TIMESTAMP, 'EXPIRED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
