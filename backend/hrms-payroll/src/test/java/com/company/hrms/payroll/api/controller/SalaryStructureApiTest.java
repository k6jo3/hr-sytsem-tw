package com.company.hrms.payroll.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.payroll.application.dto.request.CreateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;
import com.company.hrms.payroll.application.dto.request.UpdateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.application.service.CreateSalaryStructureServiceImpl;
import com.company.hrms.payroll.application.service.DeleteSalaryStructureServiceImpl;
import com.company.hrms.payroll.application.service.UpdateSalaryStructureServiceImpl;
import com.company.hrms.payroll.application.service.GetEmployeeSalaryStructureServiceImpl;
import com.company.hrms.payroll.application.service.GetSalaryStructureListServiceImpl;

/**
 * HR04 薪資結構 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>薪資結構建立、更新、刪除 (Command)</li>
 * <li>薪資結構列表查詢、員工薪資結構查詢 (Query)</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR04 薪資結構 API 合約測試")
class SalaryStructureApiTest extends BaseApiContractTest {

    @MockBean(name = "createSalaryStructureServiceImpl")
    private CreateSalaryStructureServiceImpl createSalaryStructureService;

    @MockBean(name = "updateSalaryStructureServiceImpl")
    private UpdateSalaryStructureServiceImpl updateSalaryStructureService;

    @MockBean(name = "deleteSalaryStructureServiceImpl")
    private DeleteSalaryStructureServiceImpl deleteSalaryStructureService;

    @MockBean(name = "getSalaryStructureListServiceImpl")
    private GetSalaryStructureListServiceImpl getSalaryStructureListService;

    @MockBean(name = "getEmployeeSalaryStructureServiceImpl")
    private GetEmployeeSalaryStructureServiceImpl getEmployeeSalaryStructureService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-user");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 薪資結構命令 API 測試
     */
    @Nested
    @DisplayName("薪資結構命令 API")
    class SalaryStructureCommandApiTests {

        @Test
        @DisplayName("PAY_CMD_007: 建立薪資結構 - 應回傳結構 ID 和有效狀態")
        void createSalaryStructure_ShouldReturnIdAndActiveStatus() throws Exception {
            // Arrange
            CreateSalaryStructureRequest request = new CreateSalaryStructureRequest();
            request.setEmployeeId("E001");
            request.setMonthlySalary(BigDecimal.valueOf(50000));
            request.setPayrollSystem("MONTHLY");
            request.setPayrollCycle("MONTHLY");
            request.setEffectiveDate(LocalDate.of(2025, 1, 1));

            SalaryStructureResponse response = SalaryStructureResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .employeeId("E001")
                    .payrollSystem("MONTHLY")
                    .payrollCycle("MONTHLY")
                    .monthlySalary(BigDecimal.valueOf(50000))
                    .effectiveDate(LocalDate.of(2025, 1, 1))
                    .active(true)
                    .build();

            when(createSalaryStructureService.execCommand(any(CreateSalaryStructureRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPost("/api/v1/salary-structures", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.employeeId").value("E001"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("PAY_CMD_008: 更新薪資結構 - 應回傳更新後的結構")
        void updateSalaryStructure_ShouldReturnUpdatedStructure() throws Exception {
            // Arrange
            String structureId = UUID.randomUUID().toString();

            UpdateSalaryStructureRequest request = new UpdateSalaryStructureRequest();
            request.setMonthlySalary(BigDecimal.valueOf(55000));
            request.setEffectiveDate(LocalDate.of(2025, 2, 1));

            SalaryStructureResponse response = SalaryStructureResponse.builder()
                    .id(structureId)
                    .employeeId("E001")
                    .payrollSystem("MONTHLY")
                    .monthlySalary(BigDecimal.valueOf(55000))
                    .effectiveDate(LocalDate.of(2025, 2, 1))
                    .active(true)
                    .build();

            when(updateSalaryStructureService.execCommand(any(UpdateSalaryStructureRequest.class), any(JWTModel.class), anyString()))
                    .thenReturn(response);

            // Act & Assert
            performPut("/api/v1/salary-structures/" + structureId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(structureId))
                    .andExpect(jsonPath("$.monthlySalary").value(55000));
        }

        @Test
        @DisplayName("PAY_CMD_009: 刪除薪資結構 - 應回傳刪除成功")
        void deleteSalaryStructure_ShouldReturnSuccess() throws Exception {
            // Arrange
            String structureId = UUID.randomUUID().toString();

            // DeleteSalaryStructureServiceImpl returns Void - use doNothing for void methods
            doNothing().when(deleteSalaryStructureService).execCommand(isNull(), any(JWTModel.class), anyString());

            // Act & Assert
            performDelete("/api/v1/salary-structures/" + structureId)
                    .andExpect(status().isOk());
        }
    }

    /**
     * 薪資結構查詢 API 測試
     */
    @Nested
    @DisplayName("薪資結構查詢 API")
    class SalaryStructureQueryApiTests {

        @Test
        @DisplayName("PAY_S001: 查詢薪資結構列表 - 應回傳分頁結果")
        void getSalaryStructureList_ShouldReturnPagedResult() throws Exception {
            // Arrange
            SalaryStructureResponse structure = SalaryStructureResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .employeeId("E001")
                    .payrollSystem("MONTHLY")
                    .monthlySalary(BigDecimal.valueOf(50000))
                    .active(true)
                    .build();

            PageResponse<SalaryStructureResponse> response = PageResponse.of(
                    Collections.singletonList(structure),
                    1,
                    10,
                    1L
            );

            when(getSalaryStructureListService.getResponse(any(GetSalaryStructureListRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/salary-structures")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("PAY_S001: 查詢特定員工薪資結構 - 應回傳員工的薪資結構")
        void getEmployeeSalaryStructure_ShouldReturnEmployeeStructure() throws Exception {
            // Arrange
            String employeeId = "E001";

            SalaryStructureResponse response = SalaryStructureResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .employeeId(employeeId)
                    .payrollSystem("MONTHLY")
                    .monthlySalary(BigDecimal.valueOf(50000))
                    .active(true)
                    .build();

            when(getEmployeeSalaryStructureService.getResponse(anyString(), any(JWTModel.class), anyString()))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/salary-structures/employee/" + employeeId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employeeId").value(employeeId))
                    .andExpect(jsonPath("$.active").value(true));
        }
    }
}
