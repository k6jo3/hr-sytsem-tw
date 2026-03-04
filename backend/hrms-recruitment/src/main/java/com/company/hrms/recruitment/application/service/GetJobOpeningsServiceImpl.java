package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.assembler.RecruitmentQueryAssembler;
import com.company.hrms.recruitment.application.dto.job.JobOpeningSearchDto;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

@Service("getJobOpeningsServiceImpl")
public class GetJobOpeningsServiceImpl implements QueryApiService<JobOpeningSearchDto, Page<Object>> {

    @Autowired
    private IJobOpeningRepository jobOpeningRepository;

    @Autowired
    private RecruitmentQueryAssembler queryAssembler;

    @Override
    public Page<Object> getResponse(JobOpeningSearchDto request, JWTModel currentUser, String... args)
            throws Exception {
        // 1. 解析狀態（字串轉 enum，null 安全）
        JobStatus status = null;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            status = JobStatus.valueOf(request.getStatus());
        }

        // 2. 使用 RecruitmentQueryAssembler 組裝查詢條件
        // - 自動加入 is_deleted = 0（軟刪除過濾）
        // - keyword 時自動產生 OR 子群組（title LIKE OR requirements LIKE）
        QueryGroup queryGroup = queryAssembler.toJobOpeningQuery(
                status,
                request.getDepartmentId(),
                request.getKeyword());

        // 3. 執行查詢（分頁由 Pageable 控制）
        // Pageable.unpaged() 不支援 getOffset()，改用 PageRequest
        return jobOpeningRepository.findAll(queryGroup, PageRequest.of(0, 10000))
                .map(e -> (Object) e);
    }
}
