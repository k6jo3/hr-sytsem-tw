package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.UpdateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.UpdateWorkflowDefinitionResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 更新流程定義服務
 * PUT /api/v1/workflows/definitions/{id}
 * 
 * 業務規則：
 * 1. 只能更新 DRAFT 或 INACTIVE 狀態的流程定義
 * 2. 不能修改已發布 (ACTIVE) 的流程定義
 */
@Service("updateWorkflowDefinitionServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateWorkflowDefinitionServiceImpl
        implements CommandApiService<UpdateWorkflowDefinitionRequest, UpdateWorkflowDefinitionResponse> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public UpdateWorkflowDefinitionResponse execCommand(
            UpdateWorkflowDefinitionRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 從 args[0] 取得 definitionId
        String definitionId = args.length > 0 ? args[0] : request.getDefinitionId();

        if (definitionId == null || definitionId.isBlank()) {
            throw new IllegalArgumentException("流程定義ID不可為空");
        }

        // 查詢流程定義
        WorkflowDefinition definition = definitionRepository
                .findById(new WorkflowDefinitionId(definitionId))
                .orElseThrow(() -> new IllegalArgumentException("流程定義不存在: " + definitionId));

        // 驗證狀態：只能更新 DRAFT 或 INACTIVE
        if (definition.getStatus() == DefinitionStatus.ACTIVE) {
            throw new IllegalStateException("無法修改已發布的流程定義，請先停用");
        }

        // 更新屬性
        if (request.getFlowName() != null) {
            definition.setFlowName(request.getFlowName());
        }
        if (request.getDescription() != null) {
            definition.setDescription(request.getDescription());
        }
        if (request.getNodes() != null) {
            definition.setNodes(request.getNodes());
        }
        if (request.getEdges() != null) {
            definition.setEdges(request.getEdges());
        }
        if (request.getDefaultDueDays() != null) {
            definition.setDefaultDueDays(request.getDefaultDueDays());
        }

        // 設定更新資訊
        definition.setUpdatedBy(currentUser.getUserId());

        // 保存
        definitionRepository.save(definition);

        // 組裝回應
        UpdateWorkflowDefinitionResponse response = new UpdateWorkflowDefinitionResponse();
        response.setDefinitionId(definition.getDefinitionId());
        response.setFlowName(definition.getFlowName());
        response.setFlowType(definition.getFlowType() != null ? definition.getFlowType().name() : null);
        response.setVersion(definition.getVersion());
        // updatedAt 由 Repository 層處理，設為當前時間
        response.setUpdatedAt(java.time.LocalDateTime.now());

        return response;
    }
}
