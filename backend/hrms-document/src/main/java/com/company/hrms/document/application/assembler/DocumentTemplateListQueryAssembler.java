package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentTemplateListRequest;

/**
 * 文件範本查詢組裝器
 * 
 * 根據 Fluent-Query-Engine 設計原則：
 * 1. 優先使用 fromDto() 自動解析 @QueryCondition 註解
 * 2. 確保安全過濾條件（如 is_deleted = 0）
 */
@Component
public class DocumentTemplateListQueryAssembler {

    /**
     * 將文件範本查詢請求轉換為 QueryGroup
     * 
     * fromCondition() 會自動處理所有帶 @QueryCondition.* 註解的欄位：
     * - status: @EQ("status")
     * - category: @EQ("category")
     * - name: @LIKE("name")
     * - deptId: @EQ("department_id")
     * 
     * @param request 文件範本查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetDocumentTemplateListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析所有條件
        QueryGroup query = QueryBuilder.fromCondition(request);

        // Soft Delete (Always required)
        // 確保不查詢已刪除的範本
        query.eq("is_deleted", 0);

        return query;
    }
}
