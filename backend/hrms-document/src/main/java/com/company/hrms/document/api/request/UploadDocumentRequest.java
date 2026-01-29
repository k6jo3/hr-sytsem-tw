package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {
    private String fileName;
    private String ownerId;
    private String documentType; // PDF, WORD etc
    private String businessType; // CONTRACT, RECEIPT
    private String businessId; // Linked entity ID
    private byte[] fileContent;
    private long fileSize;
    private String mimeType;
    private String visibility;
    private String classification;
    private String folderId;
}
