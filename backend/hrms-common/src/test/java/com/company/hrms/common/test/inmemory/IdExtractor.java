package com.company.hrms.common.test.inmemory;

/**
 * ID 擷取器函式介面
 * 用於從實體中擷取主鍵
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
@FunctionalInterface
public interface IdExtractor<T, ID> {

    /**
     * 從實體擷取主鍵
     *
     * @param entity 實體
     * @return 主鍵
     */
    ID extractId(T entity);
}
