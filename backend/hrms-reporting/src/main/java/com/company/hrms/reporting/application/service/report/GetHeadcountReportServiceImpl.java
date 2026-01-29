package com.company.hrms.reporting.application.service.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountItem;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountSummary;

import lombok.RequiredArgsConstructor;

/**
 * 人力盤點報表 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getHeadcountReportServiceImpl")
@RequiredArgsConstructor
public class GetHeadcountReportServiceImpl
                implements QueryApiService<GetHeadcountReportRequest, HeadcountReportResponse> {

        @Override
        public HeadcountReportResponse getResponse(
                        GetHeadcountReportRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                request.setTenantId(currentUser.getTenantId());

                // TODO: 從 CQRS 讀模型查詢，並根據 dimension 進行分組統計
                List<HeadcountItem> items = createMockData(request.getDimension());
                HeadcountSummary summary = createMockSummary();

                return HeadcountReportResponse.builder()
                                .content(items)
                                .totalElements((long) items.size())
                                .totalPages((items.size() + request.getSize() - 1) / request.getSize())
                                .summary(summary)
                                .build();
        }

        private List<HeadcountItem> createMockData(String dimension) {
                List<HeadcountItem> items = new ArrayList<>();

                items.add(HeadcountItem.builder()
                                .dimensionName("資訊部")
                                .activeCount(45)
                                .probationCount(3)
                                .leaveCount(1)
                                .totalCount(49)
                                .maleCount(35)
                                .femaleCount(14)
                                .avgServiceYears(3.5)
                                .avgAge(32.5)
                                .build());

                items.add(HeadcountItem.builder()
                                .dimensionName("業務部")
                                .activeCount(30)
                                .probationCount(2)
                                .leaveCount(0)
                                .totalCount(32)
                                .maleCount(20)
                                .femaleCount(12)
                                .avgServiceYears(2.8)
                                .avgAge(30.2)
                                .build());

                return items;
        }

        private HeadcountSummary createMockSummary() {
                return HeadcountSummary.builder()
                                .totalActive(75)
                                .totalProbation(5)
                                .totalLeave(1)
                                .grandTotal(81)
                                .newHires(8)
                                .terminations(3)
                                .turnoverRate(3.7)
                                .build();
        }
}
