-- ============================================================================
-- HR10 Training Service - Local Schema (H2)
-- 對應 Entity: TrainingCourseEntity, TrainingEnrollmentEntity, CertificateEntity
-- 注意: PK 為 VARCHAR (String)，非 UUID
-- ============================================================================

-- 訓練課程
CREATE TABLE IF NOT EXISTS training_courses (
    course_id VARCHAR(36) PRIMARY KEY,
    course_code VARCHAR(50) UNIQUE,
    course_name VARCHAR(200) NOT NULL,
    course_type VARCHAR(20) NOT NULL,
    delivery_mode VARCHAR(20),
    category VARCHAR(30),
    description TEXT,
    instructor VARCHAR(100),
    instructor_info TEXT,
    duration_hours DECIMAL(5,1),
    max_participants INT,
    min_participants INT,
    current_enrollments INT DEFAULT 0,
    start_date DATE,
    end_date DATE,
    start_time TIME,
    end_time TIME,
    location VARCHAR(200),
    cost DECIMAL(10,2),
    is_mandatory BOOLEAN DEFAULT FALSE,
    target_audience TEXT,
    prerequisites TEXT,
    enrollment_deadline DATE,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted INT DEFAULT 0
);

-- 訓練報名
CREATE TABLE IF NOT EXISTS training_enrollments (
    enrollment_id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    employee_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    remarks TEXT,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    rejected_by VARCHAR(100),
    rejected_at TIMESTAMP,
    reject_reason TEXT,
    cancelled_by VARCHAR(100),
    cancelled_at TIMESTAMP,
    cancel_reason TEXT,
    attendance BOOLEAN DEFAULT FALSE,
    attended_hours DECIMAL(5,1),
    attended_at TIMESTAMP,
    completed_hours DECIMAL(5,1),
    score DECIMAL(5,2),
    passed BOOLEAN,
    feedback TEXT,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted INT DEFAULT 0,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES training_courses(course_id)
);

-- 證照
CREATE TABLE IF NOT EXISTS certificates (
    certificate_id VARCHAR(36) PRIMARY KEY,
    employee_id VARCHAR(100) NOT NULL,
    certificate_name VARCHAR(200) NOT NULL,
    issuing_organization VARCHAR(200),
    certificate_number VARCHAR(100),
    issue_date DATE,
    expiry_date DATE,
    category VARCHAR(30),
    is_required BOOLEAN DEFAULT FALSE,
    attachment_url VARCHAR(500),
    remarks TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by VARCHAR(100),
    verified_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted INT DEFAULT 0
);
