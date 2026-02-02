package com.company.hrms.organization.application.service.employee;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.EmployeeEducationResponse;
import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEducationRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工學歷列表 Service
 */
@Service("getEmployeeEducationsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeEducationsServiceImpl implements QueryApiService<Object, List<EmployeeEducationResponse>> {

    private final IEmployeeRepository employeeRepository;
    private final IEducationRepository educationRepository;

    @Override
    public List<EmployeeEducationResponse> getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 驗證員工存在
        if (!employeeRepository.existsById(employeeId)) {
            throw new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr);
        }

        // 2. 查詢學歷列表
        List<Education> educations = educationRepository.findByEmployeeId(UUID.fromString(employeeIdStr));

        if (educations == null) {
            return Collections.emptyList();
        }

        // 3. 轉換為 Response DTO
        return educations.stream()
                .map(edu -> EmployeeEducationResponse.builder()
                        .id(edu.getId().getValue().toString())
                        .employeeId(edu.getEmployeeId().toString())
                        .schoolName(edu.getSchool())
                        .major(edu.getMajor())
                        .degree(edu.getDegree() != null ? edu.getDegree().name() : "")
                        .startDate(edu.getStartDate())
                        .endDate(edu.getEndDate())
                        .isHighest(edu.isHighestDegree())
                        .build())
                .collect(Collectors.toList());
    }
}
