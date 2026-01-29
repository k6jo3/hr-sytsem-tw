package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentTemplateListRequest;
import com.company.hrms.document.application.assembler.DocumentTemplateListQueryAssembler;
import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢文件範本列表服務實作
 */
@Service("getDocumentTemplateListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentTemplateListServiceImpl
        implements QueryApiService<GetDocumentTemplateListRequest, Page<DocumentTemplate>> {

    private final IDocumentTemplateRepository repository;
    private final DocumentTemplateListQueryAssembler queryAssembler;

    @Override
    public Page<DocumentTemplate> getResponse(GetDocumentTemplateListRequest req, JWTModel currentUser,
            String... args) {
        var query = queryAssembler.toQueryGroup(req);

        // 預設分頁
        var pageable = PageRequest.of(0, 20);

        return repository.findTemplates(query, pageable);
    }
}
