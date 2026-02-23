package com.company.hrms.recruitment.application.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.recruitment.application.assembler.RecruitmentQueryAssembler;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;

/**
 * HR09 招聘模組合約測試
 * 涵蓋 7 個職缺查詢場景
 *
 * 注意事項：
 * - 職缺：使用 Assembler 內部 @QueryCondition 類別 + QueryBuilder.fromCondition()
 * - fromCondition() 會自動處理 @QueryCondition.LIKE（自動包裹 %）
 * - fromDto() 只處理 @QueryFilter，此處不使用
 *
 * 應徵者/面試/Offer 查詢測試已規劃，待相關 DTO 完備後啟用。
 */
public class RecruitmentContractTest extends BaseContractTest {

    // ========== 職缺查詢測試 (7 場景) ==========

    /**
     * 使用 RecruitmentQueryAssembler.JobOpeningCondition +
     * QueryBuilder.fromCondition()
     *
     * @QueryCondition.LIKE 自動包裹 %，勿手動加入
     */
    @Nested
    @DisplayName("職缺查詢合約測試")
    class JobOpeningQueryContractTests {

        @Test
        @DisplayName("RCT_J001 - 查詢開放中職缺應包含狀態篩選條件")
        void searchOpenJobs_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("recruitment");

            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setStatus(JobStatus.OPEN);
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            assertContract(query, contract, "RCT_J001");
        }

        @Test
        @DisplayName("RCT_J002 - 依部門查詢職缺應包含部門篩選條件")
        void searchByDepartment_ShouldIncludeDeptFilter() throws Exception {
            String contract = loadContractSpec("recruitment");

            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            // 合約 RCT_J002 指定部門 ID 為 'D001'
            condition.setDepartmentId("D001");
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            assertContract(query, contract, "RCT_J002");
        }

