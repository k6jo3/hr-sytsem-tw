package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEducationRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得員工學歷服務實作
 */
@Service("getEmployeeEducationsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeEducationsServiceImpl
        implements QueryApiService<Void, GetEmployeeEducationsServiceImpl.EducationListResponse> {

    private final IEducationRepository educationRepository;

    @Override
    public EducationListResponse getResponse(Void request,
                                             JWTModel currentUser,
                                             String... args) throws Exception {
        String employeeId = args[0];
        log.info("Getting employee educations: {}", employeeId);

        List<Education> educations = educationRepository
                .findByEmployeeId(new EmployeeId(employeeId));

        List<EducationItemResponse> items = educations.stream()
                .map(this::toEducationItemResponse)
                .collect(Collectors.toList());

        return EducationListResponse.builder()
                .employeeId(employeeId)
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private EducationItemResponse toEducationItemResponse(Education education) {
        return EducationItemResponse.builder()
                .educationId(education.getId().getValue())
                .schoolName(education.getSchoolName())
                .degree(education.getDegree().name())
                .degreeDisplay(education.getDegree().getDisplayName())
                .major(education.getMajor())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .isGraduated(education.isGraduated())
                .build();
    }

    @Data
    @Builder
    public static class EducationListResponse {
        private String employeeId;
        private List<EducationItemResponse> items;
        private int totalCount;
    }

    @Data
    @Builder
    public static class EducationItemResponse {
        private String educationId;
        private String schoolName;
        private String degree;
        private String degreeDisplay;
        private String major;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isGraduated;
    }
}
