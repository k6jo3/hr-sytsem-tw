package com.company.hrms.organization.application.service.employee.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.organization.api.request.employee.GetEmployeeListRequest;

/**
 * 員工查詢組裝器
 */
@Component
public class EmployeeQueryAssembler {

    /**
     * 轉換請求為查詢群組
     */
    public QueryGroup toQueryGroup(GetEmployeeListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 3. 姓名模糊查詢
        if (request.getName() != null && !request.getName().isBlank()) {
            query.like("name", request.getName());
        }

        // 4. 工號查詢
        if (request.getEmployeeNo() != null && !request.getEmployeeNo().isBlank()) {
            query.eq("employee_no", request.getEmployeeNo());
        }

        // 5. 部門過濾
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        // 6. 職位過濾
        if (request.getPositionId() != null && !request.getPositionId().isBlank()) {
            query.eq("position_id", request.getPositionId());
        }

        // 7. 僱用類型
        if (request.getEmploymentType() != null && !request.getEmploymentType().isBlank()) {
            query.eq("employment_type", request.getEmploymentType());
        }

        // 8. 到職日期查詢 (區間)
        if (request.getHireStartDate() != null) {
            query.gte("hire_date", request.getHireStartDate().toString());
        }
        if (request.getHireEndDate() != null) {
            query.lte("hire_date", request.getHireEndDate().toString());
        }

        return query;
    }
}
