package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.CheckUniqueResponse;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 檢查員工編號唯一性服務實作
 */
@Service("checkEmployeeNumberServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckEmployeeNumberServiceImpl
        implements QueryApiService<Void, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(Void request,
                                           JWTModel currentUser,
                                           String... args) throws Exception {
        String employeeNumber = args[0];
        log.debug("Checking employee number uniqueness: {}", employeeNumber);

        boolean exists = employeeRepository.existsByEmployeeNumber(employeeNumber);

        return CheckUniqueResponse.builder()
                .value(employeeNumber)
                .isUnique(!exists)
                .message(exists ? "員工編號已存在" : "員工編號可使用")
                .build();
    }
}
