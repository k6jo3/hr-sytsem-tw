package com.company.hrms.document.api.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.QueryCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件存取紀錄查詢請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetDocumentAccessLogListRequest extends PageRequest {
    @QueryCondition.EQ("documentId")
    private String documentId;

    @QueryCondition.EQ("userId")
    private String userId;

    @QueryCondition.EQ("action")
    private String action;

    @QueryCondition.GTE("accessedAt")
    private LocalDate startDate;
}
