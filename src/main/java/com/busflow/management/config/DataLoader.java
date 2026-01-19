package com.busflow.management.config;
import com.busflow.management.entity.*;
import com.busflow.management.enums.IncomeType;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {
        return args -> {

            if (userRepository.count() == 0) {
                log.info("🚀 Loading initial data...");

                // ✅ 1. Create OWNER
                final User owner = userRepository.save(
                        User.builder()
                                .username("owner1")
                                .password(passwordEncoder.encode("password123"))
                                .role(Role.OWNER)
                                .build()
                );
                log.info("✅ Created owner: {}", owner.getUsername());

                // ✅ 2. Create CONDUCTOR
                final User conductor = userRepository.save(
                        User.builder()
                                .username("conductor1")
                                .password(passwordEncoder.encode("password123"))
                                .role(Role.CONDUCTOR)
                                .build()
                );
                log.info("✅ Created conductor: {}", conductor.getUsername());

                // ✅ 3. Create BUSES
                Bus bus1 = busRepository.save(
                        Bus.builder()
                                .busNumber("NB-2231")
                                .owner(owner)
                                .build()
                );
                log.info("✅ Created bus: {}", bus1.getBusNumber());

                Bus bus2 = busRepository.save(
                        Bus.builder()
                                .busNumber("NB-2089")
                                .owner(owner)
                                .build()
                );
                log.info("✅ Created bus: {}", bus2.getBusNumber());

                // ✅ 4. Create BUS ASSIGNMENTS
                BusAssignment assignment1 = busAssignmentRepository.save(
                        BusAssignment.builder()
                                .user(conductor)
                                .bus(bus1)
                                .assignedBy(owner)
                                .isActive(true)
                                .build()
                );
                log.info("✅ Assigned {} to {}", conductor.getUsername(), bus1.getBusNumber());

                BusAssignment assignment2 = busAssignmentRepository.save(
                        BusAssignment.builder()
                                .user(conductor)
                                .bus(bus2)
                                .assignedBy(owner)
                                .isActive(true)
                                .build()
                );
                log.info("✅ Assigned {} to {}", conductor.getUsername(), bus2.getBusNumber());

                // ✅ 5. Create SAMPLE INCOME
                Income income1 = Income.builder()
                        .transactionDate(LocalDate.now())
                        .amount(new BigDecimal("5000.00"))
                        .incomeType(IncomeType.TRIP)
                        .bus(bus1)
                        .createdBy(conductor)
                        .tripInfo(TripInfo.builder()
                                .numberOfTrips(2)
                                .fromAmount(new BigDecimal("3000.00"))
                                .toAmount(new BigDecimal("2500.00"))
                                .otherExpense(new BigDecimal("200.00"))
                                .driverSalary(new BigDecimal("250.00"))
                                .conductorSalary(new BigDecimal("150.00"))
                                .build())
                        .build();
                incomeRepository.save(income1);
                log.info("✅ Created income record for {}", bus1.getBusNumber());

                Income income2 = Income.builder()
                        .transactionDate(LocalDate.now().minusDays(1))
                        .amount(new BigDecimal("8000.00"))
                        .incomeType(IncomeType.HIRE)
                        .bus(bus1)
                        .createdBy(owner)
                        .hireInfo(HireInfo.builder()
                                .numberOfDays(3)
                                .fromLocation("Colombo")
                                .destination("Kandy")
                                .otherExpense(new BigDecimal("500.00"))
                                .driverSalary(new BigDecimal("1500.00"))
                                .conductorSalary(new BigDecimal("1000.00"))
                                .build())
                        .build();
                incomeRepository.save(income2);
                log.info("✅ Created hire income for {}", bus1.getBusNumber());

                // ✅ 6. Create SAMPLE EXPENSES
                Expense expense1 = Expense.builder()
                        .transactionDate(LocalDate.now())
                        .amount(new BigDecimal("1000.00"))
                        .category("Fuel")
                        .bus(bus1)
                        .createdBy(conductor)
                        .build();
                expenseRepository.save(expense1);
                log.info("✅ Created expense: Fuel for {}", bus1.getBusNumber());

                Expense expense2 = Expense.builder()
                        .transactionDate(LocalDate.now().minusDays(2))
                        .amount(new BigDecimal("500.00"))
                        .category("Maintenance")
                        .bus(bus2)
                        .createdBy(owner)
                        .build();
                expenseRepository.save(expense2);
                log.info("✅ Created expense: Maintenance for {}", bus2.getBusNumber());

                log.info("🎉 Data loading completed successfully!");

            } else {
                log.info("ℹ️ Data already exists. Skipping seed.");
            }
        };
    }
}