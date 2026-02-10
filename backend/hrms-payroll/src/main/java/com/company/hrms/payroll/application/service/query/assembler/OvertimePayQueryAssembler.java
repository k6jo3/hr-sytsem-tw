package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetOvertimePayListRequest;

/**
 * 加班費查詢組裝器 (HR04-2.5)
 */
public class OvertimePayQueryAssembler {

    public QueryGroup toQueryGroup(GetOvertimePayListRequest request) {
        return QueryBuilder.where().fromDto(request).build();
    }
}
