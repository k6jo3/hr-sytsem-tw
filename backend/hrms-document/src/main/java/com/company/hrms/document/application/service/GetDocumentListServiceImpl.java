package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentListRequest;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentListQueryAssembler;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

@Service("getDocumentListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentListServiceImpl implements QueryApiService<GetDocumentListRequest, Page<DocumentResponse>> {

    private final IDocumentRepository repository;
    private final DocumentListQueryAssembler queryAssembler;
    private final DocumentResponseAssembler responseAssembler;

    @Override
    public Page<DocumentResponse> getResponse(GetDocumentListRequest req, JWTModel currentUser, String... args) {

        // Populate implicit filters from currentUser if needed
        if (currentUser != null) {
            // TODO: 未實作邏輯
            // Logic handled in Assembler or here
        }

        var query = queryAssembler.toQueryGroup(req);

        // Default paging
        var pageable = PageRequest.of(0, 20);

        return repository.findDocuments(query, pageable)
                .map(responseAssembler::toResponse);
    }
}
