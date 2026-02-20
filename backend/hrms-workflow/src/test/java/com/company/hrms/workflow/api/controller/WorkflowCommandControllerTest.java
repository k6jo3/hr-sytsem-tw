package com.company.hrms.workflow.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.ApproveTaskRequest;
import com.company.hrms.workflow.api.request.RejectTaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("null")
public class WorkflowCommandControllerTest {

    private MockMvc mockMvc;
    private TestableController controller;

    @BeforeEach
    void setup() {
        controller = spy(new TestableController());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void approveTask_ShouldMapToPutAndExtractTaskId() throws Exception {
        // Arrange
        String taskId = "TASK-123";
        ApproveTaskRequest request = new ApproveTaskRequest();
        request.setComment("Approved");
        // taskId not set in body, should be from path

        doReturn(null).when(controller).execCommand(any(), any());

        // Act
        mockMvc.perform(put("/api/v1/workflows/tasks/{taskId}/approve", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<ApproveTaskRequest> captor = ArgumentCaptor.forClass(ApproveTaskRequest.class);
        verify(controller).execCommand(captor.capture(), any(JWTModel.class));

        ApproveTaskRequest actualReq = captor.getValue();
        if (!taskId.equals(actualReq.getTaskId())) {
            throw new AssertionError(
                    "TaskId not extracted from path. Expected: " + taskId + ", Actual: " + actualReq.getTaskId());
        }
    }

    @Test
    void rejectTask_ShouldMapToPutAndExtractTaskId() throws Exception {
        // Arrange
        String taskId = "TASK-456";
        RejectTaskRequest request = new RejectTaskRequest();
        request.setReason("Rejected");

        doReturn(null).when(controller).execCommand(any(), any());

        // Act
        mockMvc.perform(put("/api/v1/workflows/tasks/{taskId}/reject", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<RejectTaskRequest> captor = ArgumentCaptor.forClass(RejectTaskRequest.class);
        verify(controller).execCommand(captor.capture(), any(JWTModel.class));

        RejectTaskRequest actualReq = captor.getValue();
        if (!taskId.equals(actualReq.getTaskId())) {
            throw new AssertionError(
                    "TaskId not extracted from path. Expected: " + taskId + ", Actual: " + actualReq.getTaskId());
        }
    }

    // Subclass to expose protected method
    public static class TestableController extends HR11WorkflowCmdController {
        @Override
        public <T, R> R execCommand(T request, JWTModel currentUser, String... args) throws Exception {
            return super.execCommand(request, currentUser, args);
        }
    }
}
