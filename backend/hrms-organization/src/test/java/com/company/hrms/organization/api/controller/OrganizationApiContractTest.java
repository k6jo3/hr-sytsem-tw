package com.company.hrms.organization.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.aop.proxy-target-class=true")
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR02 組織員工服務 API 合約測試")
public class OrganizationApiContractTest extends BaseApiContractTest {

        private String contractSpec;

        @MockBean
        private IEmployeeRepository employeeRepository;

        @MockBean
        private IDepartmentRepository departmentRepository;

        @MockBean
        private IOrganizationRepository organizationRepository;

        @BeforeEach
        void setUp() throws Exception {
                contractSpec = loadContractSpec("organization");
        }

        private void setupUser(String role, String deptId, List<String> managedDepts) {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("test-user");
                mockUser.setUsername("testuser");
                mockUser.setRoles(Collections.singletonList(role));
                mockUser.setDepartmentId(deptId);
                mockUser.setManagedDepartmentIds(managedDepts);

                List<SimpleGrantedAuthority> authorities = Collections
                                .singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null,
                                authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
        }

        private void assertContractWithPlaceholders(QueryGroup actualQuery, String scenarioId,
                        java.util.Map<String, String> placeholders) {
                String spec = contractSpec;
                if (placeholders != null) {
                        for (java.util.Map.Entry<String, String> entry : placeholders.entrySet()) {
                                spec = spec.replace("{" + entry.getKey() + "}", entry.getValue());
                        }
                }
                assertContract(actualQuery, spec, scenarioId);
        }

        @Nested
        @DisplayName("1. 員工查詢 API 合約")
        public class EmployeeQueryApiContractTests {

                @Test
                @DisplayName("ORG_QRY_E001: 查詢在職員工")
                void ORG_QRY_E001_searchActiveEmployees() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees?status=ACTIVE"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E001");
                }

                @Test
                @DisplayName("ORG_QRY_E005: 依部門查詢員工")
                void ORG_QRY_E005_searchByDepartment() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees?departmentId=DEPT-001"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E005");
                }

                @Test
                @DisplayName("ORG_QRY_E006: 依姓名模糊查詢")
                void ORG_QRY_E006_searchByName() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees?name=王"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E006");
                }

                @Test
                @DisplayName("ORG_QRY_E007: 依工號查詢")
                void ORG_QRY_E007_searchByEmployeeNumber() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees?employeeNumber=EMP001"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E007");
                }

                @Test
                @DisplayName("ORG_QRY_E008: 主管查詢下屬")
                void ORG_QRY_E008_managerSearchSubordinates() throws Exception {
                        setupUser("MANAGER", "DEPT-001", Collections.singletonList("DEPT-001"));
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees"))
                                        .andExpect(status().isOk());

                        assertContractWithPlaceholders(queryCaptor.getValue(), "ORG_QRY_E008",
                                        java.util.Map.of("managedDeptIds", "DEPT-001"));
                }

                @Test
                @DisplayName("ORG_QRY_E009: 員工查詢同部門")
                void ORG_QRY_E009_employeeSearchColleagues() throws Exception {
                        setupUser("EMPLOYEE", "DEPT-001", null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees"))
                                        .andExpect(status().isOk());

                        assertContractWithPlaceholders(queryCaptor.getValue(), "ORG_QRY_E009",
                                        java.util.Map.of("currentUserDeptId", "DEPT-001"));
                }

                @Test
                @DisplayName("ORG_QRY_E010: 依到職日期範圍查詢")
                void ORG_QRY_E010_searchByHireDateRange() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/employees?hireDateFrom=2025-01-01&hireDateTo=2025-12-31"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E010");
                }
        }

        @Nested
        @DisplayName("2. 部門查詢 API 合約")
        public class DepartmentQueryApiContractTests {

                @Test
                @DisplayName("ORG_QRY_D001: 查詢所有啟用部門")
                void ORG_QRY_D001_searchActiveDepartments() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(departmentRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(departmentRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/departments?status=ACTIVE"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_D001");
                }

                @Test
                @DisplayName("ORG_QRY_D002: 查詢頂層部門")
                void ORG_QRY_D002_searchTopLevelDepartments() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(departmentRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(departmentRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/departments?parentId=null"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_D002");
                }

                @Test
                @DisplayName("ORG_QRY_D003: 查詢子部門")
                void ORG_QRY_D003_searchSubDepartments() throws Exception {
                        setupUser("HR", null, null);
                        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

                        when(departmentRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());
                        when(departmentRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(0L);

                        mockMvc.perform(get("/api/v1/departments/DEPT-001/sub-departments"))
                                        .andExpect(status().isOk());

                        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_D003");
                }
        }
}
