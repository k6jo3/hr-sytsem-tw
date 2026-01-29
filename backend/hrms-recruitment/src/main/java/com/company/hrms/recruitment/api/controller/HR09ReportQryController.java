package com.company.hrms.recruitment.api.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.report.DashboardResponse;
import com.company.hrms.recruitment.application.dto.report.DashboardSearchDto;
import com.company.hrms.recruitment.application.dto.report.ExportSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 招募報表查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/recruitment/dashboard")
@Tag(name = "HR09-Report", description = "招募報表與儀表板")
public class HR09ReportQryController extends QueryBaseController {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @GetMapping
    @Operation(summary = "取得招募儀表板", operationId = "getDashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = DashboardResponse.class)))
    })
    public ResponseEntity<DashboardResponse> getDashboard(
            @ParameterObject DashboardSearchDto searchDto,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(searchDto, currentUser));
    }

    @GetMapping("/export")
    @Operation(summary = "匯出招募報表", operationId = "exportDashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "匯出成功", content = @Content(mediaType = "application/octet-stream"))
    })
    public ResponseEntity<byte[]> exportDashboard(
            @ParameterObject ExportSearchDto searchDto,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        byte[] content = getResponse(searchDto, currentUser);

        // 決定 Content-Type 和檔名
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String extension = "xlsx";

        String filename = String.format("recruitment_report_%s.%s",
                LocalDate.now().format(MONTH_FORMATTER), extension);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }
}
