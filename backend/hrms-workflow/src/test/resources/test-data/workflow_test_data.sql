-- Workflow 測試資料
-- 用於 QueryEngine 契約測試與業務合約測試

-- 清除舊資料（依外鍵順序：子表先刪）
DELETE FROM workflow_approval_tasks;
DELETE FROM workflow_instances;
DELETE FROM workflow_definitions;
DELETE FROM workflow_user_delegations;

-- ===========================================================================
-- 流程定義測試資料 (共 6 筆)
-- 欄位對應 WorkflowDefinitionEntity:
--   definition_id, flow_name, flow_type (String), description, status,
--   version, is_active, created_at, updated_at
-- DefinitionStatus: DRAFT, ACTIVE, INACTIVE
-- ===========================================================================
INSERT INTO workflow_definitions (definition_id, flow_name, flow_type, description, status, version, is_active, created_at, updated_at) VALUES
('WD-001', '請假流程',     'LEAVE_APPROVAL',      '一般請假申請流程',     'ACTIVE',   1, true,  '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('WD-002', '加班流程',     'OVERTIME_APPROVAL',   '加班申請流程',         'ACTIVE',   1, true,  '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('WD-003', '費用報銷流程', 'OTHER',               '費用報銷審核流程',     'ACTIVE',   1, true,  '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('WD-004', '採購流程',     'PURCHASE_APPROVAL',   '採購申請流程 (草稿)',   'DRAFT',    1, true,  '2025-01-15 09:00:00', '2025-01-15 09:00:00'),
('WD-005', '請假流程 V2',  'LEAVE_APPROVAL',      '請假流程舊版',         'ACTIVE',   2, false, '2024-12-01 09:00:00', '2024-12-01 09:00:00'),
('WD-006', '招募流程',     'RECRUITMENT_OFFER',   '招募審核流程 (停用)',   'INACTIVE', 1, true,  '2025-01-10 09:00:00', '2025-01-20 09:00:00');

-- ===========================================================================
-- 流程實例測試資料 (共 10 筆)
-- 狀態分布: RUNNING=4, COMPLETED=3, CANCELLED=2, REJECTED=1
-- 業務類型: LEAVE=5, OVERTIME=3, EXPENSE=2
-- 部門分布: D001=6, D002=3, D003=1
-- 申請人: E001(張三)=3
--
-- 欄位對應 WorkflowInstanceEntity:
--   instance_id, definition_id, flow_type (FlowType enum), business_type,
--   business_id, business_url, applicant_id, applicant_name,
--   department_id, department_name, summary, variables_json,
--   status (InstanceStatus enum), current_node_id, current_node_name,
--   started_at, completed_at
--
-- FlowType: LEAVE_APPROVAL, OVERTIME_APPROVAL, PURCHASE_APPROVAL,
--           RECRUITMENT_OFFER, RESIGNATION_APPROVAL, OTHER
-- InstanceStatus: DRAFT, RUNNING, COMPLETED, REJECTED, CANCELLED
-- ===========================================================================
INSERT INTO workflow_instances (instance_id, definition_id, flow_type, business_type, business_id, business_url, applicant_id, applicant_name, department_id, department_name, summary, variables_json, status, current_node_id, current_node_name, started_at, completed_at) VALUES
-- RUNNING 狀態 (4 筆) -- D001=3, D002=1
('PI-001', 'WD-001', 'LEAVE_APPROVAL',    'LEAVE',    'LV-2025-001', '/leave/LV-2025-001',       'E001', '張三', 'D001', '研發部', '2025/01 特休申請',   '{}', 'RUNNING',   'N002', '主管審核', '2025-01-15 09:00:00', NULL),
('PI-002', 'WD-001', 'LEAVE_APPROVAL',    'LEAVE',    'LV-2025-002', '/leave/LV-2025-002',       'E002', '李四', 'D001', '研發部', '2025/01 病假申請',   '{}', 'RUNNING',   'N002', '主管審核', '2025-01-16 10:00:00', NULL),
('PI-003', 'WD-002', 'OVERTIME_APPROVAL', 'OVERTIME', 'OT-2025-001', '/overtime/OT-2025-001',   'E001', '張三', 'D001', '研發部', '2025/01 加班申請',   '{}', 'RUNNING',   'N003', 'HR審核',   '2025-01-17 11:00:00', NULL),
('PI-004', 'WD-003', 'OTHER',             'EXPENSE',  'EX-2025-001', '/expense/EX-2025-001',     'E003', '王五', 'D002', '業務部', '2025/01 差旅費報銷', '{}', 'RUNNING',   'N002', '財務審核', '2025-01-18 14:00:00', NULL),

-- COMPLETED 狀態 (3 筆) -- D001=2, D003=1
('PI-005', 'WD-001', 'LEAVE_APPROVAL',    'LEAVE',    'LV-2024-100', '/leave/LV-2024-100',       'E001', '張三', 'D001', '研發部', '2024/12 特休申請',   '{}', 'COMPLETED', NULL,   NULL,       '2024-12-20 09:00:00', '2024-12-22 15:00:00'),
('PI-006', 'WD-002', 'OVERTIME_APPROVAL', 'OVERTIME', 'OT-2024-050', '/overtime/OT-2024-050',   'E002', '李四', 'D001', '研發部', '2024/12 加班申請',   '{}', 'COMPLETED', NULL,   NULL,       '2024-12-25 10:00:00', '2024-12-26 11:00:00'),
('PI-007', 'WD-003', 'OTHER',             'EXPENSE',  'EX-2024-030', '/expense/EX-2024-030',     'E003', '王五', 'D003', '財務部', '2024/12 差旅費報銷', '{}', 'COMPLETED', NULL,   NULL,       '2024-12-28 14:00:00', '2024-12-30 16:00:00'),

-- CANCELLED 狀態 (2 筆) -- D002=2
('PI-008', 'WD-001', 'LEAVE_APPROVAL',    'LEAVE',    'LV-2025-003', '/leave/LV-2025-003',       'E004', '趙六', 'D002', '業務部', '2025/01 特休取消',   '{}', 'CANCELLED', NULL,   NULL,       '2025-01-10 09:00:00', '2025-01-10 10:00:00'),
('PI-009', 'WD-002', 'OVERTIME_APPROVAL', 'OVERTIME', 'OT-2025-002', '/overtime/OT-2025-002',   'E004', '趙六', 'D002', '業務部', '2025/01 加班取消',   '{}', 'CANCELLED', NULL,   NULL,       '2025-01-12 11:00:00', '2025-01-12 12:00:00'),

-- REJECTED 狀態 (1 筆) -- D001=1
('PI-010', 'WD-001', 'LEAVE_APPROVAL',    'LEAVE',    'LV-2025-004', '/leave/LV-2025-004',       'E005', '錢七', 'D001', '研發部', '2025/01 請假駁回',   '{}', 'REJECTED',  NULL,   NULL,       '2025-01-15 08:00:00', '2025-01-16 09:00:00');

-- ===========================================================================
-- 待辦任務測試資料 (共 8 筆)
-- 欄位對應 ApprovalTaskEntity:
--   task_id, instance_id (FK→workflow_instances), node_id, node_name,
--   assignee_id, assignee_name, status (TaskStatus enum),
--   due_date, created_at, approved_at, comments, is_overdue
-- TaskStatus: PENDING, APPROVED, REJECTED, DELEGATED, CANCELLED
-- ===========================================================================
INSERT INTO workflow_approval_tasks (task_id, instance_id, node_id, node_name, assignee_id, assignee_name, status, due_date, created_at, approved_at, comments, is_overdue) VALUES
-- PENDING 任務 (4 筆) — WFL_Q001: 查詢待辦 → 預期 4 筆
('T-001', 'PI-001', 'N002', '主管審核', 'E010', '主管A',   'PENDING',   '2025-01-20 00:00:00', '2025-01-15 09:00:00', NULL,                    NULL,     false),
('T-002', 'PI-002', 'N002', '主管審核', 'E010', '主管A',   'PENDING',   '2025-01-18 00:00:00', '2025-01-16 10:00:00', NULL,                    NULL,     false),
('T-003', 'PI-003', 'N003', 'HR審核',   'E020', 'HR專員',  'PENDING',   '2025-01-22 00:00:00', '2025-01-17 11:00:00', NULL,                    NULL,     false),
('T-004', 'PI-004', 'N002', '財務審核', 'E030', '財務專員', 'PENDING',   '2025-01-25 00:00:00', '2025-01-18 14:00:00', NULL,                    NULL,     false),

-- APPROVED 任務 (3 筆) — WFL_T003: 查詢已核准任務 → 預期 3 筆
('T-005', 'PI-005', 'N002', '主管審核', 'E010', '主管A',   'APPROVED',  '2024-12-25 00:00:00', '2024-12-20 09:00:00', '2024-12-21 10:00:00',  '同意',   false),
('T-006', 'PI-005', 'N003', 'HR審核',   'E020', 'HR專員',  'APPROVED',  '2024-12-27 00:00:00', '2024-12-21 10:00:00', '2024-12-22 15:00:00',  '核准',   false),
('T-007', 'PI-006', 'N002', '主管審核', 'E010', '主管A',   'APPROVED',  '2024-12-28 00:00:00', '2024-12-25 10:00:00', '2024-12-26 11:00:00',  NULL,     false),

-- CANCELLED 任務 (1 筆)
('T-008', 'PI-008', 'N002', '主管審核', 'E011', '主管B',   'CANCELLED', '2025-01-15 00:00:00', '2025-01-10 09:00:00', '2025-01-10 10:00:00',  '申請人取消', false);

-- ===========================================================================
-- 測試場景資料分布說明:
--
-- 流程定義:
--   WFL_D001: ACTIVE 流程定義    → 預期 3 筆 (WD-001, WD-002, WD-003)
--   WFL_D002: LEAVE_APPROVAL 類型 → 預期 2 筆 (WD-001, WD-005)
--   WFL_D003: is_active=true     → 預期 5 筆 (WD-001~WD-004, WD-006)
--
-- 流程實例 (共 10 筆):
--   WFL_I001: RUNNING  → 4 筆 (PI-001,002,003,004)
--   WFL_I002: COMPLETED → 3 筆 (PI-005,006,007)
--   WFL_I003: CANCELLED → 2 筆 (PI-008,009)
--   WFL_I004: REJECTED  → 1 筆 (PI-010)
--   LEAVE=5, OVERTIME=3, EXPENSE=2
--   D001=6, D002=3, D003=1
--   E001(張三)=3 (PI-001,003,005)
--   startedAt >= 2025-01-15: 5 筆 (PI-001,002,003,004,010)
--   completedAt IS NOT NULL: 6 筆 (PI-005~PI-010)
--   LIKE '%特休%' = 3 筆 (PI-001,005,008)
--   LIKE '%加班%' = 3 筆 (PI-003,006,009)
--
-- 待辦任務:
--   WFL_Q001: PENDING → 4 筆 (T-001,002,003,004)
--   WFL_T003: APPROVED → 3 筆 (T-005,006,007)
-- ===========================================================================
