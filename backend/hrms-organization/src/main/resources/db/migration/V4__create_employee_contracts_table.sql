-- HR02 組織員工服務 - V4: 建立員工合約表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE employee_contracts (
    contract_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    contract_type VARCHAR(20) NOT NULL,
    contract_number VARCHAR(100) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,  -- NULL表示不定期契約
    working_hours DECIMAL(5,2) NOT NULL DEFAULT 40,
    trial_period_months INTEGER DEFAULT 0,
    attachment_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_contract_type CHECK (contract_type IN ('INDEFINITE', 'FIXED_TERM')),
    CONSTRAINT chk_contract_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED')),
    CONSTRAINT chk_end_date CHECK (end_date IS NULL OR end_date > start_date)
);

CREATE INDEX idx_contracts_employee_id ON employee_contracts(employee_id);
CREATE INDEX idx_contracts_status ON employee_contracts(status);
CREATE INDEX idx_contracts_end_date ON employee_contracts(end_date);

COMMENT ON TABLE employee_contracts IS '員工合約表';
COMMENT ON COLUMN employee_contracts.contract_type IS 'INDEFINITE(不定期)/FIXED_TERM(定期)';
COMMENT ON COLUMN employee_contracts.working_hours IS '每週工時';
