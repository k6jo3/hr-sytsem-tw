-- ============================================================================
-- HR08 Performance Service - Local Schema (H2)
-- 對應 Entity: PerformanceCycleEntity, PerformanceReviewEntity
-- ============================================================================

-- 考核週期
CREATE TABLE IF NOT EXISTS performance_cycles (
    cycle_id UUID PRIMARY KEY,
    cycle_name VARCHAR(100) NOT NULL,
    cycle_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    self_eval_deadline DATE,
    manager_eval_deadline DATE,
    status VARCHAR(20) NOT NULL,
    template TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 員工 ReadModel（跨服務 CQRS 讀取模型，由 EmployeeCreatedEvent 維護）
CREATE TABLE IF NOT EXISTS employee_read_models (
    employee_id UUID PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL
);

-- 考核記錄
CREATE TABLE IF NOT EXISTS performance_reviews (
    review_id UUID PRIMARY KEY,
    cycle_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    reviewer_id UUID NOT NULL,
    review_type VARCHAR(20) NOT NULL,
    evaluation_items TEXT,
    overall_score DECIMAL(5,2),
    overall_rating VARCHAR(10),
    final_score DECIMAL(5,2),
    final_rating VARCHAR(10),
    adjustment_reason TEXT,
    comments TEXT,
    status VARCHAR(30) NOT NULL,
    submitted_at TIMESTAMP,
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_review_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(cycle_id)
);
