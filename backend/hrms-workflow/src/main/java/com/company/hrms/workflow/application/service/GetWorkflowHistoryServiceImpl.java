package com.company.hrms.workflow.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowHistoryRequest;
import com.company.hrms.workflow.api.response.TaskHistoryDTO;
import com.company.hrms.workflow.api.response.WorkflowHistoryResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: 查詢流程歷史
 */
@Service("getWorkflowHistoryServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWorkflowHistoryServiceImpl
        implements QueryApiService<GetWorkflowHistoryRequest, WorkflowHistoryResponse> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public WorkflowHistoryResponse getResponse(GetWorkflowHistoryRequest request, JWTModel currentUser,
            String... args) {

        String instanceId = request.getInstanceId();

        WorkflowInstance instance = instanceRepository.findById(new WorkflowInstanceId(instanceId))
                .orElseThrow(() -> new IllegalArgumentException("找不到流程實例: " + instanceId));

        // Create Task History
        List<TaskHistoryDTO> taskHistory = instance.getTasks().stream()
                .sorted(Comparator.comparing(ApprovalTask::getCreatedAt))
                .map(this::toTaskDTO)
                .collect(Collectors.toList());

        return WorkflowHistoryResponse.builder()
                .instanceId(instance.getInstanceId())
                .definitionId(instance.getDefinitionId())
                .applicantId(instance.getApplicantId())
                .applicantName(instance.getApplicantName())
                .summary(instance.getSummary())
                .status(instance.getStatus())
                .startedAt(instance.getStartedAt())
                .completedAt(instance.getCompletedAt())
                .tasks(taskHistory)
                .build();
    }

    private TaskHistoryDTO toTaskDTO(ApprovalTask task) {
        return TaskHistoryDTO.builder()
                .taskId(task.getTaskId())
                .nodeName(task.getNodeName())
                .approverId(task.getApproverId())
                // .approverName(task.getApproverName()) // Domain entity might miss name if not
                // joined
                // For now, assume approverId is sufficient or we need to fetch name separately?
                // ApprovalTaskEntity has 'assigneeName' but 'approverId' might be different
                // from assignee.
                // Does ApprovalTask (Domain) have approverName? Check Aggregate.
                .status(task.getStatus().name())
                .comment(task.getComment())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getApprovedAt())
                .build();
    }
}
