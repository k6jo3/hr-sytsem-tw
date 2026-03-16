package com.company.hrms.iam.application.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;
import com.company.hrms.iam.infrastructure.event.EmployeeCreatedEventDto;

/**
 * 自動建立員工帳號服務 - 單元測試
 *
 * <p>測試覆蓋：
 * <ul>
 *   <li>成功路徑：正常建立帳號並指派 EMPLOYEE 角色</li>
 *   <li>重複防護：已存在相同 employeeId 的帳號時跳過</li>
 *   <li>重複防護：已存在相同 username 的帳號時跳過</li>
 *   <li>缺失 email：email 為 null 時仍可建立帳號</li>
 *   <li>角色不存在：EMPLOYEE 角色不存在時仍可建立帳號（不指派角色）</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutoCreateUserFromEmployeeService 單元測試")
class AutoCreateUserFromEmployeeServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private PasswordHashingDomainService passwordHashingService;

    private AutoCreateUserFromEmployeeService service;

    @BeforeEach
    void setUp() {
        service = new AutoCreateUserFromEmployeeService(
                userRepository, roleRepository, passwordHashingService);
    }

    @Test
    @DisplayName("成功路徑：建立帳號並指派 EMPLOYEE 角色")
    void createUserForEmployee_success_createsUserAndAssignsRole() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-001")
                .employeeNumber("E001")
                .fullName("王大明")
                .companyEmail("wang@company.com")
                .build();

        when(userRepository.existsByUsername("E001")).thenReturn(false);
        when(userRepository.findByEmployeeId("emp-001")).thenReturn(Optional.empty());
        when(passwordHashingService.hash(any())).thenReturn("$2a$12$hashedpassword");

        Role employeeRole = Role.builder()
                .id(RoleId.of("role-0004"))
                .roleCode("EMPLOYEE")
                .build();
        when(roleRepository.findByRoleCode("EMPLOYEE")).thenReturn(Optional.of(employeeRole));

        // Act
        service.createUserForEmployee(dto);

        // Assert - 儲存使用者
        verify(userRepository).save(argThat(user ->
                "E001".equals(user.getUsername()) &&
                "王大明".equals(user.getDisplayName()) &&
                "emp-001".equals(user.getEmployeeId()) &&
                user.getEmail() != null &&
                "wang@company.com".equals(user.getEmail().getValue()) &&
                UserStatus.ACTIVE.equals(user.getStatus()) &&
                user.isMustChangePassword()
        ));

        // Assert - 指派角色
        verify(userRepository).updateUserRoles(any(UserId.class), argThat(roleIds ->
                roleIds.size() == 1 && roleIds.contains("role-0004")
        ));
    }

    @Test
    @DisplayName("重複防護：已存在相同 employeeId 的帳號 → 跳過建立")
    void createUserForEmployee_duplicateEmployeeId_skips() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-exists")
                .employeeNumber("E999")
                .fullName("已存在的員工")
                .companyEmail("exists@company.com")
                .build();

        User existingUser = User.builder()
                .id(UserId.generate())
                .username("E999")
                .employeeId("emp-exists")
                .build();
        when(userRepository.findByEmployeeId("emp-exists")).thenReturn(Optional.of(existingUser));

        // Act
        service.createUserForEmployee(dto);

        // Assert — 不應呼叫 save
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("重複防護：已存在相同 username 的帳號 → 跳過建立")
    void createUserForEmployee_duplicateUsername_skips() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-new")
                .employeeNumber("E001")
                .fullName("新員工")
                .companyEmail("new@company.com")
                .build();

        when(userRepository.findByEmployeeId("emp-new")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("E001")).thenReturn(true);

        // Act
        service.createUserForEmployee(dto);

        // Assert — 不應呼叫 save
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("缺失 email：email 為 null 時仍可建立帳號（使用預設 email）")
    void createUserForEmployee_nullEmail_createsWithDefaultEmail() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-no-email")
                .employeeNumber("E005")
                .fullName("沒有信箱")
                .companyEmail(null)
                .build();

        when(userRepository.existsByUsername("E005")).thenReturn(false);
        when(userRepository.findByEmployeeId("emp-no-email")).thenReturn(Optional.empty());
        when(passwordHashingService.hash(any())).thenReturn("$2a$12$hashedpassword");
        when(roleRepository.findByRoleCode("EMPLOYEE")).thenReturn(Optional.empty());

        // Act
        service.createUserForEmployee(dto);

        // Assert - 使用預設 email（employeeNumber@placeholder.local，Email VO 會轉小寫）
        verify(userRepository).save(argThat(user ->
                "E005".equals(user.getUsername()) &&
                user.getEmail() != null &&
                user.getEmail().getValue().contains("e005@placeholder.local")
        ));
    }

    @Test
    @DisplayName("EMPLOYEE 角色不存在 → 建立帳號但不指派角色")
    void createUserForEmployee_roleNotFound_createsWithoutRole() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-no-role")
                .employeeNumber("E006")
                .fullName("找不到角色")
                .companyEmail("norole@company.com")
                .build();

        when(userRepository.existsByUsername("E006")).thenReturn(false);
        when(userRepository.findByEmployeeId("emp-no-role")).thenReturn(Optional.empty());
        when(passwordHashingService.hash(any())).thenReturn("$2a$12$hashedpassword");
        when(roleRepository.findByRoleCode("EMPLOYEE")).thenReturn(Optional.empty());

        // Act
        service.createUserForEmployee(dto);

        // Assert - 帳號建立成功
        verify(userRepository).save(any(User.class));

        // Assert - 不指派角色
        verify(userRepository, never()).updateUserRoles(any(), any());
    }

    @Test
    @DisplayName("employeeId 為 null → 不建立帳號")
    void createUserForEmployee_nullEmployeeId_skips() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId(null)
                .employeeNumber("E007")
                .fullName("無ID")
                .build();

        // Act
        service.createUserForEmployee(dto);

        // Assert — 不應呼叫 save
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("employeeNumber 為 null → 不建立帳號")
    void createUserForEmployee_nullEmployeeNumber_skips() {
        // Arrange
        EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                .employeeId("emp-008")
                .employeeNumber(null)
                .fullName("無編號")
                .build();

        // Act
        service.createUserForEmployee(dto);

        // Assert — 不應呼叫 save
        verify(userRepository, never()).save(any());
    }
}
