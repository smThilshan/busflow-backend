package com.busflow.management.service;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.HireIncomeDTO;
import com.busflow.management.dto.IncomeRequestDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.dto.TripIncomeDTO;
import com.busflow.management.entity.*;
import com.busflow.management.enums.IncomeType;
import com.busflow.management.enums.Role;
import com.busflow.management.exception.ResourceNotFoundException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.BusRepository;
import com.busflow.management.repository.IncomeRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;


    @Transactional
    public IncomeResponseDTO addIncome(Long busId, IncomeRequestDTO request, User authUser) {

        // Validate user has permission for this bus
        validateBusAccess(authUser, busId);

        // Get the bus
        Bus bus = busRepository.findById(busId).orElseThrow(()-> new ResourceNotFoundException("Bus not found with id: " + busId) );

        // Validate type specific data
        validateIncomeRequest(request);

//        Create and populate income
//        Income income = new Income();



        // 1. Validate user role
//        if (authUser.getRole() != Role.CONDUCTOR) {
//            throw new RuntimeException("Only conductors can add income");
//        }
//
//        // 2. Validate bus assignment
//        Bus bus = authUser.getBus();
//        if (bus == null) {
//            throw new RuntimeException("User is not assigned to any bus");
//        }
//
//        // 3. Validate type-specific data
//        validateIncomeRequest(request);

        // 4. Create and populate income
        Income income = new Income();
        income.setIncomeType(request.getType());
        income.setTransactionDate(request.getDate());
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

    // ✅ NEW: Get all incomes for buses the user has access to
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO> getMyIncomes(User authUser) {
        List<Long> accessibleBusIds = getAccessibleBusIds(authUser);

        List<Income> incomes = incomeRepository.findByBusIdIn(accessibleBusIds);
        return incomes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ NEW: Get income by ID with access check
    @Transactional(readOnly = true)
    public IncomeResponseDTO getIncomeById(Long incomeId, User authUser) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + incomeId));

        // Verify user has access to this income's bus
        validateBusAccess(authUser, income.getBus().getId());

        return mapToResponse(income);
    }

    // ================== CRITICAL: ACCESS CONTROL ==================

    /**
     * ✅ Validates if user has permission to access the given bus
     * - OWNER: Can access ANY bus they own
     * - CONDUCTOR: Can only access buses they're assigned to (and assignment is active)
     */
    private void validateBusAccess(User user, Long busId) {
        if (user.getRole() == Role.OWNER) {
            // Owner can access any bus they own
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            if (!bus.getOwner().getId().equals(user.getId())) {
//                throw new UnauthorizedException("You don't own this bus");
                throw new ResourceNotFoundException("You don't own this bus");
            }
        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor must have active assignment
            boolean hasAccess = busAssignmentRepository
                    .existsByUserIdAndBusIdAndIsActiveTrue(user.getId(), busId);

            if (!hasAccess) {
                throw new ResourceNotFoundException(
                        "You don't have access to bus ID: " + busId
                );
            }
        } else {
            throw new ResourceNotFoundException("Invalid role for this operation");
        }
    }


    /**
     * ✅ Get list of bus IDs the user can access
     */
    private List<Long> getAccessibleBusIds(User user) {
        if (user.getRole() == Role.OWNER) {
            // Owner gets all their buses
            return busRepository.findByOwnerId(user.getId())
                    .stream()
                    .map(Bus::getId)
                    .toList();
        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor gets only assigned buses
            return busAssignmentRepository
                    .findByUserIdAndIsActiveTrue(user.getId())
                    .stream()
                    .map(assignment -> assignment.getBus().getId())
                    .toList();
        }
        return List.of(); // Empty list for other roles
    }

    public List<IncomeResponseDTO> getIncomeByBus(Long busId, User user) {
        return null;
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
        info.setFromAmount(dto.getOnwardTripAmount());
        info.setToAmount(dto.getReturnTripAmount());
        info.setOtherExpense(dto.getOtherExpense() != null ? dto.getOtherExpense() : BigDecimal.ZERO);
        info.setDriverSalary(dto.getDriverSalary());
        info.setConductorSalary(dto.getConductorSalary());
        return info;
    }

    private HireInfo mapHireInfo(HireIncomeDTO dto) {
        HireInfo info = new HireInfo();
        info.setNumberOfDays(dto.getNoOfDays());
        info.setFromLocation(dto.getOrigin());
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
