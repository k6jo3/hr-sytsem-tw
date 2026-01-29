package com.company.hrms.document.infrastructure.persistence.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hrs_documents")
public class DocumentPO {

    @Id
    @Column(name = "document_id")
    private String documentId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_id")
    private String businessId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "classification")
    private String classification;

    @Column(name = "is_encrypted")
    private boolean isEncrypted;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "folder_id")
    private String folderId;

    @Column(name = "tags")
    private String tags; // Stored as comma separated string or JSON? Using String for simplicity

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
