package com.company.hrms.iam.application.service.system;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.system.ScheduledJobConfigResponse;
import com.company.hrms.iam.domain.model.aggregate.ScheduledJobConfig;
import com.company.hrms.iam.domain.repository.IScheduledJobConfigRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢排程任務列表 Application Service
 * 對應 Controller 方法：listScheduledJobs
 */
@Service("listScheduledJobsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListScheduledJobsServiceImpl
        implements QueryApiService<Object, List<ScheduledJobConfigResponse>> {

    private final IScheduledJobConfigRepository repository;

    @Override
    public List<ScheduledJobConfigResponse> getResponse(
            Object request, JWTModel currentUser, String... args) throws Exception {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ScheduledJobConfigResponse toResponse(ScheduledJobConfig job) {
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
