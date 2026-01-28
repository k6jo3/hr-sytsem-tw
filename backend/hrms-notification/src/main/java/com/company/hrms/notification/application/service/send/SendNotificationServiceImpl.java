package com.company.hrms.notification.application.service.send;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.application.service.send.task.CheckQuietHoursTask;
import com.company.hrms.notification.application.service.send.task.CreateNotificationTask;
import com.company.hrms.notification.application.service.send.task.FilterChannelsTask;
import com.company.hrms.notification.application.service.send.task.LoadPreferenceTask;
import com.company.hrms.notification.application.service.send.task.LoadTemplateTask;
import com.company.hrms.notification.application.service.send.task.PublishEventTask;
import com.company.hrms.notification.application.service.send.task.RenderContentTask;
import com.company.hrms.notification.application.service.send.task.SaveNotificationTask;
import com.company.hrms.notification.application.service.send.task.SendEmailTask;
import com.company.hrms.notification.application.service.send.task.SendInAppTask;
import com.company.hrms.notification.application.service.send.task.SendLineTask;
import com.company.hrms.notification.application.service.send.task.SendPushTask;
import com.company.hrms.notification.application.service.send.task.SendTeamsTask;
import com.company.hrms.notification.application.service.send.task.UpdateStatusTask;
import com.company.hrms.notification.domain.model.aggregate.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發送通知 Application Service
 * <p>
 * 使用 Business Pipeline 模式編排通知發送流程
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Service("sendNotificationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SendNotificationServiceImpl
                implements CommandApiService<SendNotificationRequest, SendNotificationResponse> {

        // ==================== Infrastructure Tasks ====================
        private final LoadTemplateTask loadTemplateTask;
        private final LoadPreferenceTask loadPreferenceTask;
        private final SaveNotificationTask saveNotificationTask;
        private final PublishEventTask publishEventTask;

        // ==================== Domain Tasks ====================
        private final RenderContentTask renderContentTask;
        private final FilterChannelsTask filterChannelsTask;
        private final CheckQuietHoursTask checkQuietHoursTask;
        private final CreateNotificationTask createNotificationTask;
        private final UpdateStatusTask updateStatusTask;

        // ==================== Integration Tasks ====================
        private final SendInAppTask sendInAppTask;
        private final SendEmailTask sendEmailTask;
        private final SendPushTask sendPushTask;
        private final SendTeamsTask sendTeamsTask;
        private final SendLineTask sendLineTask;

        @Override
        public SendNotificationResponse execCommand(
                        SendNotificationRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                log.info("[SendNotificationService] 開始發送通知 - 收件人: {}, 類型: {}",
                                request.getRecipientId(),
                                request.getNotificationType());

                // 1. 建立 Pipeline Context
                SendNotificationContext ctx = new SendNotificationContext(request, currentUser);

                // 2. 執行 Business Pipeline（14 步驟）
                BusinessPipeline.start(ctx)
                                // Step 1-2: 載入資料
                                .nextIf(c -> c.hasTemplateCode(), loadTemplateTask) // 條件式：若有範本代碼才載入
                                .next(renderContentTask) // 渲染內容（範本或直接內容）
                                .next(loadPreferenceTask) // 載入偏好設定

                                // Step 3-5: 過濾與檢查
                                .next(filterChannelsTask) // 過濾禁用渠道
                                .nextIf(c -> !c.isUrgent(), checkQuietHoursTask) // 條件式：非 URGENT 才檢查靜音時段
                                .next(createNotificationTask) // 建立通知聚合根

                                // Step 6-10: 發送渠道（條件式執行）
                                .nextIf(c -> !c.isShouldDelay() && c.hasChannel("IN_APP"), sendInAppTask)
                                .nextIf(c -> !c.isShouldDelay() && c.hasChannel("EMAIL"), sendEmailTask)
                                .nextIf(c -> !c.isShouldDelay() && c.hasChannel("PUSH"), sendPushTask)
                                .nextIf(c -> !c.isShouldDelay() && c.hasChannel("TEAMS"), sendTeamsTask)
                                .nextIf(c -> !c.isShouldDelay() && c.hasChannel("LINE"), sendLineTask)

                                // Step 11-14: 更新與儲存
                                .next(updateStatusTask) // 更新發送狀態
                                .next(saveNotificationTask) // 儲存通知記錄
                                .next(publishEventTask) // 發布領域事件
                                .execute();

                // 3. 組裝回應
                SendNotificationResponse response = buildResponse(ctx);

                log.info("[SendNotificationService] 通知發送完成 - 通知ID: {}, 狀態: {}",
                                response.getNotificationId(),
                                response.getStatus());

                return response;
        }

        /**
         * 組裝回應物件
         *
         * @param ctx Pipeline Context
         * @return SendNotificationResponse
         */
        private SendNotificationResponse buildResponse(SendNotificationContext ctx) {
                Notification notification = ctx.getNotification();

                return SendNotificationResponse.builder()
                                .notificationId(notification.getId().getValue())
                                .recipientId(notification.getRecipientId())
                                .title(notification.getTitle())
                                .status(ctx.getFinalStatus())
                                .channels(notification.getChannels() != null
                                                ? notification.getChannels().stream().map(Enum::name)
                                                                .collect(Collectors.toList())
                                                : List.of())
                                .sentAt(notification.getSentAt())
                                .businessUrl(notification.getRelatedBusinessUrl())
                                .channelResults(ctx.getChannelResults() != null
                                                ? ctx.getChannelResults().values().stream().collect(Collectors.toList())
                                                : List.of())
                                .build();
        }
}
