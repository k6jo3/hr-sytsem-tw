package com.company.hrms.notification.application.service.send;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;
import com.company.hrms.notification.application.service.send.context.SendBatchNotificationContext;
import com.company.hrms.notification.application.service.send.task.ExecuteBatchSendTask;
import com.company.hrms.notification.application.service.send.task.GetBatchRecipientIdsTask;
import com.company.hrms.notification.application.service.send.task.ValidateBatchRecipientTask;

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

    private final GetBatchRecipientIdsTask getBatchRecipientIdsTask;
    private final ValidateBatchRecipientTask validateBatchRecipientTask;
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
        BusinessPipeline.start(context)
                .next(getBatchRecipientIdsTask)
                .next(validateBatchRecipientTask)
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
