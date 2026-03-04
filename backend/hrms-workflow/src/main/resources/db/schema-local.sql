-- ============================================================================
-- HR11 Workflow Service - Local Schema (H2)
-- 對應 Entity: WorkflowDefinitionEntity, WorkflowInstanceEntity,
--             ApprovalTaskEntity, UserDelegationEntity, DelegationEntity
-- ============================================================================

-- 流程定義
CREATE TABLE IF NOT EXISTS workflow_definitions (
    definition_id VARCHAR(36) PRIMARY KEY,
    flow_name VARCHAR(200) NOT NULL,
    flow_type VARCHAR(50),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    default_due_days INT,
    nodes_json TEXT,
    edges_json TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    published_at TIMESTAMP
);

-- 流程實例
CREATE TABLE IF NOT EXISTS workflow_instances (
    instance_id VARCHAR(36) PRIMARY KEY,
    definition_id VARCHAR(36),
    flow_type VARCHAR(50),
    business_type VARCHAR(50),
    business_id VARCHAR(50),
    business_url VARCHAR(500),
    applicant_id VARCHAR(100),
    applicant_name VARCHAR(100),
    department_id VARCHAR(100),
    department_name VARCHAR(100),
    summary VARCHAR(500),
    variables_json TEXT,
    status VARCHAR(20) NOT NULL,
    current_node_id VARCHAR(100),
    current_node_name VARCHAR(100),
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- 審核任務
CREATE TABLE IF NOT EXISTS workflow_approval_tasks (
    task_id VARCHAR(36) PRIMARY KEY,
    instance_id VARCHAR(36),
    node_id VARCHAR(100),
    node_name VARCHAR(100),
    assignee_id VARCHAR(100),
    assignee_name VARCHAR(100),
    delegated_to_id VARCHAR(100),
    delegated_to_name VARCHAR(100),
    approver_id VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    comments TEXT,
    due_date TIMESTAMP,
    is_overdue BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_task_instance FOREIGN KEY (instance_id) REFERENCES workflow_instances(instance_id)
);

-- 用戶代理設定
CREATE TABLE IF NOT EXISTS workflow_user_delegations (
    delegation_id VARCHAR(36) PRIMARY KEY,
    delegator_id VARCHAR(100) NOT NULL,
    delegate_id VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    delegation_scope VARCHAR(20),
    specific_flow_types VARCHAR(500),
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

-- 簡化版代理 (stub)
CREATE TABLE IF NOT EXISTS hrms_wf_delegation (
    delegation_id VARCHAR(36) PRIMARY KEY,
    applicant_id VARCHAR(100),
    delegee_id VARCHAR(100)
);
