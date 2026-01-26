package com.company.hrms.notification.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
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
     * 範本描述
     */
    private String description;

    /**
     * 通知類型
     */
    private NotificationType notificationType;

    /**
     * 預設優先級
     */
    private NotificationPriority defaultPriority;

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
     * 範本變數（從 body 中提取）
     */
    private Map<String, String> variables;

    /**
     * 是否啟用
     */
    private boolean isActive;

    /**
     * 建立者
     */
    private String createdBy;

    /**
     * 最後更新者
     */
    private String updatedBy;

    /**
     * 版本號 (樂觀鎖)
     */
    private Long version;

    /**
     * 軟刪除標記
     */
    private Boolean isDeleted;

    /**
     * 變數替換的正則表達式模式 {{variableName}}
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * 私有建構子，強制使用 Factory Method
     */
    public NotificationTemplate(TemplateId id) {
        super(id);
    }

    /**
     * 建立通知範本 (Factory Method)
     *
     * @param templateCode     範本代碼
     * @param templateName     範本名稱
     * @param subject          主旨
     * @param body             內容範本
     * @param notificationType 通知類型
     * @param defaultPriority  預設優先級
     * @param defaultChannels  預設渠道
     * @return NotificationTemplate 實例
     */
    public static NotificationTemplate create(
            String templateCode,
            String templateName,
            String subject,
            String body,
            NotificationType notificationType,
            NotificationPriority defaultPriority,
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
        template.notificationType = notificationType;
        template.defaultPriority = defaultPriority != null ? defaultPriority : NotificationPriority.NORMAL;
        template.defaultChannels = (defaultChannels != null && !defaultChannels.isEmpty())
                ? List.copyOf(defaultChannels)
                : List.of(NotificationChannel.IN_APP);
        template.isActive = true;
        template.isDeleted = false;

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

    public String getName() {
        return templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getDescription() {
        return description;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public NotificationPriority getDefaultPriority() {
        return defaultPriority;
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

    public Map<String, String> getVariables() {
        return variables;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    // ========== Setters (for Mapper) ==========

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public void setName(String name) {
        this.templateName = name;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setDefaultPriority(NotificationPriority defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDefaultChannels(List<NotificationChannel> defaultChannels) {
        this.defaultChannels = defaultChannels;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
