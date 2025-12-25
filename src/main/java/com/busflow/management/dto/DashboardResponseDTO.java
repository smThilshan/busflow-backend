package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponseDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double profit;
}
