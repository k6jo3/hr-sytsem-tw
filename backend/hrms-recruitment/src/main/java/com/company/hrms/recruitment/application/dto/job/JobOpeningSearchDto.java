package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.LIKE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "職缺查詢條件")
public class JobOpeningSearchDto implements Serializable {

    @Schema(description = "職缺標題關鍵字")
    @LIKE("jobTitle")
    private String keyword;

    @Schema(description = "部門 ID")
    @EQ
    private String departmentId;

    @Schema(description = "職缺狀態 (DRAFT/OPEN/CLOSED/FILLED)")
    @EQ
    private String status;
}
