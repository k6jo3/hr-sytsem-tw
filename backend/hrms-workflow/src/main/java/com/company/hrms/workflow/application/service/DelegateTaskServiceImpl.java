package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.DelegateTaskRequest;
import com.company.hrms.workflow.api.response.DelegateTaskResponse;

import lombok.RequiredArgsConstructor;

/**
 * 任務轉交服務
 * PUT /api/v1/workflows/tasks/{id}/delegate
 * 
 * 業務規則:
 * 1. 只能轉交 PENDING 狀態的任務
 * 2. 只有任務負責人可以轉交
 * 
 * 注意：簡單服務，無需Pipeline
 */
@Service("delegateTaskServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DelegateTaskServiceImpl
        implements CommandApiService<DelegateTaskRequest, DelegateTaskResponse> {

    // TODO: 需要 ApprovalTaskRepository

    @Override
    public DelegateTaskResponse execCommand(
            DelegateTaskRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        String taskId = args.length > 0 ? args[0] : request.getTaskId();

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("任務ID不可為空");
        }

        if (request.getDelegateToId() == null || request.getDelegateToId().isBlank()) {
            throw new IllegalArgumentException("轉交目標人員ID不可為空");
        }

        // TODO: 完整實作
        // 1. 通過ApprovalTaskRepository查找任務
        // 2. 驗證任務狀態與權限
        // 3. 執行轉交邏輯
        // 4. 保存變更

        throw new UnsupportedOperationException("DelegateTask需要ApprovalTaskRepository支援，待實作");
    }
}
