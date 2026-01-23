package com.company.hrms.notification.application.service.template;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.template.TemplateDetailResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢通知範本詳情 Application Service
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("getNotificationTemplateDetailServiceImpl")
public class GetNotificationTemplateDetailServiceImpl
        implements QueryApiService<Void, TemplateDetailResponse> {

    private final INotificationTemplateRepository templateRepository;

    public GetNotificationTemplateDetailServiceImpl(INotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public TemplateDetailResponse getResponse(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得範本 ID
        String templateId = args[0];

        // 2. 查詢範本
        NotificationTemplate template = templateRepository.findById(TemplateId.of(templateId))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("範本 [%s] 不存在", templateId)
                ));

        // 3. 組裝回應
        List<String> channelNames = template.getDefaultChannels().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return TemplateDetailResponse.builder()
                .templateId(template.getId().getValue())
                .templateCode(template.getTemplateCode())
                .templateName(template.getName())
                .description(template.getDescription())
                .subject(template.getSubject())
                .body(template.getBody())
                .notificationType(template.getNotificationType().name())
                .defaultPriority(template.getDefaultPriority().name())
                .defaultChannels(channelNames)
                .variables(template.getVariables())
                .isActive(template.isActive())
                .createdAt(template.getCreatedAt())
                .createdBy(template.getCreatedBy())
                .updatedAt(template.getUpdatedAt())
                .updatedBy(template.getUpdatedBy())
                .build();
    }
}
