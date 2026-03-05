-- =====================================================
-- HRMS Attendance Service - Local Profile Schema (H2 相容)
-- 基於 PO 類別欄位定義建立，使用 H2 相容語法
-- =====================================================

-- =====================================================
-- 班別表
-- =====================================================
CREATE TABLE IF NOT EXISTS shifts (
    id VARCHAR(50) PRIMARY KEY,
    organization_id VARCHAR(50),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    break_start_time VARCHAR(20),
    break_end_time VARCHAR(20),
    late_tolerance_minutes INTEGER,
    early_leave_tolerance_minutes INTEGER,
    is_active INTEGER DEFAULT 1,
    is_deleted INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_shifts_code ON shifts(code);
CREATE INDEX IF NOT EXISTS idx_shifts_org_id ON shifts(organization_id);

-- =====================================================
-- 出勤紀錄表
-- =====================================================
CREATE TABLE IF NOT EXISTS attendance_records (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    record_date DATE,
    shift_id VARCHAR(50),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(50),
    is_late BOOLEAN DEFAULT FALSE,
    late_minutes INTEGER DEFAULT 0,
    is_early_leave BOOLEAN DEFAULT FALSE,
    early_leave_minutes INTEGER DEFAULT 0,
    anomaly_type VARCHAR(50),
    is_corrected BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_att_records_emp_id ON attendance_records(employee_id);
CREATE INDEX IF NOT EXISTS idx_att_records_date ON attendance_records(record_date);
CREATE INDEX IF NOT EXISTS idx_att_records_status ON attendance_records(status);

-- =====================================================
-- 假別表
-- =====================================================
CREATE TABLE IF NOT EXISTS leave_types (
    id VARCHAR(50) PRIMARY KEY,
    organization_id VARCHAR(50),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    pay_rate DECIMAL(5,2),
    is_active BOOLEAN DEFAULT TRUE,
    is_statutory_leave BOOLEAN DEFAULT FALSE,
    statutory_type VARCHAR(50),
    requires_proof BOOLEAN DEFAULT FALSE,
    proof_description VARCHAR(500),
    max_days_per_year DECIMAL(5,2),
    can_carryover BOOLEAN DEFAULT FALSE,
    is_deleted INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_leave_types_code ON leave_types(code);
CREATE INDEX IF NOT EXISTS idx_leave_types_org_id ON leave_types(organization_id);

-- =====================================================
-- 假期餘額表
-- =====================================================
CREATE TABLE IF NOT EXISTS leave_balances (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    leave_type_id VARCHAR(50) NOT NULL,
    "year" INTEGER NOT NULL,
    total_days DECIMAL(5,2),
    used_days DECIMAL(5,2) DEFAULT 0,
    carry_over_days DECIMAL(5,2) DEFAULT 0,
    expiry_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_leave_bal_emp_id ON leave_balances(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_bal_type_id ON leave_balances(leave_type_id);

-- =====================================================
-- 請假申請表
-- =====================================================
CREATE TABLE IF NOT EXISTS leave_applications (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    leave_type_id VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    reason VARCHAR(255),
    start_period VARCHAR(20),
    end_period VARCHAR(20),
    proof_attachment_url VARCHAR(255),
    rejection_reason VARCHAR(255),
    department_id VARCHAR(50),
    hours DOUBLE,
    approver_id VARCHAR(50),
    is_deleted INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_leave_app_emp_id ON leave_applications(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_app_status ON leave_applications(status);

-- =====================================================
-- 加班申請表
-- =====================================================
CREATE TABLE IF NOT EXISTS overtime_applications (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    overtime_date DATE,
    start_time TIME,
    end_time TIME,
    hours DOUBLE,
    status VARCHAR(50),
    reason VARCHAR(255),
    overtime_type VARCHAR(50),
    rejection_reason VARCHAR(255),
    department_id VARCHAR(50),
    approver_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    is_deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ot_app_emp_id ON overtime_applications(employee_id);
CREATE INDEX IF NOT EXISTS idx_ot_app_status ON overtime_applications(status);

-- =====================================================
-- 出勤更正表
-- =====================================================
CREATE TABLE IF NOT EXISTS attendance_corrections (
    id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    attendance_record_id VARCHAR(50),
    correction_date DATE,
    correction_type VARCHAR(50),
    corrected_check_in_time TIME,
    corrected_check_out_time TIME,
    reason VARCHAR(500),
    status VARCHAR(50),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    approver_id VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_att_corr_emp_id ON attendance_corrections(employee_id);
CREATE INDEX IF NOT EXISTS idx_att_corr_record_id ON attendance_corrections(attendance_record_id);

-- =====================================================
-- 年假政策表（非 JPA Entity，備用）
-- =====================================================
CREATE TABLE IF NOT EXISTS annual_leave_policies (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    effective_date TIMESTAMP,
    annual_leave_system VARCHAR(20) DEFAULT 'CALENDAR_YEAR',
    overdraw_policy VARCHAR(30) DEFAULT 'DENY',
    expiry_policy VARCHAR(30) DEFAULT 'PAY_COMPENSATION',
    carry_over_limit INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- =====================================================
-- 年假規則表（非 JPA Entity，備用）
-- =====================================================
CREATE TABLE IF NOT EXISTS annual_leave_rules (
    id VARCHAR(50) PRIMARY KEY,
    policy_id VARCHAR(50),
    min_service_years INTEGER,
    max_service_years INTEGER,
    days INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);
