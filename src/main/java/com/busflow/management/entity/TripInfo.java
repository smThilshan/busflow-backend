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
    private BigDecimal onwardTripAmount;
    private BigDecimal returnTripAmount;
    private BigDecimal otherExpense;
    private BigDecimal driverSalary;
    private BigDecimal conductorSalary;
}
