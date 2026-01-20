package com.busflow.management.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusRequestDTO {
    @NotBlank(message = "Bus number is required")
//    @Size(min = 3, max = 20, message = "Bus number must be between 3 and 20 characters")
    private String busNumber;

}

