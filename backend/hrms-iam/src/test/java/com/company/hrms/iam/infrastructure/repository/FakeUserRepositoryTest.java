package com.company.hrms.iam.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.BaseInMemoryRepositoryTest;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FakeUserRepository 範例測試
 * 驗證 InMemory Repository 架構的可行性
 *
 * <p>注意事項：
 * <ul>
 *   <li>不啟動 Spring Context — 執行速度為毫秒級</li>
 *   <li>使用真實資料流（非 Mock）— 更貼近實際行為</li>
 *   <li>QueryGroup 過濾在記憶體中執行 — 驗證業務查詢邏輯</li>
 * </ul>
 */
class FakeUserRepositoryTest extends BaseInMemoryRepositoryTest {

    private FakeUserRepository repository;

    // 測試資料
    private User activeAdmin;
    private User activeEmployee;
    private User inactiveUser;
    private User lockedUser;
    private User probationUser;

    @Override
    protected InMemoryBaseRepository<?, ?> getRepository() {
        return repository;
    }

    @BeforeEach
    void setUp() {
        repository = new FakeUserRepository();

        // 建立測試資料（使用中文名、多種狀態，符合 CLAUDE.md 規範）
        activeAdmin = User.create("admin_wang", "wang@company.com", "hash123", "王大明");
        activeAdmin.setStatus(UserStatus.ACTIVE);
        activeAdmin.setEmployeeId("EMP001");
        activeAdmin.setTenantId("tenant-001");

        activeEmployee = User.create("emp_li", "li@company.com", "hash456", "李小花");
        activeEmployee.setStatus(UserStatus.ACTIVE);
        activeEmployee.setEmployeeId("EMP002");
        activeEmployee.setTenantId("tenant-001");

        inactiveUser = User.create("inactive_zhang", "zhang@company.com", "hash789", "張三");
        inactiveUser.setStatus(UserStatus.INACTIVE);
        inactiveUser.setEmployeeId("EMP003");
        inactiveUser.setTenantId("tenant-001");

        lockedUser = User.create("locked_chen", "chen@company.com", "hash101", "陳美麗");
        lockedUser.setStatus(UserStatus.LOCKED);
        lockedUser.setEmployeeId("EMP004");
        lockedUser.setTenantId("tenant-002");

        probationUser = User.create("new_lin", "lin@company.com", "hash202", "林小壯");
        probationUser.setStatus(UserStatus.PENDING);
        probationUser.setEmployeeId("EMP005");
        probationUser.setTenantId("tenant-001");

        // 填入測試資料
        repository.seed(activeAdmin, activeEmployee, inactiveUser, lockedUser, probationUser);
    }

    // ==================== CRUD 測試 ====================

    @Nested
    @DisplayName("基本 CRUD 操作")
    class CrudTests {
        @Test
        @DisplayName("save 後可透過 findById 取得")
        void saveAndFindById() {
            User newUser = User.create("new_user", "new@test.com", "hash", "新使用者");
            repository.save(newUser);

            Optional<User> found = repository.findById(newUser.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("new_user");
        }

        @Test
        @DisplayName("deleteById 後 findById 應回傳 empty")
        void deleteById() {
            repository.deleteById(activeAdmin.getId());

            assertThat(repository.findById(activeAdmin.getId())).isEmpty();
            assertThat(repository.size()).isEqualTo(4);
        }

        @Test
        @DisplayName("existsById 檢查存在性")
        void existsById() {
            assertThat(repository.existsById(activeAdmin.getId())).isTrue();
            assertThat(repository.existsById(new UserId("nonexistent"))).isFalse();
        }

        @Test
        @DisplayName("update 覆蓋既有資料")
        void update() {
            activeAdmin.setDisplayName("王大明（已更名）");
            repository.update(activeAdmin);

            Optional<User> found = repository.findById(activeAdmin.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getDisplayName()).isEqualTo("王大明（已更名）");
        }
    }

    // ==================== Custom Finder 測試 ====================

    @Nested
    @DisplayName("自訂查詢方法")
    class CustomFinderTests {
        @Test
        @DisplayName("findByUsername 查詢")
        void findByUsername() {
            Optional<User> found = repository.findByUsername("admin_wang");

            assertThat(found).isPresent();
            assertThat(found.get().getDisplayName()).isEqualTo("王大明");
        }

        @Test
        @DisplayName("findByUsername 不存在時回傳 empty")
        void findByUsernameNotFound() {
            assertThat(repository.findByUsername("nonexistent")).isEmpty();
        }

        @Test
        @DisplayName("findByEmail 查詢")
        void findByEmail() {
            Optional<User> found = repository.findByEmail(new Email("li@company.com"));

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("emp_li");
        }

        @Test
        @DisplayName("findByStatus 查詢")
        void findByStatus() {
            List<User> activeUsers = repository.findByStatus(UserStatus.ACTIVE);

            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers).extracting(User::getUsername)
                    .containsExactlyInAnyOrder("admin_wang", "emp_li");
        }

