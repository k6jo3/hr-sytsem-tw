package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentListRequest;

@Component
public class DocumentListQueryAssembler {

    /** 特殊標記:表示要查詢 IS NULL 條件 */
    public static final String NULL_MARKER = "__NULL__";

    public QueryGroup toQueryGroup(GetDocumentListRequest request, JWTModel currentUser) {
        // 使用 Fluent-Query-Engine 自動解析條件
        var builder = QueryBuilder.where().fromDto(request);

        // 1. Soft Delete (Always required)
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. Parent Folder Filter (特殊邏輯: 處理 NULL_MARKER)
        if (request.getParentId() != null) {
            if (NULL_MARKER.equals(request.getParentId())) {
                builder.isNull("parent_id");
            } else {
                builder.eq("parent_id", request.getParentId());
            }
        }

        // 3. Security Filter: 非管理員只能看到公開或個人的文件
        if (currentUser != null && !currentUser.hasRole("ADMIN")) {
            builder.orGroup(sub -> sub
                    .eq("owner_id", currentUser.getUserId())
                    .eq("visibility", "PUBLIC"));
        }

        return builder.build();
    }
}
