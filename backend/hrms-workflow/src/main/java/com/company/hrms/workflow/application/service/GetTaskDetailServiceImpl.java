package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetTaskDetailRequest;
import com.company.hrms.workflow.api.response.TaskDetailResponse;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢任務詳情服務
 * GET /api/v1/workflows/tasks/{id}
 * 
 * 注意：簡單查詢服務，無需Pipeline
 */
@Service("getTaskDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskDetailServiceImpl
        implements QueryApiService<GetTaskDetailRequest, TaskDetailResponse> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public TaskDetailResponse getResponse(
            GetTaskDetailRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        String taskId = args.length > 0 ? args[0] : request.getTaskId();

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("任務ID不可為空");
        }

        // 需要通過instanceId找到Task，但此處簡化為直接從repository查找
        // 實際應該有一個ApprovalTaskRepository或通過instanceId + taskId查找
        // 這裡暫時拋出未實作異常，等待完整實作
        throw new UnsupportedOperationException("GetTaskDetail需要ApprovalTaskRepository支援，待實作");

        // TODO: 完整實作應該：
        // 1. 通過ApprovalTaskRepository.findById(taskId)取得ApprovalTask
        // 2. 取得關聯的WorkflowInstance資訊
        // 3. 組裝TaskDetailResponse
    }
}
