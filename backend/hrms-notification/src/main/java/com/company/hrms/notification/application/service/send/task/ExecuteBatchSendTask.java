package com.company.hrms.notification.application.service.send.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import com.company.hrms.notification.application.service.send.SendNotificationServiceImpl;
import com.company.hrms.notification.application.service.send.context.SendBatchNotificationContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行批次並行發送通知 Task
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecuteBatchSendTask implements PipelineTask<SendBatchNotificationContext> {

    private final SendNotificationServiceImpl sendNotificationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static final int BATCH_SIZE = 50;

    @Override
    public void execute(SendBatchNotificationContext context) throws Exception {
        var request = context.getRequest();
        var recipientIds = context.getRecipientIds();
        var currentUser = context.getAttribute("currentUser"); // 從 Context 取得當前使用者

        if (recipientIds.isEmpty()) {
            return;
        }

        // 分批處理
        List<List<String>> batches = partitionList(recipientIds, BATCH_SIZE);

        for (List<String> batch : batches) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (String recipientId : batch) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        SendNotificationRequest singleRequest = buildSingleRequest(request, recipientId);
                        SendNotificationResponse response = sendNotificationService.execCommand(
                                singleRequest, (com.company.hrms.common.model.JWTModel) currentUser);

                        synchronized (context.getResults()) {
                            context.getResults().add(SendBatchNotificationResponse.BatchResult.builder()
                                    .recipientId(recipientId)
                                    .notificationId(response.getNotificationId())
                                    .status(response.getStatus())
                                    .build());
                        }
                    } catch (Exception e) {
                        synchronized (context.getFailedRecipients()) {
                            context.getFailedRecipients().add(SendBatchNotificationResponse.FailedRecipient.builder()
                                    .recipientId(recipientId)
                                    .reason(e.getMessage())
                                    .build());
                        }
                    }
                }, executorService));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        context.setTotalRecipients(context.getResults().size() + context.getFailedRecipients().size());
        context.setSuccessCount(context.getResults().size());
        context.setFailureCount(context.getFailedRecipients().size());
    }

    private SendNotificationRequest buildSingleRequest(SendBatchNotificationRequest batchRequest, String recipientId) {
        return SendNotificationRequest.builder()
                .recipientId(recipientId)
                .title(batchRequest.getTitle())
                .content(batchRequest.getContent())
                .notificationType(batchRequest.getNotificationType())
                .channels(batchRequest.getChannels())
                .priority(batchRequest.getPriority())
                .templateCode(batchRequest.getTemplateCode())
                .templateVariables(batchRequest.getTemplateVariables())
                .businessType(batchRequest.getBusinessType())
                .businessId(batchRequest.getBusinessId())
                .businessUrl(batchRequest.getBusinessUrl())
                .build();
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}
