package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.RescheduleInterviewRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import com.company.hrms.recruitment.infrastructure.mapper.InterviewMapper;

import lombok.RequiredArgsConstructor;

/**
 * 重新排程面試 Service
 */
@Service("rescheduleInterviewServiceImpl")
@Transactional
@RequiredArgsConstructor
public class RescheduleInterviewServiceImpl
                implements CommandApiService<RescheduleInterviewRequest, InterviewResponse> {

        private final IInterviewRepository interviewRepository;
        private final InterviewMapper interviewMapper;

        @Override
        public InterviewResponse execCommand(
                        RescheduleInterviewRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String interviewId = args[0];

                Interview interview = interviewRepository.findById(InterviewId.of(interviewId))
                                .orElseThrow(() -> new IllegalArgumentException("面試不存在：" + interviewId));

                // 呼叫 Domain 方法
                interview.reschedule(request.getNewInterviewDate(), request.getNewLocation());

                // 儲存
                Interview saved = interviewRepository.save(interview);

                return interviewMapper.toResponse(saved);
        }
}
