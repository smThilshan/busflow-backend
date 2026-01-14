package com.busflow.management.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TripInfo {
    private Integer numberOfTrips;
    private BigDecimal fromAmount;
    private BigDecimal toAmount;
    private BigDecimal otherExpense;
    private BigDecimal driverSalary;
    private BigDecimal conductorSalary;
}
