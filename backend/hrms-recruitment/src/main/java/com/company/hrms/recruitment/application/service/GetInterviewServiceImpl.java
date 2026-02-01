package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import com.company.hrms.recruitment.infrastructure.mapper.InterviewMapper;

import lombok.RequiredArgsConstructor;

/**
 * 查詢面試詳情 Service
 */
@Service("getInterviewServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetInterviewServiceImpl
                implements QueryApiService<Object, InterviewResponse> {

        private final IInterviewRepository interviewRepository;
        private final InterviewMapper interviewMapper;

        @Override
        public InterviewResponse getResponse(
                        Object request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String interviewId = args[0];

                Interview interview = interviewRepository.findById(InterviewId.of(interviewId))
                                .orElseThrow(() -> new IllegalArgumentException("面試不存在：" + interviewId));

                return interviewMapper.toResponse(interview);
        }
}
