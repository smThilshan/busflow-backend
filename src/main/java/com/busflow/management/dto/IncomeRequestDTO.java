package com.busflow.management.dto;

import com.busflow.management.enums.IncomeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeRequestDTO {

    @NotNull(message = "Income type is required")
    private IncomeType type; // TRIP | HIRE

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Valid
    private TripIncomeDTO trip;  // required if type = TRIP

    @Valid
    private HireIncomeDTO hire;  // required if type = HIRE
}
