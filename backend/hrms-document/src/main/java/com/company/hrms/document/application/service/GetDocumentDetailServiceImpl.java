package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件詳情服務實作
 */
@Service("getDocumentDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentDetailServiceImpl implements QueryApiService<String, DocumentResponse> {

    private final IDocumentRepository repository;
    private final DocumentResponseAssembler responseAssembler;

    @Override
    public DocumentResponse getResponse(String documentId, JWTModel currentUser, String... args) {
        return repository.findById(new DocumentId(documentId))
                .filter(doc -> !doc.isDeleted()) // 增加軟刪除過濾
                .map(responseAssembler::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));
    }
}
