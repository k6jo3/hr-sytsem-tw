package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.report.DashboardResponse;
import com.company.hrms.recruitment.application.dto.report.DashboardSearchDto;
import com.company.hrms.recruitment.application.service.context.DashboardContext;
import com.company.hrms.recruitment.application.service.task.BuildDashboardResponseTask;
import com.company.hrms.recruitment.application.service.task.CountCandidateStatsTask;
import com.company.hrms.recruitment.application.service.task.CountOpenJobsTask;
import com.company.hrms.recruitment.application.service.task.PrepareDashboardDatesTask;

import lombok.RequiredArgsConstructor;

/**
 * 取得招募儀表板 Service
 */
@Service("getDashboardServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetDashboardServiceImpl
                implements QueryApiService<DashboardSearchDto, DashboardResponse> {

        private final PrepareDashboardDatesTask prepareDatesTask;
        private final CountOpenJobsTask countOpenJobsTask;
        private final CountCandidateStatsTask countCandidateStatsTask;
        private final BuildDashboardResponseTask buildResponseTask;

        @Override
        public DashboardResponse getResponse(
                        DashboardSearchDto request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                DashboardContext context = new DashboardContext(request);

                BusinessPipeline.start(context)
                                .next(prepareDatesTask)
                                .next(countOpenJobsTask)
                                .next(countCandidateStatsTask)
                                .next(buildResponseTask)
                                .execute();

                return context.getResponse();
        }
}
