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
        // 使用 Fluent-Query-Engine 自動解析條件
        var builder = QueryBuilder.where().fromDto(request);

        // 1. Soft Delete (Always required)
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. Parent Folder Filter (特殊邏輯: 處理 NULL_MARKER)
        if (request.getParentId() != null) {
            if (NULL_MARKER.equals(request.getParentId())) {
                builder.isNull("parent_id");
            } else {
                builder.and("parent_id", Operator.EQ, request.getParentId());
            }
        }

        // 注意: startDate 透過 DTO @GTE("updated_at") 自動處理，
        // 若系統對於 LocalDate 與 DB String 處理有異，可能需改回手動 toString()。
        // 此處假設 QueryEngine 能正確轉型。

        return builder.build();
    }
}
