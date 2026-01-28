package com.company.hrms.notification.application.service.send.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 發送通知 Pipeline 上下文
 * <p>
 * 遵循 Business_Pipeline 規範：清晰分區 輸入/中間數據/輸出
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SendNotificationContext extends PipelineContext {

    // ==================== 輸入 ====================

    /**
     * 發送通知請求
     */
    private final SendNotificationRequest request;

    /**
     * 當前使用者
     */
    private final JWTModel currentUser;

    // ==================== 中間數據 ====================

    /**
     * 載入的通知範本（條件式：有 templateCode 時才載入）
     */
    private NotificationTemplate template;

    /**
     * 收件人的通知偏好設定
     */
    private NotificationPreference preference;

    /**
     * 過濾後的發送渠道
     */
    private List<String> filteredChannels = new ArrayList<>();

    /**
     * 渲染後的標題
     */
    private String renderedTitle;

    /**
     * 渲染後的內容
     */
    private String renderedContent;

    /**
     * 是否在靜音時段
     */
    private boolean inQuietHours;

    /**
     * 是否應延後發送
     */
    private boolean shouldDelay;

    // ==================== 輸出 ====================

    /**
     * 建立的通知聚合根
     */
    private Notification notification;

    /**
     * 各渠道發送結果
     */
    private Map<String, SendNotificationResponse.ChannelResult> channelResults = new HashMap<>();

    /**
     * 最終發送狀態
     */
    private String finalStatus;

    // ==================== 建構子 ====================

    /**
     * 建構 SendNotificationContext
     *
     * @param request     發送通知請求
     * @param currentUser 當前使用者
     */
    public SendNotificationContext(SendNotificationRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }

    // ==================== 輔助方法 ====================

    /**
     * 檢查是否有指定範本代碼
     *
     * @return true 如果有範本代碼
     */
    public boolean hasTemplateCode() {
        return request.getTemplateCode() != null && !request.getTemplateCode().isBlank();
    }

    /**
     * 檢查是否為緊急通知
     *
     * @return true 如果優先級為 URGENT
     */
    public boolean isUrgent() {
        return "URGENT".equals(request.getPriority());
    }

    /**
     * 檢查是否應發送到指定渠道
     *
     * @param channel 渠道名稱
     * @return true 如果應發送
     */
    public boolean hasChannel(String channel) {
        return filteredChannels != null && filteredChannels.contains(channel);
    }

    /**
     * 新增渠道發送結果
     *
     * @param channel 渠道
     * @param status  發送狀態
     * @param failureReason 失敗原因（可為 null）
     */
    public void addChannelResult(String channel, String status, String failureReason) {
        SendNotificationResponse.ChannelResult result = SendNotificationResponse.ChannelResult.builder()
                .channel(channel)
                .status(status)
                .failureReason(failureReason)
                .build();
        channelResults.put(channel, result);
    }

    /**
     * 新增成功的渠道結果
     *
     * @param channel 渠道
     */
    public void addSuccessResult(String channel) {
        addChannelResult(channel, "SUCCESS", null);
    }

    /**
     * 新增失敗的渠道結果
     *
     * @param channel 渠道
     * @param failureReason 失敗原因
     */
    public void addFailureResult(String channel, String failureReason) {
        addChannelResult(channel, "FAILED", failureReason);
    }
}
