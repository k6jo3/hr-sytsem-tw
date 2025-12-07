package com.company.hrms.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 標記 Controller 方法參數，用於注入當前登入使用者資訊
 * 
 * <p>使用範例：</p>
 * <pre>
 * {@code
 * @PostMapping
 * public ResponseEntity<CreateUserResponse> createUser(
 *         @RequestBody @Valid CreateUserRequest request,
 *         @CurrentUser JWTModel currentUser) throws Exception {
 *     return ResponseEntity.ok(execCommand(request, currentUser));
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
