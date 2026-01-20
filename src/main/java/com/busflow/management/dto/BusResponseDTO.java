package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusResponseDTO {
    private Long id;
    private String busNumber;
    private String ownerUsername;
    private Integer activeConductorsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

