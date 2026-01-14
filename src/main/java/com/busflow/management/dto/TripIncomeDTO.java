package com.busflow.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TripIncomeDTO {

    @NotNull
    @Min(1)
    @Max(3)
    private Integer noOfTrips;

    @NotNull
    @Positive
    private BigDecimal fromAmount;

    @NotNull
    @Positive
    private BigDecimal toAmount;

    @Positive
    private BigDecimal otherExpense;

    @NotNull
    @Positive
    private BigDecimal driverSalary;

    @NotNull
    @Positive
    private BigDecimal conductorSalary;
}
