package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.response.FileDownloadResponse;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 下載文件服務實作
 */
@Service("downloadDocumentServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DownloadDocumentServiceImpl implements QueryApiService<String, FileDownloadResponse> {

    private final IDocumentRepository repository;

    @Override
    public FileDownloadResponse getResponse(String documentId, JWTModel currentUser, String... args) {
        var doc = repository.findById(new DocumentId(documentId))
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        if (doc.isDeleted()) {
            throw new IllegalStateException("Document has been deleted.");
        }

        // TODO: 實際應從 Storage (Local/S3) 讀取檔案
        // 這裡暫時模擬回傳
        byte[] mockContent = "File content mock".getBytes();

        return FileDownloadResponse.builder()
                .fileName(doc.getFileName())
                .mimeType(doc.getMimeType())
                .content(mockContent)
                .build();
    }
}
