package com.company.hrms.document.application.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.document.api.response.DocumentVersionResponse;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;
import com.company.hrms.document.domain.model.IDocumentVersionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 獲取文件版本歷史服務實作
 */
@Service("getDocumentVersionsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentVersionsServiceImpl implements QueryApiService<String, DocumentVersionResponse> {

        private final IDocumentRepository repository;
        private final IDocumentVersionRepository versionRepository;

        @Override
        public DocumentVersionResponse getResponse(String documentId, JWTModel currentUser, String... args) {
                var doc = repository.findById(new DocumentId(documentId))
                                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

                var versions = versionRepository.findByDocumentId(documentId).stream()
                                .map(v -> DocumentVersionResponse.VersionInfo.builder()
                                                .version(v.getVersion())
                                                .fileName(v.getFileName())
                                                .fileSize(v.getFileSize())
                                                .uploadedBy(v.getUploaderId())
                                                .uploadedAt(v.getUploadedAt())
                                                .changeNote(v.getChangeNote())
                                                .isCurrent(v.getVersion() == 1) // 這裡暫時以版本號判斷，實際應在 Document 儲存
                                                                                // currentVersionId
                                                .build())
                                .collect(Collectors.toList());

                // 如果沒有版本紀錄，至少回傳當前這一份 (相容舊資料)
                if (versions.isEmpty()) {
                        versions.add(DocumentVersionResponse.VersionInfo.builder()
                                        .version(1)
                                        .fileName(doc.getFileName())
                                        .fileSize(doc.getFileSize())
                                        .uploadedBy(doc.getOwnerId())
                                        .uploadedAt(doc.getUploadedAt())
                                        .changeNote("初始版本")
                                        .isCurrent(true)
                                        .build());
                }

                return DocumentVersionResponse.builder()
                                .documentId(doc.getId().getValue())
                                .currentVersion(1)
                                .versions(versions)
                                .build();
        }
}
