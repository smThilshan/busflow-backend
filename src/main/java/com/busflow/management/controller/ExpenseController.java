package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> addExpense(@RequestBody ExpenseRequestDTO expenseRequestDTO) {

        ExpenseResponseDTO response = expenseService.createExpense(expenseRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpense(@PathVariable Long expenseId, @AuthenticationPrincipal CustomUserDetails user) {
        ExpenseResponseDTO response = expenseService.getExpenseById( expenseId,user.getId());
        return ResponseEntity.ok(response);
    }
}
