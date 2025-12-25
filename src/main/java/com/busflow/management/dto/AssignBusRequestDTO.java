package com.busflow.management.dto;

import lombok.Data;

@Data
public class AssignBusRequestDTO {
    private Long ownerId;
    private Long conductorId;
    private Long busId;
}
