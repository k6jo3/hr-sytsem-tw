package com.company.hrms.document.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.SubmitDocumentRequest;

import lombok.RequiredArgsConstructor;

/**
 * 提交文件申請服務實作
 */
@Service("submitDocumentRequestServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SubmitDocumentRequestServiceImpl implements CommandApiService<SubmitDocumentRequest, String> {

    // private final ApplicationEventPublisher eventPublisher;

    @Override
    public String execCommand(SubmitDocumentRequest request, JWTModel currentUser, String... args) throws Exception {

        String requestId = UUID.randomUUID().toString();

        // TODO: 調用 Workflow Service 啟動流程
        // 這裡暫時模擬流程開啟

        // 也可以發布事件讓其他模組知道有人申請文件

        return requestId;
    }
}
