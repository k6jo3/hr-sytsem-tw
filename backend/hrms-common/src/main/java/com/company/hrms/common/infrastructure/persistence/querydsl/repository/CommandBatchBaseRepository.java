package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.infrastructure.persistence.TableMeta;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.Query;

/**
 * 批次命令基礎倉庫
 * 繼承 CommandBaseRepository 並實作 ICommandBatchRepository
 * 
 * <p>
 * 適用於需要批次操作的 Repository
 * </p>
 * 
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public abstract class CommandBatchBaseRepository<T, ID> extends CommandBaseRepository<T, ID>
        implements ICommandBatchRepository<T> {

    protected CommandBatchBaseRepository(JPAQueryFactory factory, Class<T> clazz) {
        super(factory, clazz);
    }

    // ==================== ICommandBatchRepository 實作 ====================

    @Override
    @Transactional
    public void saveAll(List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            em.persist(entities.get(i));
            // 每 50 筆 flush 一次，避免記憶體堆積
            if (i > 0 && i % 50 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
    }

    @Override
    @Transactional
    public void saveAllNative(List<T> entities, int batchSize) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        TableMeta meta = clazz.getAnnotation(TableMeta.class);
        if (meta == null) {
            throw new IllegalStateException("Entity " + clazz.getSimpleName()
                    + " 必須標註 @TableMeta 才能使用 saveAllNative");
        }

        String tableName = meta.name();
        String[] columns = meta.columns();
        String[] fields = meta.fields();

        // 建構 INSERT 語句的欄位部分
        String columnClause = String.join(", ", columns);
        String placeholders = String.join(", ",
                Collections.nCopies(columns.length, "?"));

        // 分批處理
        for (int i = 0; i < entities.size(); i += batchSize) {
            List<T> batch = entities.subList(i, Math.min(i + batchSize, entities.size()));

            // 建構多值 INSERT 語句
            String valuesClauses = batch.stream()
                    .map(e -> "(" + placeholders + ")")
                    .collect(Collectors.joining(", "));

            String sql = "INSERT INTO " + tableName + " (" + columnClause + ") VALUES " + valuesClauses;

            Query query = em.createNativeQuery(sql);

            // 綁定參數
            int paramIndex = 1;
            for (T entity : batch) {
                for (String fieldName : fields) {
                    Object value = getFieldValue(entity, fieldName);
                    query.setParameter(paramIndex++, value);
                }
            }

            query.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    // Helper for saveAllNative
    private Object getFieldValue(T entity, String fieldName) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            throw new RuntimeException("無法讀取欄位: " + fieldName, e);
        }
    }
}
