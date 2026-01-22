package com.company.hrms.recruitment.application.dto.candidate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "應徵者詳細資料")
public class CandidateResponse implements Serializable {

    @Schema(description = "應徵者 ID")
    private String candidateId;

    @Schema(description = "職缺 ID")
    private String openingId;

    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "電子郵件")
    private String email;

    @Schema(description = "電話號碼")
    private String phoneNumber;

    @Schema(description = "履歷來源")
    private String source;

    @Schema(description = "推薦人 ID")
    private String referrerId;

    @Schema(description = "目前狀態")
    private String status;

    @Schema(description = "履歷檔案 URL")
    private String resumeUrl;

    @Schema(description = "求職信")
    private String coverLetter;

    @Schema(description = "期望薪資")
    private BigDecimal expectedSalary;

    @Schema(description = "可到職日")
    private LocalDate availableDate;

    @Schema(description = "投遞日期")
    private LocalDate applicationDate;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "最後更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "拒絕原因")
    private String rejectionReason;
}
