package com.busflow.management.dto;

import com.busflow.management.entity.HireInfo;
import com.busflow.management.entity.TripInfo;
import com.busflow.management.enums.IncomeType;
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
public class IncomeResponseDTO {

    private Long id;
    private IncomeType incomeType;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private Long busId;
    private String busNumber;
    private String createdBy;
//    private TripInfo tripInfo;  // Populated if type = TRIP
//    private HireInfo hireInfo;  // Populated if type = HIRE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
