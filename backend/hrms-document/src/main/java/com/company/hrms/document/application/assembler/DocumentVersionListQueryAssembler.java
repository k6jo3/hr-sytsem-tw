package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentVersionListRequest;

/**
 * 文件版本查詢組裝器
 */
@Component
public class DocumentVersionListQueryAssembler {

    public QueryGroup toQueryGroup(GetDocumentVersionListRequest request) {
        // TODO: 未符合Fluent-Query-Engine的設計
        var query = QueryBuilder.where();

        // 1. Document ID Filter (Required for most cases)
        if (request.getDocumentId() != null) {
            query.and("document_id", Operator.EQ, request.getDocumentId());
        }

        // 2. Version Filter
        if (request.getVersion() != null) {
            query.and("version", Operator.EQ, request.getVersion());
        }

        // 3. Latest Version Filter
        if (request.getIsLatest() != null && request.getIsLatest()) {
            query.and("is_latest", Operator.EQ, 1);
        }

        // 4. Uploader Filter
        if (request.getUploaderId() != null) {
            query.and("uploader_id", Operator.EQ, request.getUploaderId());
        }

        return query.build();
    }
}
