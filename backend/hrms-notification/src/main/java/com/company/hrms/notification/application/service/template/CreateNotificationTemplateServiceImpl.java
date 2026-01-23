package com.company.hrms.notification.application.service.template;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.template.CreateNotificationTemplateRequest;
import com.company.hrms.notification.api.response.template.CreateTemplateResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 建立通知範本 Application Service
 * <p>
 * 業務流程：
 * 1. 檢查範本代碼是否已存在
 * 2. 建立範本聚合根
 * 3. 儲存範本
 * 4. 回傳建立結果
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("createNotificationTemplateServiceImpl")
@Transactional
public class CreateNotificationTemplateServiceImpl
        implements CommandApiService<CreateNotificationTemplateRequest, CreateTemplateResponse> {

    private final INotificationTemplateRepository templateRepository;

    public CreateNotificationTemplateServiceImpl(INotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public CreateTemplateResponse execCommand(
            CreateNotificationTemplateRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 檢查範本代碼是否已存在
        if (templateRepository.existsByTemplateCode(request.getTemplateCode())) {
            throw new IllegalArgumentException(
                    String.format("範本代碼 [%s] 已存在", request.getTemplateCode())
            );
        }

        // 2. 轉換 Request 參數
        NotificationType notificationType = NotificationType.valueOf(request.getNotificationType());

        NotificationPriority defaultPriority = request.getDefaultPriority() != null
                ? NotificationPriority.valueOf(request.getDefaultPriority())
                : NotificationPriority.NORMAL;

        List<NotificationChannel> defaultChannels = request.getDefaultChannels() != null
                ? request.getDefaultChannels().stream()
                .map(NotificationChannel::valueOf)
                .collect(Collectors.toList())
                : List.of(NotificationChannel.IN_APP);

        // 3. 建立範本聚合根
        NotificationTemplate template = NotificationTemplate.create(
                request.getTemplateCode(),
                request.getTemplateName(),
                request.getSubject(),
                request.getBody(),
                notificationType,
                defaultPriority,
                defaultChannels
        );

        // 設定可選欄位
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getVariables() != null) {
            template.setVariables(request.getVariables());
        }

        // 設定審計欄位
        template.setCreatedBy(currentUser.getUserId());
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
