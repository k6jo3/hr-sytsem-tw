package com.company.hrms.insurance.domain.service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.infrastructure.client.EmployeeBasicInfo;
import com.company.hrms.insurance.infrastructure.client.OrganizationClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 申報檔案產生服務
 * 產生勞保局/健保局規範格式的申報檔案
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentReportGenerationService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String FIELD_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\r\n";

    private final OrganizationClient organizationClient;

    /**
     * 產生勞保申報檔 (加保/退保)
     * 格式: 身分證字號,姓名,加退保日期,投保薪資,加退保類別
     */
    public String generateLaborInsuranceReport(List<InsuranceEnrollment> enrollments) {
        log.info("產生勞保申報檔: {} 筆", enrollments.size());

        // 批量取得員工資訊
        Map<String, EmployeeBasicInfo> employeeMap = getEmployeeInfoMap(enrollments);

        StringBuilder sb = new StringBuilder();

        // 檔頭
        sb.append("身分證字號,姓名,加退保日期,投保薪資,加退保類別").append(LINE_SEPARATOR);

        // 資料行
        for (InsuranceEnrollment enrollment : enrollments) {
            if (enrollment.getInsuranceType() != InsuranceType.LABOR)
                continue;

            EmployeeBasicInfo emp = employeeMap.get(enrollment.getEmployeeId());
            String idNumber = emp != null ? emp.getIdNumber() : "";
            String empName = emp != null ? emp.getEmployeeName() : "未知";

            String actionType = enrollment.getStatus() == EnrollmentStatus.ACTIVE ? "1" : "2";
            String actionDate = enrollment.getStatus() == EnrollmentStatus.ACTIVE
                    ? enrollment.getEnrollDate().format(DATE_FORMAT)
                    : enrollment.getWithdrawDate().format(DATE_FORMAT);

            sb.append(idNumber).append(FIELD_SEPARATOR);
            sb.append(empName).append(FIELD_SEPARATOR);
            sb.append(actionDate).append(FIELD_SEPARATOR);
            sb.append(enrollment.getMonthlySalary().intValue()).append(FIELD_SEPARATOR);
            sb.append(actionType);
            sb.append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * 產生健保申報檔 (加保/退保)
     * 格式: 身分證字號,姓名,加退保日期,投保金額,加退保類別,眷屬人數
     */
    public String generateHealthInsuranceReport(List<InsuranceEnrollment> enrollments) {
        return generateHealthInsuranceReport(enrollments, null);
    }

    /**
     * 產生健保申報檔 (含眷屬人數)
     * 
     * @param enrollments       加保記錄
     * @param dependentCountMap 員工ID -> 眷屬人數
     */
    public String generateHealthInsuranceReport(
            List<InsuranceEnrollment> enrollments,
            Map<String, Integer> dependentCountMap) {

        log.info("產生健保申報檔: {} 筆", enrollments.size());

        // 批量取得員工資訊
        Map<String, EmployeeBasicInfo> employeeMap = getEmployeeInfoMap(enrollments);

        StringBuilder sb = new StringBuilder();

        // 檔頭
        sb.append("身分證字號,姓名,加退保日期,投保金額,加退保類別,眷屬人數").append(LINE_SEPARATOR);

        // 資料行
        for (InsuranceEnrollment enrollment : enrollments) {
            if (enrollment.getInsuranceType() != InsuranceType.HEALTH)
                continue;

            EmployeeBasicInfo emp = employeeMap.get(enrollment.getEmployeeId());
            String idNumber = emp != null ? emp.getIdNumber() : "";
            String empName = emp != null ? emp.getEmployeeName() : "未知";

            String actionType = enrollment.getStatus() == EnrollmentStatus.ACTIVE ? "1" : "2";
            String actionDate = enrollment.getStatus() == EnrollmentStatus.ACTIVE
                    ? enrollment.getEnrollDate().format(DATE_FORMAT)
                    : enrollment.getWithdrawDate().format(DATE_FORMAT);

            // 眷屬人數
            int dependentCount = dependentCountMap != null
                    ? dependentCountMap.getOrDefault(enrollment.getEmployeeId(), 0)
                    : 0;

            sb.append(idNumber).append(FIELD_SEPARATOR);
            sb.append(empName).append(FIELD_SEPARATOR);
            sb.append(actionDate).append(FIELD_SEPARATOR);
            sb.append(enrollment.getMonthlySalary().intValue()).append(FIELD_SEPARATOR);
            sb.append(actionType).append(FIELD_SEPARATOR);
            sb.append(dependentCount);
            sb.append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * 批量取得員工資訊
     */
    private Map<String, EmployeeBasicInfo> getEmployeeInfoMap(List<InsuranceEnrollment> enrollments) {
        List<String> employeeIds = enrollments.stream()
                .map(InsuranceEnrollment::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());

        return employeeIds.stream()
                .map(id -> organizationClient.getEmployeeById(id).orElse(null))
                .filter(info -> info != null)
                .collect(Collectors.toMap(
                        EmployeeBasicInfo::getEmployeeId,
                        Function.identity(),
                        (a, b) -> a));
    }

    /**
     * 將內容轉換為 Base64 編碼
     */
    public String encodeToBase64(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 產生檔案名稱
     */
    public String generateFileName(String type, String startDate, String endDate) {
        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("%s_enrollment_report_%s_%s_%s.csv", type, startDate, endDate, timestamp);
    }
}
