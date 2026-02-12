package com.company.hrms.organization.api.contract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.company.hrms.organization.domain.event.DomainEvent;

/**
 * 測試用事件攔截器
 *
 * 用於在整合測試中捕獲所有發布的 Organization 領域事件
 * 注意：Organization 服務使用自己的 DomainEvent 基類，
 *       透過 ApplicationEventPublisher 發布事件
 */
@Component
public class TestEventCaptor {

    private final List<DomainEvent> capturedEvents = new ArrayList<>();

    /**
     * 攔截所有 Organization 領域事件
     */
    @EventListener
    public void handleDomainEvent(DomainEvent event) {
        capturedEvents.add(event);
        System.out.println("TestEventCaptor captured event: " + event.getEventType());
    }

    /**
     * 取得捕獲的事件列表
     */
    public List<DomainEvent> getCapturedEvents() {
        return new ArrayList<>(capturedEvents);
    }

    /**
     * 清空事件列表
     */
    public void clear() {
        capturedEvents.clear();
    }

    /**
     * 取得事件數量
     */
    public int getEventCount() {
        return capturedEvents.size();
    }
}
