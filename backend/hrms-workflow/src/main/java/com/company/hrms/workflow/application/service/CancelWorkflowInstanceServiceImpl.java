package com.company.hrms.workflow.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.CancelWorkflowInstanceRequest;
import com.company.hrms.workflow.api.response.CancelWorkflowInstanceResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 取消流程實例服務
 * PUT /api/v1/workflows/instances/{id}/cancel
 * 
 * 業務規則：
 * 1. 只能取消 RUNNING 狀態的流程
 * 2. 只有申請人本人可以取消
 */
@Service("cancelWorkflowInstanceServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CancelWorkflowInstanceServiceImpl
        implements CommandApiService<CancelWorkflowInstanceRequest, CancelWorkflowInstanceResponse> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public CancelWorkflowInstanceResponse execCommand(
            CancelWorkflowInstanceRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 從 args[0] 取得 instanceId
        String instanceId = args.length > 0 ? args[0] : request.getInstanceId();

        if (instanceId == null || instanceId.isBlank()) {
            throw new IllegalArgumentException("流程實例ID不可為空");
        }

        // 查詢流程實例
        WorkflowInstance instance = instanceRepository
                .findById(new WorkflowInstanceId(instanceId))
                .orElseThrow(() -> new IllegalArgumentException("流程實例不存在: " + instanceId));

        // 驗證權限：只有申請人可以取消
        if (!instance.getApplicantId().equals(currentUser.getUserId())) {
            throw new IllegalStateException("只有申請人本人可以取消流程");
        }

        // 驗證狀態：只能取消進行中的流程
        if (instance.getStatus() != InstanceStatus.RUNNING) {
            throw new IllegalStateException("只能取消進行中的流程，當前狀態: " + instance.getStatus());
        }

        // 執行取消
        instance.cancel();

        // 保存
        instanceRepository.save(instance);

        // 組裝回應
        CancelWorkflowInstanceResponse response = new CancelWorkflowInstanceResponse();
        response.setInstanceId(instance.getInstanceId());
        response.setStatus(instance.getStatus().name());
        response.setCancelledAt(LocalDateTime.now());

        return response;
    }
}
