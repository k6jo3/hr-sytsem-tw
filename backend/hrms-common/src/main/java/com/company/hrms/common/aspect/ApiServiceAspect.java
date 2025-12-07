package com.company.hrms.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API Service AOP 切面
 * 攔截 Controller 方法，根據方法名稱設定目標 Service Bean 名稱
 * 
 * <p>
 * 命名規則：Controller 方法名稱 + "ServiceImpl"
 * </p>
 * <p>
 * 範例：createUser -> createUserServiceImpl
 * </p>
 */
@Aspect
@Component
public class ApiServiceAspect {

    private final BeanNameConfig beanNameConfig;

    @Autowired
    public ApiServiceAspect(BeanNameConfig beanNameConfig) {
        this.beanNameConfig = beanNameConfig;
    }

    /**
     * 攔截所有繼承 CommandBaseController 的 Controller 方法
     */
    @Around("execution(* com.company.hrms..*.api.controller..*CmdController.*(..))")
    public Object aroundCommandController(ProceedingJoinPoint joinPoint) throws Throwable {
        return processController(joinPoint);
    }

    /**
     * 攔截所有繼承 QueryBaseController 的 Controller 方法
     */
    @Around("execution(* com.company.hrms..*.api.controller..*QryController.*(..))")
    public Object aroundQueryController(ProceedingJoinPoint joinPoint) throws Throwable {
        return processController(joinPoint);
    }

    /**
     * 處理 Controller 方法調用
     * 1. 根據方法名稱計算 Service Bean 名稱
     * 2. 設定到 RequestScope 的 BeanNameConfig
     * 3. 執行原方法
     */
    private Object processController(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();

        // 方法名稱 + "ServiceImpl" = Bean 名稱
        // 例如: createUser -> createUserServiceImpl
        String serviceBeanName = methodName + "ServiceImpl";

        // 首字母小寫 (符合 Spring Bean 命名慣例)
        serviceBeanName = Character.toLowerCase(serviceBeanName.charAt(0))
                + serviceBeanName.substring(1);

        beanNameConfig.setBeanName(serviceBeanName);

        return joinPoint.proceed();
    }
}
