package com.company.hrms.document.domain.model;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.model.Entity;
import com.company.hrms.common.domain.model.Identifier;

import lombok.Getter;

/**
 * 文件版本實體
 */
@Getter
public class DocumentVersion extends Entity<DocumentVersion.DocumentVersionId> {

    private final String documentId;
    private final int version;
    private final String fileName;
    private final long fileSize;
    private final String storagePath;
    private final String uploaderId;
    private final LocalDateTime uploadedAt;
    private final String changeNote;

    public DocumentVersion(DocumentVersionId id, String documentId, int version, String fileName, long fileSize,
            String storagePath, String uploaderId, String changeNote) {
        super(id);
        this.documentId = documentId;
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.uploaderId = uploaderId;
        this.uploadedAt = LocalDateTime.now();
        this.changeNote = changeNote;
    }

    public static DocumentVersion create(String documentId, int version, String fileName, long fileSize,
            String storagePath, String uploaderId, String changeNote) {
        return new DocumentVersion(new DocumentVersionId(java.util.UUID.randomUUID().toString()),
                documentId, version, fileName, fileSize, storagePath, uploaderId, changeNote);
    }

    @Getter
    public static class DocumentVersionId extends Identifier<String> {
        public DocumentVersionId(String value) {
            super(value);
        }
    }
}
