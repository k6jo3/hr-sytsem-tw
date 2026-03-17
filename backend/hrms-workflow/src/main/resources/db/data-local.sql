-- ============================================================================
-- HR11 Workflow Service - Local Seed Data (PostgreSQL)
-- ============================================================================

-- 流程定義 (2 筆: ACTIVE + DRAFT)
INSERT INTO workflow_definitions (definition_id, flow_name, flow_type, description, status, default_due_days, nodes_json, edges_json, is_active, version, created_by, updated_by, created_at, updated_at, published_at) VALUES
('WF-DEF-001', '請假簽核流程', 'LEAVE_APPROVAL', '員工請假申請 → 主管審核 → HR 確認', 'ACTIVE', 3,
 '[{"nodeId":"start","nodeType":"START","name":"開始"},{"nodeId":"mgr","nodeType":"APPROVAL","name":"主管審核"},{"nodeId":"hr","nodeType":"APPROVAL","name":"HR確認"},{"nodeId":"end","nodeType":"END","name":"結束"}]',
 '[{"from":"start","to":"mgr"},{"from":"mgr","to":"hr"},{"from":"hr","to":"end"}]',
 TRUE, 2, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WF-DEF-002', '加班簽核流程', 'OVERTIME_APPROVAL', '員工加班申請 → 主管審核', 'DRAFT', 5,
 '[{"nodeId":"start","nodeType":"START","name":"開始"},{"nodeId":"mgr","nodeType":"APPROVAL","name":"主管審核"},{"nodeId":"end","nodeType":"END","name":"結束"}]',
 '[{"from":"start","to":"mgr"},{"from":"mgr","to":"end"}]',
 FALSE, 1, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL)
ON CONFLICT (definition_id) DO NOTHING;

-- 流程實例 (3 筆: RUNNING, COMPLETED, REJECTED)
INSERT INTO workflow_instances (instance_id, definition_id, flow_type, business_type, business_id, business_url, applicant_id, applicant_name, department_id, department_name, summary, variables_json, status, current_node_id, current_node_name, started_at, completed_at) VALUES
('WF-INS-001', 'WF-DEF-001', 'LEAVE_APPROVAL', 'LEAVE', 'LV-2026-001', '/attendance/leaves/LV-2026-001', '00000000-0000-0000-0000-000000000001', '張小明', 'D001', '研發部', '特休假 3/10-3/12 共3天', '{"leaveType":"ANNUAL","days":3}', 'RUNNING', 'mgr', '主管審核', CURRENT_TIMESTAMP, NULL),
('WF-INS-002', 'WF-DEF-001', 'LEAVE_APPROVAL', 'LEAVE', 'LV-2026-002', '/attendance/leaves/LV-2026-002', '00000000-0000-0000-0000-000000000002', '李小華', 'D002', '人資部', '病假 3/5 共1天', '{"leaveType":"SICK","days":1}', 'COMPLETED', 'end', '結束', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WF-INS-003', 'WF-DEF-001', 'LEAVE_APPROVAL', 'LEAVE', 'LV-2026-003', '/attendance/leaves/LV-2026-003', '00000000-0000-0000-0000-000000000003', '王大明', 'D001', '研發部', '事假 3/15 共1天', '{"leaveType":"PERSONAL","days":1}', 'REJECTED', 'mgr', '主管審核', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (instance_id) DO NOTHING;

-- 審核任務 (4 筆: PENDING x2, APPROVED, REJECTED)
INSERT INTO workflow_approval_tasks (task_id, instance_id, node_id, node_name, assignee_id, assignee_name, delegated_to_id, delegated_to_name, approver_id, status, created_at, approved_at, comments, due_date, is_overdue) VALUES
('WF-TASK-001', 'WF-INS-001', 'mgr', '主管審核', '00000000-0000-0000-0000-000000000010', '陳經理', NULL, NULL, NULL, 'PENDING', CURRENT_TIMESTAMP, NULL, NULL, CURRENT_TIMESTAMP + INTERVAL '3 days', FALSE),
('WF-TASK-002', 'WF-INS-002', 'mgr', '主管審核', '00000000-0000-0000-0000-000000000010', '陳經理', NULL, NULL, '00000000-0000-0000-0000-000000000010', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '同意', CURRENT_TIMESTAMP + INTERVAL '3 days', FALSE),
('WF-TASK-003', 'WF-INS-002', 'hr', 'HR確認', '00000000-0000-0000-0000-000000000020', 'HR王小姐', NULL, NULL, '00000000-0000-0000-0000-000000000020', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '確認通過', CURRENT_TIMESTAMP + INTERVAL '3 days', FALSE),
('WF-TASK-004', 'WF-INS-003', 'mgr', '主管審核', '00000000-0000-0000-0000-000000000010', '陳經理', NULL, NULL, '00000000-0000-0000-0000-000000000010', 'REJECTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '該日有重要會議，請改期', CURRENT_TIMESTAMP + INTERVAL '3 days', FALSE)
ON CONFLICT (task_id) DO NOTHING;

-- 用戶代理設定 (2 筆: active + expired)
INSERT INTO workflow_user_delegations (delegation_id, delegator_id, delegate_id, start_date, end_date, is_active, delegation_scope, specific_flow_types, reason, created_at) VALUES
('WF-DLG-001', '00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000020', '2026-03-01', '2026-03-31', TRUE, 'ALL', NULL, '出差期間代理', CURRENT_TIMESTAMP),
('WF-DLG-002', '00000000-0000-0000-0000-000000000020', '00000000-0000-0000-0000-000000000010', '2026-01-01', '2026-01-31', FALSE, 'SPECIFIC', '["LEAVE_APPROVAL"]', '休假期間代理', CURRENT_TIMESTAMP)
ON CONFLICT (delegation_id) DO NOTHING;

-- 簡化版代理 (stub)
INSERT INTO hrms_wf_delegation (delegation_id, applicant_id, delegee_id) VALUES
('WF-SDLG-001', '00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000020')
ON CONFLICT (delegation_id) DO NOTHING;
