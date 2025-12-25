package com.busflow.management.dto;

import lombok.Data;

@Data
public class IncomeRequestDTO {
    private Double amount;
    private String description;
    private Long userId; // logged-in user id from Auth
}
