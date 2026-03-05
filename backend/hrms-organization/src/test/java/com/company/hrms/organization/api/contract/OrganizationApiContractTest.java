package com.company.hrms.organization.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Email;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.model.valueobject.NationalId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

/**
 * HR02 組織員工服務 API 合約測試
 * 驗證 Controller -> Service -> Repository 的 QueryGroup 組裝正確性
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR02 組織員工服務 API 合約測試")
public class OrganizationApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "organization";

        @MockBean
        private IEmployeeRepository employeeRepository;

        @MockBean
        private IDepartmentRepository departmentRepository;

        @MockBean
        private IOrganizationRepository organizationRepository;

        private JWTModel mockUser;

        /** 建立測試用 Employee 物件 */
        private Employee createMockEmployee() {
                return Employee.builder()
                        .id(EmployeeId.generate())
                        .employeeNumber("EMP-001")
                        .firstName("大明")
                        .lastName("王")
                        .fullName("王大明")
                        .nationalId(new NationalId("A123456789"))
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .gender(Gender.MALE)
                        .companyEmail(new Email("wang@company.com"))
                        .mobilePhone("0912345678")
                        .employmentType(EmploymentType.FULL_TIME)
                        .employmentStatus(EmploymentStatus.ACTIVE)
                        .hireDate(LocalDate.of(2024, 1, 1))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
        }

        @BeforeEach
        void setUp() throws Exception {
                mockUser = new JWTModel();
                mockUser.setUserId("00000000-0000-0000-0000-000000000001");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("HR_ADMIN"));

                // 設定 SecurityContext（@CurrentUser 解析器從此取得使用者）
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

                lenient().when(employeeRepository.findByQuery(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                .thenReturn(0L);
                lenient().when(employeeRepository.existsByEmployeeNumber(anyString()))
                                .thenReturn(false);
                lenient().when(employeeRepository.existsByEmail(anyString()))
                                .thenReturn(false);
                lenient().when(employeeRepository.existsByNationalId(anyString()))
                                .thenReturn(false);
                lenient().doNothing().when(employeeRepository).save(any());

                lenient().when(departmentRepository.findByQuery(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(departmentRepository.countByQuery(any(QueryGroup.class)))
                                .thenReturn(0L);
                lenient().when(departmentRepository.existsById(any()))
                                .thenReturn(true);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Nested
        @DisplayName("員工管理 API 合約")
        class EmployeeApiContractTests {

                @Test
                @DisplayName("ORG_QRY_E001: 查詢在職員工")
                void searchActiveEmployees_ShouldIncludeFilters() throws Exception {
                        ContractSpec contract = loadContract(CONTRACT, "ORG_QRY_E001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(List.of(createMockEmployee()));
                        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                                        .thenReturn(1L);

                        MvcResult result = mockMvc.perform(get("/api/v1/employees?status=ACTIVE")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ORG_QRY_E005: 依部門查詢員工")
                void searchEmployeesByDepartment_ShouldIncludeFilters() throws Exception {
                        ContractSpec contract = loadContract(CONTRACT, "ORG_QRY_E005");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());

                        MvcResult result = mockMvc.perform(get("/api/v1/employees?departmentId=DEPT-001")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ORG_QRY_E006: 依姓名模糊查詢員工")
                void searchEmployeesByName_ShouldIncludeFilters() throws Exception {
                        ContractSpec contract = loadContract(CONTRACT, "ORG_QRY_E006");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());

                        MvcResult result = mockMvc.perform(get("/api/v1/employees?name=王")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ORG_CMD_E001: 建立員工 - 驗證儲存被呼叫")
                void createEmployee_ShouldSaveEmployee() throws Exception {
                        String requestBody = String.format("""
                                        {
                                          "employeeNumber": "EMP202603-001",
                                          "firstName": "新人",
                                          "lastName": "測",
                                          "nationalId": "A123456789",
                                          "dateOfBirth": "1995-06-15",
                                          "gender": "MALE",
                                          "companyEmail": "test.new@company.com",
                                          "mobilePhone": "0911222333",
                                          "organizationId": "11111111-1111-1111-1111-111111111111",
                                          "departmentId": "d0000001-0001-0001-0001-000000000001",
                                          "employmentType": "FULL_TIME",
                                          "jobTitle": "軟體工程師",
                                          "hireDate": "%s"
                                        }
                                        """, java.time.LocalDate.now().toString());

                        mockMvc.perform(post("/api/v1/employees")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        verify(employeeRepository).existsByEmployeeNumber("EMP202603-001");
                        verify(employeeRepository).save(any(Employee.class));
                }
        }

        @Nested
        @DisplayName("部門管理 API 合約")
        class DepartmentApiContractTests {

                @Test
                @DisplayName("ORG_QRY_D001: 查詢啟用部門")
                void searchActiveDepartments_ShouldIncludeFilters() throws Exception {
                        ContractSpec contract = loadContract(CONTRACT, "ORG_QRY_D001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(departmentRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(Collections.emptyList());

                        MvcResult result = mockMvc.perform(get("/api/v1/departments?status=ACTIVE")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }
        }
}
