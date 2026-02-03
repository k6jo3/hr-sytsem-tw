package com.company.hrms.workflow.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.ApiResponse;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.DelegateTaskRequest;
import com.company.hrms.workflow.api.response.DelegateTaskResponse;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;
import com.company.hrms.workflow.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.workflow.infrastructure.client.organization.dto.EmployeeDto;

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

    private final IApprovalTaskRepository taskRepository;
    private final OrganizationServiceClient organizationServiceClient;

    @Override
    public DelegateTaskResponse execCommand(
            DelegateTaskRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        String taskId = (args != null && args.length > 0) ? args[0] : request.getTaskId();

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("任務ID不可為空");
        }

        if (request.getDelegateToId() == null || request.getDelegateToId().isBlank()) {
            throw new IllegalArgumentException("轉交目標人員ID不可為空");
        }

        // 1. 通過ApprovalTaskRepository查找任務
        ApprovalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("任務不存在: " + taskId));

        // 2. 取得被轉交人姓名 (透過組織服務)
        ApiResponse<EmployeeDto> employeeResponse = organizationServiceClient
                .getEmployeeDetail(request.getDelegateToId());
        if (employeeResponse == null || !employeeResponse.isSuccess() || employeeResponse.getData() == null) {
            throw new IllegalArgumentException("轉交目標人員不存在: " + request.getDelegateToId());
        }
        String delegateToName = employeeResponse.getData().getFullName();

        // 3. 驗證任務狀態與權限 並 執行轉交邏輯
        task.delegate(request.getDelegateToId(), delegateToName, currentUser.getUserId());

        // 4. 保存變更
        taskRepository.save(task);

        // 封裝回應
        DelegateTaskResponse response = new DelegateTaskResponse();
        response.setTaskId(task.getTaskId());
        response.setDelegateToId(task.getDelegatedToId());
        response.setDelegateToName(task.getDelegatedToName());
        response.setDelegatedAt(LocalDateTime.now());

        return response;
    }
}
