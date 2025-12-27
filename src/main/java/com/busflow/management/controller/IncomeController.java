package com.busflow.management.controller;

import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public IncomeResponseDTO addIncome(@RequestBody IncomeRequestDTO request) {
        return incomeService.addIncome(request);
    }
}
