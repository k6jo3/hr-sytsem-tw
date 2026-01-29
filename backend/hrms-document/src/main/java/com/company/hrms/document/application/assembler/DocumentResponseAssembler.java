package com.company.hrms.document.application.assembler;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.domain.model.Document;

@Component
public class DocumentResponseAssembler {

    public DocumentResponse toResponse(Document document) {
        if (document == null) {
            return null;
        }

        return DocumentResponse.builder()
                .documentId(document.getId().getValue())
                .fileName(document.getFileName())
                .ownerId(document.getOwnerId())
                .documentType(document.getDocumentType())
                .businessType(document.getBusinessType())
                .businessId(document.getBusinessId())
                .mimeType(document.getMimeType())
                .fileSize(document.getFileSize())
                .visibility(document.getVisibility() != null ? document.getVisibility().name() : null)
                .classification(document.getClassification() != null ? document.getClassification().name() : null)
                .isEncrypted(document.isEncrypted())
                .folderId(document.getFolderId())
                .tags(document.getTags() != null ? new ArrayList<>(document.getTags()) : new ArrayList<>())
                .uploadedAt(document.getUploadedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
