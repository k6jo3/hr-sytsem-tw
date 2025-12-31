package com.company.hrms.insurance.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.insurance.api.request.ExportEnrollmentReportRequest;
import com.company.hrms.insurance.api.response.ExportEnrollmentReportResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.domain.service.EnrollmentReportGenerationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 申報檔案匯出 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/export")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-Export", description = "申報檔案匯出")
public class HR05ExportCmdController {

    private final IInsuranceEnrollmentRepository enrollmentRepository;
    private final EnrollmentReportGenerationService reportGenerationService;

    @PostMapping("/enrollment-report")
    @Operation(summary = "匯出加退保申報檔", operationId = "exportEnrollmentReport")
    public ResponseEntity<ExportEnrollmentReportResponse> exportEnrollmentReport(
            @RequestBody ExportEnrollmentReportRequest request) {

        log.info("匯出申報檔請求: type={}, startDate={}, endDate={}",
                request.getExportType(), request.getStartDate(), request.getEndDate());

        // 解析參數
        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        String exportType = request.getExportType() != null ? request.getExportType() : "ALL";
        String reportType = request.getReportType() != null ? request.getReportType() : "ALL";

        // 查詢加退保記錄
        List<InsuranceEnrollment> allEnrollments = enrollmentRepository.findByDateRange(startDate, endDate);

        // 根據申報類型過濾
        List<InsuranceEnrollment> filteredEnrollments = filterByReportType(allEnrollments, reportType);

        // 產生申報檔案
        String fileContent;
        String fileName;

        if ("LABOR".equalsIgnoreCase(exportType)) {
            fileContent = reportGenerationService.generateLaborInsuranceReport(filteredEnrollments);
            fileName = reportGenerationService.generateFileName("LABOR", request.getStartDate(), request.getEndDate());
        } else if ("HEALTH".equalsIgnoreCase(exportType)) {
            fileContent = reportGenerationService.generateHealthInsuranceReport(filteredEnrollments);
            fileName = reportGenerationService.generateFileName("HEALTH", request.getStartDate(), request.getEndDate());
        } else {
            // ALL: 產生勞保檔案 (可擴展為產生多個檔案)
            fileContent = reportGenerationService.generateLaborInsuranceReport(filteredEnrollments);
            fileName = reportGenerationService.generateFileName("ALL", request.getStartDate(), request.getEndDate());
        }

        // 建構匯出明細
        List<ExportEnrollmentReportResponse.ExportRecord> records = filteredEnrollments.stream()
                .map(this::toExportRecord)
                .collect(Collectors.toList());

        // 建構回應
        ExportEnrollmentReportResponse response = ExportEnrollmentReportResponse.builder()
                .reportType(reportType)
                .exportType(exportType)
                .totalRecords(filteredEnrollments.size())
                .fileName(fileName)
                .fileContent(reportGenerationService.encodeToBase64(fileContent))
                .records(records)
                .build();

        log.info("匯出申報檔成功: {} 筆, 檔案={}", filteredEnrollments.size(), fileName);

        return ResponseEntity.ok(response);
    }

    private List<InsuranceEnrollment> filterByReportType(List<InsuranceEnrollment> enrollments, String reportType) {
        if ("ENROLL".equalsIgnoreCase(reportType)) {
            return enrollments.stream()
                    .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                    .collect(Collectors.toList());
        } else if ("WITHDRAW".equalsIgnoreCase(reportType)) {
            return enrollments.stream()
                    .filter(e -> e.getStatus() == EnrollmentStatus.WITHDRAWN)
                    .collect(Collectors.toList());
        }
        return enrollments;
    }

    private ExportEnrollmentReportResponse.ExportRecord toExportRecord(InsuranceEnrollment enrollment) {
        String actionType = enrollment.getStatus() == EnrollmentStatus.ACTIVE ? "ENROLL" : "WITHDRAW";
        String actionDate = enrollment.getStatus() == EnrollmentStatus.ACTIVE
                ? enrollment.getEnrollDate().toString()
                : (enrollment.getWithdrawDate() != null ? enrollment.getWithdrawDate().toString() : "");

        return ExportEnrollmentReportResponse.ExportRecord.builder()
                .employeeId(enrollment.getEmployeeId())
                .employeeName("") // TODO: 需要關聯員工資料
                .idNumber("") // TODO: 需要關聯員工資料
                .insuranceType(enrollment.getInsuranceType().name())
                .actionType(actionType)
                .actionDate(actionDate)
                .monthlySalary(enrollment.getMonthlySalary().toString())
                .build();
    }
}
