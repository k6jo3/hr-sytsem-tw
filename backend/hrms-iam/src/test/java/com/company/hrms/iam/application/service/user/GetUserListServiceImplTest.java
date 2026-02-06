package com.company.hrms.iam.application.service.user;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;

import com.company.hrms.common.test.base.BaseServiceTest;
import com.company.hrms.iam.api.request.user.GetUserListRequest;
import com.company.hrms.iam.domain.repository.IUserRepository;

@DisplayName("GetUserListService 快照測試")
class GetUserListServiceImplTest extends BaseServiceTest<GetUserListServiceImpl> {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private GetUserListServiceImpl service;

    @Test
    @DisplayName("依狀態查詢應產生正確的 QueryGroup")
    void searchByStatus_ShouldMatchSnapshot() throws Exception {
        // Given
        GetUserListRequest request = GetUserListRequest.builder()
                .status("ACTIVE")
                .build();

        // Mock Repository
        when(repository.findPage(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        executeAndCaptureWithResult(() -> {
            try {
                return service.getResponse(request, mockUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        verifyCapturedQuery("user_search_by_status.json");
    }

    @Test
    @DisplayName("依使用者名稱查詢應產生正確的 QueryGroup")
    void searchByUsername_ShouldMatchSnapshot() throws Exception {
        // Given
        GetUserListRequest request = GetUserListRequest.builder()
                .username("john_doe")
                .build();

        when(repository.findPage(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        executeAndCaptureWithResult(() -> {
            try {
                return service.getResponse(request, mockUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        verifyCapturedQuery("user_search_by_username.json");
    }
}
