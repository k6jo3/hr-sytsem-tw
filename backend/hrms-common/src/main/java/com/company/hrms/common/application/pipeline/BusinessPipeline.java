package com.company.hrms.common.application.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 業務流程 Pipeline
 * 提供流暢 API 編排多個 Task 的執行順序
 *
 * <p>使用範例：
 * <pre>
 * {@literal @}Service("calculateMonthlySalaryServiceImpl")
 * public class CalculateMonthlySalaryServiceImpl implements CommandApiService&lt;...&gt; {
 *
 *     {@literal @}Autowired private LoadEmployeeTask loadEmployeeTask;
 *     {@literal @}Autowired private LoadAttendanceTask loadAttendanceTask;
 *     {@literal @}Autowired private LoadOvertimeTask loadOvertimeTask;
 *     {@literal @}Autowired private CalculateTaxTask calculateTaxTask;
 *
 *     {@literal @}Override
 *     public SalaryResponse execCommand(...) {
 *         SalaryContext ctx = new SalaryContext(req.getEmployeeId(), req.getPeriod());
 *
 *         BusinessPipeline.start(ctx)
 *             .next(loadEmployeeTask)
 *             .next(loadAttendanceTask)
 *             .nextIf(c -&gt; c.hasOvertime(), loadOvertimeTask)
 *             .next(calculateTaxTask)
 *             .execute();
 *
 *         return mapper.toResponse(ctx.getResult());
 *     }
 * }
 * </pre>
 *
 * @param <C> Context 類型，必須繼承自 PipelineContext
 */
public class BusinessPipeline<C extends PipelineContext> {

    private static final Logger log = LoggerFactory.getLogger(BusinessPipeline.class);

    private final C context;
    private final List<TaskEntry<C>> tasks = new ArrayList<>();

    private BusinessPipeline(C context) {
        this.context = context;
    }

    /**
     * 開始建構 Pipeline
     *
     * @param context Pipeline 執行上下文
     * @param <C> Context 類型
     * @return Pipeline 建構器
     */
    public static <C extends PipelineContext> BusinessPipeline<C> start(C context) {
        return new BusinessPipeline<>(context);
    }

    /**
     * 新增任務
     *
     * @param task 待執行的任務
     * @return Pipeline 建構器（支援鏈式呼叫）
     */
    public BusinessPipeline<C> next(PipelineTask<C> task) {
        tasks.add(new TaskEntry<>(task, null));
        return this;
    }

    /**
     * 條件式新增任務
     * 只有當條件滿足時才執行該任務
     *
     * @param condition 執行條件
     * @param task 待執行的任務
     * @return Pipeline 建構器（支援鏈式呼叫）
     */
    public BusinessPipeline<C> nextIf(Predicate<C> condition, PipelineTask<C> task) {
        tasks.add(new TaskEntry<>(task, condition));
        return this;
    }

    /**
     * 執行 Pipeline
     *
     * @throws PipelineExecutionException 當任務執行失敗時拋出
     */
    public void execute() {
        log.info("Pipeline started with {} tasks", tasks.size());
        long startTime = System.currentTimeMillis();

        int executedCount = 0;
        int skippedCount = 0;

        for (TaskEntry<C> entry : tasks) {
            // 檢查是否已中斷
            if (context.isAborted()) {
                log.warn("Pipeline aborted: {}", context.getAbortReason());
                break;
            }

            PipelineTask<C> task = entry.task;
            String taskName = task.getName();

            // 檢查條件
            if (entry.condition != null && !entry.condition.test(context)) {
                log.debug("Task [{}] skipped: condition not met", taskName);
                skippedCount++;
                continue;
            }

            // 檢查任務自身條件
            if (!task.shouldExecute(context)) {
                log.debug("Task [{}] skipped: shouldExecute returned false", taskName);
                skippedCount++;
                continue;
            }

            // 執行任務
            try {
                log.debug("Executing task: [{}]", taskName);
                long taskStart = System.currentTimeMillis();

                task.execute(context);

                long taskDuration = System.currentTimeMillis() - taskStart;
                log.debug("Task [{}] completed in {} ms", taskName, taskDuration);
                executedCount++;

            } catch (Exception e) {
                log.error("Task [{}] failed: {}", taskName, e.getMessage());
                throw new PipelineExecutionException(taskName, e);
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        log.info("Pipeline completed: {} executed, {} skipped, total {} ms",
                executedCount, skippedCount, totalDuration);
    }

    /**
     * 取得執行上下文
     */
    public C getContext() {
        return context;
    }

    /**
     * 任務條目（內部類）
     */
    private static class TaskEntry<C extends PipelineContext> {
        final PipelineTask<C> task;
        final Predicate<C> condition;

        TaskEntry(PipelineTask<C> task, Predicate<C> condition) {
            this.task = task;
            this.condition = condition;
        }
    }
}
