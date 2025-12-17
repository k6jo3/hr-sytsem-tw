-- HR02 組織員工服務 - V8: 建立證明文件申請表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE certificate_requests (
    request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    certificate_type VARCHAR(50) NOT NULL,
    purpose VARCHAR(500),
    quantity INTEGER DEFAULT 1,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    processed_by UUID REFERENCES employees(employee_id),
    processed_at TIMESTAMP,
    document_url VARCHAR(500),

    CONSTRAINT chk_certificate_type CHECK (certificate_type IN (
        'EMPLOYMENT_CERTIFICATE', 'SALARY_CERTIFICATE', 'TAX_WITHHOLDING'
    )),
    CONSTRAINT chk_request_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED'))
);

CREATE INDEX idx_certificate_requests_employee_id ON certificate_requests(employee_id);
CREATE INDEX idx_certificate_requests_status ON certificate_requests(status);
CREATE INDEX idx_certificate_requests_request_date ON certificate_requests(request_date);

COMMENT ON TABLE certificate_requests IS '員工證明文件申請表';
COMMENT ON COLUMN certificate_requests.certificate_type IS 'EMPLOYMENT_CERTIFICATE(在職證明)/SALARY_CERTIFICATE(薪資證明)/TAX_WITHHOLDING(扣繳憑單)';
