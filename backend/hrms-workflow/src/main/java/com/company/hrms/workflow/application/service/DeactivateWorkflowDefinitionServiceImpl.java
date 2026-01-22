package com.company.hrms.workflow.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.DeactivateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.DeactivateWorkflowDefinitionResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 停用流程定義服務
 * PUT /api/v1/workflows/definitions/{id}/deactivate
 * 
 * 業務規則：
 * 1. 只能停用 ACTIVE 狀態的流程定義
 * 2. 停用不影響已啟動的流程實例
 * 
 * 注意：此為簡單服務（單步驟），不需使用 Pipeline 模式
 */
@Service("deactivateWorkflowDefinitionServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DeactivateWorkflowDefinitionServiceImpl
        implements CommandApiService<DeactivateWorkflowDefinitionRequest, DeactivateWorkflowDefinitionResponse> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public DeactivateWorkflowDefinitionResponse execCommand(
            DeactivateWorkflowDefinitionRequest request,
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

        // 驗證狀態：只能停用 ACTIVE 狀態
        if (definition.getStatus() != DefinitionStatus.ACTIVE) {
            throw new IllegalStateException("只能停用已發布的流程定義，當前狀態: " + definition.getStatus());
        }

        // 執行停用（呼叫 Domain 方法）
        definition.deactivate();

        // 保存
        definitionRepository.save(definition);

        // 組裝回應
        DeactivateWorkflowDefinitionResponse response = new DeactivateWorkflowDefinitionResponse();
        response.setDefinitionId(definition.getDefinitionId());
        response.setStatus(definition.getStatus().name());
        response.setDeactivatedAt(LocalDateTime.now());

        return response;
    }
}
