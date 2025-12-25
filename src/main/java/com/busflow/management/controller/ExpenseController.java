package com.busflow.management.controller;

import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add")
    public ExpenseResponseDTO addExpense(@RequestBody ExpenseRequestDTO expenseRequestDTO) {
        return expenseService.createExpense(expenseRequestDTO);
    }
}
