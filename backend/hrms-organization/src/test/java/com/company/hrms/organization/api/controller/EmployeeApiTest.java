package com.company.hrms.organization.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.GetEmployeeListRequest;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.CreateEmployeeResponse;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.api.response.employee.EmployeeListItemResponse;
import com.company.hrms.organization.api.response.employee.PromoteEmployeeResponse;
import com.company.hrms.organization.api.response.employee.TerminateEmployeeResponse;
import com.company.hrms.organization.api.response.employee.TransferEmployeeResponse;
import com.company.hrms.organization.application.service.employee.CreateEmployeeServiceImpl;
import com.company.hrms.organization.application.service.employee.GetEmployeeDetailServiceImpl;
import com.company.hrms.organization.application.service.employee.GetEmployeeListServiceImpl;
import com.company.hrms.organization.application.service.employee.PromoteEmployeeServiceImpl;
import com.company.hrms.organization.application.service.employee.TerminateEmployeeServiceImpl;
import com.company.hrms.organization.application.service.employee.TransferEmployeeServiceImpl;
import com.company.hrms.organization.application.service.employee.UpdateEmployeeServiceImpl;

/**
 * HR02 員工管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>員工建立、更新 (Command)</li>
 * <li>部門調動、升遷、離職 (Command)</li>
 * <li>員工列表查詢、單一員工查詢 (Query)</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR02 員工管理 API 合約測試")
public class EmployeeApiTest extends BaseApiContractTest {

        @MockBean(name = "createEmployeeServiceImpl")
        private CreateEmployeeServiceImpl createEmployeeService;

        @MockBean(name = "updateEmployeeServiceImpl")
        private UpdateEmployeeServiceImpl updateEmployeeService;

        @MockBean(name = "transferEmployeeServiceImpl")
        private TransferEmployeeServiceImpl transferEmployeeService;

        @MockBean(name = "promoteEmployeeServiceImpl")
        private PromoteEmployeeServiceImpl promoteEmployeeService;

        @MockBean(name = "terminateEmployeeServiceImpl")
        private TerminateEmployeeServiceImpl terminateEmployeeService;

        @MockBean(name = "getEmployeeListServiceImpl")
        private GetEmployeeListServiceImpl getEmployeeListService;

        @MockBean(name = "getEmployeeDetailServiceImpl")
        private GetEmployeeDetailServiceImpl getEmployeeDetailService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("hr-user");
                mockUser.setUsername("hr_admin");
                mockUser.setRoles(Collections.singletonList("HR"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("employee:create"));
                authorities.add(new SimpleGrantedAuthority("employee:update"));
                authorities.add(new SimpleGrantedAuthority("employee:transfer"));
                authorities.add(new SimpleGrantedAuthority("employee:promote"));
                authorities.add(new SimpleGrantedAuthority("employee:terminate"));
                authorities.add(new SimpleGrantedAuthority("employee:read"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 員工命令 API 測試
         */
        @Nested
        @DisplayName("員工命令 API")
        public class EmployeeCommandApiTests {

                @Test
                @DisplayName("ORG_EMP_001: 新增員工 - 應回傳員工 ID")
                void createEmployee_ShouldReturnEmployeeId() throws Exception {
                        // Arrange
                        CreateEmployeeRequest request = new CreateEmployeeRequest();
                        request.setEmployeeNumber("EMP-2025-001");
                        request.setLastName("王");
                        request.setFirstName("小明");
                        request.setNationalId("A123456789");
                        request.setDateOfBirth(LocalDate.of(1990, 1, 15));
                        request.setGender("MALE");
                        request.setCompanyEmail("xiaoming.wang@company.com");
                        request.setMobilePhone("0912345678");
                        request.setOrganizationId("org-001");
                        request.setDepartmentId("dept-001");
                        request.setEmploymentType("FULL_TIME");
                        request.setHireDate(LocalDate.of(2025, 2, 1));

                        CreateEmployeeResponse response = CreateEmployeeResponse.builder()
                                        .employeeId("emp-001")
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .message("員工建立成功")
                                        .build();

                        when(createEmployeeService.execCommand(any(CreateEmployeeRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/employees", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value("emp-001"))
                                        .andExpect(jsonPath("$.employeeNumber").value("EMP-2025-001"))
                                        .andExpect(jsonPath("$.fullName").value("王小明"))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("ORG_EMP_002: 更新員工 - 應回傳更新後的員工資訊")
                void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";
                        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
                        request.setMobilePhone("0987654321");
                        request.setJobTitle("資深工程師");

                        EmployeeDetailResponse response = EmployeeDetailResponse.builder()
                                        .employeeId(employeeId)
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .mobilePhone("0987654321")
                                        .jobTitle("資深工程師")
                                        .employmentStatus("ACTIVE")
                                        .build();

                        when(updateEmployeeService.execCommand(any(UpdateEmployeeRequest.class), any(JWTModel.class),
                                        eq(employeeId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/employees/" + employeeId, request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value(employeeId))
                                        .andExpect(jsonPath("$.mobilePhone").value("0987654321"))
                                        .andExpect(jsonPath("$.jobTitle").value("資深工程師"));
                }

                @Test
                @DisplayName("ORG_EMP_003: 部門調動 - 應回傳調動資訊")
                void transferEmployee_ShouldReturnTransferInfo() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";
                        TransferEmployeeRequest request = new TransferEmployeeRequest();
                        request.setNewDepartmentId("dept-002");
                        request.setEffectiveDate(LocalDate.of(2025, 3, 1));
                        request.setReason("組織調整");

                        TransferEmployeeResponse response = TransferEmployeeResponse.builder()
                                        .employeeId(employeeId)
                                        .oldDepartmentName("研發一部")
                                        .newDepartmentName("研發二部")
                                        .effectiveDate(LocalDate.of(2025, 3, 1))
                                        .message("部門調動成功")
                                        .build();

                        when(transferEmployeeService.execCommand(any(TransferEmployeeRequest.class),
                                        any(JWTModel.class),
                                        eq(employeeId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/employees/" + employeeId + "/transfer", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value(employeeId))
                                        .andExpect(jsonPath("$.oldDepartmentName").value("研發一部"))
                                        .andExpect(jsonPath("$.newDepartmentName").value("研發二部"))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("ORG_EMP_004: 員工升遷 - 應回傳升遷資訊")
                void promoteEmployee_ShouldReturnPromoteInfo() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";
                        PromoteEmployeeRequest request = new PromoteEmployeeRequest();
                        request.setNewJobTitle("技術經理");
                        request.setNewJobLevel("M1");
                        request.setEffectiveDate(LocalDate.of(2025, 4, 1));
                        request.setReason("年度晉升");

                        PromoteEmployeeResponse response = PromoteEmployeeResponse.builder()
                                        .employeeId(employeeId)
                                        .oldJobTitle("資深工程師")
                                        .oldJobLevel("S3")
                                        .newJobTitle("技術經理")
                                        .newJobLevel("M1")
                                        .effectiveDate(LocalDate.of(2025, 4, 1))
                                        .message("升遷成功")
                                        .build();

                        when(promoteEmployeeService.execCommand(any(PromoteEmployeeRequest.class), any(JWTModel.class),
                                        eq(employeeId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/employees/" + employeeId + "/promote", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value(employeeId))
                                        .andExpect(jsonPath("$.newJobTitle").value("技術經理"))
                                        .andExpect(jsonPath("$.newJobLevel").value("M1"))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("ORG_EMP_005: 員工離職 - 應回傳離職資訊（含離職類型）")
                void terminateEmployee_ShouldReturnTerminateInfo() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";
                        TerminateEmployeeRequest request = new TerminateEmployeeRequest();
                        request.setTerminationDate(LocalDate.of(2025, 5, 31));
                        request.setReason("個人生涯規劃");
                        request.setTerminationType("VOLUNTARY_RESIGNATION");

                        TerminateEmployeeResponse response = TerminateEmployeeResponse.builder()
                                        .employeeId(employeeId)
                                        .terminationDate(LocalDate.of(2025, 5, 31))
                                        .terminationType("VOLUNTARY_RESIGNATION")
                                        .noticePeriodDays(20)
                                        .message("離職處理成功")
                                        .build();

                        when(terminateEmployeeService.execCommand(any(TerminateEmployeeRequest.class),
                                        any(JWTModel.class),
                                        eq(employeeId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/employees/" + employeeId + "/terminate", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value(employeeId))
                                        .andExpect(jsonPath("$.terminationType").value("VOLUNTARY_RESIGNATION"))
                                        .andExpect(jsonPath("$.noticePeriodDays").value(20))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }
        }

        /**
         * 員工查詢 API 測試
         */
        @Nested
        @DisplayName("員工查詢 API")
        public class EmployeeQueryApiTests {

                @Test
                @DisplayName("ORG_EMP_006: 查詢員工列表 - 應回傳列表結果")
                void getEmployeeList_ShouldReturnList() throws Exception {
                        // Arrange
                        EmployeeListItemResponse emp1 = EmployeeListItemResponse.builder()
                                        .employeeId("emp-001")
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .departmentId("dept-001")
                                        .jobTitle("資深工程師")
                                        .employmentStatus("ACTIVE")
                                        .build();

                        EmployeeListItemResponse emp2 = EmployeeListItemResponse.builder()
                                        .employeeId("emp-002")
                                        .employeeNumber("EMP-2025-002")
                                        .fullName("李小華")
                                        .departmentId("dept-001")
                                        .jobTitle("工程師")
                                        .employmentStatus("ACTIVE")
                                        .build();

                        PageResponse<EmployeeListItemResponse> response = PageResponse
                                        .<EmployeeListItemResponse>builder()
                                        .items(Arrays.asList(emp1, emp2))
                                        .page(1)
                                        .size(20)
                                        .total(2L)
                                        .totalPages(1)
                                        .build();

                        when(getEmployeeListService.getResponse(any(GetEmployeeListRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/employees")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.items").isArray())
                                        .andExpect(jsonPath("$.items.length()").value(2))
                                        .andExpect(jsonPath("$.total").value(2));
                }

                @Test
                @DisplayName("ORG_EMP_007: 查詢單一員工 - 應回傳員工詳情")
                void getEmployeeDetail_ShouldReturnEmployeeDetail() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";

                        EmployeeDetailResponse response = EmployeeDetailResponse.builder()
                                        .employeeId(employeeId)
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .firstName("小明")
                                        .lastName("王")
                                        .companyEmail("xiaoming.wang@company.com")
                                        .mobilePhone("0912345678")
                                        .department(EmployeeDetailResponse.DepartmentInfo.builder()
                                                        .departmentId("dept-001")
                                                        .departmentName("研發一部")
                                                        .build())
                                        .jobTitle("資深工程師")
                                        .employmentStatus("ACTIVE")
                                        .hireDate(LocalDate.of(2020, 3, 1))
                                        .build();

                        when(getEmployeeDetailService.getResponse(isNull(), any(JWTModel.class), eq(employeeId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/employees/" + employeeId)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.employeeId").value(employeeId))
                                        .andExpect(jsonPath("$.employeeNumber").value("EMP-2025-001"))
                                        .andExpect(jsonPath("$.fullName").value("王小明"))
                                        .andExpect(jsonPath("$.employmentStatus").value("ACTIVE"));
                }

                @Test
                @DisplayName("ORG_EMP_008: 依部門篩選員工列表")
                void getEmployeeList_WithDepartmentFilter_ShouldFilterByDepartment() throws Exception {
                        // Arrange
                        EmployeeListItemResponse emp = EmployeeListItemResponse.builder()
                                        .employeeId("emp-001")
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .departmentId("dept-001")
                                        .jobTitle("資深工程師")
                                        .status("ACTIVE")
                                        .build();

                        PageResponse<EmployeeListItemResponse> response = PageResponse
                                        .<EmployeeListItemResponse>builder()
                                        .items(Collections.singletonList(emp))
                                        .page(1)
                                        .size(20)
                                        .total(1L)
                                        .totalPages(1)
                                        .build();

                        when(getEmployeeListService.getResponse(any(GetEmployeeListRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/employees?departmentId=dept-001")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.items").isArray())
                                        .andExpect(jsonPath("$.items[0].departmentId").value("dept-001"));
                }

                @Test
                @DisplayName("ORG_EMP_009: 依狀態篩選員工列表")
                void getEmployeeList_WithStatusFilter_ShouldFilterByStatus() throws Exception {
                        // Arrange
                        EmployeeListItemResponse emp = EmployeeListItemResponse.builder()
                                        .employeeId("emp-001")
                                        .employeeNumber("EMP-2025-001")
                                        .fullName("王小明")
                                        .departmentId("dept-001")
                                        .jobTitle("資深工程師")
                                        .status("ACTIVE")
                                        .build();

                        PageResponse<EmployeeListItemResponse> response = PageResponse
                                        .<EmployeeListItemResponse>builder()
                                        .items(Collections.singletonList(emp))
                                        .page(1)
                                        .size(20)
                                        .total(1L)
                                        .totalPages(1)
                                        .build();

                        when(getEmployeeListService.getResponse(any(GetEmployeeListRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/employees?status=ACTIVE")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.items").isArray())
                                        .andExpect(jsonPath("$.items[0].status").value("ACTIVE"));
                }
        }
}
