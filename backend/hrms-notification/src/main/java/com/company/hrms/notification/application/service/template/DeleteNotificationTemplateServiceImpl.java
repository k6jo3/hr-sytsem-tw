package com.company.hrms.notification.application.service.template;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 刪除通知範本 Application Service
 * <p>
 * 業務流程：
 * 1. 檢查範本是否存在
 * 2. 檢查範本是否已被使用（TODO: 需實作檢查邏輯）
 * 3. 軟刪除範本
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("deleteNotificationTemplateServiceImpl")
@Transactional
public class DeleteNotificationTemplateServiceImpl
        implements CommandApiService<Void, Void> {

    private final INotificationTemplateRepository templateRepository;

    public DeleteNotificationTemplateServiceImpl(INotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Void execCommand(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得範本 ID
        String templateId = args[0];

        // 2. 檢查範本是否存在
        boolean exists = templateRepository.findById(TemplateId.of(templateId)).isPresent();
        if (!exists) {
            throw new IllegalArgumentException(
                    String.format("範本 [%s] 不存在", templateId)
            );
        }

        // 3. TODO: 檢查範本是否已被使用
        // if (templateIsInUse(templateId)) {
        //     throw new IllegalStateException("範本已被使用，無法刪除");
        // }

        // 4. 軟刪除範本
        templateRepository.deleteById(TemplateId.of(templateId));

        return null;
    }
}
