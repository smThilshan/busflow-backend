package com.busflow.management.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripInfo {
    private Integer numberOfTrips;
//    private LocalDate date;
    private BigDecimal fromAmount;
    private BigDecimal toAmount;
    private BigDecimal otherExpense;
    private BigDecimal driverSalary;
    private BigDecimal conductorSalary;
}
