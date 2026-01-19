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

import java.util.List;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {
//
//    private final IncomeService incomeService;
//
//    @PostMapping
//    public ResponseEntity<IncomeResponseDTO> addIncome(@Valid @RequestBody IncomeRequestDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
//
//        // Call service with authenticated user ID
//        IncomeResponseDTO response = incomeService.addIncome(request, userDetails.getUser());
//
//        // Return proper HTTP status
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<IncomeResponseDTO> getIncome(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user){
//        IncomeResponseDTO response = incomeService.getIncomeById(id,user.getId());
//        return ResponseEntity.ok(response);
//    }

    private final IncomeService incomeService;

    // ✅ Add income to specific bus
    @PostMapping("/bus/{busId}")
    public ResponseEntity<IncomeResponseDTO> addIncome(
            @PathVariable Long busId,
            @Valid @RequestBody IncomeRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        IncomeResponseDTO response = incomeService.addIncome(
                busId,
                request,
                userDetails.getUser()
        );
        return ResponseEntity.ok(response);
    }

    // ✅ Get all incomes user has access to
    @GetMapping
    public ResponseEntity<List<IncomeResponseDTO>> getMyIncomes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<IncomeResponseDTO> incomes = incomeService.getMyIncomes(
                userDetails.getUser()
        );
        return ResponseEntity.ok(incomes);
    }

    // ✅ Get specific income by ID
    @GetMapping("/{incomeId}")
    public ResponseEntity<IncomeResponseDTO> getIncomeById(
            @PathVariable Long incomeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        IncomeResponseDTO income = incomeService.getIncomeById(
                incomeId,
                userDetails.getUser()
        );
        return ResponseEntity.ok(income);
    }


}
