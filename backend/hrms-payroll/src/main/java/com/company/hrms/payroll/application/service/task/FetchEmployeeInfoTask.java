package com.company.hrms.payroll.application.service.task;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.payroll.infrastructure.client.organization.dto.EmployeeListResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 獲取員工資訊任務
 * 從組織服務獲取員工姓名與編號等基本資訊
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchEmployeeInfoTask implements PipelineTask<CalculatePayrollContext> {

    private final OrganizationServiceClient organizationServiceClient;

    @Override
    public void execute(CalculatePayrollContext context) {
        try {
            String orgId = context.getPayrollRun().getOrganizationId();
            log.info("正在從組織服務獲取員工資訊: orgId={}", orgId);

            EmployeeListResponseDto response = organizationServiceClient.getEmployeeList(
                    null, orgId, null, 0, 1000);

            if (response != null && response.getItems() != null) {
                context.setEmployeeInfoMap(response.getItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getId(),
                                item -> item,
                                (existing, replacement) -> existing)));
                log.info("成功載入 {} 筆員工資訊", response.getItems().size());
            }

        } catch (Exception e) {
            log.error("獲取員工資訊失敗: {}", e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "FetchEmployeeInfoTask";
    }
}
