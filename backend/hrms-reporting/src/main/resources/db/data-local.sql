-- ============================================================================
-- HR14 Reporting Service - Local Seed Data (H2)
-- ============================================================================

-- 儀表板 (1 筆: HR 總覽)
INSERT INTO rpt_dashboard (dashboard_id, dashboard_name, description, owner_id, tenant_id, is_public, is_default, widgets_config, created_at, updated_at, created_by, updated_by) VALUES
('00000000-0000-0000-0000-000000000101', 'HR 總覽儀表板', '人力資源關鍵指標總覽', '00000000-0000-0000-0000-000000000001', 'local', TRUE, TRUE,
 '[{"widgetId":"w1","widgetType":"KPI_CARD","title":"在職人數","dataSource":"employee_roster","position":{"x":0,"y":0,"w":3,"h":2}},{"widgetId":"w2","widgetType":"PIE_CHART","title":"部門人數分布","dataSource":"employee_roster","position":{"x":3,"y":0,"w":3,"h":2}},{"widgetId":"w3","widgetType":"LINE_CHART","title":"出勤率趨勢","dataSource":"attendance_statistics","position":{"x":6,"y":0,"w":6,"h":2}},{"widgetId":"w4","widgetType":"BAR_CHART","title":"專案成本分析","dataSource":"project_cost_analysis","position":{"x":0,"y":2,"w":6,"h":3}}]',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001');

-- 員工花名冊 (4 筆)
INSERT INTO rm_employee_roster (employee_id, tenant_id, employee_name, department_id, department_name, position_id, position_name, hire_date, resignation_date, service_years, status, phone, email, created_at, updated_at, is_deleted) VALUES
('00000000-0000-0000-0000-000000000001', 'local', '張小明', 'D001', '研發部', 'P001', '資深工程師', '2022-03-01', NULL, 4.0, 'ACTIVE', '0912-345-678', 'zhangxm@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('00000000-0000-0000-0000-000000000002', 'local', '李小華', 'D002', '人資部', 'P002', 'HR 專員', '2023-06-15', NULL, 2.7, 'ACTIVE', '0923-456-789', 'lixh@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('00000000-0000-0000-0000-000000000003', 'local', '王大明', 'D001', '研發部', 'P003', '工程師', '2024-01-10', NULL, 2.2, 'ACTIVE', '0934-567-890', 'wangdm@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('00000000-0000-0000-0000-000000000004', 'local', '陳美玲', 'D003', '財務部', 'P004', '會計', '2021-09-01', '2026-02-28', 4.5, 'RESIGNED', '0945-678-901', 'chenml@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 差勤統計 (4 筆: 2026年2月)
INSERT INTO rm_attendance_statistics (id, tenant_id, employee_id, employee_name, department_id, department_name, stat_date, expected_days, actual_days, late_count, early_leave_count, absent_count, leave_days, overtime_hours, attendance_rate, updated_at) VALUES
('ATT-001-202602', 'local', '00000000-0000-0000-0000-000000000001', '張小明', 'D001', '研發部', '2026-02-28', 20, 19, 1, 0, 0, 1.0, 16.5, 95.0, CURRENT_TIMESTAMP),
('ATT-002-202602', 'local', '00000000-0000-0000-0000-000000000002', '李小華', 'D002', '人資部', '2026-02-28', 20, 20, 0, 0, 0, 0, 4.0, 100.0, CURRENT_TIMESTAMP),
('ATT-003-202602', 'local', '00000000-0000-0000-0000-000000000003', '王大明', 'D001', '研發部', '2026-02-28', 20, 18, 2, 1, 0, 2.0, 24.0, 90.0, CURRENT_TIMESTAMP),
('ATT-004-202602', 'local', '00000000-0000-0000-0000-000000000004', '陳美玲', 'D003', '財務部', '2026-02-28', 20, 15, 0, 0, 0, 5.0, 0, 75.0, CURRENT_TIMESTAMP);

-- 薪資匯總 (4 筆: 2026年2月)
INSERT INTO rm_payroll_summary (id, tenant_id, employee_id, employee_name, department_id, department_name, year_month, base_salary, overtime_pay, allowances, bonus, gross_pay, labor_insurance, health_insurance, income_tax, other_deductions, net_pay, updated_at) VALUES
('PAY-001-202602', 'local', '00000000-0000-0000-0000-000000000001', '張小明', 'D001', '研發部', '2026-02', 65000.00, 5100.00, 3000.00, 0, 73100.00, 1350.00, 980.00, 3655.00, 0, 67115.00, CURRENT_TIMESTAMP),
('PAY-002-202602', 'local', '00000000-0000-0000-0000-000000000002', '李小華', 'D002', '人資部', '2026-02', 48000.00, 1200.00, 2000.00, 0, 51200.00, 1000.00, 730.00, 2560.00, 0, 46910.00, CURRENT_TIMESTAMP),
('PAY-003-202602', 'local', '00000000-0000-0000-0000-000000000003', '王大明', 'D001', '研發部', '2026-02', 55000.00, 7800.00, 2500.00, 0, 65300.00, 1200.00, 880.00, 3265.00, 0, 59955.00, CURRENT_TIMESTAMP),
('PAY-004-202602', 'local', '00000000-0000-0000-0000-000000000004', '陳美玲', 'D003', '財務部', '2026-02', 50000.00, 0, 2000.00, 0, 52000.00, 1050.00, 760.00, 2600.00, 0, 47590.00, CURRENT_TIMESTAMP);

-- 專案成本分析 (3 筆)
INSERT INTO rm_project_cost_analysis (project_id, tenant_id, project_name, customer_id, customer_name, project_manager_id, project_manager, start_date, end_date, status, budget_amount, labor_cost, other_cost, total_cost, cost_variance, cost_variance_rate, total_hours, utilization_rate, updated_at) VALUES
('PRJ-001', 'local', 'HR 系統建置專案', 'CUST-001', '台灣科技公司', '00000000-0000-0000-0000-000000000010', '陳經理', '2025-07-01', '2026-06-30', 'IN_PROGRESS', 5000000.00, 2800000.00, 350000.00, 3150000.00, 1850000.00, 37.0, 4200.0, 85.0, CURRENT_TIMESTAMP),
('PRJ-002', 'local', 'ERP 升級專案', 'CUST-002', '國際貿易公司', '00000000-0000-0000-0000-000000000010', '陳經理', '2026-01-01', '2026-12-31', 'IN_PROGRESS', 3000000.00, 500000.00, 100000.00, 600000.00, 2400000.00, 80.0, 750.0, 72.0, CURRENT_TIMESTAMP),
('PRJ-003', 'local', '網站改版專案', 'CUST-003', '媒體集團', '00000000-0000-0000-0000-000000000020', 'HR王小姐', '2025-10-01', '2026-01-31', 'COMPLETED', 1500000.00, 1200000.00, 200000.00, 1400000.00, 100000.00, 6.7, 1800.0, 92.0, CURRENT_TIMESTAMP);

-- 排程報表 (2 筆: 月報 + 週報)
INSERT INTO rm_scheduled_report (id, tenant_id, schedule_name, report_type, cron_expression, next_run_time, is_enabled, updated_at) VALUES
('SCHED-001', 'local', '月度人力資源報表', 'HR_MONTHLY', '0 0 8 1 * ?', '2026-04-01 08:00:00', TRUE, CURRENT_TIMESTAMP),
('SCHED-002', 'local', '週度專案成本報表', 'PROJECT_WEEKLY', '0 0 9 ? * MON', '2026-03-10 09:00:00', TRUE, CURRENT_TIMESTAMP);
