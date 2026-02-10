package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryCondition.EQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得補卡申請列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得補卡申請列表請求")
public class GetCorrectionListRequest {

    @Schema(description = "員工 ID")
    @EQ
    private String employeeId;

    @Schema(description = "申請狀態")
    @EQ
    private String status;

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;

    @Schema(description = "頁碼")
    private Integer page;

    @Schema(description = "每頁筆數")
    private Integer size;
}
