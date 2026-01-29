package com.company.hrms.document.api.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String documentId;
    private String fileName;
    private String ownerId;
    private String documentType;
    private String businessType;
    private String businessId;
    private String mimeType;
    private long fileSize;
    private String visibility;
    private String classification;
    private boolean isEncrypted;
    private String folderId;
    private List<String> tags;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}
