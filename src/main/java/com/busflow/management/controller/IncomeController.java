package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.service.IncomeService;
import com.sun.security.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;
    /*
        Add income to specific bus
     */
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
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
     /*
        Get all incomes user has access to
      */
    @GetMapping
    public ResponseEntity<List<IncomeResponseDTO>> getMyIncomes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<IncomeResponseDTO> incomes = incomeService.getMyIncomes(
                userDetails.getUser()
        );
        return ResponseEntity.ok(incomes);
    }
     /*
     Get income for specific bus
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<IncomeResponseDTO>> getIncomesByBus(
            @PathVariable Long busId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<IncomeResponseDTO> incomes = incomeService.getIncomeByBus(busId, userDetails.getUser());

        return ResponseEntity.ok(incomes);
    }


    /*
     Get specific income by ID
     */
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

    /*
    Get incomes by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<IncomeResponseDTO>> getIncomesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        List<IncomeResponseDTO> incomes = incomeService.getIncomesByDateRange(
                startDate, endDate, userDetails.getUser()
        );

        return ResponseEntity.ok(incomes);
    }

    // Delete income (OWNER only or creator)
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<String> deleteIncome(
            @PathVariable Long incomeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        incomeService.deleteIncome(incomeId, userDetails.getUser());
        return ResponseEntity.ok("Income with id " + incomeId + " has been deleted");
    }


}
