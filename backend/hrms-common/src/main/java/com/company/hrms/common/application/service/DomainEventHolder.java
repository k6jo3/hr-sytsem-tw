package com.company.hrms.common.application.service;

import com.company.hrms.common.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DomainEvent 暫存器
 * 用於測試時攔截 Service 發布的領域事件
 *
 * <p>使用 ThreadLocal 確保執行緒安全，適用於單元測試環境
 *
 * <p>使用範例：
 * <pre>
 * // 測試開始前
 * DomainEventHolder.clear();
 *
 * // 執行 Service
 * service.execCommand(request, user);
 *
 * // 驗證事件
 * DomainEvent event = DomainEventHolder.getLast();
 * assertThat(event).isInstanceOf(EmployeeCreatedEvent.class);
 *
 * // 或取得特定類型的事件
 * List&lt;EmployeeCreatedEvent&gt; events = DomainEventHolder.getByType(EmployeeCreatedEvent.class);
 * </pre>
 */
public final class DomainEventHolder {

    private static final ThreadLocal<List<DomainEvent>> HOLDER = ThreadLocal.withInitial(ArrayList::new);

    private DomainEventHolder() {
        // Utility class
    }

    /**
     * 記錄 DomainEvent
     */
    public static void capture(DomainEvent event) {
        if (event != null) {
            HOLDER.get().add(event);
        }
    }

    /**
     * 記錄多個 DomainEvent
     */
    public static void captureAll(List<? extends DomainEvent> events) {
        if (events != null) {
            HOLDER.get().addAll(events);
        }
    }

    /**
     * 取得最後一個被攔截的 DomainEvent
     */
    public static DomainEvent getLast() {
        List<DomainEvent> list = HOLDER.get();
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    /**
     * 取得最後一個指定類型的 DomainEvent
     */
    @SuppressWarnings("unchecked")
    public static <E extends DomainEvent> E getLast(Class<E> eventType) {
        List<DomainEvent> list = HOLDER.get();
        for (int i = list.size() - 1; i >= 0; i--) {
            DomainEvent event = list.get(i);
            if (eventType.isInstance(event)) {
                return (E) event;
            }
        }
        return null;
    }

    /**
     * 取得第 N 個被攔截的 DomainEvent (從 0 開始)
     */
    public static DomainEvent get(int index) {
        List<DomainEvent> list = HOLDER.get();
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    /**
     * 取得所有被攔截的 DomainEvent
     */
    public static List<DomainEvent> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(HOLDER.get()));
    }

    /**
     * 取得指定類型的所有事件
     */
    @SuppressWarnings("unchecked")
    public static <E extends DomainEvent> List<E> getByType(Class<E> eventType) {
        return HOLDER.get().stream()
            .filter(eventType::isInstance)
            .map(e -> (E) e)
            .collect(Collectors.toList());
    }

    /**
     * 檢查是否有特定類型的事件
     */
    public static boolean hasEventOfType(Class<? extends DomainEvent> eventType) {
        return HOLDER.get().stream().anyMatch(eventType::isInstance);
    }

    /**
     * 取得已攔截的 DomainEvent 數量
     */
    public static int count() {
        return HOLDER.get().size();
    }

    /**
     * 取得指定類型的事件數量
     */
    public static int count(Class<? extends DomainEvent> eventType) {
        return (int) HOLDER.get().stream().filter(eventType::isInstance).count();
    }

    /**
     * 清除所有攔截的 DomainEvent
     * 建議在每個測試方法開始前呼叫
     */
    public static void clear() {
        HOLDER.get().clear();
    }

    /**
     * 移除 ThreadLocal（避免記憶體洩漏）
     * 建議在測試結束後呼叫
     */
    public static void remove() {
        HOLDER.remove();
    }
}
