-- ============================================================================
-- HR05 Insurance Service - Local Schema (H2 In-Memory)
-- ============================================================================

-- 投保單位
CREATE TABLE insurance_units (
    unit_id         VARCHAR(36)  NOT NULL PRIMARY KEY,
    organization_id VARCHAR(255) NOT NULL,
    unit_code       VARCHAR(50)  NOT NULL UNIQUE,
    unit_name       VARCHAR(255) NOT NULL,
    labor_insurance_number  VARCHAR(50),
    health_insurance_number VARCHAR(50),
    pension_number          VARCHAR(50),
    is_active       BOOLEAN,
    created_at      TIMESTAMP
);

-- 投保級距
CREATE TABLE insurance_levels (
    level_id            VARCHAR(36)    NOT NULL PRIMARY KEY,
    insurance_type      VARCHAR(20)    NOT NULL,
    level_number        INTEGER        NOT NULL,
    monthly_salary      NUMERIC(10,2)  NOT NULL,
    labor_employee_rate NUMERIC(6,4),
    labor_employer_rate NUMERIC(6,4),
    health_employee_rate NUMERIC(6,4),
    health_employer_rate NUMERIC(6,4),
    pension_employer_rate NUMERIC(6,4),
    effective_date      DATE           NOT NULL,
    end_date            DATE,
    is_active           BOOLEAN
);

-- 加退保記錄
CREATE TABLE insurance_enrollments (
    enrollment_id       VARCHAR(36)    NOT NULL PRIMARY KEY,
    employee_id         VARCHAR(255)   NOT NULL,
    insurance_unit_id   VARCHAR(36)    NOT NULL,
    insurance_type      VARCHAR(20)    NOT NULL,
    enroll_date         DATE           NOT NULL,
    withdraw_date       DATE,
    insurance_level_id  VARCHAR(36),
    monthly_salary      NUMERIC(10,2)  NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    is_reported         BOOLEAN        DEFAULT FALSE,
    reported_at         TIMESTAMP,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

-- 團體保險方案
CREATE TABLE group_insurance_plans (
    plan_id             VARCHAR(36)    NOT NULL PRIMARY KEY,
    organization_id     VARCHAR(255)   NOT NULL,
    plan_name           VARCHAR(100)   NOT NULL,
    plan_code           VARCHAR(50)    NOT NULL UNIQUE,
    insurance_type      VARCHAR(30)    NOT NULL,
    insurer_name        VARCHAR(100),
    policy_number       VARCHAR(50),
    contract_start_date DATE           NOT NULL,
    contract_end_date   DATE,
    is_active           BOOLEAN        DEFAULT TRUE,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

-- 團體保險方案職等對應
CREATE TABLE group_insurance_plan_tiers (
    tier_id             VARCHAR(36)    NOT NULL PRIMARY KEY,
    plan_id             VARCHAR(36)    NOT NULL,
    job_grade           VARCHAR(20)    NOT NULL,
    coverage_amount     NUMERIC(12,2)  NOT NULL,
    monthly_premium     NUMERIC(10,2)  NOT NULL,
    employer_share_rate NUMERIC(5,4)   NOT NULL,
    CONSTRAINT fk_tier_plan FOREIGN KEY (plan_id) REFERENCES group_insurance_plans(plan_id)
);

-- 補充保費
CREATE TABLE supplementary_premiums (
    premium_id      VARCHAR(36)    NOT NULL PRIMARY KEY,
    employee_id     VARCHAR(255)   NOT NULL,
    income_type     VARCHAR(30)    NOT NULL,
    income_date     DATE           NOT NULL,
    income_amount   NUMERIC(12,2)  NOT NULL,
    insured_salary  NUMERIC(10,2)  NOT NULL,
    threshold       NUMERIC(12,2)  NOT NULL,
    premium_base    NUMERIC(12,2)  NOT NULL,
    premium_amount  NUMERIC(10,2)  NOT NULL,
    premium_year    INTEGER        NOT NULL,
    premium_month   INTEGER        NOT NULL,
    created_at      TIMESTAMP
);
