package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.domain.model.entity.WorkExperience;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IWorkExperienceRepository;
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
 * 取得員工工作經歷服務實作
 */
@Service("getEmployeeExperiencesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeExperiencesServiceImpl
        implements QueryApiService<Void, GetEmployeeExperiencesServiceImpl.ExperienceListResponse> {

    private final IWorkExperienceRepository workExperienceRepository;

    @Override
    public ExperienceListResponse getResponse(Void request,
                                              JWTModel currentUser,
                                              String... args) throws Exception {
        String employeeId = args[0];
        log.info("Getting employee work experiences: {}", employeeId);

        List<WorkExperience> experiences = workExperienceRepository
                .findByEmployeeId(new EmployeeId(employeeId));

        List<ExperienceItemResponse> items = experiences.stream()
                .map(this::toExperienceItemResponse)
                .collect(Collectors.toList());

        return ExperienceListResponse.builder()
                .employeeId(employeeId)
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private ExperienceItemResponse toExperienceItemResponse(WorkExperience experience) {
        return ExperienceItemResponse.builder()
                .experienceId(experience.getId().getValue())
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .description(experience.getDescription())
                .build();
    }

    @Data
    @Builder
    public static class ExperienceListResponse {
        private String employeeId;
        private List<ExperienceItemResponse> items;
        private int totalCount;
    }

    @Data
    @Builder
    public static class ExperienceItemResponse {
        private String experienceId;
        private String companyName;
        private String jobTitle;
        private LocalDate startDate;
        private LocalDate endDate;
        private String description;
    }
}
