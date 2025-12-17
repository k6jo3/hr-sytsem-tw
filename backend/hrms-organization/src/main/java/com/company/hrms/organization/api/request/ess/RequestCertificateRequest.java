package com.company.hrms.organization.api.request.ess;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 申請證明文件請求 DTO
 */
@Data
@Schema(description = "申請證明文件請求")
public class RequestCertificateRequest {

    @NotBlank(message = "證明類型為必填")
    @Schema(description = "證明類型", example = "EMPLOYMENT",
            allowableValues = {"EMPLOYMENT", "SALARY", "LEAVE_OF_ABSENCE", "RESIGNATION"})
    private String certificateType;

    @NotNull(message = "份數為必填")
    @Positive(message = "份數必須為正整數")
    @Schema(description = "份數", example = "1")
    private Integer copies;

    @NotBlank(message = "用途為必填")
    @Schema(description = "用途", example = "申請信用卡")
    private String purpose;

    @Schema(description = "備註")
    private String remarks;
}
