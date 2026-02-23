package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.timesheet.application.service.context.TimesheetEntryContext;
import com.company.hrms.timesheet.infrastructure.client.ProjectServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j

public class ValidateEntryTask implements PipelineTask<TimesheetEntryContext> {

    private final ProjectServiceClient projectServiceClient;

    @Override
    public void execute(TimesheetEntryContext context) {
        // Example Pre-domain Validation
        if (context.getRequest().getHours().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new DomainException("工時必須大於 0");
        }

        // Validate Project Membership via Feign
        java.util.UUID projectId = context.getRequest().getProjectId();
        try {
            var response = projectServiceClient.getProjectDetail(projectId.toString());
            var body = response.getBody();
            if (body == null) {
                throw new DomainException("專案不存在或無權限存取");
            }
            if (!body.getStatus().equals("IN_PROGRESS")) {
                // Warning or Error? HR usually allows tracking on non-closed projects.
                // But strictly only IN_PROGRESS.
                // Let's assume PLANNING is also allowed? No, usually not.
                // Let's stick to IN_PROGRESS.
                throw new DomainException("專案非進行中狀態，無法申報工時");
            }
        } catch (feign.FeignException.NotFound e) {
            throw new DomainException("專案不存在: " + projectId);
        } catch (Exception e) {
            log.error("Failed to validate project", e);
            throw new DomainException("專案驗證失敗，請稍後再試");
        }
    }
}
