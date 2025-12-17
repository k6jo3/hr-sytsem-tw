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
 * 檢查身分證號唯一性服務實作
 */
@Service("checkNationalIdServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckNationalIdServiceImpl
        implements QueryApiService<Void, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(Void request,
                                           JWTModel currentUser,
                                           String... args) throws Exception {
        String nationalId = args[0];
        log.debug("Checking national ID uniqueness");

        boolean exists = employeeRepository.existsByNationalId(nationalId);

        return CheckUniqueResponse.builder()
                .value("***") // 不回傳身分證號
                .isUnique(!exists)
                .message(exists ? "身分證號已存在" : "身分證號可使用")
                .build();
    }
}
