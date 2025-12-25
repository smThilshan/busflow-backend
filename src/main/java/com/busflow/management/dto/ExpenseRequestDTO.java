package com.busflow.management.dto;

import lombok.Data;

@Data
public class ExpenseRequestDTO {
    private Double amount;
    private String description;
    private Long userId;
}
