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
        var query = QueryBuilder.where();

        // 1. Soft Delete (Always required)
        query.and("is_deleted", Operator.EQ, 0);

        // 2. Status Filter (ACTIVE by default for most queries)
        if (request.getStatus() != null) {
            query.and("status", Operator.EQ, request.getStatus());
        }

        // 3. Category Filter
        if (request.getCategory() != null) {
            query.and("category", Operator.EQ, request.getCategory());
        }

        // 4. Name Filter (Fuzzy)
        if (request.getName() != null) {
            query.and("name", Operator.LIKE, request.getName());
        }

        // 5. Department Filter
        if (request.getDeptId() != null) {
            query.and("department_id", Operator.EQ, request.getDeptId());
        }

        return query.build();
    }
}
