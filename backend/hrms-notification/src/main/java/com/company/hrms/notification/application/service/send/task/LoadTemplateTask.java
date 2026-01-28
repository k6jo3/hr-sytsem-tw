package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 載入通知範本 Task
 * <p>
 * 職責：根據 templateCode 載入通知範本
 * </p>
 * <p>
 * Task 類型：Infrastructure Task
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoadTemplateTask implements PipelineTask<SendNotificationContext> {

    private final INotificationTemplateRepository templateRepository;

    @Override
    public void execute(SendNotificationContext ctx) {
        String templateCode = ctx.getRequest().getTemplateCode();

        log.debug("[LoadTemplateTask] 載入通知範本: {}", templateCode);

        // 查詢範本
        NotificationTemplate template = templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("通知範本不存在: %s", templateCode)
                ));

        // 檢查範本是否啟用
        if (!template.isActive()) {
            throw new IllegalStateException(
                    String.format("通知範本未啟用: %s", templateCode)
            );
        }

        ctx.setTemplate(template);
        log.debug("[LoadTemplateTask] 範本載入完成: {}", template.getTemplateName());
    }
}
