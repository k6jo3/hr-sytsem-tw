-- HR02 組織員工服務 - V3.1: 為 employees 表添加 is_deleted 欄位
-- 版本: 1.1
-- 日期: 2026-02-05

ALTER TABLE employees ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

CREATE INDEX IF NOT EXISTS idx_employees_is_deleted ON employees(is_deleted);

COMMENT ON COLUMN employees.is_deleted IS '軟刪除標記：false=未刪除, true=已刪除';
