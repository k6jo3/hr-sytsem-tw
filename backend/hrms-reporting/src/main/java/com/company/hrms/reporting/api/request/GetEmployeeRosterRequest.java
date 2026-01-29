package com.company.hrms.reporting.api.request;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.GTE;
import com.company.hrms.common.query.QueryCondition.LTE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 員工花名冊查詢請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "員工花名冊查詢請求")
public class GetEmployeeRosterRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @EQ("organizationId")
    @Schema(description = "組織ID")
    private String organizationId;

    @EQ("departmentId")
    @Schema(description = "部門ID")
    private String departmentId;

    @EQ("status")
    @Schema(description = "員工狀態", example = "ACTIVE")
    private String status;

    @GTE("hireDate")
    @Schema(description = "到職日期起", example = "2024-01-01")
    private LocalDate hireDateFrom;

    @LTE("hireDate")
    @Schema(description = "到職日期迄", example = "2024-12-31")
    private LocalDate hireDateTo;

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "50")
    private Integer size = 50;
}
