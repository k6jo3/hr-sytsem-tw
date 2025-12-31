package com.company.hrms.organization.application.service.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;

/**
 * 組織服務合約測試
 */
@DisplayName("組織服務合約測試")
public class OrganizationContractTest extends BaseContractTest {

    @Nested
    @DisplayName("員工查詢合約 (Employee Query Contract)")
    class EmployeeQueryContractTests {

        private final com.company.hrms.organization.application.service.employee.assembler.EmployeeQueryAssembler queryAssembler = new com.company.hrms.organization.application.service.employee.assembler.EmployeeQueryAssembler();

        @Test
        @DisplayName("ORG_E001: 查詢在職員工")
        void searchActiveEmployees_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("organization");
            var request = com.company.hrms.organization.api.request.employee.GetEmployeeListRequest.builder()
                    .status("ACTIVE")
                    .build();

            var query = queryAssembler.toQueryGroup(request);

            assertContract(query, contract, "ORG_E001");
        }

        @Test
        @DisplayName("ORG_E003: 依部門查詢員工")
        void searchByDepartment_ShouldIncludeDeptFilter() throws Exception {
            String contract = loadContractSpec("organization");
            var request = com.company.hrms.organization.api.request.employee.GetEmployeeListRequest.builder()
                    .deptId("D001")
                    .build();

            var query = queryAssembler.toQueryGroup(request);

            assertContract(query, contract, "ORG_E003");
        }

        @Test
        @DisplayName("ORG_E004: 依姓名模糊查詢")
        void searchByName_ShouldIncludeLikeFilter() throws Exception {
            String contract = loadContractSpec("organization");
            var request = com.company.hrms.organization.api.request.employee.GetEmployeeListRequest.builder()
                    .name("王")
                    .build();

            var query = queryAssembler.toQueryGroup(request);

            assertContract(query, contract, "ORG_E004");
        }

        @Test
        @DisplayName("ORG_E011: 依到職日期範圍查詢")
        void searchByHireDate_ShouldIncludeDateRangeFilter() throws Exception {
            String contract = loadContractSpec("organization");
            var request = com.company.hrms.organization.api.request.employee.GetEmployeeListRequest.builder()
                    .hireStartDate(LocalDate.parse("2025-01-01"))
                    .build();

            var query = queryAssembler.toQueryGroup(request);

            assertContract(query, contract, "ORG_E011");
        }
    }
}
