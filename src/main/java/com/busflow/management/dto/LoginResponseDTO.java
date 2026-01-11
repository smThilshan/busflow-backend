package com.busflow.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponseDTO {
    private Long userId;
    private String role;
    private String token;
}
