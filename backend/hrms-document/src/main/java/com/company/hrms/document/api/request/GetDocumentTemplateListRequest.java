package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件範本查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentTemplateListRequest {
    private String status;
    private String category;
    private String name;
    private String deptId;
}
