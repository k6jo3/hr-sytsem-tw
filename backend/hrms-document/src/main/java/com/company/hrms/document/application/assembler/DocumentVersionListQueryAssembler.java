package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentVersionListRequest;

/**
 * 文件版本查詢組裝器
 */
@Component
public class DocumentVersionListQueryAssembler {

    public QueryGroup toQueryGroup(GetDocumentVersionListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析條件
        return QueryBuilder.where().fromDto(request).build();
    }
}
