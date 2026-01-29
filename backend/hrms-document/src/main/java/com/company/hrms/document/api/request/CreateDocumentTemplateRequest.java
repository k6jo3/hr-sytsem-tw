package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立文件範本請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentTemplateRequest {
    private String code;
    private String name;
    private String category;
    private String content; // 範本內容或路徑
}
