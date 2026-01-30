package com.company.hrms.notification.application.service.template;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.NotificationStatus;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 刪除通知範本 Application Service
 * <p>
 * 業務流程：
 * 1. 檢查範本是否存在
 * 2. 檢查範本是否已被使用（檢查是否有 PENDING 狀態的通知使用此範本）
 * 3. 軟刪除範本
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("deleteNotificationTemplateServiceImpl")
@Transactional
@RequiredArgsConstructor
public class DeleteNotificationTemplateServiceImpl
        implements CommandApiService<Void, Void> {

    private final INotificationTemplateRepository templateRepository;
    private final INotificationRepository notificationRepository;

    @Override
    public Void execCommand(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得範本 ID
        String templateId = args[0];

        // 2. 檢查範本是否存在
        NotificationTemplate template = templateRepository.findById(TemplateId.of(templateId))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("範本 [%s] 不存在", templateId)));

        // 3. 檢查範本是否已被使用 (檢查是否有待發送的通知使用此範本)
        boolean isUsed = notificationRepository.existsByTemplateCodeAndStatus(
                template.getTemplateCode(),
                NotificationStatus.PENDING);

        if (isUsed) {
            throw new IllegalStateException(
                    String.format("範本 [%s] 正被待發送的通知使用，無法刪除", template.getTemplateCode()));
        }

        // 4. 軟刪除範本
        template.setIsDeleted(true);
        template.setUpdatedBy(currentUser.getUserId());
        templateRepository.save(template);

        return null;
    }
}
