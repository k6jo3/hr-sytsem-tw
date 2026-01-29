package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.SubmitDocumentRequest;
import com.company.hrms.document.domain.model.DocumentRequest;
import com.company.hrms.document.domain.model.IDocumentRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 提交文件申請服務實作
 */
@Service("submitDocumentRequestServiceImpl")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubmitDocumentRequestServiceImpl implements CommandApiService<SubmitDocumentRequest, String> {

    private final IDocumentRequestRepository repository;

    @Override
    public String execCommand(SubmitDocumentRequest request, JWTModel currentUser, String... args) throws Exception {
        log.info("Submitting document request: {} for user: {}", request.getTypeCode(), currentUser.getUserId());

        DocumentRequest domainRequest = DocumentRequest.create(
                request.getTypeCode(),
                currentUser.getUserId(),
                request.getReason());

        repository.save(domainRequest);

        // 發送事件通知流程引擎 (Workflow Service)
        // 此處應由 EventPublisher 發送領域事件，由監聽者非同步處理與 Workflow 的對接
        log.info("Document request {} persisted, notifying workflow engine...", domainRequest.getId().getValue());

        return domainRequest.getId().getValue();
    }
}
