-------------------------------------------------------------------------------
Test set: com.company.hrms.workflow.domain.contract.WorkflowQueryContractTest
-------------------------------------------------------------------------------
Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 1.200 s <<< FAILURE! -- in com.company.hrms.workflow.domain.contract.WorkflowQueryContractTest
com.company.hrms.workflow.domain.contract.WorkflowQueryContractTest.searchPendingTasks_ShouldMatchContract -- Time elapsed: 1.170 s <<< FAILURE!
com.company.hrms.common.test.contract.ContractViolationException: 

╔══════════════════════════════════════════════════════════════╗
║                    合約驗證失敗                               ║
╠══════════════════════════════════════════════════════════════╣
║ 場景 ID: WFL_T001                                             ║
╠══════════════════════════════════════════════════════════════╣
║ 缺失的過濾條件:                                               ║
║   ❌ assignee_id = 'EMP001'                                   ║
╠══════════════════════════════════════════════════════════════╣
║ 實際產出的過濾條件:                                           ║
║   ✓ assigneeId = 'EMP001'                                    ║
║   ✓ status = 'PENDING'                                       ║
╚══════════════════════════════════════════════════════════════╝

	at com.company.hrms.common.test.contract.MarkdownContractEngine.assertContract(MarkdownContractEngine.java:72)
	at com.company.hrms.common.test.contract.BaseContractTest.assertContract(BaseContractTest.java:76)
	at com.company.hrms.workflow.domain.contract.WorkflowQueryContractTest.searchPendingTasks_ShouldMatchContract(WorkflowQueryContractTest.java:64)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)

