package com.company.hrms.recruitment.application.dto.candidate;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新應徵者狀態請求")
public class UpdateCandidateStatusRequest implements Serializable {

    @Schema(description = "新狀態 (SCREENING, INTERVIEWING, OFFERED, HIRED, REJECTED)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "拒絕原因 (當狀態為 REJECTED 時必填)")
    private String rejectionReason;
}
