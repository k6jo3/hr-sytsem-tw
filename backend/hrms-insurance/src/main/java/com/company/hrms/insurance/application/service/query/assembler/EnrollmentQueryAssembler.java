package com.company.hrms.insurance.application.service.query.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.api.request.GetEnrollmentListRequest;

/**
 * 加退保紀錄查詢組裝器
 * 負責將 Request 轉換為 QueryGroup
 */
@Component
public class EnrollmentQueryAssembler {

    /**
     * 轉換請求為查詢群組
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件
     */
    public QueryGroup toQueryGroup(GetEnrollmentListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析條件
        var builder = QueryBuilder.where().fromDto(request);

        // 1. 基礎過濾: 未刪除
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. 當前使用者過濾 (個人查詢)
        // 手動處理，因為 employeeId 已經有標註，如果 currentUserId 也有值，
        // 則再加一個 employee_id 條件可能導致衝突或重複，
        // 但若是為了權限控管，通常是強制覆蓋或增加 AND 條件。
        // 原邏輯是: eq("employee_id", request.getCurrentUserId())
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        return builder.build();
    }

    /**
     * 建構勞保查詢條件
     */
    public QueryGroup toLaborInsuranceQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.add(new com.company.hrms.common.query.FilterUnit("insurance_type", Operator.EQ, "LABOR"));
        return query;
    }

    /**
     * 建構健保查詢條件
     */
    public QueryGroup toHealthInsuranceQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.add(new com.company.hrms.common.query.FilterUnit("insurance_type", Operator.EQ, "HEALTH"));
        return query;
    }

    /**
     * 建構勞退查詢條件
     */
    public QueryGroup toPensionQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.add(new com.company.hrms.common.query.FilterUnit("insurance_type", Operator.EQ, "PENSION"));
        return query;
    }
}
