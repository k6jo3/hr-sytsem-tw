package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.UpdateDocumentTemplateRequest;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;
import com.company.hrms.document.domain.model.enums.DocumentTemplateStatus;

import lombok.RequiredArgsConstructor;

/**
 * 更新文件範本服務實作
 */
@Service("updateDocumentTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateDocumentTemplateServiceImpl implements CommandApiService<UpdateDocumentTemplateRequest, Void> {

    private final IDocumentTemplateRepository repository;

    @Override
    public Void execCommand(UpdateDocumentTemplateRequest request, JWTModel currentUser, String... args)
            throws Exception {
        var template = repository.findById(new DocumentTemplateId(request.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + request.getId()));

        // 如果有其他欄位更新邏輯，應在 Domain Model 中實作。
        // 這裡先演示狀態切換與內容更新
        if (request.getContent() != null) {
            template.setContent(request.getContent());
        }

        if (DocumentTemplateStatus.INACTIVE.name().equals(request.getStatus())) {
            template.deactivate();
        } else if (DocumentTemplateStatus.ACTIVE.name().equals(request.getStatus())) {
            template.activate();
        }

        repository.save(template);
        return null;
    }
}
