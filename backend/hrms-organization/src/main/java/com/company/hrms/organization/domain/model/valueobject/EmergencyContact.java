package com.company.hrms.organization.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class EmergencyContact {
    private final String name;
    private final String relationship;
    private final String phone;

    @Override
    public String toString() {
        return name + " (" + relationship + ") - " + phone;
    }
}
