package com.company.hrms.notification.application.service.send;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;
import com.company.hrms.notification.application.service.send.context.SendBatchNotificationContext;
import com.company.hrms.notification.application.service.send.task.ExecuteBatchSendTask;
import com.company.hrms.notification.application.service.send.task.GetBatchRecipientIdsTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 批次發送通知 Service
 * <p>
 * 職責：
 * <ol>
 * <li>取得收件人列表（直接指定或查詢）</li>
 * <li>驗證收件人數量（上限 500 人）</li>
 * <li>分批處理（每批 50 人）</li>
 * <li>並行發送通知</li>
 * <li>彙總發送結果</li>
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

    private final GetBatchRecipientIdsTask getBatchRecipientIdsTask;
    private final ExecuteBatchSendTask executeBatchSendTask;

    @Override
    public SendBatchNotificationResponse execCommand(
            SendBatchNotificationRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        log.info("[SendBatchNotification] 開始批次發送通知 - 通知類型: {}", request.getNotificationType());

        // 1. 建立 Context
        SendBatchNotificationContext context = new SendBatchNotificationContext(request);
        context.setAttribute("currentUser", currentUser);

        // 2. 執行 Pipeline
        com.company.hrms.common.application.pipeline.BusinessPipeline.start(context)
                .next(getBatchRecipientIdsTask)
                .next(ctx -> {
                    // 驗證收件人數量
                    List<String> ids = ctx.getRecipientIds();
                    if (ids.isEmpty()) {
                        throw new IllegalArgumentException("收件人列表不可為空");
                    }
                    if (ids.size() > MAX_RECIPIENTS) {
                        throw new IllegalArgumentException(
                                String.format("收件人數量超過上限（最多 %d 人，實際 %d 人）",
                                        MAX_RECIPIENTS, ids.size()));
                    }
                })
                .next(executeBatchSendTask)
                .execute();

        // 3. 組裝回應
        return SendBatchNotificationResponse.builder()
                .totalRecipients(context.getTotalRecipients())
                .successCount(context.getSuccessCount())
                .failureCount(context.getFailureCount())
                .results(context.getResults())
                .failedRecipients(context.getFailedRecipients())
                .build();
    }
}
