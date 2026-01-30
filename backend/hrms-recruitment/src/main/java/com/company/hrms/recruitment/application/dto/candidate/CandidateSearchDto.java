package com.company.hrms.recruitment.application.dto.candidate;

import java.io.Serializable;
import java.util.List;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.IN;
import com.company.hrms.common.query.QueryCondition.LIKE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
@Schema(description = "應徵者查詢條件")
public class CandidateSearchDto extends PageRequest implements Serializable {

    @Schema(description = "職缺 ID")
    @EQ
    private String openingId;

    @Schema(description = "應徵者狀態 (NEW/SCREENING/INTERVIEWING/OFFERED/HIRED/REJECTED)")
    @IN
    private List<String> status;

    @Schema(description = "履歷來源")
    @EQ
    private String source;

    @Schema(description = "姓名關鍵字搜尋")
    @LIKE("fullName")
    private String keyword;
}
