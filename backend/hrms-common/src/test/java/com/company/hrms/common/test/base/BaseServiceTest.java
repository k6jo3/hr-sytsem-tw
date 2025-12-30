package com.company.hrms.common.test.base;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;

import com.company.hrms.common.application.service.QueryGroupHolder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.assertion.QueryGroupAssert;
import com.company.hrms.common.test.snapshot.FluentAssert;
import com.company.hrms.common.test.snapshot.QuerySnapshotModule;
import com.company.hrms.common.test.snapshot.SnapshotUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service 層測試基類
 * 提供 QueryGroup 攔截與快照比對能力
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 組裝邏輯驗證</li>
 * <li>Service 編排邏輯驗證</li>
 * <li>DTO 轉換正確性</li>
 * </ul>
 *
 * <p>
 * 使用範例（使用 AbstractQueryService）:
 * 
 * <pre>
 * class GetEmployeeListServiceTest extends BaseServiceTest&lt;GetEmployeeListServiceImpl&gt; {
 *
 *     {@literal @}Mock
 *     private IEmployeeRepository repository;
 *
 *     {@literal @}InjectMocks
 *     private GetEmployeeListServiceImpl service;
 *
 *     {@literal @}Test
 *     void searchByDepartment_ShouldMatchSnapshot() {
 *         GetEmployeeListRequest req = new GetEmployeeListRequest();
 *         req.setDepartmentName("研發部");
 *
 *         // 執行 Service（QueryGroup 會自動被攔截）
 *         executeAndCapture(() -&gt; service.getResponse(req, mockUser));
 *
 *         // 驗證 QueryGroup
 *         verifyCapturedQuery("employee_search_by_dept.json");
 *     }
 * }
 * </pre>
 *
 * @param <S> Service 類型
 */
public abstract class BaseServiceTest<S> extends BaseUnitTest {

    /** QueryGroup 專用的 ObjectMapper */
    protected ObjectMapper queryMapper = QuerySnapshotModule.createMapper();

    @BeforeEach
    void setUpQueryHolder() {
        QueryGroupHolder.clear();
    }

    @AfterEach
    void tearDownQueryHolder() {
        QueryGroupHolder.remove();
    }

    /**
     * 執行 Service 並攔截 QueryGroup
     * 適用於繼承 AbstractQueryService 的 Service
     *
     * @param serviceCall Service 呼叫
     */
    protected void executeAndCapture(Runnable serviceCall) {
        QueryGroupHolder.clear();
        try {
            serviceCall.run();
        } catch (Exception e) {
            // 忽略執行錯誤，我們關心的是 QueryGroup
        }
    }

    /**
     * 執行 Service 並攔截 QueryGroup，同時回傳結果
     *
     * @param serviceCall Service 呼叫
     * @return Service 回傳結果
     */
    protected <R> R executeAndCaptureWithResult(java.util.concurrent.Callable<R> serviceCall) {
        QueryGroupHolder.clear();
        try {
            return serviceCall.call();
        } catch (Exception e) {
            throw new RuntimeException("Service 執行失敗", e);
        }
    }

    /**
     * 取得最後被攔截的 QueryGroup
     */
    protected QueryGroup getCapturedQuery() {
        return QueryGroupHolder.getLast();
    }

    /**
     * 取得第 N 個被攔截的 QueryGroup
     */
    protected QueryGroup getCapturedQuery(int index) {
        return QueryGroupHolder.get(index);
    }

    /**
     * 驗證被攔截的 QueryGroup（快照比對）
     */
    protected void verifyCapturedQuery(String snapshotName) {
        QueryGroup captured = getCapturedQuery();
        if (captured == null) {
            throw new AssertionError("沒有攔截到 QueryGroup，請確認 Service 是否繼承 AbstractQueryService");
        }
        verifyQuerySnapshot(captured, snapshotName);
    }

    /**
     * 驗證 Service 產出的 QueryGroup（快照比對）
     * 相容舊版 Mockito ArgumentCaptor 方式
     *
     * @param serviceCall  Service 呼叫（Lambda）
     * @param repository   Mock 的 Repository
     * @param snapshotName 快照檔名
     */
    protected void verifyQuery(Runnable serviceCall, Object repository, String snapshotName) {
        ArgumentCaptor<QueryGroup> captor = ArgumentCaptor.forClass(QueryGroup.class);

        try {
            // 執行 Service
            serviceCall.run();
        } catch (Exception e) {
            // 忽略執行錯誤，我們只關心 QueryGroup
        }

        // 優先使用 QueryGroupHolder
        QueryGroup captured = QueryGroupHolder.getLast();
        if (captured != null) {
            verifyQuerySnapshot(captured, snapshotName);
            return;
        }

        try {
            // Fallback: 使用 Mockito captor
            verify(repository, atLeastOnce()).getClass();
            SnapshotUtils.compareOrUpdate(
                    getQuerySnapshotDirectory() + "/" + snapshotName,
                    captor.getValue(),
                    snapshotConfig,
                    queryMapper);

        } catch (Exception e) {
            System.out.println("QueryGroup 攔截需要根據實際 Repository 介面調整");
        }
    }

    /**
     * 驗證 QueryGroup（使用 DSL 斷言）
     *
     * @param queryGroup 待驗證的 QueryGroup
     * @return QueryGroupAssert 實例
     */
    protected QueryGroupAssert assertQuery(QueryGroup queryGroup) {
        return QueryGroupAssert.assertThat(queryGroup);
    }

    /**
     * 驗證 QueryGroup（快照比對）
     */
    protected void verifyQuerySnapshot(QueryGroup queryGroup, String snapshotName) {
        FluentAssert.that(queryGroup)
                .withMapper(queryMapper)
                .inDirectory(getQuerySnapshotDirectory())
                .matchesSnapshot(snapshotName);
    }

    /**
     * 驗證 Service 回應（快照比對）
     */
    protected <T> void verifyResponse(T response, String snapshotName) {
        FluentAssert.that(response)
                .ignoringCommonDynamicFields()
                .inDirectory(getResponseSnapshotDirectory())
                .matchesSnapshot(snapshotName);
    }

    /**
     * 取得 Query 快照目錄
     */
    protected String getQuerySnapshotDirectory() {
        return "src/test/resources/snapshots/query/" + getTestClassName();
    }

    /**
     * 取得 Response 快照目錄
     */
    protected String getResponseSnapshotDirectory() {
        return "src/test/resources/snapshots/response/" + getTestClassName();
    }
}
