package com.company.hrms.workflow.application.service;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowInstanceDetailRequest;
import com.company.hrms.workflow.api.response.TaskHistoryResponse;
import com.company.hrms.workflow.api.response.WorkflowInstanceDetailResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.company.hrms.workflow.infrastructure.repository.WorkflowInstanceQueryRepository;

import lombok.RequiredArgsConstructor;

@Service("getWorkflowInstanceDetailServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWorkflowInstanceDetailServiceImpl
                implements QueryApiService<GetWorkflowInstanceDetailRequest, WorkflowInstanceDetailResponse> {

        private final WorkflowInstanceQueryRepository repository;

        @Override
        public WorkflowInstanceDetailResponse getResponse(GetWorkflowInstanceDetailRequest req, JWTModel currentUser,
                        String... args) throws Exception {

                String instanceId = (args.length > 0) ? args[0] : req.getInstanceId();

                WorkflowInstanceEntity entity = repository
                                .findOne(QueryBuilder.where().eq("instanceId", instanceId).build())
                                .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));

                return WorkflowInstanceDetailResponse.builder()
                                .instanceId(entity.getInstanceId())
                                .definitionId(entity.getDefinitionId())
                                .businessType(entity.getBusinessType())
                                .businessId(entity.getBusinessId())
                                .applicantId(entity.getApplicantId())
                                .applicantName(entity.getApplicantName())
                                .currentNodeName(entity.getCurrentNodeName())
                                .status(entity.getStatus().name())
                                .startedAt(entity.getStartedAt())
                                .completedAt(entity.getCompletedAt())
                                .timeline(entity.getTasks().stream().map(t -> TaskHistoryResponse.builder()
                                                .taskId(t.getTaskId())
                                                .nodeName(t.getNodeName())
                                                .assigneeName(t.getAssigneeName())
                                                .status(t.getStatus().name())
                                                .createdAt(t.getCreatedAt())
                                                .completedAt(t.getApprovedAt()) // Using approvedAt as completedAt logic
                                                .comments(t.getComments())
                                                .build()).collect(Collectors.toList()))
                                .build();
        }
}
