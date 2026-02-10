-- LeaveApplication 測試資料
-- 用於 LeaveApplicationRepositoryIntegrationTest

-- 清除舊資料
DELETE FROM leave_applications;

-- 請假申請測試資料 (共 12 筆)
-- 狀態分布: PENDING=4, APPROVED=5, REJECTED=2, CANCELLED=1
-- 員工分布: E001=5, E002=4, E003=3
INSERT INTO leave_applications (id, employee_id, leave_type_id, start_date, end_date, status, reason, start_period, end_period, proof_attachment_url, rejection_reason, created_at, updated_at) VALUES
-- 待審核 (PENDING)
('LA001', 'E001', 'LT001', '2025-02-01', '2025-02-03', 'PENDING', '出國旅遊', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2025-01-15 10:00:00', '2025-01-15 10:00:00'),
('LA002', 'E002', 'LT002', '2025-01-20', '2025-01-20', 'PENDING', '感冒需就醫', 'FULL_DAY', 'FULL_DAY', '/attachments/sick_note_001.pdf', NULL, '2025-01-19 09:00:00', '2025-01-19 09:00:00'),
('LA003', 'E003', 'LT003', '2025-01-25', '2025-01-25', 'PENDING', '私事處理', 'MORNING', 'MORNING', NULL, NULL, '2025-01-20 14:00:00', '2025-01-20 14:00:00'),
('LA004', 'E001', 'LT001', '2025-03-10', '2025-03-12', 'PENDING', '春節返鄉', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2025-01-25 10:00:00', '2025-01-25 10:00:00'),

-- 已核准 (APPROVED)
('LA005', 'E001', 'LT001', '2025-01-10', '2025-01-12', 'APPROVED', '回鄉探親', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2025-01-05 10:00:00', '2025-01-06 09:00:00'),
('LA006', 'E002', 'LT002', '2025-01-05', '2025-01-06', 'APPROVED', '發燒就醫', 'FULL_DAY', 'FULL_DAY', '/attachments/sick_note_002.pdf', NULL, '2025-01-04 18:00:00', '2025-01-04 19:00:00'),
('LA007', 'E003', 'LT001', '2025-01-08', '2025-01-08', 'APPROVED', '看醫生', 'AFTERNOON', 'AFTERNOON', NULL, NULL, '2025-01-07 10:00:00', '2025-01-07 11:00:00'),
('LA008', 'E001', 'LT003', '2025-01-02', '2025-01-02', 'APPROVED', '搬家', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2024-12-28 10:00:00', '2024-12-29 09:00:00'),
('LA009', 'E002', 'LT001', '2025-01-15', '2025-01-15', 'APPROVED', '家庭日', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2025-01-10 10:00:00', '2025-01-11 09:00:00'),

-- 已駁回 (REJECTED)
('LA010', 'E001', 'LT003', '2025-01-15', '2025-01-17', 'REJECTED', '私事', 'FULL_DAY', 'FULL_DAY', NULL, '該期間為專案關鍵期，無法核准', '2025-01-10 10:00:00', '2025-01-11 09:00:00'),
('LA011', 'E002', 'LT001', '2025-01-22', '2025-01-24', 'REJECTED', '旅遊', 'FULL_DAY', 'FULL_DAY', NULL, '部門人力不足', '2025-01-18 10:00:00', '2025-01-19 09:00:00'),

-- 已取消 (CANCELLED)
('LA012', 'E003', 'LT001', '2025-01-28', '2025-01-30', 'CANCELLED', '原計畫取消', 'FULL_DAY', 'FULL_DAY', NULL, NULL, '2025-01-20 10:00:00', '2025-01-22 09:00:00');

-- 測試場景說明:
-- 1. findById(LA001): 預期返回請假申請
-- 2. findByEmployeeId(E001): 預期 5 筆
-- 3. findByEmployeeId(E002): 預期 4 筆
-- 4. findByEmployeeId(E003): 預期 3 筆
-- 5. findByStatus(PENDING): 預期 4 筆
-- 6. findByStatus(APPROVED): 預期 5 筆
-- 7. findByStatus(REJECTED): 預期 2 筆
-- 8. findByStatus(CANCELLED): 預期 1 筆
-- 9. findByEmployeeIdAndDateRange(E001, 2025-01-01, 2025-01-31): 預期 3 筆 (LA005, LA008, LA010)
-- 10. findByEmployeeIdAndDateRange(E002, 2025-01-01, 2025-01-31): 預期 4 筆 (LA002, LA006, LA009, LA011)
