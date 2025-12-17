-- HR02 組織員工服務 - V6: 建立學歷表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE educations (
    education_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    degree VARCHAR(50) NOT NULL,
    school VARCHAR(255) NOT NULL,
    major VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_highest_degree BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_degree CHECK (degree IN ('HIGH_SCHOOL', 'ASSOCIATE', 'BACHELOR', 'MASTER', 'DOCTORATE'))
);

CREATE INDEX idx_educations_employee_id ON educations(employee_id);

COMMENT ON TABLE educations IS '員工學歷表';
