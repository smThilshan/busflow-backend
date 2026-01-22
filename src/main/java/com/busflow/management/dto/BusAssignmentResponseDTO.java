package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BusAssignmentResponseDTO {

    private Long id;
    private Long conductorId;
    private String conductorUsername;
    private Long busId;
    private String busNumber;
    private String assignedByUsername;
    private Boolean isActive;
    private LocalDateTime assignedDate;
    private LocalDateTime revokedDate;
    private LocalDateTime createdAt;

}
