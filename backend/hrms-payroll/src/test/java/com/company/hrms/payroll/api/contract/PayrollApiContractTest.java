package com.company.hrms.payroll.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.payroll.domain.repository.IPayrollItemDefinitionRepository;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

/**
 * HR04 薪資管理服務 API 合約測試
 * 驗證 Controller -> Service -> Repository 的 QueryGroup 組裝正確性
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR04 薪資管理服務 API 合約測試")
public class PayrollApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "payroll";
    private String contractSpec;

    @MockBean
    private IPayrollRunRepository payrollRunRepository;

    @MockBean
    private ISalaryStructureRepository salaryStructureRepository;

    @MockBean
    private IPayslipRepository payslipRepository;

    @MockBean
    private IPayrollItemDefinitionRepository payrollItemDefinitionRepository;

    private JWTModel mockUser;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);

        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR_ADMIN"));

        lenient().when(payrollRunRepository.save(any())).thenReturn(null);
        lenient().when(salaryStructureRepository.save(any())).thenReturn(null);
    }

    @Nested
    @DisplayName("薪資批次 API 合約")
    class PayrollRunApiContractTests {

        @Test
        @DisplayName("PAY_QRY_R001: 依組織查詢薪資批次")
        void getPayrollRuns_ByOrganization_ShouldIncludeFilters() throws Exception {
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(payrollRunRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/v1/payroll-runs?organizationId=ORG001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PAY_QRY_R001");
        }

        @Test
        @DisplayName("PAY_QRY_R002: 查詢已提交的薪資批次")
        void getPayrollRuns_ByStatus_ShouldIncludeFilters() throws Exception {
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(payrollRunRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/v1/payroll-runs?status=SUBMITTED")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PAY_QRY_R002");
        }
    }

    @Nested
    @DisplayName("薪資結構 API 合約")
    class SalaryStructureApiContractTests {

        @Test
        @DisplayName("PAY_QRY_S001: 查詢員工薪資結構")
        void getSalaryStructures_ByEmployee_ShouldIncludeFilters() throws Exception {
            ArgumentCaptor<Condition<?>> conditionCaptor = ArgumentCaptor.forClass(Condition.class);
            when(salaryStructureRepository.findPageByCondition(conditionCaptor.capture()))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/v1/salary-structures?employeeId=E001&isActive=true")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            QueryGroup query = conditionCaptor.getValue().toQueryGroup();
            assertContract(query, contractSpec, "PAY_QRY_S001");
        }
    }

    @Nested
    @DisplayName("薪資單 API 合約")
    class PayslipApiContractTests {

        @Test
        @DisplayName("PAY_QRY_P001: 依批次查詢薪資單")
        void getPayslips_ByRunId_ShouldIncludeFilters() throws Exception {
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(payslipRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/v1/payslips?runId=RUN001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PAY_QRY_P001");
        }
    }
}
