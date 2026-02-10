package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetBonusListRequest;

/**
 * 獎金查詢組裝器 (HR04-2.3)
 */
public class BonusQueryAssembler {

    public QueryGroup toQueryGroup(GetBonusListRequest request) {
        return QueryBuilder.where().fromDto(request).build();
    }
}
