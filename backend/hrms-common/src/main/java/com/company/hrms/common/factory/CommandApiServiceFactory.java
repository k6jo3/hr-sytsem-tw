package com.company.hrms.common.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.company.hrms.common.aspect.BeanNameConfig;
import com.company.hrms.common.service.CommandApiService;

/**
 * Command API Service 工廠
 * 根據 BeanNameConfig 中的 Bean 名稱動態查找對應的 Command Service
 * 
 * <p>
 * 此工廠類別配合 ApiServiceAspect 使用，實現 Controller 與 Service 的動態綁定
 * </p>
 */
@Component
public class CommandApiServiceFactory {

    private final ApplicationContext applicationContext;
    private final BeanNameConfig beanNameConfig;

    public CommandApiServiceFactory(ApplicationContext applicationContext,
            BeanNameConfig beanNameConfig) {
        this.applicationContext = applicationContext;
        this.beanNameConfig = beanNameConfig;
    }

    /**
     * 取得當前請求對應的 Command Service
     * 
     * @param <T> Request 類型
     * @param <R> Response 類型
     * @return CommandApiService 實例
     * @throws IllegalStateException 若找不到對應的 Service Bean
     */
    @SuppressWarnings("unchecked")
    public <T, R> CommandApiService<T, R> getService() {
        String beanName = beanNameConfig.getBeanName();

        if (beanName == null || beanName.isEmpty()) {
            throw new IllegalStateException("Service bean name not set. " +
                    "Ensure ApiServiceAspect is properly configured.");
        }

        if (!applicationContext.containsBean(beanName)) {
            throw new IllegalStateException("Service bean not found: " + beanName +
                    ". Ensure the service is annotated with @Service(\"" + beanName + "\")");
        }

        return (CommandApiService<T, R>) applicationContext.getBean(beanName);
    }
}
