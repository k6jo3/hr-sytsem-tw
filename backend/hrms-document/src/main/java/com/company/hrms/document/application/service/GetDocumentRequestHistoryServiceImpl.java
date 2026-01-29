package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.request.GetDocumentRequestListRequest;

import lombok.RequiredArgsConstructor;

/**
 * 查詢文件申請歷史服務實作
 */
@Service("getDocumentRequestHistoryServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentRequestHistoryServiceImpl
        implements QueryApiService<GetDocumentRequestListRequest, Page<Object>> {

    @Override
    public Page<Object> getResponse(GetDocumentRequestListRequest req, JWTModel currentUser, String... args) {

        // TODO: 調用 Workflow Service 查詢流程執行紀錄

        return Page.empty();
    }
}
