package com.busflow.management.service;

import com.busflow.management.dto.ExpenseRequestDTO;
import com.busflow.management.dto.ExpenseResponseDTO;
import com.busflow.management.dto.IncomeResponseDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.Expense;
import com.busflow.management.entity.Income;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.exception.ResourceNotFoundException;
import com.busflow.management.exception.UnauthorizedException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.BusRepository;
import com.busflow.management.repository.ExpenseRepository;
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
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;


    /**
     * Add expense to a specific bus
     */
    @Transactional
    public ExpenseResponseDTO addExpense(Long busId,ExpenseRequestDTO request, User authUser) {

        // Validate user has permission for this bus
        validateBusAccess(authUser, busId);

        // Get the bus
        Bus bus = busRepository.findById(busId).orElseThrow(()-> new ResourceNotFoundException("bus not found" + busId));

        // Create expense
        Expense expense = Expense.builder()
                .transactionDate(request.getTransactionDate())
                .amount(request.getAmount())
                .category(request.getCategory())
                .bus(bus)
                .createdBy(authUser)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        log.info("User {} added expense {} for bus {}",
                authUser.getUsername(), savedExpense.getId(), bus.getBusNumber());

        return mapToResponseDTO(savedExpense);
    }


    /**
     * Get all expenses user has access to
     */
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getMyExpenses(User authUser) {
        List<Long> accessibleBusIds = getAccessibleBusIds(authUser);

        List<Expense> expenses = expenseRepository.findByBusIdIn(accessibleBusIds);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expenses for a specific bus
     */
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByBus(Long busId, User authUser) {

        // Validate access
        validateBusAccess(authUser, busId);

        List<Expense> expenses = expenseRepository.findByBusId(busId);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expense by ID
     */
    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long expenseId, User authUser) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        // Validate user has access to this expense's bus
        validateBusAccess(authUser, expense.getBus().getId());

        return mapToResponseDTO(expense);
    }


    /**
     * Get expenses by date range
     */
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            User authUser) {

        List<Long> accessibleBusIds = getAccessibleBusIds(authUser);

        List<Expense> expenses = expenseRepository
                .findByBusIdInAndTransactionDateBetween(accessibleBusIds, startDate, endDate);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expenses by category
     */
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByCategory(String category, User authUser) {

        List<Long> accessibleBusIds = getAccessibleBusIds(authUser);

        List<Expense> expenses = expenseRepository
                .findByBusIdInAndCategory(accessibleBusIds, category);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /*
     * Get expenses for a specific bus by date range
     */

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByBusAndDateRange(
            Long busId,
            LocalDate startDate,
            LocalDate endDate,
            User authUser
    ){
        // Validate access
        validateBusAccess(authUser, busId);

        List<Expense> expenses = expenseRepository.findByBusIdAndTransactionDateBetween(busId,startDate,endDate);
        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

    }

    /**
     * Update expense
     */
    @Transactional
    public ExpenseResponseDTO updateExpense(
            Long expenseId,
            ExpenseRequestDTO request,
            User authUser
    ){
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(()-> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        // Validate access
        validateBusAccess(authUser, expense.getBus().getId());

        // Only owner or the creator can update
        if (authUser.getRole() != Role.OWNER && !expense.getCreatedBy().getId().equals(authUser.getId())) {
            throw new UnauthorizedException("You can only update your own expenses");
        }

        // Update fields
        expense.setTransactionDate(request.getTransactionDate());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());

        Expense updatedExpense = expenseRepository.save(expense);

        log.info("User {} updated expense {}", authUser.getUsername(), expenseId);

        return mapToResponseDTO(updatedExpense);

    }

    /**
     * Delete expense
     */
    @Transactional
    public void deleteExpense(Long expenseId, User authUser) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        // Validate access
        validateBusAccess(authUser, expense.getBus().getId());

        // Only owner or the creator can delete
        if (authUser.getRole() != Role.OWNER && !expense.getCreatedBy().getId().equals(authUser.getId())) {
            throw new UnauthorizedException("You can only delete your own expenses");
        }

        expenseRepository.delete(expense);

        log.info("User {} deleted expense {}", authUser.getUsername(), expenseId);
    }





    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Validate if user has permission to access the given bus
     */
    private void validateBusAccess(User user, Long busId) {
        if (user.getRole() == Role.OWNER) {
            // Owner can access any bus they own
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            if (!bus.getOwner().getId().equals(user.getId())) {
                throw new UnauthorizedException("You don't own this bus");
            }
        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor must have active assignment
            boolean hasAccess = busAssignmentRepository
                    .existsByUserIdAndBusIdAndIsActiveTrue(user.getId(), busId);

            if (!hasAccess) {
                throw new UnauthorizedException("You don't have access to bus ID: " + busId);
            }
        } else {
            throw new UnauthorizedException("Invalid role for this operation");
        }
    }

    /**
     * Get list of bus IDs the user can access
     */
    private List<Long> getAccessibleBusIds(User user) {
        if (user.getRole() == Role.OWNER) {
            // Owner gets all their buses
            return busRepository.findByOwnerId(user.getId())
                    .stream()
                    .map(Bus::getId)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor gets only assigned buses
            return busAssignmentRepository
                    .findByUserIdAndIsActiveTrue(user.getId())
                    .stream()
                    .map(assignment -> assignment.getBus().getId())
                    .collect(Collectors.toList());
        }
        return List.of(); // Empty list for other roles
    }

    /**
     * Map Expense entity to ExpenseResponseDTO
     */
    private ExpenseResponseDTO mapToResponseDTO(Expense expense) {
        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .transactionDate(expense.getTransactionDate())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .busId(expense.getBus().getId())
                .busNumber(expense.getBus().getBusNumber())
                .createdBy(expense.getCreatedBy().getUsername())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }


}
