package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.CheckUniqueResponse;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 檢查 Email 唯一性 Service
 */
@Service("checkEmailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckEmailServiceImpl implements QueryApiService<String, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(String email, JWTModel currentUser, String... args) throws Exception {
        if (email == null || email.isBlank()) {
            return CheckUniqueResponse.notAvailable("Email 不可為空");
        }

        boolean exists = employeeRepository.existsByEmail(email);

        if (exists) {
            return CheckUniqueResponse.notAvailable("Email 已被使用");
        } else {
            return CheckUniqueResponse.available();
        }
    }
}
