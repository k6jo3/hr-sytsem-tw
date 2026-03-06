package com.company.hrms.reporting.application.eventhandler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 員工事件處理器單元測試
 *
 * <p>
 * 驗證 EmployeeEventHandler 收到 Kafka JSON 訊息後，
 * 正確建立、更新、刪除 EmployeeRosterReadModel。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeEventHandler 單元測試")
class EmployeeEventHandlerTest {

    @Mock
    private EmployeeRosterReadModelRepository employeeRosterRepository;

    @Captor
    private ArgumentCaptor<EmployeeRosterReadModel> readModelCaptor;

    private EmployeeEventHandler handler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        handler = new EmployeeEventHandler(employeeRosterRepository, objectMapper);
    }

    @Nested
    @DisplayName("handleEmployeeCreated")
    class HandleEmployeeCreated {

        @Test
        @DisplayName("完整 JSON → ReadModel 正確建立含年資計算")
        void validJson_createsReadModelWithServiceYears() {
            // Arrange
            String message = """
                    {
                        "employeeId": "emp-001",
                        "tenantId": "tenant-001",
                        "name": "王大明",
                        "departmentId": "dept-001",
                        "departmentName": "研發部",
                        "positionId": "pos-001",
                        "positionName": "資深工程師",
                        "hireDate": "2024-01-15",
                        "status": "ACTIVE",
                        "phone": "0912345678",
                        "email": "wang@company.com"
                    }
                    """;

            // Act
            handler.handleEmployeeCreated(message);

            // Assert
            verify(employeeRosterRepository).save(readModelCaptor.capture());
            EmployeeRosterReadModel saved = readModelCaptor.getValue();

            assertThat(saved.getEmployeeId()).isEqualTo("emp-001");
            assertThat(saved.getTenantId()).isEqualTo("tenant-001");
            assertThat(saved.getName()).isEqualTo("王大明");
            assertThat(saved.getDepartmentId()).isEqualTo("dept-001");
            assertThat(saved.getDepartmentName()).isEqualTo("研發部");
            assertThat(saved.getPositionName()).isEqualTo("資深工程師");
            assertThat(saved.getStatus()).isEqualTo("ACTIVE");
            assertThat(saved.getIsDeleted()).isFalse();
            // 年資應 > 0（2024-01-15 到現在超過 1 年）
            assertThat(saved.getServiceYears()).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("最少欄位 JSON → ReadModel 建立成功且選填欄位為 null")
        void minimalJson_createsReadModelWithDefaults() {
            // Arrange
            String message = """
                    {
                        "employeeId": "emp-002",
                        "tenantId": "tenant-001",
                        "name": "李小美"
                    }
                    """;

            // Act
            handler.handleEmployeeCreated(message);

            // Assert
            verify(employeeRosterRepository).save(readModelCaptor.capture());
            EmployeeRosterReadModel saved = readModelCaptor.getValue();

            assertThat(saved.getEmployeeId()).isEqualTo("emp-002");
            assertThat(saved.getName()).isEqualTo("李小美");
            assertThat(saved.getDepartmentId()).isNull();
            assertThat(saved.getHireDate()).isNull();
            assertThat(saved.getStatus()).isEqualTo("ACTIVE"); // 預設值
            assertThat(saved.getServiceYears()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("格式錯誤 JSON → 不拋出異常、不儲存")
        void invalidJson_doesNotThrow() {
            handler.handleEmployeeCreated("{ bad json");

            verify(employeeRosterRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("handleEmployeeUpdated")
    class HandleEmployeeUpdated {

        @Test
        @DisplayName("既有員工 → 更新指定欄位")
        void existingEmployee_updatesFields() {
            // Arrange
            EmployeeRosterReadModel existing = EmployeeRosterReadModel.builder()
                    .employeeId("emp-001")
                    .tenantId("tenant-001")
                    .name("王大明")
                    .departmentName("研發部")
                    .status("ACTIVE")
                    .isDeleted(false)
                    .build();
            when(employeeRosterRepository.findById("emp-001")).thenReturn(Optional.of(existing));

            String message = """
                    {
                        "employeeId": "emp-001",
                        "name": "王大明(改)",
                        "departmentName": "產品部",
                        "status": "ON_LEAVE"
                    }
                    """;

            // Act
            handler.handleEmployeeUpdated(message);

            // Assert
            verify(employeeRosterRepository).save(readModelCaptor.capture());
            EmployeeRosterReadModel saved = readModelCaptor.getValue();

            assertThat(saved.getName()).isEqualTo("王大明(改)");
            assertThat(saved.getDepartmentName()).isEqualTo("產品部");
            assertThat(saved.getStatus()).isEqualTo("ON_LEAVE");
        }

        @Test
        @DisplayName("不存在的員工 → 不儲存")
        void nonExistingEmployee_doesNotSave() {
            // Arrange
            when(employeeRosterRepository.findById("emp-999")).thenReturn(Optional.empty());

            String message = """
                    {
                        "employeeId": "emp-999",
                        "name": "不存在"
                    }
                    """;

            // Act
            handler.handleEmployeeUpdated(message);

            // Assert
            verify(employeeRosterRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("handleEmployeeDeleted")
    class HandleEmployeeDeleted {

        @Test
        @DisplayName("既有員工 → isDeleted 標記為 true")
        void existingEmployee_marksAsDeleted() {
            // Arrange
            EmployeeRosterReadModel existing = EmployeeRosterReadModel.builder()
                    .employeeId("emp-001")
                    .tenantId("tenant-001")
                    .name("王大明")
                    .isDeleted(false)
                    .build();
            when(employeeRosterRepository.findById("emp-001")).thenReturn(Optional.of(existing));

            String message = """
                    {
                        "employeeId": "emp-001"
                    }
                    """;

            // Act
            handler.handleEmployeeDeleted(message);

            // Assert
            verify(employeeRosterRepository).save(readModelCaptor.capture());
            EmployeeRosterReadModel saved = readModelCaptor.getValue();

            assertThat(saved.getIsDeleted()).isTrue();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("格式錯誤 JSON → 不拋出異常")
        void invalidJson_doesNotThrow() {
            handler.handleEmployeeDeleted("not json");

            verify(employeeRosterRepository, never()).save(any());
        }
    }
}
