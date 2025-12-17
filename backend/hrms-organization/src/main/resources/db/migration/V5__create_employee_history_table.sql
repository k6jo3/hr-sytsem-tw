-- HR02 組織員工服務 - V5: 建立員工人事歷程表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE employee_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    effective_date DATE NOT NULL,
    old_value JSONB,
    new_value JSONB,
    reason TEXT,
    approved_by UUID REFERENCES employees(employee_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_event_type CHECK (event_type IN (
        'ONBOARDING', 'PROBATION_PASSED', 'DEPARTMENT_TRANSFER',
        'JOB_CHANGE', 'PROMOTION', 'SALARY_ADJUSTMENT',
        'TERMINATION', 'REHIRE'
    ))
);

CREATE INDEX idx_history_employee_id ON employee_history(employee_id);
CREATE INDEX idx_history_event_type ON employee_history(event_type);
CREATE INDEX idx_history_effective_date ON employee_history(effective_date);

COMMENT ON TABLE employee_history IS '員工人事歷程記錄表';
COMMENT ON COLUMN employee_history.old_value IS '變更前資料 (JSON格式)';
COMMENT ON COLUMN employee_history.new_value IS '變更後資料 (JSON格式)';
