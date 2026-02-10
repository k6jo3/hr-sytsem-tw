-- HR02 組織員工服務 - V2.1: 為 departments 表添加 is_deleted 欄位
-- 版本: 1.1
-- 日期: 2026-02-05

ALTER TABLE departments ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

CREATE INDEX IF NOT EXISTS idx_departments_is_deleted ON departments(is_deleted);

COMMENT ON COLUMN departments.is_deleted IS '軟刪除標記：false=未刪除, true=已刪除';
