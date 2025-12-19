package com.company.hrms.common.domain.model;

import java.io.Serializable;

/**
 * 值對象基類
 * 值對象是不可變的，由其屬性值定義相等性（非識別碼）
 *
 * <p>使用範例：
 * <pre>
 * public class Email extends ValueObject {
 *     private final String value;
 *
 *     public Email(String value) {
 *         validateEmail(value);
 *         this.value = value;
 *     }
 * }
 * </pre>
 *
 * <p>實作須知：
 * <ul>
 *   <li>所有欄位應宣告為 final</li>
 *   <li>實作 equals() 與 hashCode() 基於所有屬性</li>
 *   <li>提供驗證邏輯於建構子中</li>
 * </ul>
 */
public abstract class ValueObject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 子類別必須實作基於屬性值的相等性比較
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * 子類別必須實作基於屬性值的雜湊碼
     */
    @Override
    public abstract int hashCode();

    /**
     * 子類別應提供有意義的字串表示
     */
    @Override
    public abstract String toString();
}
