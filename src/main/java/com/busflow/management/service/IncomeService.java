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
import com.busflow.management.exception.UnauthorizedException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.BusRepository;
import com.busflow.management.repository.IncomeRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;


    /**
     * Add income to a specific bus
     */
    @Transactional
    public IncomeResponseDTO addIncome(Long busId, IncomeRequestDTO request, User authUser) {

        // Validate user has permission for this bus
        validateBusAccess(authUser, busId);

        // Get the bus
        Bus bus = busRepository.findById(busId).orElseThrow(()-> new ResourceNotFoundException("Bus not found with id: " + busId) );

        // Validate type specific data
        validateIncomeRequest(request);

//        Create and populate income
        Income income = Income.builder()
                .incomeType(request.getType())
                .transactionDate(request.getDate())
                .bus(bus)
                .createdBy(authUser)
                .build();



        //  Set type-specific info and calculate amount
        if (request.getType() == IncomeType.TRIP) {
            TripInfo tripInfo = mapTripInfo(request.getTrip());
            income.setTripInfo(tripInfo);
            income.setAmount(calculateTripAmount(tripInfo));
        } else {
            HireInfo hireInfo = mapHireInfo(request.getHire());
            income.setHireInfo(hireInfo);
            income.setAmount(calculateHireAmount(hireInfo));
        }

        // Save and return
        Income savedIncome = incomeRepository.save(income);


        log.info("User {} added {} income {} for bus {}",
                authUser.getUsername(), request.getType(), savedIncome.getId(), bus.getBusNumber());


        return mapToResponseDTO(savedIncome);

        }

    /**
     * Get all incomes user has access to
     */
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO> getMyIncomes(User authUser) {
        List<Long> accessibleBusIds = getAccessibleBusIds(authUser);

        List<Income> incomes = incomeRepository.findByBusIdIn(accessibleBusIds);
        return incomes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get incomes for a specific bus
     */
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO> getIncomesByBus(Long busId, User authUser) {

        // Validate access
        validateBusAccess(authUser, busId);

        List<Income> incomes = incomeRepository.findByBusId(busId);

        return incomes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }



    /**
     * Get income by ID
     */
    @Transactional(readOnly = true)
    public IncomeResponseDTO getIncomeById(Long incomeId, User authUser) {

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + incomeId));

        // Validate user has access to this income's bus
        validateBusAccess(authUser, income.getBus().getId());

        return mapToResponseDTO(income);
    }

    /**
     * Get incomes by date range
     */
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO> getIncomesByDateRange(LocalDate startDate, LocalDate endDate, User authUser) {

        List<Long>  accessibleBusIds = getAccessibleBusIds(authUser);

        List<Income> incomes = incomeRepository.findByBusIdInAndTransactionDateBetween(accessibleBusIds, startDate, endDate);

        return incomes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    /**
     * Get incomes for a specific bus by date range
     */
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO> getIncomesByBusAndDateRange(Long busId, LocalDate startDate, LocalDate endDate, User authUser) {

        // validate access
        validateBusAccess(authUser, busId);

        List<Income> incomes = incomeRepository.findByBusIdAndTransactionDateBetween(busId, startDate, endDate);

        return incomes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    /*
    Get income by type
     */
    @Transactional(readOnly = true)
    public List<IncomeResponseDTO>  getIncomesByType(IncomeType incomeType, User authUser) {
        List<Long>  accessibleBusIds = getAccessibleBusIds(authUser);

        List<Income> incomes = incomeRepository.findByBusIdInAndIncomeType(accessibleBusIds, incomeType);

        return incomes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }



    /**
     * Update income
     */
    @Transactional
    public IncomeResponseDTO updateIncome(Long incomeId, IncomeRequestDTO request, User authUser) {

        Income income = incomeRepository.findById(incomeId).orElseThrow(()-> new ResourceNotFoundException("Income not found with id: " + incomeId));

        // validate bus access
        validateBusAccess(authUser, income.getBus().getId());

        // Only owner or the creator can update
        if (authUser.getRole() != Role.OWNER && !income.getCreatedBy().getId().equals(authUser.getId())) {
            throw  new UnauthorizedException("You or Owner can only update your own incomes");
        }

        //validate request
        validateIncomeRequest(request);

//      // Update fields
        income.setIncomeType(request.getType());
        income.setTransactionDate(request.getDate());

        // Update the specific info
        if (request.getType() == IncomeType.TRIP) {
            TripInfo tripInfo = mapTripInfo(request.getTrip());
            income.setTripInfo(tripInfo);
            income.setHireInfo(null); // Clear hire info
            income.setAmount(calculateTripAmount(tripInfo));
        } else if (request.getType() == IncomeType.HIRE) {
            HireInfo hireInfo = mapHireInfo(request.getHire());
            income.setHireInfo(hireInfo);
            income.setTripInfo(null);
            income.setAmount(calculateHireAmount(hireInfo));
        }

        Income updatedIncome = incomeRepository.save(income);

        log.info("User {} updated income {}", authUser.getUsername(), incomeId);

        return mapToResponseDTO(updatedIncome);

    }

    /**
     * Delete income
     */
    @Transactional
    public void deleteIncome(Long incomeId, User authUser) {

        Income income = incomeRepository.findById(incomeId).orElseThrow(()-> new ResourceNotFoundException("Income not found with id: " + incomeId));

        // validate access
        validateBusAccess(authUser, income.getBus().getId());

        // Only owner or the creator can delete
        if (authUser.getRole() != Role.OWNER &&  !income.getCreatedBy().getId().equals(authUser.getId())) {
            throw new UnauthorizedException("You or Owner can only delete your own incomes");
        }

        incomeRepository.delete(income);

        log.info("User {} deleted income {}", authUser.getUsername(), incomeId);
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
        info.setOnwardTripAmount(dto.getOnwardTripAmount());
        info.setReturnTripAmount(dto.getReturnTripAmount());
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
        info.setHireAmount(dto.getHireAmount());
        info.setOtherExpense(dto.getOtherExpense() != null ? dto.getOtherExpense() : BigDecimal.ZERO);
        info.setDriverSalary(dto.getDriverSalary());
        info.setConductorSalary(dto.getConductorSalary());
        return info;
    }

    // ------------------ CALCULATIONS ------------------

    private BigDecimal calculateTripAmount(TripInfo info) {
        BigDecimal totalRevenue = info.getOnwardTripAmount()
                .add(info.getReturnTripAmount());
//                .multiply(BigDecimal.valueOf(info.getNumberOfTrips()));

        BigDecimal totalExpenses = info.getDriverSalary()
                .add(info.getConductorSalary())
                .add(info.getOtherExpense());

        return totalRevenue.subtract(totalExpenses);
    }

    private BigDecimal calculateHireAmount(HireInfo info) {

        BigDecimal revenue = defaultZero(info.getHireAmount());
        BigDecimal expenses = defaultZero(info.getDriverSalary())
                .add(defaultZero(info.getConductorSalary()))
                .add(defaultZero(info.getOtherExpense()));

        return revenue.subtract(expenses);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    // ------------------ RESPONSE MAPPER ------------------
    private IncomeResponseDTO mapToResponseDTO(Income income) {
        return IncomeResponseDTO.builder()
                .id(income.getId())
                .incomeType(income.getIncomeType())
                .profitAmount(income.getAmount())
                .transactionDate(income.getTransactionDate())
                .busId(income.getBus().getId())
                .busNumber(income.getBus().getBusNumber())
                .createdBy(income.getCreatedBy().getUsername())
                .tripInfo(income.getTripInfo())
                .hireInfo(income.getHireInfo())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }


}
