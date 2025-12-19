package com.company.hrms.common.test.base;

import com.company.hrms.common.test.snapshot.FluentAssert;
import com.company.hrms.common.test.snapshot.SnapshotConfig;

/**
 * Domain Entity 測試基類
 * 專門用於測試 Domain 層的實體、值對象與領域服務
 *
 * <p>測試重點:
 * <ul>
 *   <li>業務規則驗證</li>
 *   <li>不變條件 (Invariants) 檢查</li>
 *   <li>領域事件產生</li>
 *   <li>計算邏輯正確性</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * class EmployeeTest extends BaseDomainTest {
 *
 *     {@literal @}Test
 *     void calculateBonus_ShouldApplyCorrectRules() {
 *         Employee employee = createTestEmployee();
 *         Policy policy = createTestPolicy();
 *
 *         CalculationResult&lt;BigDecimal&gt; result = employee.calculateBonus(policy);
 *
 *         verifyResult(result, "bonus_standard_case.json");
 *     }
 * }
 * </pre>
 */
public abstract class BaseDomainTest extends BaseUnitTest {

    /**
     * 驗證計算結果（快照比對）
     */
    protected <T> void verifyResult(T result, String snapshotName) {
        FluentAssert.that(result)
            .ignoringCommonDynamicFields()
            .inDirectory(getSnapshotDirectory())
            .matchesSnapshot(snapshotName);
    }

    /**
     * 驗證計算結果（帶自訂配置）
     */
    protected <T> void verifyResult(T result, String snapshotName, SnapshotConfig config) {
        FluentAssert.assertMatchesSnapshot(
            getSnapshotDirectory() + "/" + snapshotName,
            result,
            config);
    }

    /**
     * 取得快照目錄
     */
    protected String getSnapshotDirectory() {
        return "src/test/resources/snapshots/domain/" + getTestClassName();
    }

    /**
     * 驗證領域事件已產生
     */
    protected void assertEventRegistered(com.company.hrms.common.domain.model.AggregateRoot<?> aggregate,
                                          Class<? extends com.company.hrms.common.domain.event.DomainEvent> eventClass) {
        boolean found = aggregate.getDomainEvents().stream()
            .anyMatch(e -> eventClass.isInstance(e));

        if (!found) {
            throw new AssertionError(String.format(
                "預期聚合根註冊了 [%s] 事件，但未找到。\n已註冊的事件: %s",
                eventClass.getSimpleName(),
                aggregate.getDomainEvents()));
        }
    }

    /**
     * 驗證沒有產生領域事件
     */
    protected void assertNoEventsRegistered(com.company.hrms.common.domain.model.AggregateRoot<?> aggregate) {
        if (!aggregate.getDomainEvents().isEmpty()) {
            throw new AssertionError(String.format(
                "預期聚合根沒有註冊任何事件，但找到了: %s",
                aggregate.getDomainEvents()));
        }
    }
}
