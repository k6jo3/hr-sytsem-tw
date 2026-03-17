package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 工時表列表查詢請求
 * <p>
 * 支援 employeeId、status、日期區間篩選，供管理者查詢所有工時表
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "工時表列表查詢請求")
public class GetTimesheetListRequest extends PageRequest {

    /**
     * 員工 ID 篩選
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    @Schema(description = "員工 ID 篩選")
    private UUID employeeId;

    /**
     * 工時表狀態篩選
     */
    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "工時表狀態篩選（DRAFT/PENDING/APPROVED/REJECTED/LOCKED）")
    private String status;

    /**
     * 起始日期篩選（大於等於）
     */
    @QueryFilter(property = "periodStartDate", operator = Operator.GTE)
    @Schema(description = "起始日期篩選")
    private LocalDate startDate;

    /**
     * 結束日期篩選（小於等於）
     */
    @QueryFilter(property = "periodEndDate", operator = Operator.LTE)
    @Schema(description = "結束日期篩選")
    private LocalDate endDate;
}
