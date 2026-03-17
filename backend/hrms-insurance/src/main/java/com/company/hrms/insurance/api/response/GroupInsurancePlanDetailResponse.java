package com.company.hrms.insurance.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 團體保險方案詳情回應 DTO（含職等方案列表）
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GroupInsurancePlanDetailResponse extends GroupInsurancePlanResponse {

    /** 職等方案列表 */
    private List<PlanTierResponse> tiers;
}
