package com.company.hrms.document.infrastructure.persistence.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 文件版本持久化物件
 */
@Data
@Entity
@Table(name = "hrs_document_versions")
public class DocumentVersionPO {
    @Id
    @Column(name = "version_id")
    private String versionId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "version_number")
    private int versionNumber;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "uploader_id")
    private String uploaderId;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "change_note")
    private String changeNote;
}
