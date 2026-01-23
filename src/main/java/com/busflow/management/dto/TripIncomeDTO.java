package com.busflow.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TripIncomeDTO {

    @NotNull(message = "Number of trips is required")
    @Min(value = 1, message = "At least 1 trip is required")
    @Max(value = 3, message = "Maximum 3 trips allowed per day")
    private Integer noOfTrips;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Selected date should be present or past")
    private LocalDate date;

    @NotNull(message = "Onward trip amount is required")
    @Positive(message = "Onward trip amount must be greater than zero")
    private BigDecimal onwardTripAmount;

    @NotNull(message = "Return trip amount is required")
    @Positive(message = "Return trip amount must be greater than zero")
    private BigDecimal returnTripAmount;

    // Other expense should come as  optional in future
    @NotNull(message = "Other expenses amount is required")
    @PositiveOrZero(message = "Other expenses cannot be negative")
    private BigDecimal otherExpense;

    @NotNull(message = "Driver salary is required")
    @Positive(message = "Driver salary must be greater than zero")
    private BigDecimal driverSalary;

    @NotNull(message = "Conductor salary is required")
    @Positive(message = "Conductor salary must be greater than zero")
    private BigDecimal conductorSalary;
}
