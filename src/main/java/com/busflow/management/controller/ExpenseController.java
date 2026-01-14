package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.enums.ExpenseCategory;
import com.busflow.management.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
    public ResponseEntity<ExpenseResponseDTO> getExpense(@PathVariable Long expenseId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ExpenseResponseDTO response = expenseService.getExpenseById( expenseId,userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public List<String> getExpenseCategories(){
        return Arrays.stream(ExpenseCategory.values())
                .map(ExpenseCategory::getDisplayName)
                .toList();
    }
}
