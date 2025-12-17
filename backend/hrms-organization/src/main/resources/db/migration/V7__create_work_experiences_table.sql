-- HR02 組織員工服務 - V7: 建立工作經歷表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE work_experiences (
    experience_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    company VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,  -- NULL表示目前在職
    description TEXT
);

CREATE INDEX idx_work_experiences_employee_id ON work_experiences(employee_id);

COMMENT ON TABLE work_experiences IS '員工工作經歷表';
