package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseResponseDTO {
    private Long expenseId;
    private String message;
}
