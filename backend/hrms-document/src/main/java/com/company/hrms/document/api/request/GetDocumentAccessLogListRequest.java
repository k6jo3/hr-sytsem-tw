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
@lombok.EqualsAndHashCode(callSuper = true)
public class GetDocumentAccessLogListRequest extends com.company.hrms.common.api.request.PageRequest {
    @com.company.hrms.common.query.QueryCondition.EQ("documentId")
    private String documentId;

    @com.company.hrms.common.query.QueryCondition.EQ("userId")
    private String userId;

    @com.company.hrms.common.query.QueryCondition.EQ("action")
    private String action;

    @com.company.hrms.common.query.QueryCondition.GTE("accessedAt")
    private LocalDate startDate;
}
