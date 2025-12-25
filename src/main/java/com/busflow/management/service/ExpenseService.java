package com.busflow.management.service;

import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.Expense;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.ExpenseRepository;
import com.busflow.management.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {
        //        Get the user
        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new RuntimeException("User Not Found"));

        // 2. Role validation
        if (user.getRole() != Role.CONDUCTOR){
            throw new RuntimeException("Only conductor can add income");
        }

        // 3. Assigned bus check
        Bus bus = user.getBus();
        if (bus == null) {
            throw new RuntimeException("User is not assigned to any bus");
        }

        // 4. Save expense
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getDescription());
        expense.setBus(bus);

        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseResponseDTO(
                savedExpense.getId(),
                "Expense added successfully"
        );
    }
}
