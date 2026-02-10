package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetDeductionListRequest;

/**
 * 扣款項目查詢組裝器 (HR04-2.4)
 */
public class DeductionQueryAssembler {

    public QueryGroup toQueryGroup(GetDeductionListRequest request) {
        return QueryBuilder.where().fromDto(request).build();
    }
}
