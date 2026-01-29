package com.company.hrms.document.api.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件存取紀錄查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentAccessLogListRequest {
    private String documentId;
    private String userId;
    private String action;
    private LocalDate startDate;
}
