package com.company.hrms.organization.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class Address {
    private final String country;
    private final String city;
    private final String district;
    private final String street;

    public String getFullAddress() {
        return (country != null ? country : "") +
               (city != null ? city : "") +
               (district != null ? district : "") +
               (street != null ? street : "");
    }

    public String getPostalCode() { return null; }

    @Override
    public String toString() {
        return getFullAddress();
    }
}
