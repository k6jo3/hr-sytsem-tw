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
import com.company.hrms.organization.api.response.employee.EmployeeExperienceResponse;
import com.company.hrms.organization.domain.model.entity.WorkExperience;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IWorkExperienceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工工作經歷列表 Service
 */
@Service("getEmployeeExperiencesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeExperiencesServiceImpl implements QueryApiService<Object, List<EmployeeExperienceResponse>> {

    private final IEmployeeRepository employeeRepository;
    private final IWorkExperienceRepository workExperienceRepository;

    @Override
    public List<EmployeeExperienceResponse> getResponse(Object request, JWTModel currentUser, String... args)
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

        // 2. 查詢工作經歷列表
        List<WorkExperience> experiences = workExperienceRepository.findByEmployeeId(UUID.fromString(employeeIdStr));

        if (experiences == null) {
            return Collections.emptyList();
        }

        // 3. 轉換為 Response DTO
        return experiences.stream()
                .map(exp -> EmployeeExperienceResponse.builder()
                        .id(exp.getId().getValue().toString())
                        .employeeId(exp.getEmployeeId().toString())
                        .companyName(exp.getCompanyName())
                        .jobTitle(exp.getJobTitle())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .reasonForLeaving(exp.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
