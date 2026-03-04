-- ============================================================================
-- HR14 Reporting Service - Local Schema (H2)
-- 對應 PO/ReadModel: DashboardPO, ExportTaskPO, EmployeeRosterReadModel,
--   AttendanceStatisticsReadModel, PayrollSummaryReadModel,
--   ProjectCostAnalysisReadModel, ScheduledReportReadModel, ReportExportEntity
-- ============================================================================

-- 儀表板
CREATE TABLE IF NOT EXISTS rpt_dashboard (
    dashboard_id UUID PRIMARY KEY,
    dashboard_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    owner_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    widgets_config TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID
);

-- 報表匯出任務
CREATE TABLE IF NOT EXISTS report_exports (
    id UUID PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    file_name VARCHAR(200),
    file_path VARCHAR(500),
    requester_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    filters_json TEXT,
    error_message VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

-- 員工花名冊讀模型
CREATE TABLE IF NOT EXISTS rm_employee_roster (
    employee_id VARCHAR(50) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    department_id VARCHAR(50),
    department_name VARCHAR(100),
    position_id VARCHAR(50),
    position_name VARCHAR(100),
    hire_date DATE,
    resignation_date DATE,
    service_years DOUBLE,
    status VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 差勤統計讀模型
CREATE TABLE IF NOT EXISTS rm_attendance_statistics (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100),
    department_id VARCHAR(50),
    department_name VARCHAR(100),
    stat_date DATE,
    expected_days INT,
    actual_days INT,
    late_count INT,
    early_leave_count INT,
    absent_count INT,
    leave_days DOUBLE,
    overtime_hours DOUBLE,
    attendance_rate DOUBLE,
    updated_at TIMESTAMP
);

-- 薪資匯總讀模型
CREATE TABLE IF NOT EXISTS rm_payroll_summary (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100),
    department_id VARCHAR(50),
    department_name VARCHAR(100),
    year_month VARCHAR(7),
    base_salary DECIMAL(15,2),
    overtime_pay DECIMAL(15,2),
    allowances DECIMAL(15,2),
    bonus DECIMAL(15,2),
    gross_pay DECIMAL(15,2),
    labor_insurance DECIMAL(15,2),
    health_insurance DECIMAL(15,2),
    income_tax DECIMAL(15,2),
    other_deductions DECIMAL(15,2),
    net_pay DECIMAL(15,2),
    updated_at TIMESTAMP
);

-- 專案成本分析讀模型
CREATE TABLE IF NOT EXISTS rm_project_cost_analysis (
    project_id VARCHAR(50) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    customer_id VARCHAR(50),
    customer_name VARCHAR(200),
    project_manager_id VARCHAR(50),
    project_manager VARCHAR(100),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20),
    budget_amount DECIMAL(15,2),
    labor_cost DECIMAL(15,2),
    other_cost DECIMAL(15,2),
    total_cost DECIMAL(15,2),
    cost_variance DECIMAL(15,2),
    cost_variance_rate DOUBLE,
    total_hours DOUBLE,
    utilization_rate DOUBLE,
    updated_at TIMESTAMP
);

-- 排程報表讀模型
CREATE TABLE IF NOT EXISTS rm_scheduled_report (
    id VARCHAR(50) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    schedule_name VARCHAR(100),
    report_type VARCHAR(50),
    cron_expression VARCHAR(50),
    next_run_time TIMESTAMP,
    is_enabled BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP
);
