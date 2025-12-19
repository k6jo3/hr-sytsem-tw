package com.company.hrms.common.application.pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * Pipeline 執行上下文基類
 * 在 Pipeline 各 Task 間傳遞資料
 *
 * <p>使用範例：
 * <pre>
 * public class SalaryContext extends PipelineContext {
 *     private String employeeId;
 *     private YearMonth period;
 *     private Employee employee;
 *     private List&lt;AttendanceRecord&gt; attendances;
 *     private SalaryResult result;
 *
 *     public SalaryContext(String employeeId, YearMonth period) {
 *         this.employeeId = employeeId;
 *         this.period = period;
 *     }
 *
 *     // getters and setters...
 * }
 * </pre>
 */
public abstract class PipelineContext {

    /**
     * 通用屬性儲存區
     * 用於儲存 Task 間需要傳遞的臨時資料
     */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * 是否中斷後續 Task 執行
     */
    private boolean aborted = false;

    /**
     * 中斷原因
     */
    private String abortReason;

    /**
     * 儲存屬性
     *
     * @param key 屬性鍵
     * @param value 屬性值
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * 取得屬性
     *
     * @param key 屬性鍵
     * @param <T> 屬性值類型
     * @return 屬性值，若不存在則返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * 取得屬性（帶預設值）
     *
     * @param key 屬性鍵
     * @param defaultValue 預設值
     * @param <T> 屬性值類型
     * @return 屬性值，若不存在則返回預設值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        Object value = attributes.get(key);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 移除屬性
     *
     * @param key 屬性鍵
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    /**
     * 檢查是否存在屬性
     *
     * @param key 屬性鍵
     * @return 是否存在
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * 中斷 Pipeline 執行
     *
     * @param reason 中斷原因
     */
    public void abort(String reason) {
        this.aborted = true;
        this.abortReason = reason;
    }

    /**
     * 是否已中斷
     */
    public boolean isAborted() {
        return aborted;
    }

    /**
     * 取得中斷原因
     */
    public String getAbortReason() {
        return abortReason;
    }
}
