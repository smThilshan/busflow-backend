package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.enums.ExpenseCategory;
import com.busflow.management.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /*
    Add expense to specific bus
     */
    @PostMapping("bus/{busId}")
    public ResponseEntity<ExpenseResponseDTO> addExpense(
            @PathVariable Long busId,
            @Valid @RequestBody ExpenseRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ExpenseResponseDTO response = expenseService.addExpense(busId, request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
    Get all expenses user has access to
    */
    public ResponseEntity<List<ExpenseResponseDTO>> getMyExpenses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<ExpenseResponseDTO> expense = expenseService.getMyExpenses(userDetails.getUser());
        return ResponseEntity.ok(expense);
    }

    /*
    Get expenses for specific bus
    */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByBus(
            @PathVariable Long busId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<ExpenseResponseDTO> expense = expenseService.getExpensesByBus(busId, userDetails.getUser());
        return ResponseEntity.ok(expense);

    }

    /*
    Get expense by ID
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ExpenseResponseDTO response = expenseService.getExpenseById( expenseId,userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /*
    Get expenses by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){

        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByDateRange(
                startDate, endDate, userDetails.getUser()
        );
        return ResponseEntity.ok(expenses);
    }

    /*
    Get expenses by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByCategory(
            @PathVariable String category,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByCategory(category, userDetails.getUser());
        return ResponseEntity.ok(expenses);

    }

    /*
    Delete expense
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        expenseService.deleteExpense(expenseId, userDetails.getUser());
        return ResponseEntity.ok("Expense has been deleted");
    }


}
