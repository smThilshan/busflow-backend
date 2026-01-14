package com.busflow.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HireIncomeDTO {

    @NotNull
    @Positive
    private Integer noOfDays;

    @NotNull
    private String fromLocation;

    @NotNull
    private String destination;

    @Positive
    private BigDecimal otherExpense;

    @NotNull
    @Positive
    private BigDecimal driverSalary;

    @NotNull
    @Positive
    private BigDecimal conductorSalary;
}
