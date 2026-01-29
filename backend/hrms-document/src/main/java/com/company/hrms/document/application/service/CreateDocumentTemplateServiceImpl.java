package com.company.hrms.document.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.CreateDocumentTemplateRequest;
import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立文件範本服務實作
 */
@Service("createDocumentTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateDocumentTemplateServiceImpl implements CommandApiService<CreateDocumentTemplateRequest, String> {

    private final IDocumentTemplateRepository repository;

    @Override
    public String execCommand(CreateDocumentTemplateRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 檢查 Code 是否重複
        if (repository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Template code already exists: " + request.getCode());
        }

        DocumentTemplateId id = new DocumentTemplateId(UUID.randomUUID().toString());
        DocumentTemplate template = DocumentTemplate.create(
                id,
                request.getCode(),
                request.getName(),
                request.getCategory());

        // TODO: 處理範本內容 (儲存到 Blob 或 Storage)

        var saved = repository.save(template);
        return saved.getId().getValue();
    }
}
