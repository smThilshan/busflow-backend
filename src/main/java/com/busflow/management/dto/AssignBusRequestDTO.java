package com.busflow.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignBusRequestDTO {

    @NotNull(message = "Conductor ID is required")
    @Positive(message = "Conductor ID must be positive")
    private Long conductorId;

    @NotNull(message = "Bus ID is required")
    @Positive(message = "Bus ID must be positive")
    private Long busId;
}
