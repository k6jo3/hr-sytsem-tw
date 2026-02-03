package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        // 建立查詢條件，並傳入 currentUser 以進行權限過濾 (如：非管理員僅能查看公開或個人的文件)
        var query = queryAssembler.toQueryGroup(req, currentUser);

        // 使用 Request 中帶的分頁資訊
        Pageable pageable = req.toPageable();

        return repository.findDocuments(query, pageable)
                .map(responseAssembler::toResponse);
    }
}
