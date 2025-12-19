package com.company.hrms.common.application.pipeline;

/**
 * Pipeline 任務介面
 * 定義 Pipeline 中單一步驟的執行邏輯
 *
 * <p>任務類型與命名規則：
 * <ul>
 *   <li>Infrastructure Task：Load{Entity}Task（載入資料）</li>
 *   <li>Domain Task：{動詞}{業務}Task（業務計算）</li>
 *   <li>Integration Task：{動詞}{Service}Task（外部服務呼叫）</li>
 * </ul>
 *
 * <p>使用範例：
 * <pre>
 * {@literal @}Component
 * public class LoadEmployeeTask implements PipelineTask&lt;SalaryContext&gt; {
 *
 *     {@literal @}Autowired
 *     private IEmployeeRepository employeeRepository;
 *
 *     {@literal @}Override
 *     public void execute(SalaryContext context) {
 *         Employee employee = employeeRepository
 *             .findById(context.getEmployeeId())
 *             .orElseThrow(() -&gt; new EntityNotFoundException("Employee not found"));
 *
 *         context.setEmployee(employee);
 *     }
 *
 *     {@literal @}Override
 *     public String getName() {
 *         return "載入員工資料";
 *     }
 * }
 * </pre>
 *
 * @param <C> Context 類型，必須繼承自 PipelineContext
 */
public interface PipelineTask<C extends PipelineContext> {

    /**
     * 執行任務
     *
     * @param context Pipeline 執行上下文
     * @throws Exception 執行失敗時拋出異常
     */
    void execute(C context) throws Exception;

    /**
     * 取得任務名稱（用於日誌與監控）
     *
     * @return 任務名稱
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否應該執行此任務
     * 可用於條件式執行
     *
     * @param context Pipeline 執行上下文
     * @return true 表示應執行，false 表示跳過
     */
    default boolean shouldExecute(C context) {
        return true;
    }
}
