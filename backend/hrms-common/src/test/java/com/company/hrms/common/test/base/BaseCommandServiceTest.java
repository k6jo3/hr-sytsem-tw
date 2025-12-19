package com.company.hrms.common.test.base;

import com.company.hrms.common.application.service.DomainEventHolder;
import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.test.snapshot.FluentAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Command Service 層測試基類
 * 提供 DomainEvent 攔截與驗證能力
 *
 * <p>測試重點:
 * <ul>
 *   <li>Command 執行結果驗證</li>
 *   <li>領域事件發布驗證</li>
 *   <li>狀態變更驗證</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * class CreateEmployeeServiceTest extends BaseCommandServiceTest&lt;CreateEmployeeServiceImpl&gt; {
 *
 *     {@literal @}Mock
 *     private IEmployeeRepository repository;
 *
 *     {@literal @}InjectMocks
 *     private CreateEmployeeServiceImpl service;
 *
 *     {@literal @}Test
 *     void create_ShouldPublishEvent() throws Exception {
 *         CreateEmployeeRequest req = new CreateEmployeeRequest();
 *         req.setName("張三");
 *
 *         // 執行 Service
 *         executeCommand(() -&gt; service.execCommand(req, mockUser));
 *
 *         // 驗證事件
 *         assertEventPublished(EmployeeCreatedEvent.class);
 *         EmployeeCreatedEvent event = getLastEvent(EmployeeCreatedEvent.class);
 *         assertEquals("張三", event.getEmployeeName());
 *     }
 * }
 * </pre>
 *
 * @param <S> Service 類型
 */
public abstract class BaseCommandServiceTest<S> extends BaseUnitTest {

    @BeforeEach
    void setUpEventHolder() {
        DomainEventHolder.clear();
    }

    @AfterEach
    void tearDownEventHolder() {
        DomainEventHolder.remove();
    }

    /**
     * 執行 Command Service
     *
     * @param commandCall Service 呼叫
     */
    protected void executeCommand(Runnable commandCall) {
        DomainEventHolder.clear();
        try {
            commandCall.run();
        } catch (Exception e) {
            throw new RuntimeException("Command 執行失敗", e);
        }
    }

    /**
     * 執行 Command Service 並回傳結果
     *
     * @param commandCall Service 呼叫
     * @return Service 回傳結果
     */
    protected <R> R executeCommandWithResult(java.util.concurrent.Callable<R> commandCall) {
        DomainEventHolder.clear();
        try {
            return commandCall.call();
        } catch (Exception e) {
            throw new RuntimeException("Command 執行失敗", e);
        }
    }

    /**
     * 取得最後被發布的事件
     */
    protected DomainEvent getLastEvent() {
        return DomainEventHolder.getLast();
    }

    /**
     * 取得最後一個指定類型的事件
     */
    protected <E extends DomainEvent> E getLastEvent(Class<E> eventType) {
        return DomainEventHolder.getLast(eventType);
    }

    /**
     * 取得所有事件
     */
    protected List<DomainEvent> getAllEvents() {
        return DomainEventHolder.getAll();
    }

    /**
     * 取得指定類型的所有事件
     */
    protected <E extends DomainEvent> List<E> getEvents(Class<E> eventType) {
        return DomainEventHolder.getByType(eventType);
    }

    /**
     * 斷言有發布特定類型的事件
     */
    protected void assertEventPublished(Class<? extends DomainEvent> eventType) {
        assertTrue(
            DomainEventHolder.hasEventOfType(eventType),
            String.format("預期發布 [%s] 事件，但未找到。\n已發布的事件: %s",
                eventType.getSimpleName(), DomainEventHolder.getAll()));
    }

    /**
     * 斷言沒有發布特定類型的事件
     */
    protected void assertEventNotPublished(Class<? extends DomainEvent> eventType) {
        assertFalse(
            DomainEventHolder.hasEventOfType(eventType),
            String.format("預期沒有發布 [%s] 事件，但找到了。",
                eventType.getSimpleName()));
    }

    /**
     * 斷言發布了特定數量的事件
     */
    protected void assertEventCount(int expectedCount) {
        assertEquals(expectedCount, DomainEventHolder.count(),
            String.format("預期發布 [%d] 個事件，但實際發布 [%d] 個。\n事件列表: %s",
                expectedCount, DomainEventHolder.count(), DomainEventHolder.getAll()));
    }

    /**
     * 斷言沒有發布任何事件
     */
    protected void assertNoEventsPublished() {
        assertTrue(
            DomainEventHolder.count() == 0,
            String.format("預期沒有發布任何事件，但發布了: %s",
                DomainEventHolder.getAll()));
    }

    /**
     * 驗證事件快照
     */
    protected void verifyEventSnapshot(DomainEvent event, String snapshotName) {
        FluentAssert.that(event)
            .ignoringCommonDynamicFields()
            .inDirectory(getEventSnapshotDirectory())
            .matchesSnapshot(snapshotName);
    }

    /**
     * 驗證 Response 快照
     */
    protected <T> void verifyResponse(T response, String snapshotName) {
        FluentAssert.that(response)
            .ignoringCommonDynamicFields()
            .inDirectory(getResponseSnapshotDirectory())
            .matchesSnapshot(snapshotName);
    }

    /**
     * 取得事件快照目錄
     */
    protected String getEventSnapshotDirectory() {
        return "src/test/resources/snapshots/event/" + getTestClassName();
    }

    /**
     * 取得 Response 快照目錄
     */
    protected String getResponseSnapshotDirectory() {
        return "src/test/resources/snapshots/response/" + getTestClassName();
    }
}
