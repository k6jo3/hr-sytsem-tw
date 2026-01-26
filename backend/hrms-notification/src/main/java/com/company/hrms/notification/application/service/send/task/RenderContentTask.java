package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.service.TemplateRendererService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 渲染通知內容 Task
 * <p>
 * 職責：替換範本變數，產生最終通知內容
 * </p>
 * <p>
 * Task 類型：Domain Task
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RenderContentTask implements PipelineTask<SendNotificationContext> {

    private final TemplateRendererService templateRendererService;

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[RenderContentTask] 開始渲染通知內容");

        String title;
        String content;

        // 檢查是否使用範本
        if (ctx.getTemplate() != null) {
            // 使用範本渲染
            NotificationTemplate template = ctx.getTemplate();
            Map<String, Object> variables = ctx.getRequest().getTemplateVariables();

            title = templateRendererService.renderSubject(template, variables);
            content = templateRendererService.renderContent(template, variables);

            log.debug("[RenderContentTask] 使用範本渲染 - 範本: {}", template.getTemplateCode());
        } else {
            // 直接使用請求中的內容
            title = ctx.getRequest().getTitle();
            content = ctx.getRequest().getContent();

            log.debug("[RenderContentTask] 使用直接內容（無範本）");
        }

        // 設定渲染結果
        ctx.setRenderedTitle(title);
        ctx.setRenderedContent(content);

        log.debug("[RenderContentTask] 內容渲染完成 - 標題長度: {}, 內容長度: {}",
                title.length(),
                content != null ? content.length() : 0);
    }
}
