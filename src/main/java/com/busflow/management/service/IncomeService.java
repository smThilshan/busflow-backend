package com.busflow.management.service;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.HireIncomeDTO;
import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.dto.TripIncomeDTO;
import com.busflow.management.entity.*;
import com.busflow.management.enums.IncomeType;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.IncomeRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;


    @Transactional
    public IncomeResponseDTO addIncome(IncomeRequestDTO request, User authUser) {

        // 1. Validate user role
        if (authUser.getRole() != Role.CONDUCTOR) {
            throw new RuntimeException("Only conductors can add income");
        }

        // 2. Validate bus assignment
        Bus bus = authUser.getBus();
        if (bus == null) {
            throw new RuntimeException("User is not assigned to any bus");
        }

        // 3. Validate type-specific data
        validateIncomeRequest(request);

        // 4. Create and populate income
        Income income = new Income();
        income.setIncomeType(request.getType());
        income.setBus(bus);
        income.setCreatedBy(authUser);

        // 5. Set type-specific info and calculate amount
        if (request.getType() == IncomeType.TRIP) {
            TripInfo tripInfo = mapTripInfo(request.getTrip());
            income.setTripInfo(tripInfo);
            income.setAmount(calculateTripAmount(tripInfo));
        } else {
            HireInfo hireInfo = mapHireInfo(request.getHire());
            income.setHireInfo(hireInfo);
            income.setAmount(calculateHireAmount(hireInfo));
        }

        // 6. Save and return
        Income saved = incomeRepository.save(income);
        return mapToResponse(saved);

        }

    // ------------------ VALIDATION ------------------

    private void validateIncomeRequest(IncomeRequestDTO request) {
        if (request.getType() == IncomeType.TRIP && request.getTrip() == null) {
            throw new RuntimeException("Trip details are required for TRIP income");
        }
        if (request.getType() == IncomeType.HIRE && request.getHire() == null) {
            throw new RuntimeException("Hire details are required for HIRE income");
        }
    }

    // ------------------ MAPPERS ------------------

    private TripInfo mapTripInfo(TripIncomeDTO dto) {
        TripInfo info = new TripInfo();
        info.setNumberOfTrips(dto.getNoOfTrips());
        info.setFromAmount(dto.getFromAmount());
        info.setToAmount(dto.getToAmount());
        info.setOtherExpense(dto.getOtherExpense() != null ? dto.getOtherExpense() : BigDecimal.ZERO);
        info.setDriverSalary(dto.getDriverSalary());
        info.setConductorSalary(dto.getConductorSalary());
        return info;
    }

    private HireInfo mapHireInfo(HireIncomeDTO dto) {
        HireInfo info = new HireInfo();
        info.setNumberOfDays(dto.getNoOfDays());
        info.setFromLocation(dto.getFromLocation());
        info.setDestination(dto.getDestination());
        info.setOtherExpense(dto.getOtherExpense() != null ? dto.getOtherExpense() : BigDecimal.ZERO);
        info.setDriverSalary(dto.getDriverSalary());
        info.setConductorSalary(dto.getConductorSalary());
        return info;
    }

    // ------------------ CALCULATIONS ------------------

    private BigDecimal calculateTripAmount(TripInfo info) {
        BigDecimal totalRevenue = info.getFromAmount()
                .add(info.getToAmount())
                .multiply(BigDecimal.valueOf(info.getNumberOfTrips()));

        BigDecimal totalExpenses = info.getDriverSalary()
                .add(info.getConductorSalary())
                .add(info.getOtherExpense());

        return totalRevenue.subtract(totalExpenses);
    }

    private BigDecimal calculateHireAmount(HireInfo info) {
        // Assuming you have a daily rate or total hire amount
        // For now, just calculating expenses
        BigDecimal totalExpenses = info.getDriverSalary()
                .add(info.getConductorSalary())
                .add(info.getOtherExpense());

        // You might want to add hire revenue here
        return totalExpenses.negate(); // Negative = expense only
    }

    // ------------------ RESPONSE MAPPER ------------------

    private IncomeResponseDTO mapToResponse(Income income) {
        return new IncomeResponseDTO(
                income.getId(),
                income.getIncomeType(),
                income.getAmount()
        );
    }



//    public IncomeResponseDTO getIncomeById(Long incomeId, Long userId) {
//
//        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Not Found"));
//
//        Bus bus = user.getBus();
//        if (bus == null) {
//            throw new RuntimeException("User not assigned to a bus");
//        }
//
//        Income income = incomeRepository.findById(incomeId).orElseThrow(()-> new RuntimeException("Income Not Found"));
//        return new IncomeResponseDTO(
//                income.getId(),
//                income.getIncomeType(),
//                income.getAmount()
//        );
//    }
}