        @Test
        @DisplayName("findByEmployeeId 查詢")
        void findByEmployeeId() {
            Optional<User> found = repository.findByEmployeeId("EMP003");

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("inactive_zhang");
        }

        @Test
        @DisplayName("existsByUsername 檢查")
        void existsByUsername() {
            assertThat(repository.existsByUsername("admin_wang")).isTrue();
            assertThat(repository.existsByUsername("nonexistent")).isFalse();
        }

        @Test
        @DisplayName("existsByEmail 檢查")
        void existsByEmail() {
            assertThat(repository.existsByEmail(new Email("wang@company.com"))).isTrue();
            assertThat(repository.existsByEmail(new Email("nonexistent@test.com"))).isFalse();
        }
    }

    // ==================== QueryGroup 動態查詢測試 ====================

    @Nested
    @DisplayName("QueryGroup 動態查詢")
    class QueryGroupTests {
        @Test
        @DisplayName("EQ 查詢 status=ACTIVE")
        void findByStatusActive() {
            QueryGroup query = QueryGroup.and()
                    .eq("status", "ACTIVE");

            List<User> result = repository.findAll(query);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("AND 複合查詢 status=ACTIVE AND tenantId=tenant-001")
        void findByStatusAndTenant() {
            QueryGroup query = QueryGroup.and()
                    .eq("status", "ACTIVE")
                    .eq("tenantId", "tenant-001");

            List<User> result = repository.findAll(query);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(u -> "tenant-001".equals(u.getTenantId()));
        }

        @Test
        @DisplayName("LIKE 模糊查詢 username")
        void findByUsernameLike() {
            QueryGroup query = QueryGroup.and()
                    .like("username", "admin");

            List<User> result = repository.findAll(query);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("admin_wang");
        }

        @Test
        @DisplayName("IN 查詢多狀態")
        void findByStatusIn() {
            QueryGroup query = QueryGroup.and()
                    .in("status", "ACTIVE", "LOCKED");

            List<User> result = repository.findAll(query);

            assertThat(result).hasSize(3); // 2 ACTIVE + 1 LOCKED
        }

        @Test
        @DisplayName("count 查詢")
        void countByQuery() {
            QueryGroup query = QueryGroup.and()
                    .eq("tenantId", "tenant-001");

            long count = repository.count(query);

            assertThat(count).isEqualTo(4); // admin, employee, inactive, probation
        }

        @Test
        @DisplayName("OR 子群組查詢")
        void findWithOrSubGroup() {
            // tenantId = 'tenant-002' OR status = 'INACTIVE'
            QueryGroup query = QueryGroup.or()
                    .eq("tenantId", "tenant-002")
                    .eq("status", "INACTIVE");

            List<User> result = repository.findAll(query);

            assertThat(result).hasSize(2); // locked_chen (tenant-002) + inactive_zhang
        }
    }

    // ==================== 分頁與排序測試 ====================

    @Nested
    @DisplayName("分頁與排序")
    class PaginationTests {
        @Test
        @DisplayName("分頁查詢第一頁")
        void findPageFirstPage() {
            QueryGroup query = QueryGroup.and()
                    .eq("tenantId", "tenant-001");
            PageRequest pageRequest = PageRequest.of(0, 2);

            Page<User> page = repository.findPage(query, pageRequest);

            assertThat(page.getTotalElements()).isEqualTo(4);
            assertThat(page.getTotalPages()).isEqualTo(2);
            assertThat(page.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("分頁查詢第二頁")
        void findPageSecondPage() {
            QueryGroup query = QueryGroup.and()
                    .eq("tenantId", "tenant-001");
            PageRequest pageRequest = PageRequest.of(1, 2);

            Page<User> page = repository.findPage(query, pageRequest);

            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("排序查詢 username ASC")
        void findPageSorted() {
            QueryGroup query = QueryGroup.and();
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("username").ascending());

            Page<User> page = repository.findPage(query, pageRequest);

            assertThat(page.getContent()).extracting(User::getUsername)
                    .isSorted();
        }

        @Test
        @DisplayName("空結果回傳空頁")
        void findPageEmpty() {
            QueryGroup query = QueryGroup.and()
                    .eq("status", "DELETED");
            PageRequest pageRequest = PageRequest.of(0, 10);

            Page<User> page = repository.findPage(query, pageRequest);

            assertThat(page.getTotalElements()).isEqualTo(0);
            assertThat(page.getContent()).isEmpty();
        }
    }

    // ==================== updateUserRoles 測試 ====================

    @Test
    @DisplayName("updateUserRoles 應更新角色列表")
    void updateUserRoles() {
        List<String> newRoles = List.of("ADMIN", "HR_MANAGER");
        repository.updateUserRoles(activeAdmin.getId(), newRoles);

        Optional<User> found = repository.findById(activeAdmin.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRoles()).containsExactly("ADMIN", "HR_MANAGER");
    }
}
