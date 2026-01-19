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
public class HireInfo {
    private Integer numberOfDays;
    private String fromLocation;
    private String destination;
    private BigDecimal otherExpense;
    private BigDecimal driverSalary;
    private BigDecimal conductorSalary;
}
