package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刪除文件請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDocumentRequest {
    private String documentId;
}
