package com.company.hrms.notification.application.service.template;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.template.UpdateNotificationTemplateRequest;
import com.company.hrms.notification.api.response.template.CreateTemplateResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更新通知範本 Application Service
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("updateNotificationTemplateServiceImpl")
@Transactional
public class UpdateNotificationTemplateServiceImpl
        implements CommandApiService<UpdateNotificationTemplateRequest, CreateTemplateResponse> {

    private final INotificationTemplateRepository templateRepository;

    public UpdateNotificationTemplateServiceImpl(INotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public CreateTemplateResponse execCommand(
            UpdateNotificationTemplateRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得範本 ID
        String templateId = args[0];

        // 2. 查詢範本
        NotificationTemplate template = templateRepository.findById(TemplateId.of(templateId))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("範本 [%s] 不存在", templateId)
                ));

        // 3. 更新範本欄位
        if (request.getTemplateName() != null) {
            template.setName(request.getTemplateName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getSubject() != null) {
            template.setSubject(request.getSubject());
        }
        if (request.getBody() != null) {
            template.setBody(request.getBody());
        }
        if (request.getDefaultPriority() != null) {
            template.setDefaultPriority(NotificationPriority.valueOf(request.getDefaultPriority()));
        }
        if (request.getDefaultChannels() != null) {
            List<NotificationChannel> channels = request.getDefaultChannels().stream()
                    .map(NotificationChannel::valueOf)
                    .collect(Collectors.toList());
            template.setDefaultChannels(channels);
        }
        if (request.getVariables() != null) {
            template.setVariables(request.getVariables());
        }
        if (request.getIsActive() != null) {
            if (request.getIsActive()) {
                template.activate();
            } else {
                template.deactivate();
            }
        }

        // 設定更新者
        template.setUpdatedBy(currentUser.getUserId());

        // 4. 儲存範本
        NotificationTemplate savedTemplate = templateRepository.save(template);

        // 5. 組裝回應
        return CreateTemplateResponse.builder()
                .templateId(savedTemplate.getId().getValue())
                .templateCode(savedTemplate.getTemplateCode())
                .templateName(savedTemplate.getName())
                .isActive(savedTemplate.isActive())
                .createdAt(savedTemplate.getCreatedAt())
                .build();
    }
}
