package com.busflow.management.dto;

import com.busflow.management.enums.IncomeType;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
public class IncomeRequestDTO {
//    private Double amount;
//    private String description;
//    private Long userId; // logged-in user id from Auth

    @NotNull
    private IncomeType type; // TRIP | HIRE

    @NotNull
//    @Positive
    private BigDecimal amount;
//    private String description;
//    private Long userId; // logged-in user id from Auth
}
