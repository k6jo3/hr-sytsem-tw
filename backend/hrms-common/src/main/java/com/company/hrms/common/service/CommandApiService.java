package com.company.hrms.common.service;

import com.company.hrms.common.model.JWTModel;

/**
 * Command API Service 介面
 * 所有 Command 類型的 Application Service 必須實作此介面
 * 
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 範例：CreateUserServiceImpl, UpdateUserServiceImpl
 * </p>
 * 
 * @param <T> Request 類型
 * @param <R> Response 類型
 */
public interface CommandApiService<T, R> {

    /**
     * 執行 Command 操作
     * 
     * @param request     請求物件
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (如 PathVariable)
     * @return 回應物件
     * @throws Exception 業務邏輯例外
     */
    R execCommand(T request, JWTModel currentUser, String... args) throws Exception;
}
