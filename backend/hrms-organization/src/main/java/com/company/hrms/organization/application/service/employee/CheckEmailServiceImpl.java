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
 * 檢查 Email 唯一性服務實作
 */
@Service("checkEmailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckEmailServiceImpl
        implements QueryApiService<Void, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(Void request,
                                           JWTModel currentUser,
                                           String... args) throws Exception {
        String email = args[0];
        log.debug("Checking email uniqueness: {}", email);

        boolean exists = employeeRepository.existsByEmail(email);

        return CheckUniqueResponse.builder()
                .value(email)
                .isUnique(!exists)
                .message(exists ? "Email 已存在" : "Email 可使用")
                .build();
    }
}
