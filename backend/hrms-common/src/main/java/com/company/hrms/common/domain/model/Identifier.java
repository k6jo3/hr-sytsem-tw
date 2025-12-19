package com.company.hrms.common.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 識別碼基類
 * 所有 Domain ID 類型的基類，提供類型安全的識別碼封裝
 *
 * @param <T> 底層識別碼類型（通常為 String 或 Long）
 */
public abstract class Identifier<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final T value;

    protected Identifier(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Identifier value cannot be null");
        }
        this.value = value;
    }

    /**
     * 取得識別碼值
     * @return 底層識別碼值
     */
    public T getValue() {
        return value;
    }

    /**
     * 產生新的 UUID 字串
     * @return UUID 字串
     */
    protected static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier<?> that = (Identifier<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
