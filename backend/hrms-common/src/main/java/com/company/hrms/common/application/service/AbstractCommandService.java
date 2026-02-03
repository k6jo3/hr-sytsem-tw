package com.company.hrms.common.application.service;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 抽象命令服務基類
 * 提供領域事件收集與發布能力
 *
 * <p>設計理念：
 * <ul>
 *   <li>統一領域事件發布流程</li>
 *   <li>支援測試時攔截事件</li>
 *   <li>符合 DDD 事件驅動架構</li>
 * </ul>
 *
 * <p>使用範例：
 * <pre>
 * {@literal @}Service("createEmployeeServiceImpl")
 * public class CreateEmployeeServiceImpl
 *         extends AbstractCommandService&lt;CreateEmployeeRequest, CreateEmployeeResponse&gt; {
 *
 *     private final IEmployeeRepository repository;
 *
 *     {@literal @}Override
 *     protected CreateEmployeeResponse doExecute(
 *             CreateEmployeeRequest request,
 *             JWTModel currentUser,
 *             String... args) {
 *
 *         Employee employee = Employee.create(
 *             request.getName(),
 *             new Email(request.getEmail())
 *         );
 *
 *         repository.save(employee);
 *
 *         // 註冊領域事件
 *         registerEvent(new EmployeeCreatedEvent(employee.getId()));
 *
 *         return new CreateEmployeeResponse(employee.getId().getValue());
 *     }
 * }
 * </pre>
 *
 * @param <T> Request 類型
 * @param <R> Response 類型
 */
public abstract class AbstractCommandService<T, R> implements CommandApiService<T, R> {

    /** 事件發布器 (可由子類注入) */
    protected EventPublisher eventPublisher;

    /** 收集的領域事件 */
    private List<DomainEvent> collectedEvents;

    /** 是否自動發布事件 */
    private boolean autoPublish = true;

    /** 是否啟用事件攔截（供測試使用） */
    private boolean captureEnabled = true;

    /**
     * 初始化 collectedEvents
     * 在構造後立即執行，確保字段被正確初始化
     */
    public AbstractCommandService() {
        this.collectedEvents = new ArrayList<>();
    }

    @Override
    public final R execCommand(T request, JWTModel currentUser, String... args) throws Exception {
        // 確保 collectedEvents 被初始化（延遲初始化）
        if (collectedEvents == null) {
            collectedEvents = new ArrayList<>();
        }

        // 清空先前收集的事件
        collectedEvents.clear();

        // 執行業務邏輯
        R result = doExecute(request, currentUser, args);

        // 攔截事件（供測試使用）
        if (captureEnabled && !collectedEvents.isEmpty()) {
            DomainEventHolder.captureAll(collectedEvents);
        }

        // 自動發布事件
        if (autoPublish && eventPublisher != null) {
            publishCollectedEvents();
        }

        return result;
    }

    /**
     * 執行命令邏輯
     * 子類實作此方法處理業務邏輯
     *
     * @param request     請求物件
     * @param currentUser 當前使用者
     * @param args        額外參數
     * @return 回應物件
     */
    protected abstract R doExecute(T request, JWTModel currentUser, String... args) throws Exception;

    /**
     * 註冊領域事件
     * 事件會在命令執行完成後自動發布
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            collectedEvents.add(event);
        }
    }

    /**
     * 註冊多個領域事件
     */
    protected void registerEvents(List<? extends DomainEvent> events) {
        if (events != null) {
            collectedEvents.addAll(events);
        }
    }

    /**
     * 發布所有收集的事件
     */
    protected void publishCollectedEvents() {
        for (DomainEvent event : collectedEvents) {
            eventPublisher.publish(event);
        }
        collectedEvents.clear();
    }

    /**
     * 取得已收集的事件（供測試使用）
     */
    public List<DomainEvent> getCollectedEvents() {
        return Collections.unmodifiableList(new ArrayList<>(collectedEvents));
    }

    /**
     * 設定事件發布器
     */
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 設定是否自動發布事件
     */
    public void setAutoPublish(boolean autoPublish) {
        this.autoPublish = autoPublish;
    }

    /**
     * 檢查是否自動發布事件
     */
    public boolean isAutoPublish() {
        return autoPublish;
    }

    /**
     * 設定是否啟用事件攔截
     */
    public void setCaptureEnabled(boolean captureEnabled) {
        this.captureEnabled = captureEnabled;
    }

    /**
     * 檢查是否啟用事件攔截
     */
    public boolean isCaptureEnabled() {
        return captureEnabled;
    }
}
