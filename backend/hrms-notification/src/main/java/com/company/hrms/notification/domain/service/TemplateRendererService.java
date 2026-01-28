package com.company.hrms.notification.domain.service;

import java.util.Map;

import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;

/**
 * 範本渲染領域服務
 * <p>
 * 負責處理通知範本的變數替換邏輯
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@org.springframework.stereotype.Service
public class TemplateRendererService {

    /**
     * 渲染範本內容
     * <p>
     * 將範本中的變數 {{variableName}} 替換為實際值
     * </p>
     *
     * @param template  通知範本
     * @param variables 變數映射表
     * @return 渲染後的內容
     */
    public String renderContent(NotificationTemplate template, Map<String, Object> variables) {
        if (template == null) {
            throw new IllegalArgumentException("範本不可為空");
        }

        // 委派給聚合根執行
        return template.renderContent(variables);
    }

    /**
     * 渲染範本主旨
     * <p>
     * 將主旨中的變數 {{variableName}} 替換為實際值
     * </p>
     *
     * @param template  通知範本
     * @param variables 變數映射表
     * @return 渲染後的主旨
     */
    public String renderSubject(NotificationTemplate template, Map<String, Object> variables) {
        if (template == null) {
            throw new IllegalArgumentException("範本不可為空");
        }

        // 委派給聚合根執行
        return template.renderSubject(variables);
    }

    /**
     * 驗證範本變數完整性
     * <p>
     * 檢查範本中的所有變數是否都有提供值
     * </p>
     *
     * @param template  通知範本
     * @param variables 變數映射表
     * @return true 表示所有變數都有提供
     */
    public boolean validateVariables(NotificationTemplate template, Map<String, Object> variables) {
        if (template == null || template.getBody() == null) {
            return false;
        }

        // 提取範本中的所有變數名稱
        String body = template.getBody();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{\\{([^}]+)\\}\\}");
        java.util.regex.Matcher matcher = pattern.matcher(body);

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            if (variables == null || !variables.containsKey(variableName)) {
                return false;
            }
        }

        return true;
    }
}
