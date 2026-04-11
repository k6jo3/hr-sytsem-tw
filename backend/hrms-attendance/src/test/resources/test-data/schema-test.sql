-- HR03 考勤服務測試用 Schema
-- ddl-auto: none 時，需手動建立所有資料表
-- 所有 CREATE TABLE 皆使用 IF NOT EXISTS 避免重複建立

-- ==================== 班別 ====================
CREATE TABLE IF NOT EXISTS shifts (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    organization_id VARCHAR(50),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    break_start_time VARCHAR(20),
    break_end_time VARCHAR(20),
    late_tolerance_minutes INT,
    early_leave_tolerance_minutes INT,
    late_check_enabled BOOLEAN,
    late_salary_deduction BOOLEAN,
    is_active INT,
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 出勤紀錄 ====================
CREATE TABLE IF NOT EXISTS attendance_records (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    record_date DATE,
    shift_id VARCHAR(50),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(50),
    is_late BOOLEAN,
    late_minutes INT,
    is_early_leave BOOLEAN,
    early_leave_minutes INT,
    anomaly_type VARCHAR(50),
    is_corrected BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 補卡申請 ====================
CREATE TABLE IF NOT EXISTS attendance_corrections (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    attendance_record_id VARCHAR(50),
    correction_date DATE,
    correction_type VARCHAR(50),
    corrected_check_in_time TIME,
    corrected_check_out_time TIME,
    reason VARCHAR(500),
    status VARCHAR(50),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    approver_id VARCHAR(50)
);

-- ==================== 假別 ====================
CREATE TABLE IF NOT EXISTS leave_types (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    organization_id VARCHAR(50),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    is_paid BOOLEAN,
    pay_rate DECIMAL(5,2),
    is_active BOOLEAN,
    is_statutory_leave BOOLEAN,
    statutory_type VARCHAR(50),
    requires_proof BOOLEAN,
    proof_description VARCHAR(500),
    max_days_per_year DECIMAL(5,2),
    can_carryover BOOLEAN,
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 請假申請 ====================
CREATE TABLE IF NOT EXISTS leave_applications (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
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
    hours DOUBLE PRECISION,
    approver_id VARCHAR(50),
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 假別餘額 ====================
CREATE TABLE IF NOT EXISTS leave_balances (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    leave_type_id VARCHAR(50) NOT NULL,
    "year" INT NOT NULL,
    total_days DECIMAL(5,2),
    used_days DECIMAL(5,2),
    carry_over_days DECIMAL(5,2),
    expiry_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 加班申請 ====================
CREATE TABLE IF NOT EXISTS overtime_applications (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    overtime_date DATE,
    start_time TIME,
    end_time TIME,
    hours DOUBLE PRECISION,
    status VARCHAR(50),
    reason VARCHAR(255),
    overtime_type VARCHAR(50),
    rejection_reason VARCHAR(255),
    department_id VARCHAR(50),
    approver_id VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    is_deleted INT
);

-- ==================== 排班表 ====================
CREATE TABLE IF NOT EXISTS shift_schedules (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    shift_id VARCHAR(50) NOT NULL,
    schedule_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    rotation_pattern_id VARCHAR(50),
    note VARCHAR(500),
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 換班申請 ====================
CREATE TABLE IF NOT EXISTS shift_swap_requests (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    requester_id VARCHAR(50) NOT NULL,
    counterpart_id VARCHAR(50) NOT NULL,
    requester_date DATE NOT NULL,
    counterpart_date DATE NOT NULL,
    requester_shift_id VARCHAR(50),
    counterpart_shift_id VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    reason VARCHAR(500),
    rejection_reason VARCHAR(500),
    approver_id VARCHAR(50),
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 輪班模式 ====================
CREATE TABLE IF NOT EXISTS rotation_patterns (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    organization_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    cycle_days INT NOT NULL,
    is_active INT,
    is_deleted INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- ==================== 輪班天序 ====================
CREATE TABLE IF NOT EXISTS rotation_days (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    pattern_id VARCHAR(50) NOT NULL,
    day_order INT NOT NULL,
    shift_id VARCHAR(50),
    is_rest_day BOOLEAN
);
