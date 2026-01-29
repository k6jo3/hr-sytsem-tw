package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件版本查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentVersionListRequest {
    private String documentId;
    private String version;
    private Boolean isLatest;
    private String uploaderId;
}
