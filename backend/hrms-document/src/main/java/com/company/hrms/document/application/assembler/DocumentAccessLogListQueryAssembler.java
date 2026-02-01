package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentAccessLogListRequest;

/**
 * 文件存取紀錄查詢組裝器
 */
@Component
public class DocumentAccessLogListQueryAssembler {

    public QueryGroup toQueryGroup(GetDocumentAccessLogListRequest request) {
        // 使用 Fluent-Query-Engine 的設計
        return QueryBuilder.fromCondition(request);
    }
}
