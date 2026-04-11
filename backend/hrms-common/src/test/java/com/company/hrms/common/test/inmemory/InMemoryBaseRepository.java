package com.company.hrms.common.test.inmemory;

import com.company.hrms.common.query.QueryGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InMemory 基底 Repository
 * 使用 ConcurrentHashMap 儲存資料，InMemoryQueryEngine 執行 QueryGroup 過濾
 *
 * <p>子類只需：
 * <ol>
 *   <li>呼叫 super(entityClass, idExtractor) 指定實體型別和 ID 擷取方式</li>
 *   <li>實作 domain 介面中的 custom finders（可用 findOneByField/findByField 便利方法）</li>
 * </ol>
 *
 * <p>使用範例：
 * <pre>
 * public class FakeUserRepository
 *     extends InMemoryBaseRepository&lt;User, UserId&gt;
 *     implements IUserRepository {
 *
 *     public FakeUserRepository() {
 *         super(User.class, User::getId);
 *     }
 *
 *     &#64;Override
 *     public Optional&lt;User&gt; findByUsername(String username) {
 *         return findOneByField("username", username);
 *     }
 * }
 * </pre>
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public class InMemoryBaseRepository<T, ID> {

    private final ConcurrentHashMap<ID, T> store = new ConcurrentHashMap<>();
    private final Class<T> entityClass;
    private final IdExtractor<T, ID> idExtractor;

    /**
     * 建構子
     *
     * @param entityClass 實體類別
     * @param idExtractor ID 擷取器
     */
    protected InMemoryBaseRepository(Class<T> entityClass, IdExtractor<T, ID> idExtractor) {
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass 不能為 null");
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor 不能為 null");
    }

    // ==================== CRUD 操作 ====================

    /**
     * 儲存實體到記憶體（核心方法）
     * 子類透過此方法實作 domain 介面的 save()
     *
     * <p>使用方式：
     * <pre>
     * // domain 介面 save 回傳 void 時：
     * &#64;Override
     * public void save(User user) { doSave(user); }
     *
     * // domain 介面 save 回傳實體時：
     * &#64;Override
     * public Payslip save(Payslip payslip) { return doSave(payslip); }
     * </pre>
     */
    protected T doSave(T entity) {
        ID id = idExtractor.extractId(entity);
        Objects.requireNonNull(id, "實體 ID 不能為 null");
        store.put(id, entity);
        return entity;
    }

    /**
     * 根據 ID 查找
     */
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    /**
     * 根據 ID 刪除
     */
    public void deleteById(ID id) {
        if (id != null) {
            store.remove(id);
        }
    }

    /**
     * 刪除實體
     */
    public void delete(T entity) {
        if (entity != null) {
            deleteById(idExtractor.extractId(entity));
        }
    }

    /**
     * 檢查 ID 是否存在
     */
    public boolean existsById(ID id) {
        return id != null && store.containsKey(id);
    }

    /**
     * 查詢所有資料
     */
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    // ==================== QueryGroup 查詢 ====================

    /**
     * 動態查詢所有符合條件的資料
     */
    public List<T> findAll(QueryGroup group) {
        Predicate<T> predicate = InMemoryQueryEngine.toPredicate(group, entityClass);
        return store.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 動態分頁查詢
     */
    public Page<T> findPage(QueryGroup group, Pageable pageable) {
        Predicate<T> predicate = InMemoryQueryEngine.toPredicate(group, entityClass);
        List<T> filtered = store.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());

        long total = filtered.size();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 排序
        Stream<T> sorted = applySorting(filtered.stream(), pageable.getSort());

        // 分頁
        List<T> content = sorted
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 動態查詢單筆
     */
    public Optional<T> findOne(QueryGroup group) {
        Predicate<T> predicate = InMemoryQueryEngine.toPredicate(group, entityClass);
        return store.values().stream()
                .filter(predicate)
                .findFirst();
    }

    /**
     * 動態查詢筆數
     */
    public long count(QueryGroup group) {
        Predicate<T> predicate = InMemoryQueryEngine.toPredicate(group, entityClass);
        return store.values().stream()
                .filter(predicate)
                .count();
    }

    /**
     * 動態查詢是否存在
     */
    public boolean exists(QueryGroup group) {
        return count(group) > 0;
    }

    // ==================== 便利方法（給子類實作 custom finders） ====================

    /**
     * 依欄位值查詢單筆
     * 使用 FieldAccessor 取值並解包 Value Object 後比較
     *
     * @param fieldPath 欄位路徑
     * @param value     期望值
     * @return 第一筆符合的結果
     */
    protected Optional<T> findOneByField(String fieldPath, Object value) {
        return store.values().stream()
                .filter(entity -> matchesField(entity, fieldPath, value))
                .findFirst();
    }

    /**
     * 依欄位值查詢多筆
     */
    protected List<T> findByField(String fieldPath, Object value) {
        return store.values().stream()
                .filter(entity -> matchesField(entity, fieldPath, value))
                .collect(Collectors.toList());
    }

    /**
     * 檢查是否存在符合欄位值的資料
     */
    protected boolean existsByField(String fieldPath, Object value) {
        return store.values().stream()
                .anyMatch(entity -> matchesField(entity, fieldPath, value));
    }

    /**
     * 計算符合欄位值的資料筆數
     */
    protected int countByField(String fieldPath, Object value) {
        return (int) store.values().stream()
                .filter(entity -> matchesField(entity, fieldPath, value))
                .count();
    }

    /**
     * 依多個 ID 查詢
     */
    protected List<T> findByIdIn(Collection<ID> ids) {
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 測試輔助方法 ====================

    /**
     * 清空所有資料
     */
    public void clear() {
        store.clear();
    }

    /**
     * 目前資料筆數
     */
    public int size() {
        return store.size();
    }

    /**
     * 批量填入測試資料
     */
    @SafeVarargs
    public final void seed(T... entities) {
        for (T entity : entities) {
            doSave(entity);
        }
    }

    /**
     * 批量填入測試資料（Collection）
     */
    public void seedAll(Collection<T> entities) {
        for (T entity : entities) {
            doSave(entity);
        }
    }

    /**
     * 取得內部儲存（唯讀，用於測試驗證）
     */
    protected Map<ID, T> getStore() {
        return Collections.unmodifiableMap(store);
    }

    // ==================== 內部方法 ====================

    /**
     * 比較實體的欄位值是否與期望值相符
     */
    private boolean matchesField(T entity, String fieldPath, Object expectedValue) {
        Object fieldVal = FieldAccessor.getUnwrappedValue(entity, fieldPath);

        if (fieldVal == null && expectedValue == null) {
            return true;
        }
        if (fieldVal == null || expectedValue == null) {
            return false;
        }

        // Value Object 解包後比較
        Object unwrappedExpected = FieldAccessor.unwrapValueObject(expectedValue);

        if (fieldVal.getClass().equals(unwrappedExpected.getClass())) {
            return Objects.equals(fieldVal, unwrappedExpected);
        }

        // toString 比較（如 Enum.name() vs String）
        return Objects.equals(fieldVal.toString(), unwrappedExpected.toString());
    }

    /**
     * 套用排序
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Stream<T> applySorting(Stream<T> stream, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return stream;
        }

        Comparator<T> comparator = null;

        for (Sort.Order order : sort) {
            Comparator<T> orderComparator = (a, b) -> {
                Object valA = FieldAccessor.getUnwrappedValue(a, order.getProperty());
                Object valB = FieldAccessor.getUnwrappedValue(b, order.getProperty());

                // nulls last
                if (valA == null && valB == null) return 0;
                if (valA == null) return 1;
                if (valB == null) return -1;

                if (valA instanceof Comparable && valB instanceof Comparable) {
                    try {
                        return ((Comparable) valA).compareTo(valB);
                    } catch (ClassCastException e) {
                        return valA.toString().compareTo(valB.toString());
                    }
                }
                return valA.toString().compareTo(valB.toString());
            };

            if (order.isDescending()) {
                orderComparator = orderComparator.reversed();
            }

            comparator = comparator == null ? orderComparator : comparator.thenComparing(orderComparator);
        }

        return comparator != null ? stream.sorted(comparator) : stream;
    }
}
