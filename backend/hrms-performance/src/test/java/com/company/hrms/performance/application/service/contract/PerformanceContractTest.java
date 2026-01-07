package com.company.hrms.performance.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;
import com.company.hrms.performance.application.service.assembler.PerformanceQueryAssembler;

/**
 * HR08 績效管理服務合約測試
 */
@DisplayName("HR08 績效管理服務合約測試")
public class PerformanceContractTest extends BaseContractTest {

    private final PerformanceQueryAssembler assembler = new PerformanceQueryAssembler();

    /**
     * 考核週期查詢合約
     */
    @Nested
    @DisplayName("考核週期查詢合約 (Performance Cycle Query Contract)")
    class CycleQueryContractTests {

        @Test
        @DisplayName("PFM_C001: 查詢草稿狀態的週期應包含狀態過濾條件")
        void searchDraftCycles_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("performance_cycle");
            var request = new GetCyclesRequest();
            request.setStatus(com.company.hrms.performance.domain.model.valueobject.CycleStatus.DRAFT);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C001");
        }

        @Test
        @DisplayName("PFM_C002: 查詢特定年份的週期")
        void searchByYear_ShouldIncludeYearFilter() throws Exception {
            String contract = loadContractSpec("performance_cycle");
            var request = new GetCyclesRequest();
            request.setYear(2025);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C002");
        }
    }

    /**
     * 我的考核查詢合約
     */
    @Nested
    @DisplayName("我的考核查詢合約 (My Reviews Query Contract)")
    class MyReviewsQueryContractTests {

        @Test
        @DisplayName("PFM_R001: 查詢我的考核應包含員工ID過濾條件")
        void searchMyReviews_ShouldIncludeEmployeeIdFilter() throws Exception {
            String contract = loadContractSpec("performance_review");
            var request = new GetMyReviewsRequest();
            request.setEmployeeId("EMP001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_R001");
        }
    }
}
