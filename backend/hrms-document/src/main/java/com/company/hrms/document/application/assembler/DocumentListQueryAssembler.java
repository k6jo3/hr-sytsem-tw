package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentListRequest;

/**
 * 文件查詢組裝器
 * 
 * 根據 Fluent-Query-Engine 設計原則：
 * 1. 優先使用 fromDto() 自動解析 @QueryCondition 註解
 * 2. 手動補充特殊邏輯（如 NULL_MARKER 處理）
 * 3. 確保安全過濾條件（如 is_deleted = 0）
 */
@Component
public class DocumentListQueryAssembler {

    /** 特殊標記:表示要查詢 IS NULL 條件 */
    public static final String NULL_MARKER = "__NULL__";

    /**
     * 將文件查詢請求轉換為 QueryGroup
     * 
     * @param request     文件查詢請求
     * @param currentUser 當前使用者（測試時可為 null）
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetDocumentListRequest request, JWTModel currentUser) {
        // 1. 使用 Fluent-Query-Engine 自動解析 @QueryCondition 註解
        // fromCondition() 會自動處理所有帶 @QueryCondition.* 註解的欄位：
        // - folderId: @EQ("folder_id")
        // - name: @LIKE("name")
        // - documentType: @EQ("type")
        // - ownerId: @EQ("owner_id")
        // - visibility: @EQ("visibility")
        // - tag: @LIKE("tags")
        // - classification: @EQ("classification")
        // - accessibleVisibilities: @IN("visibility")
        // - startDate: @GTE("updated_at")
        QueryGroup query = QueryBuilder.fromCondition(request);

        // 2. Soft Delete (Always required)
        query.eq("isDeleted", false);

        // 3. Parent Folder Filter (特殊邏輯: 處理 NULL_MARKER)
        // 因為 parentId 沒有 @QueryCondition 註解，需要手動處理
        if (request.getParentId() != null) {
            if (NULL_MARKER.equals(request.getParentId())) {
                query.isNull("folderId");
            } else {
                query.eq("folderId", request.getParentId());
            }
        }

        // 4. Security Filter: 非管理員只能看到公開或個人的文件
        if (currentUser != null && !currentUser.hasRole("ADMIN") && currentUser.getUserId() != null) {
            query.orGroup(sub -> sub
                    .eq("ownerId", currentUser.getUserId())
                    .eq("visibility", "PUBLIC"));
        }

        return query;
    }
}
