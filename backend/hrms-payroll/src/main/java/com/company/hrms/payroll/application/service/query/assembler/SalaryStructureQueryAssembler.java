package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;

/**
 * 薪資結構查詢組裝器
 * 將 GetSalaryStructureListRequest 轉換為 QueryGroup
 */
public class SalaryStructureQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetSalaryStructureListRequest request) {
        QueryGroup queryGroup = QueryGroup.and();

        // 員工過濾
        if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            queryGroup.eq("employee_id", request.getEmployeeId());
        }

        // 有效狀態過濾
        if (request.getActive() != null) {
            queryGroup.eq("active", request.getActive());
        }

        // 薪資制度過濾
        if (request.getPayrollSystem() != null && !request.getPayrollSystem().isEmpty()) {
            queryGroup.eq("payroll_system", request.getPayrollSystem());
        }

        // 軟刪除過濾 (始終添加)
        queryGroup.eq("is_deleted", 0);

        return queryGroup;
    }
}
