package com.company.hrms.notification.application.service.send;

import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 批次發送通知 Service
 * <p>
 * 職責：
 * <ol>
 *     <li>取得收件人列表（直接指定或查詢）</li>
 *     <li>驗證收件人數量（上限 500 人）</li>
 *     <li>分批處理（每批 50 人）</li>
 *     <li>並行發送通知</li>
 *     <li>彙總發送結果</li>
 * </ol>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Service("sendBatchNotificationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SendBatchNotificationServiceImpl
        implements CommandApiService<SendBatchNotificationRequest, SendBatchNotificationResponse> {

    private static final int MAX_RECIPIENTS = 500;
    private static final int BATCH_SIZE = 50;

    private final SendNotificationServiceImpl sendNotificationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public SendBatchNotificationResponse execCommand(
            SendBatchNotificationRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        log.info("[SendBatchNotification] 開始批次發送通知 - 通知類型: {}", request.getNotificationType());

        // Step 1: 取得收件人列表
        List<String> recipientIds = getRecipientIds(request);

        // Step 2: 驗證收件人數量
        if (recipientIds.isEmpty()) {
            throw new IllegalArgumentException("收件人列表不可為空");
        }
        if (recipientIds.size() > MAX_RECIPIENTS) {
            throw new IllegalArgumentException(
                    String.format("收件人數量超過上限（最多 %d 人，實際 %d 人）",
                            MAX_RECIPIENTS, recipientIds.size())
            );
        }

        log.info("[SendBatchNotification] 收件人數量: {}", recipientIds.size());

        // Step 3: 分批處理並並行發送
        List<SendBatchNotificationResponse.BatchResult> results = new ArrayList<>();
        List<SendBatchNotificationResponse.FailedRecipient> failedRecipients = new ArrayList<>();

        // 分批
        List<List<String>> batches = partitionList(recipientIds, BATCH_SIZE);
        log.info("[SendBatchNotification] 分批數量: {} 批（每批 {} 人）", batches.size(), BATCH_SIZE);

        // 並行處理每一批
        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            log.debug("[SendBatchNotification] 處理第 {} 批，共 {} 人", i + 1, batch.size());

            List<CompletableFuture<Void>> futures = batch.stream()
                    .map(recipientId -> CompletableFuture.runAsync(() -> {
                        try {
                            // 建立單一發送請求
                            SendNotificationRequest singleRequest = buildSingleRequest(request, recipientId);

                            // 發送通知
                            SendNotificationResponse response = sendNotificationService.execCommand(
                                    singleRequest, currentUser
                            );

                            // 記錄成功結果
                            synchronized (results) {
                                results.add(SendBatchNotificationResponse.BatchResult.builder()
                                        .recipientId(recipientId)
                                        .notificationId(response.getNotificationId())
                                        .status(response.getStatus())
                                        .build());
                            }

                            log.debug("[SendBatchNotification] 收件人 {} 發送成功 - 通知ID: {}",
                                    recipientId, response.getNotificationId());

                        } catch (Exception e) {
                            // 記錄失敗結果
                            synchronized (failedRecipients) {
                                failedRecipients.add(SendBatchNotificationResponse.FailedRecipient.builder()
                                        .recipientId(recipientId)
                                        .reason(e.getMessage())
                                        .build());
                            }

                            log.warn("[SendBatchNotification] 收件人 {} 發送失敗: {}",
                                    recipientId, e.getMessage());
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            // 等待當前批次完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        // Step 4: 彙總結果
        int successCount = results.size();
        int failureCount = failedRecipients.size();

        SendBatchNotificationResponse response = SendBatchNotificationResponse.builder()
                .totalRecipients(recipientIds.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .failedRecipients(failedRecipients)
                .build();

        log.info("[SendBatchNotification] 批次發送完成 - 總計: {}, 成功: {}, 失敗: {}",
                recipientIds.size(), successCount, failureCount);

        return response;
    }

    /**
     * 取得收件人 ID 列表
     *
     * @param request 批次發送請求
     * @return 收件人 ID 列表
     */
    private List<String> getRecipientIds(SendBatchNotificationRequest request) {
        // 優先使用直接指定的收件人列表
        if (request.getRecipientIds() != null && !request.getRecipientIds().isEmpty()) {
            return request.getRecipientIds();
        }

        // 使用過濾條件查詢（TODO: 實作查詢邏輯）
        if (request.getRecipientFilter() != null) {
            log.warn("[SendBatchNotification] RecipientFilter 查詢功能尚未實作，返回空列表");
            // TODO: 整合 Organization Service 查詢員工
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    /**
     * 建立單一發送請求
     *
     * @param batchRequest 批次發送請求
     * @param recipientId  收件人 ID
     * @return 單一發送請求
     */
    private SendNotificationRequest buildSingleRequest(
            SendBatchNotificationRequest batchRequest,
            String recipientId) {

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

    /**
     * 將列表分批
     *
     * @param list      原始列表
     * @param batchSize 批次大小
     * @return 分批後的列表
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}
