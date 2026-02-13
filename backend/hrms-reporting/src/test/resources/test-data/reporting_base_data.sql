-- HR14 報表分析服務合約測試基礎資料
-- 用於 ReportingContractTest 整合測試

-- 清空順序考慮外鍵約束
DELETE FROM rm_employee_roster;
DELETE FROM rpt_dashboard;

-- ==================== 儀表板測試資料 ====================
-- 用於 Dashboard CRUD 合約測試 (RPT_CMD_001~003, RPT_QRY_010~011)
INSERT INTO rpt_dashboard (dashboard_id, dashboard_name, description, owner_id, tenant_id, is_public, is_default, widgets_config, created_at, updated_at, created_by, updated_by) VALUES
-- 使用者 1 的儀表板
('10000000-0000-0000-0000-000000000001', 'HR 總覽儀表板', '人力資源關鍵指標', '11111111-1111-1111-1111-111111111111', 'T001', true, true, '[{"widgetId":"w1","widgetType":"KPI_CARD","title":"在職人數","dataSource":"HR_HEADCOUNT_SUMMARY","position":{"x":0,"y":0,"w":3,"h":2}},{"widgetId":"w2","widgetType":"BAR_CHART","title":"部門人數分布","dataSource":"HR_HEADCOUNT_BY_DEPT","position":{"x":3,"y":0,"w":9,"h":4}},{"widgetId":"w3","widgetType":"KPI_CARD","title":"本月離職率","dataSource":"HR_TURNOVER_SUMMARY","position":{"x":0,"y":2,"w":3,"h":2}}]', '2025-01-01 09:00:00', '2025-01-15 10:00:00', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
('10000000-0000-0000-0000-000000000002', '考勤儀表板', '出勤與請假統計', '11111111-1111-1111-1111-111111111111', 'T001', false, false, '[{"widgetId":"w4","widgetType":"TABLE","title":"異常出勤明細","dataSource":"ATTENDANCE_STATISTICS","position":{"x":0,"y":0,"w":12,"h":6}}]', '2025-01-02 09:00:00', '2025-01-16 10:00:00', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'),
-- 使用者 2 的儀表板
('10000000-0000-0000-0000-000000000003', '薪資分析儀表板', '薪資統計與成本分析', '22222222-2222-2222-2222-222222222222', 'T001', false, false, '[]', '2025-01-03 09:00:00', '2025-01-17 10:00:00', '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222'),
-- 用於刪除測試的儀表板
('10000000-0000-0000-0000-000000000099', '待刪除儀表板', '用於刪除測試', '11111111-1111-1111-1111-111111111111', 'T001', false, false, '[]', '2025-01-10 09:00:00', '2025-01-10 10:00:00', '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111');

-- ==================== 員工花名冊測試資料 ====================
-- 用於 RPT_QRY_001 員工花名冊查詢合約測試
INSERT INTO rm_employee_roster (employee_id, tenant_id, employee_name, department_id, department_name, position_id, position_name, hire_date, resignation_date, service_years, status, phone, email, is_deleted) VALUES
-- D001 人資部 - 2 筆在職
('EMP-001', 'T001', '王小明', 'D001', '人資部', 'POS-001', 'HR 專員', '2023-03-01', NULL, 2.9, 'ACTIVE', '0912345678', 'wang@example.com', false),
('EMP-002', 'T001', '李小華', 'D001', '人資部', 'POS-002', 'HR 主管', '2022-06-15', NULL, 3.7, 'ACTIVE', '0923456789', 'li@example.com', false),
-- D002 研發部 - 1 筆在職、1 筆離職
('EMP-003', 'T001', '張大偉', 'D002', '研發部', 'POS-003', '軟體工程師', '2024-01-10', NULL, 2.1, 'ACTIVE', '0934567890', 'zhang@example.com', false),
('EMP-004', 'T001', '陳美玲', 'D002', '研發部', 'POS-004', '前端工程師', '2023-08-20', '2026-02-15', 1.5, 'RESIGNED', '0945678901', 'chen@example.com', false);

-- ==================== 差勤統計測試資料 ====================
-- 用於 RPT_QRY_003 差勤統計報表查詢合約測試
DELETE FROM rm_attendance_statistics;
INSERT INTO rm_attendance_statistics (id, employee_id, tenant_id, employee_name, department_id, department_name, stat_date, expected_days, actual_days, late_count, early_leave_count, absent_count, leave_days, overtime_hours, attendance_rate, updated_at) VALUES
('EMP-001-2026-01', 'EMP-001', 'T001', '王小明', 'D001', '人資部', '2026-01-01', 22, 22, 0, 0, 0, 0.0, 2.0, 100.0, CURRENT_TIMESTAMP),
('EMP-002-2026-01', 'EMP-002', 'T001', '李小華', 'D001', '人資部', '2026-01-01', 22, 21, 1, 0, 0, 1.0, 0.0, 95.45, CURRENT_TIMESTAMP),
('EMP-003-2026-01', 'EMP-003', 'T001', '張大偉', 'D002', '研發部', '2026-01-01', 22, 20, 0, 0, 1, 1.0, 5.0, 90.90, CURRENT_TIMESTAMP);

-- ==================== 專案成本分析測試資料 ====================
-- 用於 RPT_QRY_005~006 專案報表查詢合約測試
DELETE FROM rm_project_cost_analysis;
INSERT INTO rm_project_cost_analysis (project_id, tenant_id, project_name, customer_id, customer_name, project_manager, start_date, end_date, status, budget_amount, labor_cost, other_cost, total_cost, cost_variance, cost_variance_rate, total_hours, utilization_rate, updated_at) VALUES
('PRJ-001', 'T001', 'HR核心系統開發', 'CUS-001', '宏達電', '張大偉', '2025-10-01', '2026-06-30', 'IN_PROGRESS', 5000000.00, 1200000.00, 150000.00, 1350000.00, 3650000.00, 27.0, 1500.0, 85.5, CURRENT_TIMESTAMP),
('PRJ-002', 'T001', '內部管理Portal', 'INTERNAL', '內部專案', '王小明', '2026-01-01', '2026-03-31', 'IN_PROGRESS', 1000000.00, 350000.00, 20000.00, 370000.00, 630000.00, 37.0, 800.0, 60.0, CURRENT_TIMESTAMP);

-- ==================== 薪資匯總測試資料 ====================
-- 用於 RPT_QRY_007~009 財務報表查詢合約測試
DELETE FROM rm_payroll_summary;
INSERT INTO rm_payroll_summary (id, tenant_id, employee_id, employee_name, department_id, department_name, year_month, base_salary, overtime_pay, allowances, bonus, gross_pay, labor_insurance, health_insurance, income_tax, other_deductions, net_pay, updated_at) VALUES
('EMP-001-2026-02', 'T001', 'EMP-001', '王小明', 'D001', '人資部', '2026-02', 50000.00, 2000.00, 1000.00, 0.00, 53000.00, 1100.00, 800.00, 500.00, 0.00, 50600.00, CURRENT_TIMESTAMP),
('EMP-002-2026-02', 'T001', 'EMP-002', '李小華', 'D001', '人資部', '2026-02', 75000.00, 0.00, 2000.00, 5000.00, 82000.00, 1500.00, 1200.00, 1200.00, 0.00, 78100.00, CURRENT_TIMESTAMP),
('EMP-003-2026-02', 'T001', 'EMP-003', '張大偉', 'D002', '研發部', '2026-02', 65000.00, 5000.00, 1500.00, 0.00, 71500.00, 1300.00, 1000.00, 800.00, 0.00, 68400.00, CURRENT_TIMESTAMP);

