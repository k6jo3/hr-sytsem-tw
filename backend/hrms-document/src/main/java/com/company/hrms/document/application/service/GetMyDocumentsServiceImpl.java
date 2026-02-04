package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // 建立查詢條件
        QueryGroup query = queryAssembler.toQueryGroup(req, currentUser);

        // 強制篩選為本人文件 (覆蓋可能傳入的 ownerId)
        if (currentUser != null && currentUser.getUserId() != null) {
            query.eq("ownerId", currentUser.getUserId());
        }

        // 使用 Request 中的分頁資訊
        Pageable pageable = req.toPageable();

        return repository.findDocuments(query, pageable)
                .map(responseAssembler::toResponse);
    }
}
