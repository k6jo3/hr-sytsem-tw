package com.company.hrms.iam.application.service.system;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.system.UpdateScheduledJobRequest;
import com.company.hrms.iam.api.response.system.ScheduledJobConfigResponse;
import com.company.hrms.iam.domain.model.aggregate.ScheduledJobConfig;
import com.company.hrms.iam.domain.repository.IScheduledJobConfigRepository;

import lombok.RequiredArgsConstructor;

/**
 * 更新排程任務配置 Application Service
 * 對應 Controller 方法：updateScheduledJob
 *
 * 流程：
 * 1. 根據 jobCode 查詢排程配置
 * 2. 更新 Cron 表達式和啟用狀態
 * 3. 持久化更新
 * 4. 回傳更新後的配置
 */
@Service("updateScheduledJobServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateScheduledJobServiceImpl
        implements CommandApiService<UpdateScheduledJobRequest, ScheduledJobConfigResponse> {

    private final IScheduledJobConfigRepository repository;

    @Override
    public ScheduledJobConfigResponse execCommand(
            UpdateScheduledJobRequest request, JWTModel currentUser, String... args) throws Exception {

        String jobCode = args[0];
        String operator = currentUser.getUsername();

        // 1. 查詢排程配置
        ScheduledJobConfig job = repository.findByJobCode(jobCode)
                .orElseThrow(() -> new IllegalArgumentException("排程任務不存在: " + jobCode));

        // 2. Domain 層更新
        if (request.getCronExpression() != null) {
            job.updateCron(request.getCronExpression(), operator);
        }
        if (request.getEnabled() != null) {
            if (request.getEnabled()) {
                job.enable(operator);
            } else {
                job.disable(operator);
            }
        }

        // 3. 持久化
        repository.update(job);

        // 4. 組裝回應
        return ScheduledJobConfigResponse.builder()
                .jobCode(job.getJobCode())
                .jobName(job.getJobName())
                .module(job.getModule())
                .cronExpression(job.getCronExpression())
                .enabled(job.isEnabled())
                .description(job.getDescription())
                .lastExecutedAt(job.getLastExecutedAt())
                .lastExecutionStatus(job.getLastExecutionStatus())
                .lastErrorMessage(job.getLastErrorMessage())
                .consecutiveFailures(job.getConsecutiveFailures())
                .updatedAt(job.getUpdatedAt())
                .updatedBy(job.getUpdatedBy())
                .build();
    }
}
