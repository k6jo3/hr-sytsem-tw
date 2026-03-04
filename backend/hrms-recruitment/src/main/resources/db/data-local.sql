-- ============================================================================
-- HR09 Recruitment Service - Local Seed Data (H2)
-- UUID 規則: 00000000-0000-0000-9xxx-00000000000x
-- ============================================================================

-- 職缺 (3 筆: OPEN x2, DRAFT x1)
INSERT INTO job_openings (opening_id, job_title, department_id, number_of_positions, filled_positions, salary_min, salary_max, salary_currency, requirements, responsibilities, employment_type, work_location, status, open_date, close_date, created_at, updated_at) VALUES
('00000000-0000-0000-9001-000000000001', '資深Java工程師', '00000000-0000-0000-0000-000000000001', 2, 0, 70000, 100000, 'TWD', '5年以上Java開發經驗，熟悉Spring Boot', '負責後端系統開發與維護', 'FULL_TIME', '台北市信義區', 'OPEN', '2026-01-01', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9001-000000000002', '前端工程師', '00000000-0000-0000-0000-000000000001', 1, 0, 50000, 80000, 'TWD', '3年以上React開發經驗', '負責前端UI開發', 'FULL_TIME', '台北市信義區', 'OPEN', '2026-02-01', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9001-000000000003', 'DevOps工程師', '00000000-0000-0000-0000-000000000002', 1, 0, 60000, 90000, 'TWD', '熟悉CI/CD、Docker、K8s', '負責系統部署與維運', 'FULL_TIME', '台北市內湖區', 'DRAFT', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 應徵者 (6 筆: 各狀態覆蓋)
INSERT INTO candidates (candidate_id, opening_id, full_name, email, phone_number, resume_url, source, referrer_id, application_date, status, rejection_reason, cover_letter, expected_salary, available_date, created_at, updated_at) VALUES
('00000000-0000-0000-9002-000000000001', '00000000-0000-0000-9001-000000000001', '王大明', 'wang.dm@example.com', '0912345678', 'https://resume.example.com/wang', 'JOB_BANK', NULL, '2026-02-01', 'NEW', NULL, '希望加入貴公司', 80000, '2026-03-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9002-000000000002', '00000000-0000-0000-9001-000000000001', '李小華', 'li.xh@example.com', '0923456789', 'https://resume.example.com/li', 'REFERRAL', '00000000-0000-0000-0000-000000000001', '2026-02-05', 'SCREENING', NULL, NULL, 85000, '2026-03-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9002-000000000003', '00000000-0000-0000-9001-000000000001', '張美玲', 'zhang.ml@example.com', '0934567890', 'https://resume.example.com/zhang', 'LINKEDIN', NULL, '2026-01-20', 'INTERVIEWING', NULL, '期待面試機會', 90000, '2026-04-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9002-000000000004', '00000000-0000-0000-9001-000000000002', '陳志偉', 'chen.zw@example.com', '0945678901', 'https://resume.example.com/chen', 'WEBSITE', NULL, '2026-02-10', 'OFFERED', NULL, NULL, 65000, '2026-03-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9002-000000000005', '00000000-0000-0000-9001-000000000002', '林雅婷', 'lin.yt@example.com', '0956789012', 'https://resume.example.com/lin', 'HEADHUNTER', NULL, '2026-01-15', 'HIRED', NULL, '對前端開發有濃厚興趣', 70000, '2026-02-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9002-000000000006', '00000000-0000-0000-9001-000000000001', '黃建文', 'huang.jw@example.com', '0967890123', 'https://resume.example.com/huang', 'JOB_BANK', NULL, '2026-01-25', 'REJECTED', '經驗不足', NULL, 75000, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 面試 (3 筆: SCHEDULED, COMPLETED, CANCELLED)
INSERT INTO interviews (interview_id, candidate_id, candidate_name, interview_round, interview_type, interview_date, location, interviewer_ids_json, status, evaluations_json, created_at, updated_at) VALUES
('00000000-0000-0000-9003-000000000001', '00000000-0000-0000-9002-000000000003', '張美玲', 1, 'PHONE', '2026-03-10 10:00:00', '線上', '["00000000-0000-0000-0000-000000000001"]', 'SCHEDULED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9003-000000000002', '00000000-0000-0000-9002-000000000005', '林雅婷', 1, 'ONSITE', '2026-02-10 14:00:00', '台北市信義區辦公室', '["00000000-0000-0000-0000-000000000001","00000000-0000-0000-0000-000000000002"]', 'COMPLETED', '[{"interviewer_id":"00000000-0000-0000-0000-000000000001","technical_score":85,"communication_score":90,"culture_fit_score":88,"overall_rating":"HIRE","comments":"技術能力優秀"}]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9003-000000000003', '00000000-0000-0000-9002-000000000006', '黃建文', 1, 'VIDEO', '2026-02-05 11:00:00', '線上 Google Meet', '["00000000-0000-0000-0000-000000000001"]', 'CANCELLED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Offer (2 筆: PENDING, ACCEPTED)
INSERT INTO offers (offer_id, candidate_id, candidate_name, offered_position, offered_salary, offered_start_date, offer_date, expiry_date, status, response_date, rejection_reason, created_at, updated_at) VALUES
('00000000-0000-0000-9004-000000000001', '00000000-0000-0000-9002-000000000004', '陳志偉', '前端工程師', 65000, '2026-04-01', '2026-03-01', '2026-03-15', 'PENDING', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-9004-000000000002', '00000000-0000-0000-9002-000000000005', '林雅婷', '前端工程師', 70000, '2026-03-01', '2026-02-15', '2026-03-01', 'ACCEPTED', '2026-02-20', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
