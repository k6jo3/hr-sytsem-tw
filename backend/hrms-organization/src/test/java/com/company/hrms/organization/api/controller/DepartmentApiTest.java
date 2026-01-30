package com.company.hrms.organization.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.organization.api.request.department.AssignManagerRequest;
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.request.department.UpdateDepartmentRequest;
import com.company.hrms.organization.api.response.department.CreateDepartmentResponse;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse;
import com.company.hrms.organization.application.service.department.AssignManagerServiceImpl;
import com.company.hrms.organization.application.service.department.CreateDepartmentServiceImpl;
import com.company.hrms.organization.application.service.department.DeleteDepartmentServiceImpl;
import com.company.hrms.organization.application.service.department.GetDepartmentDetailServiceImpl;
import com.company.hrms.organization.application.service.department.GetDepartmentManagersServiceImpl;
import com.company.hrms.organization.application.service.department.UpdateDepartmentServiceImpl;

/**
 * HR02 部門管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>部門建立、更新、刪除 (Command)</li>
 * <li>指派主管 (Command)</li>
 * <li>部門詳情查詢、主管層級查詢 (Query)</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR02 部門管理 API 合約測試")
public class DepartmentApiTest extends BaseApiContractTest {

        @MockBean(name = "createDepartmentServiceImpl")
        private CreateDepartmentServiceImpl createDepartmentService;

        @MockBean(name = "updateDepartmentServiceImpl")
        private UpdateDepartmentServiceImpl updateDepartmentService;

        @MockBean(name = "deleteDepartmentServiceImpl")
        private DeleteDepartmentServiceImpl deleteDepartmentService;

        @MockBean(name = "assignManagerServiceImpl")
        private AssignManagerServiceImpl assignManagerService;

        @MockBean(name = "getDepartmentDetailServiceImpl")
        private GetDepartmentDetailServiceImpl getDepartmentDetailService;

        @MockBean(name = "getDepartmentManagersServiceImpl")
        private GetDepartmentManagersServiceImpl getDepartmentManagersService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("hr-user");
                mockUser.setUsername("hr_admin");
                mockUser.setRoles(Collections.singletonList("HR"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("department:create"));
                authorities.add(new SimpleGrantedAuthority("department:update"));
                authorities.add(new SimpleGrantedAuthority("department:delete"));
                authorities.add(new SimpleGrantedAuthority("department:read"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 部門命令 API 測試
         */
        @Nested
        @DisplayName("部門命令 API")
        public class DepartmentCommandApiTests {

                @Test
                @DisplayName("ORG_DEPT_001: 新增部門 - 應回傳部門 ID")
                void createDepartment_ShouldReturnDepartmentId() throws Exception {
                        // Arrange
                        CreateDepartmentRequest request = new CreateDepartmentRequest();
                        request.setCode("RD-001");
                        request.setName("研發一部");
                        request.setOrganizationId("org-001");
                        request.setParentId(null);

                        CreateDepartmentResponse response = CreateDepartmentResponse.builder()
                                        .departmentId("dept-001")
                                        .code("RD-001")
                                        .name("研發一部")
                                        .message("部門建立成功")
                                        .build();

                        when(createDepartmentService.execCommand(any(CreateDepartmentRequest.class),
                                        any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/departments", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.departmentId").value("dept-001"))
                                        .andExpect(jsonPath("$.code").value("RD-001"))
                                        .andExpect(jsonPath("$.name").value("研發一部"))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("ORG_DEPT_002: 更新部門 - 應回傳更新後的部門資訊")
                void updateDepartment_ShouldReturnUpdatedDepartment() throws Exception {
                        // Arrange
                        String departmentId = "dept-001";
                        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
                        request.setName("研發一部（更名）");

                        DepartmentDetailResponse response = DepartmentDetailResponse.builder()
                                        .departmentId(departmentId)
                                        .code("RD-001")
                                        .name("研發一部（更名）")
                                        .status("ACTIVE")
                                        .build();

                        when(updateDepartmentService.execCommand(any(UpdateDepartmentRequest.class),
                                        any(JWTModel.class),
                                        eq(departmentId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/departments/" + departmentId, request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.departmentId").value(departmentId))
                                        .andExpect(jsonPath("$.name").value("研發一部（更名）"));
                }

                @Test
                @DisplayName("ORG_DEPT_003: 刪除部門 - 應回傳 204")
                void deleteDepartment_ShouldReturn204() throws Exception {
                        // Arrange
                        String departmentId = "dept-001";
                        doNothing().when(deleteDepartmentService).execCommand(isNull(), any(JWTModel.class),
                                        eq(departmentId));

                        // Act & Assert
                        performDelete("/api/v1/departments/" + departmentId)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("ORG_DEPT_004: 指派部門主管 - 應回傳部門詳情")
                void assignManager_ShouldReturnDepartmentDetail() throws Exception {
                        // Arrange
                        String departmentId = "dept-001";
                        AssignManagerRequest request = new AssignManagerRequest();
                        request.setManagerId("emp-001");

                        DepartmentDetailResponse response = DepartmentDetailResponse.builder()
                                        .departmentId(departmentId)
                                        .code("RD-001")
                                        .name("研發一部")
                                        .managerId("emp-001")
                                        .managerName("王小明")
                                        .status("ACTIVE")
                                        .build();

                        when(assignManagerService.execCommand(any(AssignManagerRequest.class), any(JWTModel.class),
                                        eq(departmentId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/departments/" + departmentId + "/assign-manager", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.departmentId").value(departmentId))
                                        .andExpect(jsonPath("$.managerId").value("emp-001"))
                                        .andExpect(jsonPath("$.managerName").value("王小明"));
                }
        }

        /**
         * 部門查詢 API 測試
         */
        @Nested
        @DisplayName("部門查詢 API")
        public class DepartmentQueryApiTests {

                @Test
                @DisplayName("ORG_DEPT_005: 查詢部門詳情 - 應回傳部門詳情")
                void getDepartmentDetail_ShouldReturnDepartmentDetail() throws Exception {
                        // Arrange
                        String departmentId = "dept-001";

                        DepartmentDetailResponse response = DepartmentDetailResponse.builder()
                                        .departmentId(departmentId)
                                        .code("RD-001")
                                        .name("研發一部")
                                        .organizationId("org-001")
                                        .organizationName("總公司")
                                        .managerId("emp-001")
                                        .managerName("王小明")
                                        .status("ACTIVE")
                                        .level(1)
                                        .employeeCount(25)
                                        .build();

                        when(getDepartmentDetailService.getResponse(isNull(), any(JWTModel.class), eq(departmentId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/departments/" + departmentId)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.departmentId").value(departmentId))
                                        .andExpect(jsonPath("$.code").value("RD-001"))
                                        .andExpect(jsonPath("$.name").value("研發一部"))
                                        .andExpect(jsonPath("$.managerId").value("emp-001"))
                                        .andExpect(jsonPath("$.status").value("ACTIVE"));
                }

                @Test
                @DisplayName("ORG_DEPT_006: 查詢部門主管層級 - 應回傳主管列表")
                void getDepartmentManagers_ShouldReturnManagerList() throws Exception {
                        // Arrange
                        String departmentId = "dept-003";

                        DepartmentManagersResponse.ManagerInfo manager1 = DepartmentManagersResponse.ManagerInfo
                                        .builder()
                                        .departmentId("dept-001")
                                        .departmentName("總公司")
                                        .employeeId("emp-001")
                                        .fullName("張總經理")
                                        .managerLevel(1)
                                        .build();

                        DepartmentManagersResponse.ManagerInfo manager2 = DepartmentManagersResponse.ManagerInfo
                                        .builder()
                                        .departmentId("dept-002")
                                        .departmentName("研發部")
                                        .employeeId("emp-002")
                                        .fullName("李副總")
                                        .managerLevel(2)
                                        .build();

                        DepartmentManagersResponse.ManagerInfo manager3 = DepartmentManagersResponse.ManagerInfo
                                        .builder()
                                        .departmentId("dept-003")
                                        .departmentName("研發一組")
                                        .employeeId("emp-003")
                                        .fullName("王經理")
                                        .managerLevel(3)
                                        .build();

                        DepartmentManagersResponse response = DepartmentManagersResponse.builder()
                                        .departmentId(departmentId)
                                        .managers(Arrays.asList(manager1, manager2, manager3))
                                        .build();

                        when(getDepartmentManagersService.getResponse(isNull(), any(JWTModel.class), eq(departmentId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/departments/" + departmentId + "/managers")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.departmentId").value(departmentId))
                                        .andExpect(jsonPath("$.managers").isArray())
                                        .andExpect(jsonPath("$.managers.length()").value(3));
                }
        }
}
