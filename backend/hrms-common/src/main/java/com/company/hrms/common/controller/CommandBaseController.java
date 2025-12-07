package com.company.hrms.common.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.hrms.common.factory.CommandApiServiceFactory;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

/**
 * Command Controller 基類
 * 所有處理寫入操作 (POST, PUT, DELETE) 的 Controller 必須繼承此類別
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}CmdController
 * </p>
 * <p>
 * 範例：HR01UserCmdController, HR02EmployeeCmdController
 * </p>
 * 
 * <p>
 * 此基類提供 execCommand 方法，自動透過工廠模式查找對應的 Service 並執行
 * </p>
 */
public abstract class CommandBaseController {

    @Autowired
    private CommandApiServiceFactory commandApiServiceFactory;

    /**
     * 執行 Command 操作
     * 根據 Controller 方法名稱自動查找對應的 Service 並執行
     * 
     * @param request     請求物件
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (如 PathVariable)
     * @param <T>         Request 類型
     * @param <R>         Response 類型
     * @return 回應物件
     * @throws Exception 業務邏輯例外
     */
    protected <T, R> R execCommand(T request, JWTModel currentUser, String... args)
            throws Exception {
        CommandApiService<T, R> service = commandApiServiceFactory.getService();
        return service.execCommand(request, currentUser, args);
    }
}
