-- ============================================================================
-- HR09 Recruitment Service - Local Schema (H2)
-- 對應 Entity: JobOpeningEntity, CandidateEntity, InterviewEntity, OfferEntity
-- ============================================================================

-- 職缺
CREATE TABLE IF NOT EXISTS job_openings (
    opening_id UUID PRIMARY KEY,
    job_title VARCHAR(100) NOT NULL,
    department_id UUID,
    number_of_positions INT NOT NULL DEFAULT 1,
    filled_positions INT NOT NULL DEFAULT 0,
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    salary_currency VARCHAR(10) DEFAULT 'TWD',
    requirements TEXT,
    responsibilities TEXT,
    employment_type VARCHAR(20) NOT NULL,
    work_location VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    open_date DATE,
    close_date DATE,
    close_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted INT NOT NULL DEFAULT 0
);

-- 應徵者
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id UUID PRIMARY KEY,
    opening_id UUID,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    phone_number VARCHAR(50),
    resume_url VARCHAR(500),
    source VARCHAR(20),
    referrer_id UUID,
    application_date DATE,
    status VARCHAR(20) NOT NULL,
    rejection_reason VARCHAR(500),
    cover_letter TEXT,
    expected_salary DECIMAL(12,2),
    available_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 面試
CREATE TABLE IF NOT EXISTS interviews (
    interview_id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL,
    candidate_name VARCHAR(100),
    interview_round INT NOT NULL DEFAULT 1,
    interview_type VARCHAR(20) NOT NULL,
    interview_date TIMESTAMP NOT NULL,
    location VARCHAR(200),
    interviewer_ids_json TEXT,
    status VARCHAR(20) NOT NULL,
    evaluations_json TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Offer
CREATE TABLE IF NOT EXISTS offers (
    offer_id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL,
    candidate_name VARCHAR(100),
    offered_position VARCHAR(100) NOT NULL,
    offered_salary DECIMAL(12,2),
    offered_start_date DATE,
    offer_date DATE NOT NULL,
    expiry_date DATE,
    status VARCHAR(20) NOT NULL,
    response_date DATE,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
