package com.company.hrms.workflow.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.api.response.PendingTaskResponse;

/**
 * Repository Interface: 審核任務
 */
public interface IApprovalTaskRepository {
    Page<PendingTaskResponse> searchPendingTasks(QueryGroup queryGroup, Pageable pageable);
}
