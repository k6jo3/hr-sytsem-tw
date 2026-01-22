package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowDefinitionDetailRequest;
import com.company.hrms.workflow.api.response.WorkflowDefinitionDetailResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢流程定義詳情服務
 * GET /api/v1/workflows/definitions/{id}
 */
@Service("getWorkflowDefinitionDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWorkflowDefinitionDetailServiceImpl
        implements QueryApiService<GetWorkflowDefinitionDetailRequest, WorkflowDefinitionDetailResponse> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public WorkflowDefinitionDetailResponse getResponse(
            GetWorkflowDefinitionDetailRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 從 args[0] 取得 definitionId (Controller 傳入)
        String definitionId = args.length > 0 ? args[0] : request.getDefinitionId();

        if (definitionId == null || definitionId.isBlank()) {
            throw new IllegalArgumentException("流程定義ID不可為空");
        }

        // 查詢流程定義
        WorkflowDefinition definition = definitionRepository
                .findById(new WorkflowDefinitionId(definitionId))
                .orElseThrow(() -> new IllegalArgumentException("流程定義不存在: " + definitionId));

        // 轉換為回應
        return toResponse(definition);
    }

    /**
     * 將 Domain 物件轉換為 Response DTO
     */
    private WorkflowDefinitionDetailResponse toResponse(WorkflowDefinition definition) {
        WorkflowDefinitionDetailResponse response = new WorkflowDefinitionDetailResponse();

        response.setDefinitionId(definition.getDefinitionId());
        response.setFlowName(definition.getFlowName());
        response.setFlowType(definition.getFlowType() != null ? definition.getFlowType().name() : null);
        response.setDescription(definition.getDescription());
        response.setStatus(definition.getStatus() != null ? definition.getStatus().name() : null);
        response.setVersion(definition.getVersion());
        response.setNodes(definition.getNodes());
        response.setEdges(definition.getEdges());
        response.setDefaultDueDays(definition.getDefaultDueDays());
        response.setCreatedAt(definition.getCreatedAt());
        response.setCreatedBy(definition.getCreatedBy());
        response.setUpdatedAt(definition.getUpdatedAt());
        response.setUpdatedBy(definition.getUpdatedBy());
        response.setPublishedAt(definition.getPublishedAt());

        return response;
    }
}
