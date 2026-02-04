package com.company.hrms.document.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.api.request.GetDocumentVersionListRequest;

/**
 * 文件版本查詢組裝器
 * 
 * 根據 Fluent-Query-Engine 設計原則：
 * 所有欄位都已在 Request DTO 中標註 @QueryCondition 註解，
 * 因此可以完全依賴 fromDto() 自動解析。
 */
@Component
public class DocumentVersionListQueryAssembler {

    /**
     * 將文件版本查詢請求轉換為 QueryGroup
     * 
     * fromCondition() 會自動處理所有帶 @QueryCondition.* 註解的欄位：
     * - documentId: @EQ("document_id")
     * - version: @EQ("version")
     * - isLatest: @EQ("is_latest") - Boolean 會自動轉換為 1/0
     * - uploaderId: @EQ("uploader_id")
     * 
     * @param request 文件版本查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetDocumentVersionListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析所有條件
        // 文件版本表不需要 soft delete 過濾（版本是歷史記錄，不應該被刪除）
        return QueryBuilder.fromCondition(request);
    }
}
