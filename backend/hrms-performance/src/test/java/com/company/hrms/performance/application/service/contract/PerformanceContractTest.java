package com.company.hrms.performance.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;
import com.company.hrms.performance.api.request.GetTeamReviewsRequest;
import com.company.hrms.performance.application.service.assembler.PerformanceQueryAssembler;
import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;

/**
 * HR08 績效管理服務合約測試
 * 涵蓋 14 個查詢場景：7 週期 + 4 我的考核 + 3 團隊考核
 */
@DisplayName("HR08 績效管理服務合約測試")
public class PerformanceContractTest extends BaseContractTest {

    private final PerformanceQueryAssembler assembler = new PerformanceQueryAssembler();

    /**
     * 考核週期查詢合約 (7 場景)
     */
    @Nested
    @DisplayName("考核週期查詢合約 (Performance Cycle Query Contract)")
    class CycleQueryContractTests {

        @Test
        @DisplayName("PFM_C001: 查詢進行中週期應包含狀態過濾條件")
        void searchInProgressCycles_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setStatus(CycleStatus.IN_PROGRESS);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C001");
        }

        @Test
        @DisplayName("PFM_C002: 查詢特定考核類型週期")
        void searchByType_ShouldIncludeCycleTypeFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setCycleType(CycleType.ANNUAL);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C002");
        }

        @Test
        @DisplayName("PFM_C003: 查詢特定年份的週期")
        void searchByYear_ShouldIncludeYearFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setYear(2025);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C003");
        }

        @Test
        @DisplayName("PFM_C004: 查詢草稿週期")
        void searchDraftCycles_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setStatus(CycleStatus.DRAFT);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C004");
        }

        @Test
        @DisplayName("PFM_C005: 查詢已完成週期")
        void searchCompletedCycles_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setStatus(CycleStatus.COMPLETED);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C005");
        }

        @Test
        @DisplayName("PFM_C006: 組合查詢 - 年份+類型")
        void searchByYearAndType_ShouldIncludeBothFilters() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setYear(2025);
            request.setCycleType(CycleType.QUARTERLY);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C006");
        }

        @Test
        @DisplayName("PFM_C007: 組合查詢 - 年份+狀態+類型")
        void searchByYearStatusAndType_ShouldIncludeAllFilters() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetCyclesRequest();
            request.setYear(2025);
            request.setStatus(CycleStatus.IN_PROGRESS);
            request.setCycleType(CycleType.ANNUAL);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_C007");
        }
    }

    /**
     * 我的考核查詢合約 (4 場景)
     */
    @Nested
    @DisplayName("我的考核查詢合約 (My Reviews Query Contract)")
    class MyReviewsQueryContractTests {

        @Test
        @DisplayName("PFM_R001: 查詢我的考核應包含員工ID過濾條件")
        void searchMyReviews_ShouldIncludeEmployeeIdFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetMyReviewsRequest();
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_R001");
        }

        @Test
        @DisplayName("PFM_R002: 查詢我的考核 - 依週期")
        void searchMyReviewsByCycle_ShouldIncludeCycleIdFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetMyReviewsRequest();
            request.setCycleId("CYC001");
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_R002");
        }

        @Test
        @DisplayName("PFM_R003: 查詢我的考核 - 依狀態篩選待自評")
        void searchMyReviewsByStatus_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetMyReviewsRequest();
            request.setEmployeeId("E001");
            request.setStatus(ReviewStatus.PENDING_SELF);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_R003");
        }

        @Test
        @DisplayName("PFM_R004: 查詢我的已完成考核")
        void searchMyFinalizedReviews_ShouldIncludeFinalizedStatusFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetMyReviewsRequest();
            request.setEmployeeId("E001");
            request.setStatus(ReviewStatus.FINALIZED);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_R004");
        }
    }

    /**
     * 團隊考核查詢合約 (3 場景)
     */
    @Nested
    @DisplayName("團隊考核查詢合約 (Team Reviews Query Contract)")
    class TeamReviewsQueryContractTests {

        @Test
        @DisplayName("PFM_T001: 查詢團隊考核 - 依評核者")
        void searchTeamReviews_ShouldIncludeReviewerIdFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetTeamReviewsRequest();
            request.setReviewerId("MGR001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_T001");
        }

        @Test
        @DisplayName("PFM_T002: 查詢團隊考核 - 依週期+評核者")
        void searchTeamReviewsByCycleAndReviewer_ShouldIncludeBothFilters() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetTeamReviewsRequest();
            request.setCycleId("CYC001");
            request.setReviewerId("MGR001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_T002");
        }

        @Test
        @DisplayName("PFM_T003: 查詢團隊考核 - 僅依週期")
        void searchTeamReviewsByCycle_ShouldIncludeCycleIdFilter() throws Exception {
            String contract = loadContractSpec("performance");
            var request = new GetTeamReviewsRequest();
            request.setCycleId("CYC001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PFM_T003");
        }
    }
}
