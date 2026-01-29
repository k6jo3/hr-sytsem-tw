package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentAccessLogListRequest;

import lombok.RequiredArgsConstructor;

/**
 * 查詢文件存取紀錄服務實作
 */
@Service("getDocumentAccessLogListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentAccessLogListServiceImpl
        implements QueryApiService<GetDocumentAccessLogListRequest, Page<Object>> {

    // private final IDocumentRepository repository;
    // private final DocumentAccessLogListQueryAssembler queryAssembler;

    @Override
    public Page<Object> getResponse(GetDocumentAccessLogListRequest req, JWTModel currentUser, String... args) {
        // var query = queryAssembler.toQueryGroup(req);

        // TODO: 應實作 AccessLogRepository。目前為了演示，我們使用空的 Page。
        // return accessLogRepository.findLogs(query, PageRequest.of(0, 10));
        return Page.empty();
    }
}
