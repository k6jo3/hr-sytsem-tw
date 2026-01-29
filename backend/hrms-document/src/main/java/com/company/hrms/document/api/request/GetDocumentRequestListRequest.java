package com.company.hrms.document.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢文件申請列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentRequestListRequest {
    private String employeeId;
    private String status;
    private String typeCode;
}
