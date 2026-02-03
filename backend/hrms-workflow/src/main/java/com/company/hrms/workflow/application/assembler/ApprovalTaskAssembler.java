package com.company.hrms.workflow.application.assembler;

import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.infrastructure.entity.ApprovalTaskEntity;

public class ApprovalTaskAssembler {

    public static ApprovalTask toDomain(ApprovalTaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return ApprovalTask.builder()
                .taskId(entity.getTaskId())
                .instanceId(entity.getWorkflowInstance() != null ? entity.getWorkflowInstance().getInstanceId() : null)
                .nodeId(entity.getNodeId())
                .nodeName(entity.getNodeName())
                .assigneeId(entity.getAssigneeId())
                .assigneeName(entity.getAssigneeName())
                .delegatedToId(entity.getDelegatedToId())
                .delegatedToName(entity.getDelegatedToName())
                .approverId(entity.getApproverId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .comment(entity.getComments())
                .dueDate(entity.getDueDate())
                .isOverdue(entity.isOverdue())
                .build();
    }

    public static ApprovalTaskEntity toEntity(ApprovalTask domain, ApprovalTaskEntity entity) {
        if (domain == null) {
            return null;
        }
        if (entity == null) {
            entity = new ApprovalTaskEntity();
            entity.setTaskId(domain.getTaskId());
        }

        entity.setNodeId(domain.getNodeId());
        entity.setNodeName(domain.getNodeName());
        entity.setAssigneeId(domain.getAssigneeId());
        entity.setAssigneeName(domain.getAssigneeName());
        entity.setDelegatedToId(domain.getDelegatedToId());
        entity.setDelegatedToName(domain.getDelegatedToName());
        entity.setApproverId(domain.getApproverId());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setComments(domain.getComment());
        entity.setDueDate(domain.getDueDate());
        entity.setOverdue(domain.isOverdue());

        // Note: WorkflowInstance is usually not updated via task delegation
        // but if it's a new entity, we might need a way to set it.
        // However, findById + update logic won't need to change instanceId.

        return entity;
    }
}
