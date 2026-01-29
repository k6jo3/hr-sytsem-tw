package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新文件範本請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentTemplateRequest {
    private String id;
    private String name;
    private String category;
    private String content;
    private String status; // ACTIVE/INACTIVE
}
