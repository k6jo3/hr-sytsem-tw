package com.company.hrms.workflow.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetPendingTasksRequest;
import com.company.hrms.workflow.api.response.PendingTaskResponse;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: 查詢待辦任務
 */
@Service("getPendingTasksServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPendingTasksServiceImpl implements QueryApiService<GetPendingTasksRequest, Page<PendingTaskResponse>> {

    private final IApprovalTaskRepository approvalTaskRepository;

    @Override
    public Page<PendingTaskResponse> getResponse(GetPendingTasksRequest request, JWTModel currentUser, String... args) {

        // Auto-fill userId from token if not present
        if (currentUser != null) {
            // Assume we query by assigneeId which corresponds to EmployeeNumber
            // Need to ensure GetPendingTasksRequest userId field is populated
            // But QueryFilter annotation reads from the field.
            // Unlike CommandRequest, QueryGroup is often just POJO.
            // We set the field here manually.
            request.setUserId(currentUser.getEmployeeNumber());
        }

        // Default to PENDING if not set (though DTO has default)
        if (request.getStatus() == null) {
            request.setStatus("PENDING");
        }

        // Construct Pageable from request if it implements Pageable or use args?
        // QueryApiService signature doesn't pass Pageable directly usually.
        // Assuming args or Request carries page info.
        // Common pattern: Request extends PageDTO or similar.
        // Or we use PageRequest.of(request.getPage(), request.getSize())

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                getOrDefault(request.getPage(), 0),
                getOrDefault(request.getSize(), 20));

        QueryGroup queryGroup = QueryBuilder.where()
                .fromDto(request)
                .build();

        return approvalTaskRepository.searchPendingTasks(queryGroup, pageable);
    }

    private int getOrDefault(Integer val, int def) {
        return val != null ? val : def;
    }
}
