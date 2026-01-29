package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件範本詳情服務實作
 */
@Service("getDocumentTemplateDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentTemplateDetailServiceImpl implements QueryApiService<String, DocumentTemplate> {

    private final IDocumentTemplateRepository repository;

    @Override
    public DocumentTemplate getResponse(String id, JWTModel currentUser, String... args) {
        return repository.findById(new DocumentTemplateId(id))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
    }
}
