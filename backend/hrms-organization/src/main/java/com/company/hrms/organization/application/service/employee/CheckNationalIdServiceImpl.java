package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.CheckUniqueResponse;
import com.company.hrms.organization.domain.model.valueobject.NationalId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 檢查身分證號唯一性 Service
 */
@Service("checkNationalIdServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckNationalIdServiceImpl implements QueryApiService<String, CheckUniqueResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public CheckUniqueResponse getResponse(String nationalIdStr, JWTModel currentUser, String... args)
            throws Exception {
        if (nationalIdStr == null || nationalIdStr.isBlank()) {
            return CheckUniqueResponse.notAvailable("身分證號不可為空");
        }

        try {
            NationalId nationalId = new NationalId(nationalIdStr);
            boolean exists = employeeRepository.existsByNationalId(nationalId);

            if (exists) {
                return CheckUniqueResponse.notAvailable("身分證號已被使用");
            } else {
                return CheckUniqueResponse.available();
            }
        } catch (IllegalArgumentException e) {
            return CheckUniqueResponse.notAvailable("身分證號格式錯誤");
        }
    }
}
