package com.company.hrms.common.test.base;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 單元測試基類
 * 提供純粹的單元測試環境，不依賴 Spring Context
 *
 * <p>特點:
 * <ul>
 *   <li>使用 Mockito 進行依賴模擬</li>
 *   <li>不啟動 Spring 容器</li>
 *   <li>執行速度快</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * class MyServiceTest extends BaseUnitTest {
 *     {@literal @}Mock
 *     private MyRepository repository;
 *
 *     {@literal @}InjectMocks
 *     private MyServiceImpl service;
 *
 *     {@literal @}Test
 *     void shouldDoSomething() {
 *         // 測試邏輯
 *     }
 * }
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest extends BaseTest {

    /**
     * 驗證物件不為 null
     */
    protected void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }

    /**
     * 驗證條件為真
     */
    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * 驗證兩個物件相等
     */
    protected void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(String.format(
                "%s: 預期 [%s]，但實際為 [%s]",
                message, expected, actual));
        }
    }
}
