package com.company.hrms.common.domain.model;

import com.company.hrms.common.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基類
 * 聚合根是一組相關物件的邊界，確保業務不變性的一致性
 *
 * <p>使用範例：
 * <pre>
 * public class User extends AggregateRoot&lt;UserId&gt; {
 *     private Email email;
 *     private Password password;
 *
 *     public static User create(String email, String password) {
 *         User user = new User(UserId.generate());
 *         user.email = new Email(email);
 *         user.password = Password.hash(password);
 *         user.registerEvent(new UserCreatedEvent(user.getId(), email));
 *         return user;
 *     }
 * }
 * </pre>
 *
 * <p>設計原則：
 * <ul>
 *   <li>外部只能透過聚合根修改聚合內的實體</li>
 *   <li>聚合根負責維護業務不變性</li>
 *   <li>領域事件在聚合根中註冊，由 Application Layer 發布</li>
 * </ul>
 *
 * @param <ID> 識別碼類型，必須繼承自 Identifier
 */
public abstract class AggregateRoot<ID extends Identifier<?>> extends Entity<ID> {

    private static final long serialVersionUID = 1L;

    /**
     * 待發布的領域事件列表
     */
    private transient List<DomainEvent> domainEvents;

    /**
     * 聚合創建時間
     */
    protected LocalDateTime createdAt;

    /**
     * 聚合最後更新時間
     */
    protected LocalDateTime updatedAt;

    /**
     * 建立聚合根實例
     * @param id 聚合根識別碼
     */
    protected AggregateRoot(ID id) {
        super(id);
        this.domainEvents = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 註冊領域事件
     * 事件將在 Repository.save() 後由 Application Layer 發布
     *
     * @param event 待發布的領域事件
     */
    protected void registerEvent(DomainEvent event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    /**
     * 取得所有待發布的領域事件（不可修改）
     * @return 領域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        if (this.domainEvents == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.domainEvents);
    }

    /**
     * 清除所有待發布的領域事件
     * 通常在事件發布後由 Repository 呼叫
     */
    public void clearDomainEvents() {
        if (this.domainEvents != null) {
            this.domainEvents.clear();
        }
    }

    /**
     * 取得創建時間
     * @return 創建時間
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 取得最後更新時間
     * @return 最後更新時間
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 更新最後修改時間
     * 子類別在修改狀態時應呼叫此方法
     */
    protected void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
