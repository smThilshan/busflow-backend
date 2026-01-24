package com.busflow.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HireIncomeDTO {

    @NotNull(message = "Number of hire days is required")
    @Min(value = 1, message = "Number of hire days must be at least 1")
    @Max(value = 30, message = "Number of hire days cannot exceed 30")
    private Integer noOfDays;

//    @NotNull(message = "Date is required")
//    @PastOrPresent(message = "Selected date should be present or past")
//    private LocalDate date;

    // Starting location of the hire
    @NotBlank(message = "Hire start location is required")
    private String origin;

    // Destination of the hire
    @NotBlank(message = "Hire end destination is required")
    private String destination;

    @NotNull(message = "Hire amount is required")
    @Positive(message = "Hire amount must be greater than zero")
    private BigDecimal hireAmount;

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
