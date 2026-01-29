package com.company.hrms.document.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.application.assembler.DocumentAccessLogListQueryAssembler;
import com.company.hrms.document.domain.model.IDocumentAccessLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件存取日誌服務實作
 */
@Service("getDocumentAccessLogListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentAccessLogListServiceImpl implements QueryApiService<Void, Page<Object>> {

    private final IDocumentAccessLogRepository repository;
    private final DocumentAccessLogListQueryAssembler queryAssembler;

    @Override
    public Page<Object> getResponse(Void req, JWTModel currentUser, String... args) {
        QueryGroup query = queryAssembler.toQueryGroup(null);
        Pageable pageable = PageRequest.of(0, 10);
        return repository.findLogs(query, pageable).map(log -> (Object) log);
    }
}
