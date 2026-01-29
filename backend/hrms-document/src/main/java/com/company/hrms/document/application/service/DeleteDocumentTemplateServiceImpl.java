package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 刪除文件範本服務實作
 */
@Service("deleteDocumentTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DeleteDocumentTemplateServiceImpl implements CommandApiService<String, Void> {

    private final IDocumentTemplateRepository repository;

    @Override
    public Void execCommand(String id, JWTModel currentUser, String... args) throws Exception {
        var template = repository.findById(new DocumentTemplateId(id))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));

        // 專案慣例：範本通常只做停用。但若規格要求刪除，這裡可以實作。
        // 為安全起見，這裡先停用
        template.deactivate();
        repository.save(template);

        return null;
    }
}
