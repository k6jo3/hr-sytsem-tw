package com.company.hrms.document.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.document.domain.model.enums.DocumentClassification;
import com.company.hrms.document.domain.model.enums.DocumentVisibility;

import lombok.Getter;

@Getter
public class Document extends AggregateRoot<DocumentId> {

    private String documentType;
    private String businessType;
    private String businessId;
    private String fileName;
    private String mimeType;
    private long fileSize;
    private String storagePath;

    private DocumentVisibility visibility;
    private DocumentClassification classification;

    private boolean isEncrypted;
    private String ownerId;
    private boolean isDeleted;

    private String folderId;
    private List<String> tags;

    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;

    // Constructor required by JPA/Framework (protected/private)
    protected Document() {
        super(null);
    }

    private Document(DocumentId id) {
        super(id);
    }

    // Factory method
    public static Document create(DocumentId id, String fileName, String ownerId) {
        Document doc = new Document(id);
        doc.fileName = fileName;
        doc.ownerId = ownerId;
        doc.uploadedAt = LocalDateTime.now();
        doc.updatedAt = doc.uploadedAt;
        doc.isDeleted = false;

        // Defaults
        doc.visibility = DocumentVisibility.PUBLIC;
        doc.classification = DocumentClassification.INTERNAL;
        doc.tags = new ArrayList<>();

        return doc;
    }

    /**
     * 重建 Aggregate (Persistence -> Domain)
     */
    public static Document reconstitute(
            DocumentId id,
            String fileName,
            String ownerId,
            String documentType,
            String businessType,
            String businessId,
            String mimeType,
            long fileSize,
            String storagePath,
            DocumentVisibility visibility,
            DocumentClassification classification,
            boolean isEncrypted,
            boolean isDeleted,
            String folderId,
            List<String> tags,
            LocalDateTime uploadedAt,
            LocalDateTime updatedAt) {

        Document doc = new Document(id);
        doc.fileName = fileName;
        doc.ownerId = ownerId;
        doc.documentType = documentType;
        doc.businessType = businessType;
        doc.businessId = businessId;
        doc.mimeType = mimeType;
        doc.fileSize = fileSize;
        doc.storagePath = storagePath;
        doc.visibility = visibility;
        doc.classification = classification;
        doc.isEncrypted = isEncrypted;
        doc.isDeleted = isDeleted;
        doc.folderId = folderId;
        doc.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        doc.uploadedAt = uploadedAt;
        doc.updatedAt = updatedAt;

        return doc;
    }

    public void completeUpload(String storagePath, String mimeType, long fileSize) {
        this.storagePath = storagePath;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.updatedAt = LocalDateTime.now();
        this.touch();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
        this.touch();
    }

    public void moveToFolder(String folderId) {
        this.folderId = folderId;
        this.updatedAt = LocalDateTime.now();
        this.touch();
    }

    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
            this.touch();
        }
    }
}
