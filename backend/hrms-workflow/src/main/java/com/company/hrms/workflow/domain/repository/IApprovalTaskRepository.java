package com.company.hrms.workflow.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.api.response.PendingTaskResponse;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;

/**
 * Repository Interface: 審核任務
 */
public interface IApprovalTaskRepository {
    Page<PendingTaskResponse> searchPendingTasks(QueryGroup queryGroup, Pageable pageable);

    Optional<ApprovalTask> findById(String taskId);

    void save(ApprovalTask task);
}
