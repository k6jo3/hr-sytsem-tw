package com.company.hrms.insurance.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立團體保險方案請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupInsurancePlanRequest {

    /** 組織 ID */
    @NotBlank(message = "組織 ID 不可為空")
    private String organizationId;

    /** 方案名稱 */
    @NotBlank(message = "方案名稱不可為空")
    private String planName;

    /** 方案代碼 */
    @NotBlank(message = "方案代碼不可為空")
    private String planCode;

    /** 保險類型（GROUP_LIFE / GROUP_ACCIDENT / GROUP_MEDICAL） */
    @NotBlank(message = "保險類型不可為空")
    private String insuranceType;

    /** 保險公司名稱 */
    @NotBlank(message = "保險公司名稱不可為空")
    private String insurerName;

    /** 保單號碼 */
    private String policyNumber;

    /** 合約起始日（格式: yyyy-MM-dd） */
    @NotNull(message = "合約起始日不可為空")
    private String contractStartDate;

    /** 合約結束日（格式: yyyy-MM-dd） */
    private String contractEndDate;
}
