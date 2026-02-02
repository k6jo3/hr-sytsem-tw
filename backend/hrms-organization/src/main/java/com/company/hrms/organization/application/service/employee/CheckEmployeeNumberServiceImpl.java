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
 * 檢查員工編號唯一性 Service
 */
@Service("checkEmployeeNumberServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckEmployeeNumberServiceImpl implements QueryApiService<String, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(String employeeNumber, JWTModel currentUser, String... args)
            throws Exception {
        if (employeeNumber == null || employeeNumber.isBlank()) {
            return CheckUniqueResponse.notAvailable("員工編號不可為空");
        }

        boolean exists = employeeRepository.existsByEmployeeNumber(employeeNumber);

        if (exists) {
            return CheckUniqueResponse.notAvailable("員工編號已被使用");
        } else {
            return CheckUniqueResponse.available();
        }
    }
}
