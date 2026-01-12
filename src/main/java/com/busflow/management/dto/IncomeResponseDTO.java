package com.busflow.management.dto;

import com.busflow.management.enums.IncomeType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class IncomeResponseDTO {
//    private Long incomeId;
//    private String message;

    private Long id;
    private IncomeType type;
//    private String description;
    private BigDecimal amount;
}
