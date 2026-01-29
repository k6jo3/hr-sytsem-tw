package com.company.hrms.notification.application.service.send.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendBatchNotificationContext;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeListResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得批次收件人 ID 列表 Task
 * (目前為基本實作，待整合 Organization Service)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetBatchRecipientIdsTask implements PipelineTask<SendBatchNotificationContext> {

    private final OrganizationServiceClient organizationServiceClient;

    @Override
    public void execute(SendBatchNotificationContext context) {
        var request = context.getRequest();

        // 1. 優先使用直接指定的收件人列表
        if (request.getRecipientIds() != null && !request.getRecipientIds().isEmpty()) {
            List<String> validIds = new ArrayList<>();
            for (String id : request.getRecipientIds()) {
                if (id != null && !id.trim().isEmpty()) {
                    validIds.add(id);
                } else {
                    context.getFailedRecipients().add(
                            com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse.FailedRecipient
                                    .builder()
                                    .recipientId(id)
                                    .reason("收件人 ID 為空或無效")
                                    .build());
                }
            }
            context.setRecipientIds(validIds);
            return;
        }

        // 2. 使用過濾條件查詢 (整合 Organization Service)
        if (request.getRecipientFilter() != null) {
            var filter = request.getRecipientFilter();
            log.info("[GetBatchRecipientIdsTask] 使用 RecipientFilter 查詢收件人: {}", filter);

            try {
                EmployeeListResponseDto response = organizationServiceClient.getEmployeeList(
                        filter.getEmployeeStatuses(),
                        filter.getDepartmentIds(),
                        0,
                        1000);

                if (response != null && response.getContent() != null) {
                    List<String> ids = response.getContent().stream()
                            .map(EmployeeDto::getEmployeeId)
                            .collect(java.util.stream.Collectors.toList());
                    context.setRecipientIds(ids);
                    log.info("[GetBatchRecipientIdsTask] 查詢到 {} 位符合條件的收件人", ids.size());
                } else {
                    context.setRecipientIds(new ArrayList<>());
                }
            } catch (Exception e) {
                log.error("[GetBatchRecipientIdsTask] 查詢 Organization Service 失敗: {}", e.getMessage());
                context.setRecipientIds(new ArrayList<>());
            }
            return;
        }

        context.setRecipientIds(new ArrayList<>());
    }
}
