package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;

/**
 * 薪資結構查詢組裝器
 */
public class SalaryStructureQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetSalaryStructureListRequest request) {
        QueryBuilder builder = QueryBuilder.where().fromDto(request);

        // 特定生效日期查詢 (HR04 v2.1.2 PAY_QRY_S007)
        if (request.getEffectiveDate() != null) {
            builder.lte("effectiveDate", request.getEffectiveDate());
            builder.orGroup(sub -> sub
                    .isNull("endDate")
                    .gt("endDate", request.getEffectiveDate()));
        }

        // 軟刪除過濾 (HR04 v2.0): 不使用 is_deleted，改用 isActive
        // 如果 request 中沒有指定 isActive，預設應查詢有效結構
        if (request.getIsActive() == null && request.getEffectiveDate() == null) {
            builder.eq("isActive", true);
        }

        return builder.build();
    }
}
