package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentAccessLogListRequest;

/**
 * 文件存取紀錄查詢組裝器
 */
@Component
public class DocumentAccessLogListQueryAssembler {

    public QueryGroup toQueryGroup(GetDocumentAccessLogListRequest request) {
        var query = QueryBuilder.where();

        // 1. Document ID Filter
        if (request.getDocumentId() != null) {
            query.and("document_id", Operator.EQ, request.getDocumentId());
        }

        // 2. User ID Filter
        if (request.getUserId() != null) {
            query.and("user_id", Operator.EQ, request.getUserId());
        }

        // 3. Action Filter
        if (request.getAction() != null) {
            query.and("action", Operator.EQ, request.getAction());
        }

        // 4. Date Range Filter
        if (request.getStartDate() != null) {
            query.and("access_time", Operator.GTE, request.getStartDate().toString());
        }

        return query.build();
    }
}
