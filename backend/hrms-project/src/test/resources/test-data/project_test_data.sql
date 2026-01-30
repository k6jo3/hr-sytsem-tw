-- Project 測試資料
-- 用於 ProjectRepositoryIntegrationTest

-- 清除舊資料
DELETE FROM project_members;
DELETE FROM projects;

-- 專案測試資料 (共 10 筆)
-- 狀態分布: PLANNING=2, IN_PROGRESS=4, ON_HOLD=1, COMPLETED=2, CANCELLED=1
-- 類型分布: TIME_MATERIAL=4, FIXED_PRICE=4, INTERNAL=2
INSERT INTO projects (project_id, project_code, project_name, project_type, start_date, end_date, description, status, customer_id, planned_start_date, planned_end_date, budget_type, budget_amount, budget_hours, actual_hours, actual_cost, created_at, updated_at, version) VALUES
-- 進行中專案
('P001', 'PRJ-2025-001', '數位轉型專案', 'TIME_MATERIAL', '2025-01-01', '2025-06-30', '企業數位轉型', 'IN_PROGRESS', 'C001', '2025-01-01', '2025-06-30', 'TIME_MATERIAL', 5000000.00, 2000, 500, 1250000.00, '2025-01-01 09:00:00', '2025-01-15 09:00:00', 1),
('P002', 'PRJ-2025-002', 'ERP系統導入', 'FIXED_PRICE', '2025-02-01', '2025-08-31', 'ERP系統建置', 'IN_PROGRESS', 'C002', '2025-02-01', '2025-08-31', 'FIXED_PRICE', 8000000.00, 3000, 800, 2400000.00, '2025-01-15 09:00:00', '2025-01-20 09:00:00', 1),
('P003', 'PRJ-2025-003', '行動App開發', 'TIME_MATERIAL', '2025-01-15', '2025-05-15', '行動應用開發', 'IN_PROGRESS', 'C001', '2025-01-15', '2025-05-15', 'TIME_MATERIAL', 3000000.00, 1500, 400, 1000000.00, '2025-01-10 09:00:00', '2025-01-18 09:00:00', 1),
('P004', 'PRJ-2025-004', '雲端遷移專案', 'FIXED_PRICE', '2025-03-01', '2025-09-30', '系統上雲', 'IN_PROGRESS', 'C003', '2025-03-01', '2025-09-30', 'FIXED_PRICE', 6000000.00, 2500, 200, 600000.00, '2025-02-01 09:00:00', '2025-02-15 09:00:00', 1),

-- 規劃中專案
('P005', 'PRJ-2025-005', 'AI整合專案', 'TIME_MATERIAL', '2025-04-01', '2025-10-31', 'AI功能整合', 'PLANNING', 'C002', '2025-04-01', '2025-10-31', 'TIME_MATERIAL', 4000000.00, 2000, 0, 0.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00', 1),
('P006', 'PRJ-2025-006', '資安強化專案', 'FIXED_PRICE', '2025-05-01', '2025-08-31', '資訊安全強化', 'PLANNING', 'C001', '2025-05-01', '2025-08-31', 'FIXED_PRICE', 2000000.00, 1000, 0, 0.00, '2025-02-10 09:00:00', '2025-02-10 09:00:00', 1),

-- 暫停專案
('P007', 'PRJ-2024-010', '舊系統維護', 'TIME_MATERIAL', '2024-01-01', '2024-12-31', '舊系統維護作業', 'ON_HOLD', 'C003', '2024-01-01', '2024-12-31', 'TIME_MATERIAL', 1000000.00, 500, 450, 900000.00, '2024-01-01 09:00:00', '2024-10-01 09:00:00', 1),

-- 已完成專案
('P008', 'PRJ-2024-005', '網站改版專案', 'FIXED_PRICE', '2024-03-01', '2024-09-30', '企業網站改版', 'COMPLETED', 'C001', '2024-03-01', '2024-09-30', 'FIXED_PRICE', 1500000.00, 800, 850, 1600000.00, '2024-03-01 09:00:00', '2024-10-01 09:00:00', 1),
('P009', 'PRJ-2024-008', '內部培訓系統', 'INTERNAL', '2024-06-01', '2024-11-30', '內部訓練平台', 'COMPLETED', NULL, '2024-06-01', '2024-11-30', 'TIME_MATERIAL', 500000.00, 300, 280, 480000.00, '2024-06-01 09:00:00', '2024-12-01 09:00:00', 1),

-- 已取消專案
('P010', 'PRJ-2024-012', '取消的專案', 'INTERNAL', '2024-10-01', '2025-03-31', '因預算問題取消', 'CANCELLED', NULL, '2024-10-01', '2025-03-31', 'TIME_MATERIAL', 2000000.00, 1000, 50, 100000.00, '2024-10-01 09:00:00', '2024-11-01 09:00:00', 1);

-- 專案成員測試資料
INSERT INTO project_members (member_id, project_id, employee_id, role, start_date, end_date, allocation_percentage, hourly_rate) VALUES
-- P001 成員
('PM001', 'P001', 'E001', 'PM', '2025-01-01', NULL, 100, 1500.00),
('PM002', 'P001', 'E002', 'DEVELOPER', '2025-01-01', NULL, 80, 1200.00),
('PM003', 'P001', 'E003', 'DEVELOPER', '2025-01-15', NULL, 100, 1200.00),
-- P002 成員
('PM004', 'P002', 'E001', 'CONSULTANT', '2025-02-01', NULL, 50, 1500.00),
('PM005', 'P002', 'E004', 'PM', '2025-02-01', NULL, 100, 1400.00),
-- P003 成員
('PM006', 'P003', 'E002', 'PM', '2025-01-15', NULL, 100, 1200.00),
('PM007', 'P003', 'E005', 'DEVELOPER', '2025-01-15', NULL, 100, 1000.00);

-- 測試場景說明:
-- 1. findById: 查詢特定專案
-- 2. findAll: 預期 10 筆
-- 3. findProjects(status=IN_PROGRESS): 預期 4 筆
-- 4. findProjects(status=PLANNING): 預期 2 筆
-- 5. findProjects(status=COMPLETED): 預期 2 筆
-- 6. findByMemberEmployeeId(E001): 預期 2 筆 (P001, P002)
-- 7. findByMemberEmployeeId(E002): 預期 2 筆 (P001, P003)
-- 8. existsById: 測試存在性檢查
