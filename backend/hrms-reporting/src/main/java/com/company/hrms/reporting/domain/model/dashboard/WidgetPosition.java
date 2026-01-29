package com.company.hrms.reporting.domain.model.dashboard;

import com.company.hrms.common.domain.model.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Widget 位置值物件
 * 
 * <p>
 * 使用 Grid 布局系統，座標從 (0,0) 開始
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WidgetPosition extends ValueObject {

    private int x; // X 座標 (列)
    private int y; // Y 座標 (行)
    private int w; // 寬度 (佔用列數)
    private int h; // 高度 (佔用行數)

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WidgetPosition that = (WidgetPosition) o;
        return x == that.x && y == that.y && w == that.w && h == that.h;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, w, h);
    }

    @Override
    public String toString() {
        return String.format("Position(x=%d, y=%d, w=%d, h=%d)", x, y, w, h);
    }

    /**
     * 驗證位置參數
     */
    public void validate() {
        if (x < 0) {
            throw new IllegalArgumentException("X 座標不可為負數");
        }

        if (y < 0) {
            throw new IllegalArgumentException("Y 座標不可為負數");
        }

        if (w <= 0) {
            throw new IllegalArgumentException("寬度必須大於 0");
        }

        if (h <= 0) {
            throw new IllegalArgumentException("高度必須大於 0");
        }

        // 假設最大 12 列的 Grid 系統
        if (x + w > 12) {
            throw new IllegalArgumentException("Widget 超出 Grid 範圍 (最大 12 列)");
        }
    }
}
