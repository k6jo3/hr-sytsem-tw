-- Workflow 測試資料
-- 用於 QueryEngine 契約測試與業務合約測試

-- 清除舊資料
DELETE FROM approval_tasks;
DELETE FROM workflow_instances;
DELETE FROM workflow_definitions;
DELETE FROM user_delegations;

-- 流程定義測試資料 (共 6 筆)
INSERT INTO workflow_definitions (definition_id, name, type, status, is_latest, department_id, description, version, created_at, updated_at, is_deleted) VALUES
('WD-001', '請假流程', 'LEAVE', 'ACTIVE', true, 'D001', '一般請假申請流程', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('WD-002', '加班流程', 'OVERTIME', 'ACTIVE', true, 'D001', '加班申請流程', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('WD-003', '費用報銷流程', 'EXPENSE', 'ACTIVE', true, NULL, '費用報銷審核流程', 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('WD-004', '採購流程', 'PURCHASE', 'DRAFT', true, NULL, '採購申請流程 (草稿)', 1, '2025-01-15 09:00:00', '2025-01-15 09:00:00', 0),
('WD-005', '請假流程 V2', 'LEAVE', 'ACTIVE', false, 'D001', '請假流程舊版', 1, '2024-12-01 09:00:00', '2024-12-01 09:00:00', 0),
('WD-006', '招募流程', 'RECRUITMENT', 'INACTIVE', true, 'D002', '招募審核流程 (停用)', 1, '2025-01-10 09:00:00', '2025-01-20 09:00:00', 0);

-- 流程實例測試資料 (共 10 筆)
-- 狀態分布: RUNNING=4, COMPLETED=3, CANCELLED=2, REJECTED=1
INSERT INTO workflow_instances (instance_id, definition_id, flow_type, business_type, business_id, business_url, applicant_id, applicant_name, department_id, department_name, summary, variables_json, status, current_node_id, current_node_name, started_at, completed_at) VALUES
-- RUNNING 狀態
('PI-001', 'WD-001', 'SEQUENTIAL', 'LEAVE', 'LV-2025-001', '/leave/LV-2025-001', 'E001', '張三', 'D001', '研發部', '2025/01 特休申請', '{}', 'RUNNING', 'N002', '主管審核', '2025-01-15 09:00:00', NULL),
('PI-002', 'WD-001', 'SEQUENTIAL', 'LEAVE', 'LV-2025-002', '/leave/LV-2025-002', 'E002', '李四', 'D001', '研發部', '2025/01 病假申請', '{}', 'RUNNING', 'N002', '主管審核', '2025-01-16 10:00:00', NULL),
('PI-003', 'WD-002', 'SEQUENTIAL', 'OVERTIME', 'OT-2025-001', '/overtime/OT-2025-001', 'E001', '張三', 'D001', '研發部', '2025/01 加班申請', '{}', 'RUNNING', 'N003', 'HR審核', '2025-01-17 11:00:00', NULL),
('PI-004', 'WD-003', 'SEQUENTIAL', 'EXPENSE', 'EX-2025-001', '/expense/EX-2025-001', 'E003', '王五', 'D002', '業務部', '2025/01 差旅費報銷', '{}', 'RUNNING', 'N002', '財務審核', '2025-01-18 14:00:00', NULL),

-- COMPLETED 狀態
('PI-005', 'WD-001', 'SEQUENTIAL', 'LEAVE', 'LV-2024-100', '/leave/LV-2024-100', 'E001', '張三', 'D001', '研發部', '2024/12 特休申請', '{}', 'COMPLETED', NULL, NULL, '2024-12-20 09:00:00', '2024-12-22 15:00:00'),
('PI-006', 'WD-002', 'SEQUENTIAL', 'OVERTIME', 'OT-2024-050', '/overtime/OT-2024-050', 'E002', '李四', 'D001', '研發部', '2024/12 加班申請', '{}', 'COMPLETED', NULL, NULL, '2024-12-25 10:00:00', '2024-12-26 11:00:00'),
('PI-007', 'WD-003', 'SEQUENTIAL', 'EXPENSE', 'EX-2024-030', '/expense/EX-2024-030', 'E003', '王五', 'D002', '業務部', '2024/12 差旅費報銷', '{}', 'COMPLETED', NULL, NULL, '2024-12-28 14:00:00', '2024-12-30 16:00:00'),

-- CANCELLED 狀態
('PI-008', 'WD-001', 'SEQUENTIAL', 'LEAVE', 'LV-2025-003', '/leave/LV-2025-003', 'E004', '趙六', 'D002', '業務部', '2025/01 特休取消', '{}', 'CANCELLED', NULL, NULL, '2025-01-10 09:00:00', '2025-01-10 10:00:00'),
('PI-009', 'WD-002', 'SEQUENTIAL', 'OVERTIME', 'OT-2025-002', '/overtime/OT-2025-002', 'E004', '趙六', 'D002', '業務部', '2025/01 加班取消', '{}', 'CANCELLED', NULL, NULL, '2025-01-12 11:00:00', '2025-01-12 12:00:00'),

-- REJECTED 狀態
('PI-010', 'WD-003', 'SEQUENTIAL', 'EXPENSE', 'EX-2025-002', '/expense/EX-2025-002', 'E005', '錢七', 'D003', '財務部', '2025/01 費用駁回', '{}', 'REJECTED', NULL, NULL, '2025-01-14 15:00:00', '2025-01-15 09:00:00');

-- 待辦任務測試資料 (共 8 筆)
INSERT INTO approval_tasks (task_id, workflow_instance_id, node_id, node_name, task_type, assignee_id, assignee_name, candidate_groups, status, priority, due_date, started_at, completed_at, action, comment) VALUES
-- PENDING 任務
('T-001', 'PI-001', 'N002', '主管審核', 'APPROVE', 'E010', '主管A', 'MANAGER', 'PENDING', 'NORMAL', '2025-01-20', '2025-01-15 09:00:00', NULL, NULL, NULL),
('T-002', 'PI-002', 'N002', '主管審核', 'APPROVE', 'E010', '主管A', 'MANAGER', 'PENDING', 'HIGH', '2025-01-18', '2025-01-16 10:00:00', NULL, NULL, NULL),
('T-003', 'PI-003', 'N003', 'HR審核', 'APPROVE', 'E020', 'HR專員', 'HR', 'PENDING', 'NORMAL', '2025-01-22', '2025-01-17 11:00:00', NULL, NULL, NULL),
('T-004', 'PI-004', 'N002', '財務審核', 'APPROVE', 'E030', '財務專員', 'FINANCE', 'PENDING', 'NORMAL', '2025-01-25', '2025-01-18 14:00:00', NULL, NULL, NULL),

-- COMPLETED 任務
('T-005', 'PI-005', 'N002', '主管審核', 'APPROVE', 'E010', '主管A', 'MANAGER', 'COMPLETED', 'NORMAL', '2024-12-25', '2024-12-20 09:00:00', '2024-12-21 10:00:00', 'APPROVED', '同意'),
('T-006', 'PI-005', 'N003', 'HR審核', 'APPROVE', 'E020', 'HR專員', 'HR', 'COMPLETED', 'NORMAL', '2024-12-27', '2024-12-21 10:00:00', '2024-12-22 15:00:00', 'APPROVED', '核准'),
('T-007', 'PI-006', 'N002', '主管審核', 'APPROVE', 'E010', '主管A', 'MANAGER', 'COMPLETED', 'NORMAL', '2024-12-28', '2024-12-25 10:00:00', '2024-12-26 11:00:00', 'APPROVED', NULL),

-- CANCELLED 任務
('T-008', 'PI-008', 'N002', '主管審核', 'APPROVE', 'E011', '主管B', 'MANAGER', 'CANCELLED', 'NORMAL', '2025-01-15', '2025-01-10 09:00:00', '2025-01-10 10:00:00', NULL, '申請人取消');

-- 測試場景說明:
-- WFL_D001: 查詢 ACTIVE 流程定義 → 預期 3 筆 (WD-001, WD-002, WD-003)
-- WFL_D002: 查詢 LEAVE 類型 → 預期 2 筆 (WD-001, WD-005)
-- WFL_D003: 查詢最新版本 → 預期 5 筆 (is_latest = true)
-- WFL_I001: 查詢 RUNNING 實例 → 預期 4 筆
-- WFL_I002: 查詢 COMPLETED 實例 → 預期 3 筆
-- WFL_T001: 查詢待辦 → 預期 4 筆 (PENDING)
-- WFL_T003: 查詢已完成任務 → 預期 3 筆 (COMPLETED)
