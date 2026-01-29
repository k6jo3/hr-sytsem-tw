package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentListRequest;

@Component
public class DocumentListQueryAssembler {

    /** 特殊標記:表示要查詢 IS NULL 條件 */
    public static final String NULL_MARKER = "__NULL__";

    public QueryGroup toQueryGroup(GetDocumentListRequest request) {
        var query = QueryBuilder.where();

        // 1. Soft Delete (Always required)
        query.and("is_deleted", Operator.EQ, 0);

        // 2. Folder Filter
        if (request.getFolderId() != null) {
            query.and("folder_id", Operator.EQ, request.getFolderId());
        }

        // 2.1 Parent Folder Filter (for folder hierarchy)
        // Note: We need to handle explicit null check from request
        // If parentId is explicitly set to null in request, we should add IS NULL
        // condition
        // This is a special case for DOC_F001 (root folders)
        // Support explicit NULL check using NULL_MARKER
        if (request.getParentId() != null) {
            if (NULL_MARKER.equals(request.getParentId())) {
                query.isNull("parent_id");
            } else {
                query.and("parent_id", Operator.EQ, request.getParentId());
            }
        }

        // 3. Name Filter (Fuzzy)
        if (request.getName() != null) {
            query.and("name", Operator.LIKE, request.getName());
        }

        // 4. Type Filter
        if (request.getDocumentType() != null) {
            query.and("type", Operator.EQ, request.getDocumentType());
        }

        // 5. Owner Filter (My Documents)
        if (request.getOwnerId() != null) {
            query.and("owner_id", Operator.EQ, request.getOwnerId());
        }

        // 6. Visibility Filter (Single)
        if (request.getVisibility() != null) {
            query.and("visibility", Operator.EQ, request.getVisibility());
        }

        // 6.1 Visibility Permission Filter (List)
        if (request.getAccessibleVisibilities() != null && !request.getAccessibleVisibilities().isEmpty()) {
            query.and("visibility", Operator.IN, request.getAccessibleVisibilities());
        }

        // 7. Tag Filter (Fuzzy)
        if (request.getTag() != null) {
            query.and("tags", Operator.LIKE, request.getTag());
        }

        // 8. Classification Filter (Security Level)
        if (request.getClassification() != null) {
            query.and("classification", Operator.EQ, request.getClassification());
        }

        // 9. Recent Documents (Date Range)
        if (request.getStartDate() != null) {
            query.and("updated_at", Operator.GTE, request.getStartDate().toString());
        }

        return query.build();
    }
}
