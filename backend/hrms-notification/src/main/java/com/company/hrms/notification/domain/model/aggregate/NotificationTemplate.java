package com.company.hrms.notification.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通知範本聚合根
 * <p>
 * 管理通知範本的內容與變數替換邏輯
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationTemplate extends AggregateRoot<TemplateId> {

    /**
     * 範本代碼（唯一識別）
     */
    private String templateCode;

    /**
     * 範本名稱
     */
    private String templateName;

    /**
     * 主旨（用於 Email）
     */
    private String subject;

    /**
     * 內容範本
     */
    private String body;

    /**
     * 預設發送渠道
     */
    private List<NotificationChannel> defaultChannels;

    /**
     * 是否啟用
     */
    private boolean isActive;

    /**
     * 變數替換的正則表達式模式 {{variableName}}
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * 私有建構子，強制使用 Factory Method
     */
    private NotificationTemplate(TemplateId id) {
        super(id);
    }

    /**
     * 建立通知範本 (Factory Method)
     *
     * @param templateCode    範本代碼
     * @param templateName    範本名稱
     * @param subject         主旨
     * @param body            內容範本
     * @param defaultChannels 預設渠道
     * @return NotificationTemplate 實例
     */
    public static NotificationTemplate create(
            String templateCode,
            String templateName,
            String subject,
            String body,
            List<NotificationChannel> defaultChannels) {

        // 驗證必填欄位
        Objects.requireNonNull(templateCode, "範本代碼不可為空");
        Objects.requireNonNull(templateName, "範本名稱不可為空");
        Objects.requireNonNull(body, "範本內容不可為空");

        if (templateCode.trim().isEmpty()) {
            throw new IllegalArgumentException("範本代碼不可為空白");
        }
        if (templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("範本名稱不可為空白");
        }
        if (body.trim().isEmpty()) {
            throw new IllegalArgumentException("範本內容不可為空白");
        }

        // 建立範本實例
        NotificationTemplate template = new NotificationTemplate(TemplateId.generate());
        template.templateCode = templateCode;
        template.templateName = templateName;
        template.subject = subject;
        template.body = body;
        template.defaultChannels = (defaultChannels != null && !defaultChannels.isEmpty())
                ? List.copyOf(defaultChannels)
                : List.of(NotificationChannel.IN_APP);
        template.isActive = true;

        return template;
    }

    /**
     * 更新範本內容
     *
     * @param templateName 範本名稱
     * @param subject      主旨
     * @param body         內容範本
     */
    public void updateContent(String templateName, String subject, String body) {
        if (templateName != null && !templateName.trim().isEmpty()) {
            this.templateName = templateName;
        }
        this.subject = subject;
        if (body != null && !body.trim().isEmpty()) {
            this.body = body;
        }
        touch();
    }

    /**
     * 更新預設渠道
     *
     * @param channels 渠道列表
     */
    public void updateDefaultChannels(List<NotificationChannel> channels) {
        if (channels != null && !channels.isEmpty()) {
            this.defaultChannels = List.copyOf(channels);
            touch();
        }
    }

    /**
     * 啟用範本
     */
    public void activate() {
        this.isActive = true;
        touch();
    }

    /**
     * 停用範本
     */
    public void deactivate() {
        this.isActive = false;
        touch();
    }

    /**
     * 替換範本變數，產生實際內容
     *
     * @param variables 變數映射表
     * @return 替換後的內容
     */
    public String renderContent(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return this.body;
        }

        String rendered = this.body;
        Matcher matcher = VARIABLE_PATTERN.matcher(rendered);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = variables.get(variableName);
            String replacement = value != null ? String.valueOf(value) : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 替換主旨變數
     *
     * @param variables 變數映射表
     * @return 替換後的主旨
     */
    public String renderSubject(Map<String, Object> variables) {
        if (this.subject == null) {
            return null;
        }
        if (variables == null || variables.isEmpty()) {
            return this.subject;
        }

        String rendered = this.subject;
        Matcher matcher = VARIABLE_PATTERN.matcher(rendered);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = variables.get(variableName);
            String replacement = value != null ? String.valueOf(value) : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    // ========== Getters ==========

    public String getTemplateCode() {
        return templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public List<NotificationChannel> getDefaultChannels() {
        return defaultChannels;
    }

    public boolean isActive() {
        return isActive;
    }
}
