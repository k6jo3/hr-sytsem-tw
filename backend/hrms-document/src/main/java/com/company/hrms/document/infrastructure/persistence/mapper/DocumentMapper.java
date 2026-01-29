package com.company.hrms.document.infrastructure.persistence.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.enums.DocumentClassification;
import com.company.hrms.document.domain.model.enums.DocumentVisibility;
import com.company.hrms.document.infrastructure.persistence.po.DocumentPO;

public class DocumentMapper {

    public static Document toDomain(DocumentPO po) {
        if (po == null) {
            return null;
        }

        List<String> tags = po.getTags() != null && !po.getTags().isEmpty()
                ? Arrays.asList(po.getTags().split(","))
                : Collections.emptyList();

        return Document.reconstitute(
                new DocumentId(po.getDocumentId()),
                po.getFileName(),
                po.getOwnerId(),
                po.getDocumentType(),
                po.getBusinessType(),
                po.getBusinessId(),
                po.getMimeType(),
                po.getFileSize(),
                po.getStoragePath(),
                po.getVisibility() != null ? DocumentVisibility.valueOf(po.getVisibility()) : null,
                po.getClassification() != null ? DocumentClassification.valueOf(po.getClassification()) : null,
                po.isEncrypted(),
                po.isDeleted(),
                po.getFolderId(),
                tags,
                po.getUploadedAt(),
                po.getUpdatedAt());
    }

    public static DocumentPO toPO(Document domain) {
        if (domain == null) {
            return null;
        }

        DocumentPO po = new DocumentPO();
        po.setDocumentId(domain.getId().getValue());
        po.setFileName(domain.getFileName());
        po.setOwnerId(domain.getOwnerId());
        po.setDocumentType(domain.getDocumentType());
        po.setBusinessType(domain.getBusinessType());
        po.setBusinessId(domain.getBusinessId());
        po.setMimeType(domain.getMimeType());
        po.setFileSize(domain.getFileSize());
        po.setStoragePath(domain.getStoragePath());

        if (domain.getVisibility() != null) {
            po.setVisibility(domain.getVisibility().name());
        }

        if (domain.getClassification() != null) {
            po.setClassification(domain.getClassification().name());
        }

        po.setEncrypted(domain.isEncrypted());
        po.setDeleted(domain.isDeleted());
        po.setFolderId(domain.getFolderId());

        if (domain.getTags() != null && !domain.getTags().isEmpty()) {
            po.setTags(String.join(",", domain.getTags()));
        }

        po.setUploadedAt(domain.getUploadedAt());
        po.setUpdatedAt(domain.getUpdatedAt());

        return po;
    }
}
