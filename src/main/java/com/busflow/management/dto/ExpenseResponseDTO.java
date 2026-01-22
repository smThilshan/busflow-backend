package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseResponseDTO {
    private Long id;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String category;
    private Long busId;
    private String busNumber;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
