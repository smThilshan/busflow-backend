package com.busflow.management.service;

import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.Income;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.IncomeRepository;
import com.busflow.management.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;


    public IncomeService(IncomeRepository incomeRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    public IncomeResponseDTO addIncome(IncomeRequestDTO request){

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

//        Create income
        Income income = new Income();
        income.setAmount(request.getAmount());
        income.setDescription(request.getDescription());
        income.setBus(bus);

        Income savedIncome = incomeRepository.save(income);
        return new IncomeResponseDTO(savedIncome.getId(), "Income added successfully");

    }
}
