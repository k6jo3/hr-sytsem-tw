package com.company.hrms.workflow.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.workflow.api.request.StartWorkflowRequest;
import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.application.service.task.CreateWorkflowInstanceTask;
import com.company.hrms.workflow.application.service.task.LoadWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.SaveWorkflowInstanceTask;
import com.company.hrms.workflow.domain.model.enums.FlowType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工作流事件應用服務
 *
 * <p>提供由外部事件（如 Kafka）觸發的流程啟動能力。
 * 內部複用既有的 Pipeline Tasks（LoadWorkflowDefinition -> CreateWorkflowInstance -> SaveWorkflowInstance），
 * 確保與 API 端點啟動流程的邏輯一致。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowEventApplicationService {

    private final LoadWorkflowDefinitionTask loadWorkflowDefinitionTask;
    private final CreateWorkflowInstanceTask createWorkflowInstanceTask;
    private final SaveWorkflowInstanceTask saveWorkflowInstanceTask;

    /**
     * 依據外部事件啟動簽核流程
     *
     * @param flowType     流程類型（如 LEAVE_APPROVAL、OVERTIME_APPROVAL）
     * @param applicantId  申請人 ID
     * @param businessId   業務單據 ID
     * @param businessType 業務單據類型（如 "LEAVE"、"OVERTIME"）
     * @param summary      摘要說明
     * @param variables    流程變數
     * @return 建立的流程實例 ID
     */
    @Transactional
    public String startWorkflowByEvent(FlowType flowType,
                                       String applicantId,
                                       String businessId,
                                       String businessType,
                                       String summary,
                                       Map<String, Object> variables) {

        log.info("[WorkflowEventAppService] 收到事件觸發流程：flowType={}, businessId={}, applicantId={}",
                flowType, businessId, applicantId);

        // 組裝 StartWorkflowRequest
        StartWorkflowRequest request = new StartWorkflowRequest();
        request.setFlowType(flowType);
        request.setApplicantId(applicantId);
        request.setBusinessId(businessId);
        request.setBusinessType(businessType);
        request.setSummary(summary);
        request.setVariables(variables);

        StartWorkflowContext context = new StartWorkflowContext(request);

        try {
            BusinessPipeline.start(context)
                    .next(loadWorkflowDefinitionTask)
                    .next(createWorkflowInstanceTask)
                    .next(saveWorkflowInstanceTask)
                    .execute();

            String instanceId = context.getInstance().getInstanceId();
            log.info("[WorkflowEventAppService] 流程實例建立成功：instanceId={}", instanceId);
            return instanceId;

        } catch (Exception e) {
            log.error("[WorkflowEventAppService] 事件觸發流程建立失敗：flowType={}, businessId={}, 原因={}",
                    flowType, businessId, e.getMessage(), e);
            throw new RuntimeException("事件觸發流程建立失敗: " + e.getMessage(), e);
        }
    }
}
