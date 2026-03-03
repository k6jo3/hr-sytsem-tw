-- ============================================================================
-- HR07 Timesheet Service - Local Schema (H2)
-- ============================================================================

CREATE TABLE IF NOT EXISTS timesheets (
    timesheet_id    VARCHAR(36) PRIMARY KEY,
    employee_id     VARCHAR(36) NOT NULL,
    period_type     VARCHAR(50),
    period_start_date DATE,
    period_end_date   DATE,
    total_hours     DECIMAL(10,2),
    status          VARCHAR(50),
    submitted_at    TIMESTAMP,
    approved_by     VARCHAR(36),
    approved_at     TIMESTAMP,
    rejection_reason TEXT,
    is_locked       BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS timesheet_entries (
    entry_id        VARCHAR(36) PRIMARY KEY,
    timesheet_id    VARCHAR(36) NOT NULL,
    project_id      VARCHAR(36) NOT NULL,
    task_id         VARCHAR(36),
    work_date       DATE NOT NULL,
    hours           DECIMAL(10,2) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP,
    FOREIGN KEY (timesheet_id) REFERENCES timesheets(timesheet_id)
);
