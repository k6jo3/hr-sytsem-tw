package com.company.hrms.performance.application.service.context;

import com.company.hrms.performance.api.request.SaveTemplateRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * 儲存考核範本 Pipeline Context
 */
@Getter
@Setter
public class SaveTemplateContext extends StartCycleContext {

    /**
     * 儲存範本請求
     */
    private final SaveTemplateRequest request;

    public SaveTemplateContext(SaveTemplateRequest request) {
        super(request.getCycleId());
        this.request = request;
    }
}
