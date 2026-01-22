package com.company.hrms.recruitment.application.dto.candidate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "建立應徵者請求")
public class CreateCandidateRequest implements Serializable {

    @Schema(description = "職缺 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String openingId;

    @Schema(description = "應徵者姓名", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String fullName;

    @Schema(description = "電子郵件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "電話號碼")
    private String phoneNumber;

    @Schema(description = "履歷檔案 URL")
    private String resumeUrl;

    @Schema(description = "求職信")
    private String coverLetter;

    @Schema(description = "履歷來源 (JOB_BANK, REFERRAL, SOCIAL_MEDIA, COMPANY_WEBSITE, EXTERNAL_RECRUITER, OTHER)")
    private String source;

    @Schema(description = "推薦人 ID (僅當 source=REFERRAL 時有效)")
    private String referrerId;

    @Schema(description = "期望薪資")
    private BigDecimal expectedSalary;

    @Schema(description = "可到職日")
    private LocalDate availableDate;
}
