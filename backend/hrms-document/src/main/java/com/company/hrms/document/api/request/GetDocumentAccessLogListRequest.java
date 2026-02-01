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
    @com.company.hrms.common.query.QueryCondition.EQ("document_id")
    private String documentId;

    @com.company.hrms.common.query.QueryCondition.EQ("user_id")
    private String userId;

    @com.company.hrms.common.query.QueryCondition.EQ("action")
    private String action;

    @com.company.hrms.common.query.QueryCondition.GTE("access_time")
    private LocalDate startDate;
}
