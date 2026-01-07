package com.company.hrms.project.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.request.GetMyProjectsRequest;
import com.company.hrms.project.api.request.GetProjectDetailRequest;
import com.company.hrms.project.api.request.GetTaskDetailRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.api.response.GetProjectDetailResponse;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.application.service.GetCustomerDetailServiceImpl;
import com.company.hrms.project.application.service.GetMyProjectsServiceImpl;
import com.company.hrms.project.application.service.GetProjectDetailServiceImpl;
import com.company.hrms.project.application.service.GetTaskDetailServiceImpl;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;

@AutoConfigureMockMvc(addFilters = false)
class ProjectQueryApiTest extends BaseApiContractTest {

        @MockBean(name = "getProjectDetailServiceImpl")
        private GetProjectDetailServiceImpl getProjectDetailService;

        @MockBean(name = "getMyProjectsServiceImpl")
        private GetMyProjectsServiceImpl getMyProjectsService;

        @MockBean(name = "getTaskDetailServiceImpl")
        private GetTaskDetailServiceImpl getTaskDetailService;

        @MockBean(name = "getCustomerDetailServiceImpl")
        private GetCustomerDetailServiceImpl getCustomerDetailService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("test-user");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser,
                                null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        @Test
        void getProjectDetail_ShouldReturnData() throws Exception {
                // Arrange
                GetProjectDetailResponse response = GetProjectDetailResponse.builder()
                                .projectId("PROJ-001")
                                .projectCode("P001")
                                .projectName("Test Project")
                                .status(ProjectStatus.IN_PROGRESS)
                                .build();

                when(getProjectDetailService.getResponse(any(GetProjectDetailRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/projects/PROJ-001")
                                .andExpect(status().isOk());
        }

        @Test
        void getMyProjects_ShouldReturnData() throws Exception {
                // Arrange
                GetMyProjectsResponse response = GetMyProjectsResponse.builder()
                                .build();

                when(getMyProjectsService.getResponse(any(GetMyProjectsRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/projects/my")
                                .andExpect(status().isOk());
        }

        @Test
        void getTaskDetail_ShouldReturnData() throws Exception {
                // Arrange
                GetTaskDetailResponse response = GetTaskDetailResponse.builder()
                                .taskId("TASK-001")
                                .taskName("Task 1")
                                .build();

                when(getTaskDetailService.getResponse(any(GetTaskDetailRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/projects/PROJ-001/tasks/TASK-001")
                                .andExpect(status().isOk());
        }

        @Test
        void getCustomerDetail_ShouldReturnData() throws Exception {
                // Arrange
                GetCustomerDetailResponse response = GetCustomerDetailResponse.builder()
                                .customerId("CUST-001")
                                .customerName("Tech Corp")
                                .build();

                when(getCustomerDetailService.getResponse(any(GetCustomerDetailRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/customers/CUST-001")
                                .andExpect(status().isOk());
        }
}
