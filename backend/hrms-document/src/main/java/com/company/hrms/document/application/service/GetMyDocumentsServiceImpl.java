package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentListRequest;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentListQueryAssembler;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取個人文件列表服務實作 (ESS)
 */
@Service("getMyDocumentsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyDocumentsServiceImpl implements QueryApiService<GetDocumentListRequest, Page<DocumentResponse>> {

    private final IDocumentRepository repository;
    private final DocumentResponseAssembler responseAssembler;
    private final DocumentListQueryAssembler queryAssembler;

    @Override
    public Page<DocumentResponse> getResponse(GetDocumentListRequest req, JWTModel currentUser, String... args) {
        QueryGroup query = queryAssembler.toQueryGroup(req);

        // 強制篩選為本人文件
        query.eq("ownerId", currentUser.getUserId());

        // 預設分頁
        var pageable = PageRequest.of(0, 20);

        return repository.findDocuments(query, pageable)
                .map(responseAssembler::toResponse);
    }
}
