package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentTemplateListRequest;

/**
 * 文件範本查詢組裝器
 */
@Component
public class DocumentTemplateListQueryAssembler {

    public QueryGroup toQueryGroup(GetDocumentTemplateListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析條件
        var builder = QueryBuilder.where().fromDto(request);

        // 1. Soft Delete (Always required)
        builder.and("is_deleted", Operator.EQ, 0);

        return builder.build();
    }
}
