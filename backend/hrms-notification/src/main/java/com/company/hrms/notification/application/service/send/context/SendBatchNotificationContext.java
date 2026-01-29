package com.company.hrms.notification.application.service.send.context;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 批次發送通知上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SendBatchNotificationContext extends PipelineContext {

    // === 輸入 ===
    private final SendBatchNotificationRequest request;

    // === 中間數據 ===
    private List<String> recipientIds = new ArrayList<>();
    private List<SendBatchNotificationResponse.BatchResult> results = new ArrayList<>();
    private List<SendBatchNotificationResponse.FailedRecipient> failedRecipients = new ArrayList<>();

    // === 輸出 ===
    private int totalRecipients;
    private int successCount;
    private int failureCount;

    public SendBatchNotificationContext(SendBatchNotificationRequest request) {
        this.request = request;
    }
}
