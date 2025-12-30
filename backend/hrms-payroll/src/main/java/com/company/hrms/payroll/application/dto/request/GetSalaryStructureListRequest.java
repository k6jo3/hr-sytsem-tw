package com.company.hrms.payroll.application.dto.request;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.QueryCondition.EQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 薪資結構列表查詢請求
 *
 * <p>使用 {@code @EQ} 等註解宣告查詢條件，無需手動撰寫 if-else 判斷。
 * 當欄位值為 null 時，該條件會自動被忽略。</p>
 *
 * <p><b>使用範例：</b></p>
 * <pre>
 * // 查詢特定員工的有效薪資結構
 * GetSalaryStructureListRequest req = new GetSalaryStructureListRequest();
 * req.setEmployeeId("EMP001");
 * req.setActive(true);
 * req.setPage(1);
 * req.setSize(20);
 *
 * // 透過 Condition 包裝器執行查詢
 * Page&lt;SalaryStructure&gt; result = repository.findPage(Condition.of(req));
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetSalaryStructureListRequest extends PageRequest {

    /**
     * 員工編號 (精確匹配)
     */
    @EQ
    private String employeeId;

    /**
     * 是否有效 (精確匹配)
     */
    @EQ
    private Boolean active;

    /**
     * 薪資制度 (精確匹配)
     * 可選值：MONTHLY (月薪制), HOURLY (時薪制)
     */
    @EQ
    private String payrollSystem;
}