        @Test
        @DisplayName("RCT_J003 - 查詢草稿職缺應包含狀態篩選條件")
        void searchDraftJobs_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("recruitment");

            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setStatus(JobStatus.DRAFT);
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            assertContract(query, contract, "RCT_J003");
        }

        @Test
        @DisplayName("RCT_J004 - 查詢已關閉職缺應包含狀態篩選條件")
        void searchClosedJobs_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("recruitment");

            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setStatus(JobStatus.CLOSED);
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            assertContract(query, contract, "RCT_J004");
        }

        @Test
        @DisplayName("RCT_J005 - 依狀態與部門查詢職缺應同時包含兩個篩選條件")
        void searchByStatusAndDept_ShouldIncludeBothFilters() throws Exception {
            String contract = loadContractSpec("recruitment");

            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setStatus(JobStatus.OPEN);
            // 合約 RCT_J005 指定部門 ID 為 'D001'
            condition.setDepartmentId("D001");
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            assertContract(query, contract, "RCT_J005");
        }

        @Test
        @DisplayName("RCT_J006 - 依關鍵字查詢職缺應包含關鍵字 OR 條件群組")
        void searchByKeyword_ShouldIncludeKeywordFilters() throws Exception {
            String contract = loadContractSpec("recruitment");

            // 主群組：is_deleted = 0
            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            // 關鍵字 OR 子群組：@LIKE 注解自動加 % 前後綴，此處傳入原始關鍵字即可
            var kwCondition = new RecruitmentQueryAssembler.JobOpeningKeywordCondition();
            kwCondition.setTitle("工程師");
            kwCondition.setRequirements("工程師");
            QueryGroup kwGroup = QueryBuilder.fromCondition(kwCondition);
            kwGroup.setJunction(LogicalOp.OR);
            query.addSubGroup(kwGroup);

            assertContract(query, contract, "RCT_J006");
        }

        @Test
        @DisplayName("RCT_J007 - 依狀態與關鍵字查詢職缺應同時包含所有篩選條件")
        void searchByStatusAndKeyword_ShouldIncludeAllFilters() throws Exception {
            String contract = loadContractSpec("recruitment");

            // 主群組：status = OPEN, is_deleted = 0
            var condition = new RecruitmentQueryAssembler.JobOpeningCondition();
            condition.setStatus(JobStatus.OPEN);
            condition.setIsDeleted(0);
            QueryGroup query = QueryBuilder.fromCondition(condition);

            // 關鍵字 OR 子群組：@LIKE 注解自動加 % 前後綴，此處傳入原始關鍵字即可
            var kwCondition = new RecruitmentQueryAssembler.JobOpeningKeywordCondition();
            kwCondition.setTitle("工程師");
            kwCondition.setRequirements("工程師");
            QueryGroup kwGroup = QueryBuilder.fromCondition(kwCondition);
            kwGroup.setJunction(LogicalOp.OR);
            query.addSubGroup(kwGroup);

            assertContract(query, contract, "RCT_J007");
        }
    }

    // ========== 應徵者查詢測試 (5 場景) ==========
    // 以下測試尚在計畫中，待 CandidateCondition 測試基礎設施完備後啟用

    // @Nested
    // @DisplayName("應徵者查詢合約測試")
    // class CandidateQueryContractTests {
    //
    // @Test
    // @DisplayName("RCT_CD001 - 依職缺查詢應徵者應包含職缺 ID 篩選條件")
    // void searchByOpening_ShouldIncludeOpeningIdFilter() throws Exception {
    // String contract = loadContractSpec("recruitment");
    // var condition = new RecruitmentQueryAssembler.CandidateCondition();
    // condition.setOpeningId(java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    // QueryGroup query = QueryBuilder.fromCondition(condition);
    // assertContract(query, contract, "RCT_CD001");
    // }
    //
    // @Test
    // @DisplayName("RCT_CD002 - 依狀態查詢應徵者應包含狀態篩選條件")
    // void searchByStatus_ShouldIncludeStatusFilter() throws Exception {
    // String contract = loadContractSpec("recruitment");
    // var condition = new RecruitmentQueryAssembler.CandidateCondition();
    // // condition.setStatus(CandidateStatus.SCREENING);
    // QueryGroup query = QueryBuilder.fromCondition(condition);
    // assertContract(query, contract, "RCT_CD002");
    // }
    //
    // @Test
    // @DisplayName("RCT_CD003 - 依關鍵字查詢應徵者應包含關鍵字 OR 條件群組")
    // void searchByKeyword_ShouldIncludeKeywordFilters() throws Exception {
    // String contract = loadContractSpec("recruitment");
    // QueryGroup query = new QueryGroup(LogicalOp.AND);
    // var kwCondition = new RecruitmentQueryAssembler.CandidateKeywordCondition();
    // kwCondition.setFullName("John");
    // kwCondition.setEmail("John");
    // QueryGroup kwGroup = QueryBuilder.fromCondition(kwCondition);
    // kwGroup.setJunction(LogicalOp.OR);
    // query.addSubGroup(kwGroup);
    // assertContract(query, contract, "RCT_CD003");
    // }
    //
    // @Test
    // @DisplayName("RCT_CD004 - 依職缺與狀態查詢應徵者應同時包含兩個篩選條件")
    // void searchByOpeningAndStatus_ShouldIncludeBothFilters() throws Exception {
    // String contract = loadContractSpec("recruitment");
    // var condition = new RecruitmentQueryAssembler.CandidateCondition();
    // condition.setOpeningId(java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    // // condition.setStatus(CandidateStatus.NEW);
    // QueryGroup query = QueryBuilder.fromCondition(condition);
    // assertContract(query, contract, "RCT_CD004");
    // }
    //
    // @Test
    // @DisplayName("RCT_CD005 - 查詢已錄取應徵者應包含狀態篩選條件")
    // void searchHiredCandidates_ShouldIncludeStatusFilter() throws Exception {
    // String contract = loadContractSpec("recruitment");
    // var condition = new RecruitmentQueryAssembler.CandidateCondition();
    // // condition.setStatus(CandidateStatus.HIRED);
    // QueryGroup query = QueryBuilder.fromCondition(condition);
    // assertContract(query, contract, "RCT_CD005");
    // }
    // }

    // ========== 面試查詢測試 (6 場景) ==========
    // 以下測試尚在計畫中，待 InterviewSearchDto 測試基礎設施完備後啟用

    // @Nested
    // @DisplayName("面試查詢合約測試")
    // class InterviewQueryContractTests {
    //
    // @Test
    // @DisplayName("RCT_I001 - 依應徵者 ID 查詢面試應包含應徵者篩選條件")
    // void searchByCandidateId_ShouldIncludeCandidateFilter() throws Exception {
    // // 待實作：需要 InterviewCondition 配合 toInterviewQuery assembler 方法
    // }
    //
    // @Test
    // @DisplayName("RCT_I002 - 依狀態查詢面試應包含狀態篩選條件")
    // void searchByStatus_ShouldIncludeStatusFilter() throws Exception {
    // // 待實作
    // }
    //
    // @Test
    // @DisplayName("RCT_I006 - 依應徵者與狀態查詢面試應同時包含兩個篩選條件")
    // void searchByCandidateAndStatus_ShouldIncludeBothFilters() throws Exception {
    // // 待實作
    // }
    // }

    // ========== Offer 查詢測試 (5 場景) ==========
    // 以下測試尚在計畫中，待 OfferSearchDto 測試基礎設施完備後啟用

    // @Nested
    // @DisplayName("Offer 查詢合約測試")
    // class OfferQueryContractTests {
    //
    // @Test
    // @DisplayName("RCT_O001 - 依應徵者 ID 查詢 Offer 應包含應徵者篩選條件")
    // void searchByCandidateId_ShouldIncludeCandidateFilter() throws Exception {
    // // 待實作：需要 OfferCondition 配合 toOfferQuery assembler 方法
    // }
    //
    // @Test
    // @DisplayName("RCT_O005 - 依應徵者與狀態查詢 Offer 應同時包含兩個篩選條件")
    // void searchByCandidateAndStatus_ShouldIncludeBothFilters() throws Exception {
    // // 待實作
    // }
    // }
}
