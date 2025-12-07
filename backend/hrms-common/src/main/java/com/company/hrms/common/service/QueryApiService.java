package com.company.hrms.common.service;

import com.company.hrms.common.model.JWTModel;

/**
 * Query API Service 介面
 * 所有 Query 類型的 Application Service 必須實作此介面
 * 
 * <p>
 * 命名規範：Get{名詞}ServiceImpl, Get{名詞}ListServiceImpl
 * </p>
 * <p>
 * 範例：GetUserServiceImpl, GetUserListServiceImpl
 * </p>
 * 
 * @param <T> Request 類型
 * @param <R> Response 類型
 */
public interface QueryApiService<T, R> {

    /**
     * 執行 Query 操作
     * 
     * @param request     請求物件
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (如 PathVariable)
     * @return 回應物件
     * @throws Exception 業務邏輯例外
     */
    R getResponse(T request, JWTModel currentUser, String... args) throws Exception;
}
