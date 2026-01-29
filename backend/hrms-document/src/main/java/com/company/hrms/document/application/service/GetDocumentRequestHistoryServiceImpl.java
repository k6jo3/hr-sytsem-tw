package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentRequestListRequest;
import com.company.hrms.document.domain.model.IDocumentRequestRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件申請歷史服務實作
 */
@Service("getDocumentRequestListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentRequestHistoryServiceImpl
        implements QueryApiService<GetDocumentRequestListRequest, Page<Object>> {

    private final IDocumentRequestRepository repository;

    @Override
    public Page<Object> getResponse(GetDocumentRequestListRequest req, JWTModel currentUser, String... args) {
        // ESS 查詢：僅顯示本人申請
        QueryGroup query = QueryGroup.and().eq("requesterId", currentUser.getUserId());

        Pageable pageable = PageRequest.of(0, 20);
        return repository.findRequests(query, pageable).map(r -> (Object) r);
    }
}
