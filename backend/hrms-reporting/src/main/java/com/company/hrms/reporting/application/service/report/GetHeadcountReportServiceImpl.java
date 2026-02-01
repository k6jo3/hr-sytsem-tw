package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;
import com.company.hrms.reporting.application.service.report.task.BuildHeadcountReportResponseTask;
import com.company.hrms.reporting.application.service.report.task.CalculateHeadcountItemsTask;
import com.company.hrms.reporting.application.service.report.task.CalculateHeadcountSummaryTask;
import com.company.hrms.reporting.application.service.report.task.GroupHeadcountDataTask;
import com.company.hrms.reporting.application.service.report.task.LoadHeadcountDataTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 人力盤點報表 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getHeadcountReportServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class GetHeadcountReportServiceImpl
                implements QueryApiService<GetHeadcountReportRequest, HeadcountReportResponse> {

        private final LoadHeadcountDataTask loadHeadcountDataTask;
        private final GroupHeadcountDataTask groupHeadcountDataTask;
        private final CalculateHeadcountItemsTask calculateHeadcountItemsTask;
        private final CalculateHeadcountSummaryTask calculateHeadcountSummaryTask;
        private final BuildHeadcountReportResponseTask buildHeadcountReportResponseTask;

        @Override
        public HeadcountReportResponse getResponse(
                        GetHeadcountReportRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                request.setTenantId(currentUser.getTenantId());

                GetHeadcountReportContext ctx = new GetHeadcountReportContext(request, currentUser.getTenantId());

                BusinessPipeline.start(ctx)
                                .next(loadHeadcountDataTask)
                                .next(groupHeadcountDataTask)
                                .next(calculateHeadcountItemsTask)
                                .next(calculateHeadcountSummaryTask)
                                .next(buildHeadcountReportResponseTask)
                                .execute();

                return ctx.getResponse();
        }
}
