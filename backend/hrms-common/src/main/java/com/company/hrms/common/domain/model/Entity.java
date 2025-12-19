package com.company.hrms.common.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 實體基類
 * 實體具有唯一識別碼，相等性由識別碼決定（而非屬性值）
 *
 * <p>使用範例：
 * <pre>
 * public class Employee extends Entity&lt;EmployeeId&gt; {
 *     private String name;
 *     private Email email;
 *
 *     protected Employee(EmployeeId id) {
 *         super(id);
 *     }
 * }
 * </pre>
 *
 * @param <ID> 識別碼類型，必須繼承自 Identifier
 */
public abstract class Entity<ID extends Identifier<?>> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 實體識別碼
     */
    protected final ID id;

    /**
     * 建立實體實例
     * @param id 實體識別碼，不可為 null
     */
    protected Entity(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity id cannot be null");
        }
        this.id = id;
    }

    /**
     * 取得實體識別碼
     * @return 實體識別碼
     */
    public ID getId() {
        return id;
    }

    /**
     * 實體相等性由識別碼決定
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
