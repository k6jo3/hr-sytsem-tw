package com.company.hrms.organization.domain.model.valueobject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 地址值對象
 */
@Getter
@Builder
@EqualsAndHashCode
public class Address {

    /**
     * 郵遞區號
     */
    private final String postalCode;

    /**
     * 縣市
     */
    private final String city;

    /**
     * 區域
     */
    private final String district;

    /**
     * 街道地址
     */
    private final String street;

    /**
     * 取得完整地址
     * @return 完整地址字串
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (postalCode != null && !postalCode.isBlank()) {
            sb.append(postalCode).append(" ");
        }
        if (city != null && !city.isBlank()) {
            sb.append(city);
        }
        if (district != null && !district.isBlank()) {
            sb.append(district);
        }
        if (street != null && !street.isBlank()) {
            sb.append(street);
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    /**
     * 建立空地址
     * @return 空地址實例
     */
    public static Address empty() {
        return Address.builder().build();
    }
}
