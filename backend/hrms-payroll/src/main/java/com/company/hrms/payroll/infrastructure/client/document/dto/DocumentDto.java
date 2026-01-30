package com.company.hrms.payroll.infrastructure.client.document.dto;

import lombok.Data;

@Data
public class DocumentDto {
    private String documentId;
    private String fileName;
    private String mimeType;
    private long fileSize;
}
