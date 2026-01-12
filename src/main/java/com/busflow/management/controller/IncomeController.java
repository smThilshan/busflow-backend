package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.service.IncomeService;
import com.sun.security.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponseDTO> addIncome(@Valid @RequestBody IncomeRequestDTO request, @AuthenticationPrincipal CustomUserDetails user){

        // Call service with authenticated user ID
        IncomeResponseDTO response = incomeService.addIncome(request, user.getId());

        // Return proper HTTP status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> getIncome(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user){
        IncomeResponseDTO response = incomeService.getIncomeById(id,user.getId());
        return ResponseEntity.ok(response);
    }


}
