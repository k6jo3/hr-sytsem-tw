package com.company.hrms.project.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.request.CompleteProjectRequest;
import com.company.hrms.project.api.request.CreateProjectRequest;
import com.company.hrms.project.api.request.HoldProjectRequest;
import com.company.hrms.project.api.request.RemoveProjectMemberRequest;
import com.company.hrms.project.api.request.StartProjectRequest;
import com.company.hrms.project.api.request.UpdateProjectRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.api.response.CompleteProjectResponse;
import com.company.hrms.project.api.response.CreateProjectResponse;
import com.company.hrms.project.api.response.HoldProjectResponse;
import com.company.hrms.project.api.response.RemoveProjectMemberResponse;
import com.company.hrms.project.api.response.StartProjectResponse;
import com.company.hrms.project.api.response.UpdateProjectResponse;
import com.company.hrms.project.application.service.AddProjectMemberServiceImpl;
import com.company.hrms.project.application.service.CompleteProjectServiceImpl;
import com.company.hrms.project.application.service.CreateProjectServiceImpl;
import com.company.hrms.project.application.service.HoldProjectServiceImpl;
import com.company.hrms.project.application.service.RemoveProjectMemberServiceImpl;
import com.company.hrms.project.application.service.StartProjectServiceImpl;
import com.company.hrms.project.application.service.UpdateProjectServiceImpl;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

/**
 * HR06 專案管理 Command API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>專案建立、更新、啟動、暫停、結案</li>
 * <li>成員新增、移除</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR06 專案管理 Command API 合約測試")
class ProjectCommandApiTest extends BaseApiContractTest {

    @MockBean(name = "createProjectServiceImpl")
    private CreateProjectServiceImpl createProjectService;

    @MockBean(name = "updateProjectServiceImpl")
    private UpdateProjectServiceImpl updateProjectService;

    @MockBean(name = "startProjectServiceImpl")
    private StartProjectServiceImpl startProjectService;

    @MockBean(name = "holdProjectServiceImpl")
    private HoldProjectServiceImpl holdProjectService;

    @MockBean(name = "completeProjectServiceImpl")
    private CompleteProjectServiceImpl completeProjectService;

    @MockBean(name = "addProjectMemberServiceImpl")
    private AddProjectMemberServiceImpl addProjectMemberService;

    @MockBean(name = "removeProjectMemberServiceImpl")
    private RemoveProjectMemberServiceImpl removeProjectMemberService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-user");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("PM"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 專案生命週期 API 測試
     */
    @Nested
    @DisplayName("專案生命週期 API")
    class ProjectLifecycleApiTests {

        @Test
        @DisplayName("PRJ_CMD_001: 建立專案 - 應回傳專案 ID")
        void createProject_ShouldReturnProjectId() throws Exception {
            // Arrange
            CreateProjectRequest request = new CreateProjectRequest();
            request.setProjectCode("P-2026-001");
            request.setProjectName("新專案");
            request.setProjectType(ProjectType.DEVELOPMENT);
            request.setPlannedStartDate(LocalDate.now());
            request.setPlannedEndDate(LocalDate.now().plusMonths(3));
            request.setBudgetAmount(BigDecimal.valueOf(500000));
            request.setBudgetHours(BigDecimal.valueOf(1000));

            CreateProjectResponse response = CreateProjectResponse.builder()
                    .projectId(UUID.randomUUID().toString())
                    .build();

            when(createProjectService.execCommand(any(CreateProjectRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPost("/api/v1/projects", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").isNotEmpty());
        }

        @Test
        @DisplayName("PRJ_CMD_002: 更新專案 - 應回傳成功狀態")
        void updateProject_ShouldReturnSuccess() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            UpdateProjectRequest request = new UpdateProjectRequest();
            request.setProjectName("更新後的專案名稱");
            request.setDescription("新描述");

            UpdateProjectResponse response = UpdateProjectResponse.builder()
                    .success(true)
                    .build();

            when(updateProjectService.execCommand(any(UpdateProjectRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPut("/api/v1/projects/" + projectId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("PRJ_CMD_003: 啟動專案 - 應回傳成功狀態與當前狀態")
        void startProject_ShouldReturnSuccessAndStatus() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            StartProjectRequest request = new StartProjectRequest();

            StartProjectResponse response = StartProjectResponse.builder()
                    .success(true)
                    .currentStatus("IN_PROGRESS")
                    .build();

            when(startProjectService.execCommand(any(StartProjectRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPut("/api/v1/projects/" + projectId + "/start", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.currentStatus").value("IN_PROGRESS"));
        }

        @Test
        @DisplayName("PRJ_CMD_004: 暫停專案 - 應記錄暫停原因與日期")
        void holdProject_ShouldRecordReasonAndDate() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            HoldProjectRequest request = new HoldProjectRequest();
            request.setReason("客戶需求變更，暫停開發");

            HoldProjectResponse response = HoldProjectResponse.builder()
                    .projectId(projectId)
                    .status(ProjectStatus.ON_HOLD)
                    .holdReason("客戶需求變更，暫停開發")
                    .holdDate(LocalDate.now())
                    .build();

            when(holdProjectService.execCommand(any(HoldProjectRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPut("/api/v1/projects/" + projectId + "/hold", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ON_HOLD"))
                    .andExpect(jsonPath("$.holdReason").value("客戶需求變更，暫停開發"))
                    .andExpect(jsonPath("$.holdDate").isNotEmpty());
        }

        @Test
        @DisplayName("PRJ_CMD_005: 結案 - 應回傳成功狀態")
        void completeProject_ShouldReturnSuccess() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            CompleteProjectRequest request = new CompleteProjectRequest();

            CompleteProjectResponse response = CompleteProjectResponse.builder()
                    .success(true)
                    .currentStatus("COMPLETED")
                    .build();

            when(completeProjectService.execCommand(any(CompleteProjectRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPut("/api/v1/projects/" + projectId + "/complete", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.currentStatus").value("COMPLETED"));
        }
    }

    /**
     * 專案成員 API 測試
     */
    @Nested
    @DisplayName("專案成員 API")
    class ProjectMemberApiTests {

        @Test
        @DisplayName("PRJ_CMD_006: 新增成員 - 應回傳成功狀態")
        void addProjectMember_ShouldReturnSuccess() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();
            String employeeId = UUID.randomUUID().toString();

            AddProjectMemberRequest request = new AddProjectMemberRequest();
            request.setEmployeeId(UUID.fromString(employeeId));
            request.setRole("DEVELOPER");
            request.setAllocatedHours(BigDecimal.valueOf(40));

            AddProjectMemberResponse response = AddProjectMemberResponse.builder()
                    .success(true)
                    .build();

            when(addProjectMemberService.execCommand(any(AddProjectMemberRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performPost("/api/v1/projects/" + projectId + "/members", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("PRJ_CMD_007: 移除成員 - 應回傳離開日期")
        void removeProjectMember_ShouldReturnLeaveDate() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();
            String memberId = UUID.randomUUID().toString();

            RemoveProjectMemberResponse response = RemoveProjectMemberResponse.builder()
                    .memberId(memberId)
                    .removed(true)
                    .leaveDate(LocalDate.now())
                    .build();

            when(removeProjectMemberService.execCommand(any(RemoveProjectMemberRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performDelete("/api/v1/projects/" + projectId + "/members/" + memberId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.removed").value(true))
                    .andExpect(jsonPath("$.leaveDate").isNotEmpty());
        }
    }
}
