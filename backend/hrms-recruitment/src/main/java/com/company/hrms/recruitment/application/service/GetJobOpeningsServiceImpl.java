package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.job.JobOpeningSearchDto;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

@Service("getJobOpeningsServiceImpl")
public class GetJobOpeningsServiceImpl implements QueryApiService<JobOpeningSearchDto, Page<Object>> {

    @Autowired
    private IJobOpeningRepository jobOpeningRepository;

    @Override
    public Page<Object> getResponse(JobOpeningSearchDto request, JWTModel currentUser, String... args)
            throws Exception {
        // 1. Build QueryGroup from DTO
        QueryGroup queryGroup = QueryBuilder.fromCondition(request);

        // 2. Add Security/Role Filters - 軟刪除過濾
        queryGroup.eq("is_deleted", 0);

        // 3. Pageable
        return jobOpeningRepository.findAll(queryGroup, org.springframework.data.domain.Pageable.unpaged())
                .map(e -> (Object) e);
    }
}
