//package com.busflow.management.service;
//
//import com.busflow.management.dto.DashboardResponseDTO;
//import com.busflow.management.entity.Bus;
//import com.busflow.management.entity.User;
//import com.busflow.management.enums.Role;
//import com.busflow.management.repository.ExpenseRepository;
//import com.busflow.management.repository.IncomeRepository;
//import com.busflow.management.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Service
//public class DashboardService {
//    private final UserRepository userRepository;
//    private final IncomeRepository incomeRepository;
//    private final ExpenseRepository expenseRepository;
//
//    public DashboardService(UserRepository userRepository,
//                            IncomeRepository incomeRepository,
//                            ExpenseRepository expenseRepository) {
//        this.userRepository = userRepository;
//        this.incomeRepository = incomeRepository;
//        this.expenseRepository = expenseRepository;
//    }
//
//    public DashboardResponseDTO getConductorYesterday(Long userId){
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (user.getRole() != Role.CONDUCTOR) {
//            throw new RuntimeException("Access denied");
//        }
//
//        Bus bus = user.getBus();
//        if (bus == null) {
//            throw new RuntimeException("Bus not assigned");
//        }
//
//        // Weekly range: today to yesterday
//        LocalDate today = LocalDate.now();
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end = today.plusDays(1).atStartOfDay();
//
//
//        Double income = incomeRepository.getIncomeBetween(bus.getId(),start, end);
//        Double expense = expenseRepository.getTodayExpenseByBus(bus.getId());
//
//        return new DashboardResponseDTO(
//                income,
//                expense,
//                income - expense
//        );
//    }
//
//}
