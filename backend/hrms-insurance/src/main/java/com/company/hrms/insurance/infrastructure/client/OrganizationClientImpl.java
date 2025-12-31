package com.company.hrms.insurance.infrastructure.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 組織服務客戶端實作
 * 使用 REST API 調用 Organization 服務
 */
@Component
@Slf4j
public class OrganizationClientImpl implements OrganizationClient {

    private static final String ORGANIZATION_SERVICE_URL = "http://hrms-organization/api/v1/employees";

    private final RestTemplate restTemplate;

    public OrganizationClientImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<EmployeeBasicInfo> getEmployeeById(String employeeId) {
        try {
            log.debug("調用 Organization 服務: employeeId={}", employeeId);

            String url = ORGANIZATION_SERVICE_URL + "/" + employeeId;
            EmployeeBasicInfo info = restTemplate.getForObject(url, EmployeeBasicInfo.class);

            if (info != null) {
                log.info("取得員工資訊成功: employeeId={}, name={}", employeeId, info.getEmployeeName());
                return Optional.of(info);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("調用 Organization 服務失敗: employeeId={}, error={}", employeeId, e.getMessage());

            // 返回模擬資料 (開發階段)
            return Optional.of(EmployeeBasicInfo.builder()
                    .employeeId(employeeId)
                    .employeeName("員工" + employeeId)
                    .idNumber("A123456789")
                    .build());
        }
    }
}
