package com.company.hrms.insurance.api.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 團體保險方案列表回應 DTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInsurancePlanResponse {

    /** 方案 ID */
    private String planId;

    /** 方案名稱 */
    private String planName;

    /** 方案代碼 */
    private String planCode;

    /** 保險類型 */
    private String insuranceType;

    /** 保險類型顯示名稱 */
    private String insuranceTypeDisplay;

    /** 保險公司名稱 */
    private String insurerName;

    /** 保單號碼 */
    private String policyNumber;

    /** 是否啟用 */
    private boolean active;

    /** 合約起始日 */
    private LocalDate contractStartDate;

    /** 合約結束日 */
    private LocalDate contractEndDate;

    /** 職等方案數量 */
    private int tierCount;
}
