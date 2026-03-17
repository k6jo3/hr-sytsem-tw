package com.company.hrms.insurance.infrastructure.client;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 組織服務客戶端實作
 * 使用 REST API 調用 Organization 服務取得員工基本資訊
 *
 * <p>注意：Organization 服務的 EmployeeDetailResponse 欄位名稱與
 * EmployeeBasicInfo 不同，需手動映射：
 * <ul>
 *   <li>fullName → employeeName</li>
 *   <li>nationalId → idNumber</li>
 *   <li>dateOfBirth → birthDate</li>
 * </ul>
 */
@Component
@Slf4j
public class OrganizationClientImpl implements OrganizationClient {

    private final String organizationServiceUrl;
    private final RestTemplate restTemplate;

    public OrganizationClientImpl(
            @Value("${hrms.services.organization.url:http://localhost:8082}") String organizationServiceUrl) {
        this.organizationServiceUrl = organizationServiceUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<EmployeeBasicInfo> getEmployeeById(String employeeId) {
        try {
            log.debug("調用 Organization 服務: employeeId={}", employeeId);

            String url = organizationServiceUrl + "/api/v1/employees/" + employeeId;

            // 以 Map 接收回應，避免欄位名稱不匹配導致反序列化失敗
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                EmployeeBasicInfo info = mapToEmployeeBasicInfo(employeeId, response);
                log.info("取得員工資訊成功: employeeId={}, name={}", employeeId, info.getEmployeeName());
                return Optional.of(info);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("調用 Organization 服務失敗: employeeId={}, error={}", employeeId, e.getMessage());
            // 跨服務呼叫失敗時回傳空值，由呼叫端決定如何處理
            return Optional.empty();
        }
    }

    /**
     * 將 Organization 服務的 EmployeeDetailResponse 映射為 EmployeeBasicInfo
     *
     * <p>欄位對應：
     * Organization EmployeeDetailResponse → Insurance EmployeeBasicInfo
     * - fullName → employeeName
     * - nationalId → idNumber
     * - dateOfBirth → birthDate
     * - hireDate → hireDate
     * - gender → gender
     * - department.departmentName → departmentName
     * - jobTitle → jobTitle
     */
    @SuppressWarnings("unchecked")
    private EmployeeBasicInfo mapToEmployeeBasicInfo(String employeeId, Map<String, Object> response) {
        EmployeeBasicInfo.EmployeeBasicInfoBuilder builder = EmployeeBasicInfo.builder()
                .employeeId(employeeId)
                .employeeName(getStringValue(response, "fullName"))
                .idNumber(getStringValue(response, "nationalId"))
                .birthDate(getStringValue(response, "dateOfBirth"))
                .hireDate(getStringValue(response, "hireDate"))
                .gender(getStringValue(response, "gender"))
                .jobTitle(getStringValue(response, "jobTitle"));

        // 嘗試從巢狀的 department 物件取得部門名稱
        Object deptObj = response.get("department");
        if (deptObj instanceof Map) {
            Map<String, Object> dept = (Map<String, Object>) deptObj;
            builder.departmentName(getStringValue(dept, "departmentName"));
        }

        return builder.build();
    }

    /**
     * 安全取得 Map 中的字串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}
