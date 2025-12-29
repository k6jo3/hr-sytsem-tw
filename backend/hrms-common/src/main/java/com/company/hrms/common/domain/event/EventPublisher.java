package com.company.hrms.common.domain.event;

import java.util.List;

/**
 * 事件發布器介面
 * Application Layer 使用此介面發布領域事件
 * <p>此介面定義了發布領域事件的標準契約</p>
 *
 * <p>使用範例：
 * <pre>
 * {@literal @}Service
 * public class CreateUserServiceImpl implements CommandApiService&lt;...&gt; {
 *     {@literal @}Autowired
 *     private EventPublisher eventPublisher;
 *
 *     public CreateUserResponse execCommand(...) {
 *         User user = User.create(...);
 *         userRepository.save(user);
 *
 *         // 發布聚合根中的所有事件
 *         eventPublisher.publishAll(user.getDomainEvents());
 *         user.clearDomainEvents();
 *
 *         return response;
 *     }
 * }
 * </pre>
 */
public interface EventPublisher {

    /**
     * 發布單一領域事件
     * @param event 待發布的領域事件
     */
    void publish(DomainEvent event);

    /**
     * 發布多個領域事件
     * @param events 待發布的領域事件列表
     */
    default void publishAll(List<DomainEvent> events) {
        if (events != null) {
            events.forEach(this::publish);
        }
    }
}
