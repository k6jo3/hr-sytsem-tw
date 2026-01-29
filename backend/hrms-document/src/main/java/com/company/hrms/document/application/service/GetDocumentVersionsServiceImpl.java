package com.company.hrms.document.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.response.DocumentVersionResponse;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件版本歷史服務實作
 */
@Service("getDocumentVersionsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentVersionsServiceImpl implements QueryApiService<String, DocumentVersionResponse> {

    private final IDocumentRepository repository;

    @Override
    public DocumentVersionResponse getResponse(String documentId, JWTModel currentUser, String... args) {
        var doc = repository.findById(new DocumentId(documentId))
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        // TODO: 實際應從 DocumentVersion 分離表查詢
        // 目前模擬回傳當前版本作為第一筆
        List<DocumentVersionResponse.VersionInfo> versions = new ArrayList<>();
        versions.add(DocumentVersionResponse.VersionInfo.builder()
                .version(1)
                .fileName(doc.getFileName())
                .fileSize(doc.getFileSize())
                .uploadedBy(doc.getOwnerId())
                .uploadedAt(doc.getUploadedAt())
                .changeNote("初始版本")
                .isCurrent(true)
                .build());

        return DocumentVersionResponse.builder()
                .documentId(doc.getId().getValue())
                .currentVersion(1)
                .versions(versions)
                .build();
    }
}
