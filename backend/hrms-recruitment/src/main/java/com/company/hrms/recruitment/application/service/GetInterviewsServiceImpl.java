package com.company.hrms.recruitment.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.InterviewSearchDto;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import com.company.hrms.recruitment.infrastructure.mapper.InterviewMapper;

import lombok.RequiredArgsConstructor;

/**
 * 查詢面試列表 Service
 */
@Service("getInterviewsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetInterviewsServiceImpl
                implements QueryApiService<InterviewSearchDto, Page<InterviewResponse>> {

        private final IInterviewRepository interviewRepository;
        private final InterviewMapper interviewMapper;

        @Override
        public Page<InterviewResponse> getResponse(
                        InterviewSearchDto request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 從 DTO 取得 QueryGroup
                QueryGroup query = QueryBuilder.fromCondition(request);
                Pageable pageable = request;

                // 查詢
                Page<Interview> page = interviewRepository.findAll(query, pageable);

                // 轉換為 Response
                return page.map(interviewMapper::toResponse);
        }
}
