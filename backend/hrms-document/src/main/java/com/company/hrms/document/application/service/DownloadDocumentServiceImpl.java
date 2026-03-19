package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.response.FileDownloadResponse;
import com.company.hrms.document.domain.model.DocumentAccessLog;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentAccessLogRepository;
import com.company.hrms.document.domain.model.IDocumentRepository;
import com.company.hrms.document.domain.service.IFileStorageService;

import lombok.RequiredArgsConstructor;

/**
 * 下載文件服務實作
 * <p>
 * 透過 {@link IFileStorageService} 從儲存空間讀取實際檔案內容。
 * </p>
 */
@Service("downloadDocumentServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DownloadDocumentServiceImpl implements QueryApiService<String, FileDownloadResponse> {

    private final IDocumentRepository repository;
    private final IDocumentAccessLogRepository accessLogRepository;
    private final IFileStorageService fileStorageService;

    @Override
    public FileDownloadResponse getResponse(String documentId, JWTModel currentUser, String... args) {
        var doc = repository.findById(new DocumentId(documentId))
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        if (doc.isDeleted()) {
            throw new EntityNotFoundException("Document has been deleted: " + documentId);
        }

        // 紀錄存取日誌
        accessLogRepository.save(DocumentAccessLog.create(
                documentId, currentUser.getUserId(), "DOWNLOAD", "127.0.0.1"));

        // 從儲存空間讀取實際檔案內容
        byte[] content = fileStorageService.load(doc.getStoragePath());

        return FileDownloadResponse.builder()
                .fileName(doc.getFileName())
                .mimeType(doc.getMimeType())
                .content(content)
                .build();
    }
}
