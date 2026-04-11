package com.company.hrms.common.test.inmemory;

import com.company.hrms.common.test.base.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InMemory Repository 測試基底類別
 * 不啟動 Spring Context，直接使用 InMemoryBaseRepository
 *
 * <p>繼承關係：
 * <pre>
 * BaseTest
 *   └── BaseInMemoryRepositoryTest
 *         └── 各模組的 Repository 測試
 * </pre>
 *
 * <p>使用範例：
 * <pre>
 * class FakeUserRepositoryTest extends BaseInMemoryRepositoryTest {
 *     private FakeUserRepository repository;
 *
 *     &#64;BeforeEach
 *     void setUp() {
 *         repository = new FakeUserRepository();
 *         repository.seed(createTestUser("u1"), createTestUser("u2"));
 *     }
 *
 *     &#64;Test
 *     void findByUsername_ShouldReturnUser() {
 *         Optional&lt;User&gt; result = repository.findByUsername("testuser");
 *         assertThat(result).isPresent();
 *     }
 * }
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseInMemoryRepositoryTest extends BaseTest {

    /**
     * 子類應在 @BeforeEach 中初始化 repository 並填入測試資料
     * 可使用 repository.seed(...) 批量填入
     */

    /**
     * 子類可覆寫此方法回傳要在每次測試後清空的 repository
     * 預設不做任何事（子類自行管理）
     */
    protected InMemoryBaseRepository<?, ?> getRepository() {
        return null;
    }

    @AfterEach
    void tearDownInMemory() {
        InMemoryBaseRepository<?, ?> repo = getRepository();
        if (repo != null) {
            repo.clear();
        }
    }
}
