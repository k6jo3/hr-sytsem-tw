package com.company.hrms.insurance.application.service.export;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.ExportEnrollmentReportRequest;
import com.company.hrms.insurance.api.response.ExportEnrollmentReportResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.domain.service.EnrollmentReportGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("exportEnrollmentReportServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ExportEnrollmentReportServiceImpl
        implements CommandApiService<ExportEnrollmentReportRequest, ExportEnrollmentReportResponse> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;
    private final EnrollmentReportGenerationService reportService;

    @Override
    public ExportEnrollmentReportResponse execCommand(ExportEnrollmentReportRequest request, JWTModel currentUser,
            String... args)
            throws Exception {

        log.info("匯出申報檔請求: type={}, start={}", request.getReportType(), request.getStartDate());

        LocalDate start = LocalDate.parse(request.getStartDate());
        LocalDate end = LocalDate.parse(request.getEndDate());

        // 查詢符合條件的記錄
        List<InsuranceEnrollment> enrollments = enrollmentRepository.findByDateRange(start, end);

        if (enrollments.isEmpty()) {
            throw new IllegalArgumentException("指定區間內無加退保記錄");
        }

        // 產生報表內容
        String content;
        if ("LABOR".equalsIgnoreCase(request.getReportType())) {
            content = reportService.generateLaborInsuranceReport(enrollments);
        } else if ("HEALTH".equalsIgnoreCase(request.getReportType())) {
            content = reportService.generateHealthInsuranceReport(enrollments);
        } else {
            throw new IllegalArgumentException("不支援的報表類型: " + request.getReportType());
        }

        // 編碼為 Base64
        String base64Content = reportService.encodeToBase64(content);
        String fileName = reportService.generateFileName(request.getReportType(), request.getStartDate(),
                request.getEndDate());

        return ExportEnrollmentReportResponse.builder()
                .fileContent(base64Content)
                .fileName(fileName)
                .totalRecords(enrollments.size())
                .build();
    }
}
