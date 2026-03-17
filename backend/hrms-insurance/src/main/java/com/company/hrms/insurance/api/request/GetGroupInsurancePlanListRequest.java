package com.company.hrms.insurance.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢團體保險方案列表請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupInsurancePlanListRequest {

    /** 組織 ID */
    @QueryFilter(property = "organization_id", operator = Operator.EQ)
    private String organizationId;

    /** 保險類型（GROUP_LIFE / GROUP_ACCIDENT / GROUP_MEDICAL） */
    @QueryFilter(property = "insurance_type", operator = Operator.EQ)
    private String insuranceType;

    /** 是否啟用 */
    @QueryFilter(property = "is_active", operator = Operator.EQ)
    private Boolean active;
}
