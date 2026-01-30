package com.company.hrms.insurance.application.service.query.assembler;

import org.springframework.stereotype.Component;

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
        QueryGroup query = QueryGroup.and();
        // TODO: 未符合Fluent-Query-Engine的設計

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 員工編號過濾
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 3. 當前使用者過濾 (個人查詢)
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            query.eq("employee_id", request.getCurrentUserId());
        }

        // 4. 保險類型過濾
        if (request.getInsuranceType() != null && !request.getInsuranceType().isBlank()) {
            query.eq("insurance_type", request.getInsuranceType());
        }

        // 5. 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 6. 加保日期過濾
        if (request.getEnrollDate() != null && !request.getEnrollDate().isBlank()) {
            query.eq("enroll_date", request.getEnrollDate());
        }

        // 7. 投保級距過濾
        if (request.getSalaryGrade() != null && !request.getSalaryGrade().isBlank()) {
            query.eq("salary_grade", Integer.parseInt(request.getSalaryGrade()));
        }

        // 8. 投保單位過濾
        if (request.getInsuranceUnit() != null && !request.getInsuranceUnit().isBlank()) {
            query.eq("insurance_unit", request.getInsuranceUnit());
        }

        // 9. 眷屬過濾
        if (request.getHasDependents() != null && request.getHasDependents()) {
            query.eq("has_dependents", 1);
        }

        return query;
    }

    /**
     * 建構勞保查詢條件
     */
    public QueryGroup toLaborInsuranceQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.eq("insurance_type", "LABOR");
        return query;
    }

    /**
     * 建構健保查詢條件
     */
    public QueryGroup toHealthInsuranceQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.eq("insurance_type", "HEALTH");
        return query;
    }

    /**
     * 建構勞退查詢條件
     */
    public QueryGroup toPensionQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.eq("insurance_type", "PENSION");
        return query;
    }
}
